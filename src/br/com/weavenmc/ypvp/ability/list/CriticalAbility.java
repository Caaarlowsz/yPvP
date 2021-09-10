package br.com.weavenmc.ypvp.ability.list;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class CriticalAbility extends Ability {
	public CriticalAbility() {
		this.setName("Critical");
		this.setHasItem(false);
		this.setGroupToUse(Group.BETA);
		this.setIcon(Material.REDSTONE_BLOCK);
		this.setDescription(new String[] { "§7A cada hit tenha 30% de um", "§7critical aumentado 3x." });
		this.setPrice(75000);
		this.setTempPrice(7000);
	}

	@Override
	public void eject(final Player p) {
	}

	@EventHandler
	public void onEntityDamage(final EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player e = (Player) event.getEntity();
			Player d = (Player) event.getDamager();
			if (this.hasKit(d) && !event.isCancelled()) {
				Integer chance = new Random().nextInt(100);
				if (chance > 0 && chance <= 30) {
					event.setDamage(event.getDamage() + 2.5);
					d.getWorld().playEffect(e.getLocation(), Effect.STEP_SOUND, (Object) Material.REDSTONE_BLOCK, 10);
					e.sendMessage("§5§lCRITICAL§f Voc\u00ea recebeu um §9§lGOLPE CRITICO§f de §9§l" + d.getName());
					d.sendMessage("§5§lCRITICAL§f Voc\u00ea deu um §9§lGOLPE CRITICO§f no §9§l" + e.getName());
				}
				chance = null;
			}
			e = null;
			d = null;
		}
	}
}
