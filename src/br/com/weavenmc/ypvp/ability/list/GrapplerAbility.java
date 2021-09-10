package br.com.weavenmc.ypvp.ability.list;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.util.Hook;
import net.minecraft.server.v1_8_R3.EntityHuman;

public class GrapplerAbility extends Ability {
	private HashMap<UUID, Hook> hooks;

	public GrapplerAbility() {
		this.hooks = new HashMap<UUID, Hook>();
		this.setName("Grappler");
		this.setHasItem(true);
		this.setGroupToUse(Group.LIGHT);
		this.setIcon(Material.LEASH);
		this.setDescription(new String[] { "§7Seja capaz de ir \u00e1 qualquer lugar", "§7com sua corda." });
		this.setPrice(50000);
		this.setTempPrice(5000);
	}

	@Override
	public void eject(final Player p) {
		if (this.hooks.containsKey(p.getUniqueId())) {
			this.hooks.get(p.getUniqueId()).remove();
			this.hooks.remove(p.getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (!this.hasKit(event.getPlayer())) {
			return;
		}
		if (event.getItem() == null) {
			return;
		}
		final Action a = event.getAction();
		final Player p = event.getPlayer();
		final ItemStack item = p.getItemInHand();
		if (!this.isItem(item)) {
			return;
		}
		if (a.name().contains("RIGHT")) {
			event.setCancelled(true);
		}
		p.updateInventory();
		if (this.inCooldown(p)) {
			p.playSound(p.getLocation(), Sound.IRONGOLEM_HIT, 0.5f, 1.0f);
			this.sendCooldown(p);
			return;
		}
		if (event.getAction().name().contains("LEFT")) {
			if (this.hooks.containsKey(p.getUniqueId())) {
				this.hooks.get(p.getUniqueId()).remove();
				this.hooks.remove(p.getUniqueId());
			}
			final Hook hook = new Hook(p.getWorld(), (EntityHuman) ((CraftPlayer) p).getHandle());
			final Vector direction = p.getLocation().getDirection();
			hook.spawn(p.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()));
			hook.move(direction.getX() * 5.0, direction.getY() * 5.0, direction.getZ() * 5.0);
			this.hooks.put(p.getUniqueId(), hook);
		} else if (event.getAction().name().contains("RIGHT") && this.hooks.containsKey(p.getUniqueId())) {
			if (!this.hooks.get(p.getUniqueId()).isHooked()) {
				return;
			}
			final Hook hook = this.hooks.get(p.getUniqueId());
			final Location loc = hook.getBukkitEntity().getLocation();
			final Location pLoc = p.getLocation();
			final double t = loc.distance(p.getLocation());
			final double v_x = (1.0 + 0.04000000000000001 * t)
					* ((this.isNear(loc, pLoc) ? 0.0 : (loc.getX() - pLoc.getX())) / t);
			final double v_y = (0.9 + 0.03 * t) * ((this.isNear(loc, pLoc) ? 0.1 : (loc.getY() - pLoc.getY())) / t);
			final double v_z = (1.0 + 0.04000000000000001 * t)
					* ((this.isNear(loc, pLoc) ? 0.0 : (loc.getZ() - pLoc.getZ())) / t);
			final Vector v = p.getVelocity();
			v.setX(v_x);
			v.setY(v_y);
			v.setZ(v_z);
			p.setVelocity(v.multiply(1.0));
			final double player = p.getLocation().getY();
			final double grappler = hook.getBukkitEntity().getLocation().getY();
			if (player < grappler || player > grappler) {
				p.setFallDistance(0.0f);
			}
			p.getWorld().playSound(p.getLocation(), Sound.STEP_GRAVEL, 1.0f, 1.0f);
		}
	}

	@EventHandler
	public void onPlayerItemHeldListener(final PlayerItemHeldEvent e) {
		if (this.hooks.containsKey(e.getPlayer().getUniqueId())) {
			this.hooks.get(e.getPlayer().getUniqueId()).remove();
			this.hooks.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerQuitListener(final PlayerQuitEvent e) {
		if (this.hooks.containsKey(e.getPlayer().getUniqueId())) {
			this.hooks.remove(e.getPlayer().getUniqueId());
			this.hooks.get(e.getPlayer().getUniqueId()).remove();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLeash(final PlayerLeashEntityEvent event) {
		if (!this.hasKit(event.getPlayer())) {
			return;
		}
		final Player p = event.getPlayer();
		if (p.getItemInHand() == null) {
			return;
		}
		final ItemStack item = p.getItemInHand();
		if (!this.isItem(item)) {
			return;
		}
		event.setCancelled(true);
		if (this.hooks.containsKey(p.getUniqueId()) && this.hooks.get(p.getUniqueId()).isHooked()) {
			final Hook hook = this.hooks.get(p.getUniqueId());
			final Location loc = hook.getBukkitEntity().getLocation();
			final Location playerLoc = p.getLocation();
			final double t = loc.distance(playerLoc);
			final double v_x = (1.0 + 0.04000000000000001 * t)
					* ((this.isNear(loc, playerLoc) ? 0.0 : (loc.getX() - playerLoc.getX())) / t);
			final double v_y = (0.9 + 0.03 * t)
					* ((this.isNear(loc, playerLoc) ? 0.1 : (loc.getY() - playerLoc.getY())) / t);
			final double v_z = (1.0 + 0.04000000000000001 * t)
					* ((this.isNear(loc, playerLoc) ? 0.0 : (loc.getZ() - playerLoc.getZ())) / t);
			final Vector v = p.getVelocity();
			v.setX(v_x);
			v.setY(v_y);
			v.setZ(v_z);
			p.setVelocity(v.multiply(1.0));
			final double player = p.getLocation().getY();
			final double grappler = hook.getBukkitEntity().getLocation().getY();
			if (player < grappler || player > grappler) {
				p.setFallDistance(0.0f);
			}
			p.getWorld().playSound(playerLoc, Sound.STEP_GRAVEL, 1.0f, 1.0f);
		}
	}

	@EventHandler
	public void onDamage(final EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (this.hasKit(p) && !event.isCancelled() && event.getDamager() instanceof Player) {
				if (this.hooks.containsKey(p.getUniqueId())) {
					this.hooks.get(p.getUniqueId()).remove();
					this.hooks.remove(p.getUniqueId());
				}
				this.addCooldown(p, 7);
			}
			p = null;
		}
	}

	private boolean isNear(final Location loc, final Location playerLoc) {
		return loc.distance(playerLoc) < 1.5;
	}
}
