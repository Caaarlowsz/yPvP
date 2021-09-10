package br.com.weavenmc.ypvp.ability;

import org.bukkit.entity.Player;

public class NoneAbility extends Ability {
	public NoneAbility() {
		this.setName("Nenhum");
	}

	@Override
	public void eject(final Player p) {
	}
}
