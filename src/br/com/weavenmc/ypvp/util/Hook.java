package br.com.weavenmc.ypvp.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSnowball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;

import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntitySnowball;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;

public class Hook extends EntityFishingHook {
	private Snowball sb;
	private EntitySnowball controller;
	public int a;
	public EntityHuman owner;
	public Entity hooked;
	public boolean lastControllerDead;
	public boolean isHooked;

	public Hook(final World world, final EntityHuman entityhuman) {
		super((net.minecraft.server.v1_8_R3.World) ((CraftWorld) world).getHandle(), entityhuman);
		this.owner = entityhuman;
	}

	public void t_() {
		if (!this.lastControllerDead && this.controller.dead) {
			((Player) this.owner.getBukkitEntity()).sendMessage("§5§lGRAPPLER§f Corda §9§lPRESA§f!");
		}
		this.lastControllerDead = this.controller.dead;
		for (final Entity entity : this.controller.world.getWorld().getEntities()) {
			if (entity instanceof Firework) {
				continue;
			}
			if (entity instanceof Snowball) {
				continue;
			}
			if (entity.getEntityId() == this.getBukkitEntity().getEntityId()) {
				continue;
			}
			if (entity.getEntityId() == this.owner.getBukkitEntity().getEntityId()) {
				continue;
			}
			if (entity.getEntityId() == this.controller.getBukkitEntity().getEntityId()) {
				continue;
			}
			if (entity.getLocation().distance(this.controller.getBukkitEntity().getLocation()) > 2.0) {
				continue;
			}
			this.controller.die();
			this.hooked = entity;
			this.isHooked = true;
			this.locX = entity.getLocation().getX();
			this.locY = entity.getLocation().getY();
			this.locZ = entity.getLocation().getZ();
			this.motX = 0.0;
			this.motY = 0.04;
			this.motZ = 0.0;
		}
		try {
			this.locX = this.hooked.getLocation().getX();
			this.locY = this.hooked.getLocation().getY();
			this.locZ = this.hooked.getLocation().getZ();
			this.motX = 0.0;
			this.motY = 0.04;
			this.motZ = 0.0;
			this.isHooked = true;
		} catch (Exception e) {
			if (this.controller.dead) {
				this.isHooked = true;
			}
			this.locX = this.controller.locX;
			this.locY = this.controller.locY;
			this.locZ = this.controller.locZ;
		}
	}

	public void die() {
	}

	public void remove() {
		super.die();
	}

	public void spawn(final Location location) {
		this.sb = (Snowball) this.owner.getBukkitEntity().launchProjectile(Snowball.class);
		this.controller = ((CraftSnowball) this.sb).getHandle();
		final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { this.controller.getId() });
		for (final Player p : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
		((CraftWorld) location.getWorld()).getHandle().addEntity((net.minecraft.server.v1_8_R3.Entity) this);
	}

	public boolean isHooked() {
		return this.isHooked;
	}

	public void setHookedEntity(final Entity damaged) {
		this.hooked = damaged;
	}
}
