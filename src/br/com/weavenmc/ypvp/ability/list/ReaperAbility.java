package br.com.weavenmc.ypvp.ability.list;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class ReaperAbility extends Ability {
	public ReaperAbility() {
		this.setName("Reaper");
		this.setHasItem(false);
		this.setGroupToUse(Group.LIGHT);
		this.setIcon(Material.WOOD_AXE);
		this.setDescription(
				new String[] { "§7A cada hit tenha 30% de chance de", "§7seu inimigo pegar efeito de wither." });
		this.setPrice(50000);
		this.setTempPrice(5300);
	}

	@Override
	public void eject(final Player p) {
		p.getActivePotionEffects().clear();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(final EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			final Player p = (Player) event.getDamager();
			if (this.hasKit(p) && !event.isCancelled()) {
				final Player target = (Player) event.getEntity();
				if (!target.hasPotionEffect(PotionEffectType.WITHER)) {
					final int random = new Random().nextInt(100);
					if (random > 0 && random <= 30) {
						target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 140, 3));
					}
				}
			}
		}
	}
}
