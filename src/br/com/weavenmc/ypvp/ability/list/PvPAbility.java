package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class PvPAbility extends Ability {
	public PvPAbility() {
		this.setName("PvP");
		this.setHasItem(false);
		this.setGroupToUse(Group.MEMBRO);
		if (yPvP.getPlugin().getPvpType() == yPvP.PvPType.FULLIRON) {
			this.setIcon(Material.DIAMOND_SWORD);
			this.setDescription(new String[] { "§7Voc\u00ea n\u00e3o recebe habilidades mas recebe",
					"§7uma espada de diamante com afia\u00e7\u00e3o 1." });
		} else {
			this.setIcon(Material.STONE_SWORD);
			this.setDescription(new String[] { "§7Voc\u00ea n\u00e3o recebe habilidades mas recebe",
					"§7uma espada de pedra com afia\u00e7\u00e3o 1." });
		}
	}

	@Override
	public void eject(final Player p) {
	}
}
