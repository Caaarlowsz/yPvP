package br.com.weavenmc.ypvp.ability.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.core.permission.Group;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class SupernovaAbility extends Ability {
	private ArrayList<ArrowDirection> directions;
	private HashMap<Arrow, Vector> arrows;
	private Set<UUID> damaged;

	public SupernovaAbility() {
		this.damaged = new HashSet<UUID>();
		this.setName("Supernova");
		this.setHasItem(true);
		this.setGroupToUse(Group.LIGHT);
		this.setIcon(Material.ARROW);
		this.setDescription(new String[] { "�7Invoque flechas ao seu redor e", "�7cause dano em seus inimigos." });
		this.setPrice(90000);
		this.setTempPrice(6500);
		this.directions = new ArrayList<ArrowDirection>();
		ArrayList<Double> list = new ArrayList<Double>();
		list.add(0.0);
		list.add(22.5);
		list.add(45.0);
		list.add(67.5);
		list.add(90.0);
		list.add(112.5);
		list.add(135.0);
		list.add(157.5);
		list.add(180.0);
		list.add(202.5);
		list.add(225.0);
		list.add(247.5);
		list.add(270.0);
		list.add(292.5);
		list.add(315.0);
		list.add(337.5);
		for (final double i : list) {
			this.directions.add(new ArrowDirection(i, 67.5));
			this.directions.add(new ArrowDirection(i, 45.0));
			this.directions.add(new ArrowDirection(i, 22.5));
			this.directions.add(new ArrowDirection(i, 0.0));
			this.directions.add(new ArrowDirection(i, -22.5));
			this.directions.add(new ArrowDirection(i, -45.0));
			this.directions.add(new ArrowDirection(i, -67.5));
		}
		this.directions.add(new ArrowDirection(90.0, 0.0));
		this.directions.add(new ArrowDirection(-90.0, 0.0));
		this.directions.add(new ArrowDirection(0.0, 90.0));
		this.directions.add(new ArrowDirection(0.0, -90.0));
		list.clear();
		list = null;
		this.arrows = new HashMap<Arrow, Vector>();
	}

	@Override
	public void eject(final Player p) {
	}

	@EventHandler
	public void onUpdate(final UpdateEvent event) {
		final Iterator<Map.Entry<Arrow, Vector>> entrys = this.arrows.entrySet().iterator();
		while (entrys.hasNext()) {
			final Map.Entry<Arrow, Vector> entry = entrys.next();
			final Arrow arrow = entry.getKey();
			final Vector vec = entry.getValue();
			if (!arrow.isDead()) {
				arrow.setVelocity(vec.normalize().multiply(vec.lengthSquared() / 4.0));
				if (!arrow.isOnGround() && arrow.getTicksLived() < 100) {
					continue;
				}
				arrow.remove();
			} else {
				entrys.remove();
			}
		}
	}

	@EventHandler
	public void onSupernovaListener(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		if (this.hasKit(p) && this.isItem(p.getItemInHand()) && event.getAction().toString().contains("RIGHT")) {
			event.setCancelled(true);
			if (this.inCooldown(p)) {
				this.sendCooldown(p);
				return;
			}
			this.addCooldown(p, 20);
			final Location loc = p.getLocation();
			for (final ArrowDirection d : this.directions) {
				synchronized (this) {
					final Arrow arrow = (Arrow) loc.getWorld().spawn(loc.clone().add(0.0, 1.0, 0.0),
							Arrow.class);
					arrow.setMetadata("Supernova", (MetadataValue) new FixedMetadataValue((Plugin) YPvP.getPlugin(),
							(Object) p.getUniqueId()));
					final double pitch = (d.pitch + 90.0) * 3.141592653589793 / 180.0;
					final double yaw = (d.yaw + 90.0) * 3.141592653589793 / 180.0;
					final double x = Math.sin(pitch) * Math.cos(yaw);
					final double y = Math.sin(pitch) * Math.sin(yaw);
					final double z = Math.cos(pitch);
					final Vector vec = new Vector(x, z, y);
					arrow.setShooter((ProjectileSource) p);
					arrow.setVelocity(vec.multiply(2));
					this.arrows.put(arrow, vec);
				}
			}
			p.playSound(p.getLocation(), Sound.SHOOT_ARROW, 0.5f, 1.0f);
		}
	}

	@EventHandler
	public void onDamage(final EntityDamageByEntityEvent e) {
		if (e.getDamager().hasMetadata("Supernova") && e.getDamager() instanceof Arrow) {
			final Arrow arrow = (Arrow) e.getDamager();
			if (arrow.getShooter() instanceof Player) {
				final Player s = (Player) arrow.getShooter();
				if (e.getEntity() instanceof Player) {
					final Player p = (Player) e.getEntity();
					if (s.getUniqueId() == p.getUniqueId()) {
						e.setCancelled(true);
						return;
					}
					if (this.damaged.contains(p.getUniqueId())) {
						e.setCancelled(true);
						return;
					}
				}
				e.setDamage(10.0);
				if (e.getEntity() instanceof Player) {
					final Player p = (Player) e.getEntity();
					this.damaged.add(p.getUniqueId());
					new BukkitRunnable() {
						public void run() {
							SupernovaAbility.this.damaged.remove(p.getUniqueId());
						}
					}.runTaskLater((Plugin) YPvP.getPlugin(), 10L);
				}
			}
		}
	}

	protected static class ArrowDirection {
		private double pitch;
		private double yaw;

		public ArrowDirection(final double pitch, final double yaw) {
			this.pitch = pitch;
			this.yaw = yaw;
		}
	}
}
