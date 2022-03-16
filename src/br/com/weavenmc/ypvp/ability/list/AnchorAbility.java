package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import br.com.weavenmc.commons.core.permission.Group;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class AnchorAbility extends Ability {
	public AnchorAbility() {
		this.setName("Anchor");
		this.setHasItem(false);
		this.setGroupToUse(Group.LIGHT);
		this.setIcon(Material.ANVIL);
		this.setDescription(new String[] { "�7N\u00e3o d\u00ea e nem receba knockback." });
		this.setPrice(55000);
		this.setTempPrice(5000);
	}

	@Override
	public void eject(final Player p) {
	}

	@EventHandler
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			final Player e = (Player) event.getEntity();
			final Player d = (Player) event.getDamager();
			if ((this.hasKit(e) || this.hasKit(d)) && !event.isCancelled()) {
				this.anchor(e, d);
			}
		}
	}

	public void anchor(final Player a, final Player b) {
		a.setVelocity(new Vector(0.0, 0.0, 0.0));
		b.setVelocity(new Vector(0.0, 0.0, 0.0));
		Bukkit.getScheduler().runTaskLater((Plugin) YPvP.getPlugin(), () -> {
			a.setVelocity(new Vector(0.0, 0.0, 0.0));
			b.setVelocity(new Vector(0.0, 0.0, 0.0));
		}, 1L);
	}
}
