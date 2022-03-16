package br.com.weavenmc.ypvp.managers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Listener;

import br.com.weavenmc.ypvp.Management;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.minigame.BattleMinigame;
import br.com.weavenmc.ypvp.minigame.FramesMinigame;
import br.com.weavenmc.ypvp.minigame.LavaChallengeMinigame;
import br.com.weavenmc.ypvp.minigame.Minigame;
import br.com.weavenmc.ypvp.minigame.SpawnMinigame;
import br.com.weavenmc.ypvp.minigame.VoidChallengeMinigame;

public class WarpManager extends Management {
	private Set<Minigame> minigames;

	public WarpManager(final YPvP plugin) {
		super(plugin);
		this.minigames = new HashSet<Minigame>();
	}

	@Override
	public void enable() {
		this.minigames.add(new BattleMinigame());
		this.minigames.add(new FramesMinigame());
		this.minigames.add(new LavaChallengeMinigame());
		this.minigames.add(new SpawnMinigame());
		this.minigames.add(new VoidChallengeMinigame());
		for (final Minigame minigame : this.minigames) {
			this.registerListener((Listener) minigame);
			this.getPlugin().getLogger().info("Registrado a Warp: " + minigame.getName());
		}
	}

	public Minigame getWarp(final Class<?> clazz) {
		for (final Minigame minigame : this.minigames) {
			if (!minigame.getClass().getSimpleName().equalsIgnoreCase(clazz.getSimpleName())) {
				continue;
			}
			return minigame;
		}
		return null;
	}

	public Minigame getWarp(final String name) {
		for (final Minigame minigame : this.minigames) {
			if (minigame.getName().equalsIgnoreCase(name)) {
				return minigame;
			}
			String[] otherNames;
			for (int length = (otherNames = minigame.getOtherNames()).length, i = 0; i < length; ++i) {
				final String other = otherNames[i];
				if (other.equalsIgnoreCase(name)) {
					return minigame;
				}
			}
		}
		return null;
	}

	@Override
	public void disable() {
		this.minigames.clear();
		this.minigames = null;
	}
}
