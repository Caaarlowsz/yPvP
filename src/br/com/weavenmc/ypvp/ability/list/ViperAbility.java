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

public class ViperAbility extends Ability {
	public ViperAbility() {
		this.setName("Viper");
		this.setHasItem(false);
		this.setGroupToUse(Group.MEMBRO);
		this.setIcon(Material.SPIDER_EYE);
		this.setDescription(
				new String[] { "§7A cada hit tenha 30% de chance de", "§7seu inimigo pegar efeito de veneno." });
		this.setPrice(50000);
		this.setTempPrice(2000);
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
				if (!target.hasPotionEffect(PotionEffectType.POISON)) {
					final int random = new Random().nextInt(100);
					if (random > 0 && random <= 30) {
						target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 140, 3));
					}
				}
			}
		}
	}
}
