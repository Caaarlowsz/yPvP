package br.com.weavenmc.ypvp.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Listener;

import br.com.weavenmc.commons.util.ClassGetter;
import br.com.weavenmc.ypvp.Management;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.ability.NoneAbility;

public class AbilityManager extends Management {
	private List<Ability> abilities;
	private final Ability none;

	public AbilityManager(final YPvP plugin) {
		super(plugin);
		this.abilities = new ArrayList<Ability>();
		this.none = new NoneAbility();
	}

	@Override
	public void enable() {
		for (final Class<?> clazz : ClassGetter.getClassesForPackageByFile(this.getPlugin().getFile(),
				"br.com.weavenmc.ypvp.ability.list")) {
			if (Ability.class.isAssignableFrom(clazz)) {
				try {
					final Ability ability = (Ability) clazz.newInstance();
					this.registerListener((Listener) ability);
					this.abilities.add(ability);
					this.getPlugin().getLogger().info("Habilidade Carregada: " + ability.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Ability getAbility(final String name) {
		for (final Ability ability : this.abilities) {
			if (!ability.getName().equalsIgnoreCase(name)) {
				continue;
			}
			return ability;
		}
		return null;
	}

	@Override
	public void disable() {
	}

	public List<Ability> getAbilities() {
		return this.abilities;
	}

	public Ability getNone() {
		return this.none;
	}
}
