package br.com.weavenmc.ypvp.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import br.com.weavenmc.ypvp.Management;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;

public class GamerManager extends Management {
	private HashMap<UUID, Gamer> gamers;

	public GamerManager(final YPvP plugin) {
		super(plugin);
	}

	public void loadGamer(final UUID uuid, final Gamer gamer) {
		this.gamers.put(uuid, gamer);
	}

	public Gamer getGamer(final UUID uuid) {
		return this.gamers.get(uuid);
	}

	public Collection<Gamer> getGamers() {
		return this.gamers.values();
	}

	public void unloadGamer(final UUID uuid) {
		this.gamers.remove(uuid);
	}

	@Override
	public void enable() {
		this.gamers = new HashMap<UUID, Gamer>();
	}

	@Override
	public void disable() {
		this.gamers.clear();
		this.gamers = null;
	}
}
