package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

import br.com.weavenmc.commons.core.permission.Group;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class JumperAbility extends Ability {
	public JumperAbility() {
		this.setName("Jumper");
		this.setHasItem(true);
		this.setGroupToUse(Group.LIGHT);
		this.setIcon(Material.EYE_OF_ENDER);
		this.setDescription(new String[] { "�7Monte em sua ender pearl", "�7e voe junto com ela." });
		this.setPrice(80000);
		this.setTempPrice(8500);
	}

	@Override
	public void eject(final Player p) {
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJumperListener(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		if (this.hasKit(p) && this.isItem(p.getItemInHand())) {
			event.setCancelled(true);
			p.updateInventory();
			if (!this.inCooldown(p)) {
				this.addCooldown(p, 15);
				p.setFallDistance(0.0f);
				final EnderPearl ender = (EnderPearl) p.launchProjectile(EnderPearl.class);
				ender.setPassenger((Entity) p);
				ender.setMetadata("Jumper",
						(MetadataValue) new FixedMetadataValue((Plugin) YPvP.getPlugin(), (Object) p.getUniqueId()));
				ender.setShooter((ProjectileSource) null);
			} else {
				this.sendCooldown(p);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJumperHit(final ProjectileHitEvent event) {
		if (!event.getEntity().hasMetadata("Jumper")) {
			return;
		}
		if (event.getEntity().getPassenger() != null) {
			event.getEntity().eject();
		}
	}
}
