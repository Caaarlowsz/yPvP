package br.com.weavenmc.ypvp.minigame;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;

public abstract class Minigame implements Listener {
	private String name;
	private String[] otherNames;
	private Set<UUID> protection;
	private Set<UUID> players;
	private UUID topperKs;
	private int topKs;
	private boolean topKillStreakMinigame;

	public Minigame() {
		this.protection = new HashSet<UUID>();
		this.players = new HashSet<UUID>();
		this.topperKs = null;
		this.topKs = 0;
		this.topKillStreakMinigame = false;
	}

	public abstract void join(final Player p0);

	public abstract void quit(final Player p0);

	public void updateTopKs() {
		int maxKillStreak = 0;
		UUID topper = null;
		for (final Player p : Bukkit.getOnlinePlayers()) {
			final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (gamer.getWarp() != this) {
				continue;
			}
			final BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
			if (bP == null) {
				continue;
			}
			final int killStreak = bP.getData(DataType.PVP_KILLSTREAK).asInt();
			if (killStreak <= maxKillStreak) {
				continue;
			}
			maxKillStreak = killStreak;
			topper = p.getUniqueId();
		}
		if (topper != null) {
			this.topKs = maxKillStreak;
			this.topperKs = topper;
		} else {
			this.topKs = 0;
			this.topperKs = null;
		}
	}

	public String getTopKsName() {
		if (this.topperKs == null) {
			return "Ningu\u00e9m";
		}
		final Player p = Bukkit.getPlayer(this.topperKs);
		if (p != null) {
			return p.getName();
		}
		return "Ningu\u00e9m";
	}

	public int getTopKs() {
		if (this.topperKs == null) {
			return 0;
		}
		final Player p = Bukkit.getPlayer(this.topperKs);
		if (p != null) {
			return this.topKs;
		}
		return 0;
	}

	@EventHandler
	public void onUpdate(final UpdateEvent event) {
		if (event.getCurrentTick() % 40L != 0L) {
			return;
		}
		if (!this.topKillStreakMinigame) {
			return;
		}
		this.updateTopKs();
	}

	@EventHandler
	public void quit(final PlayerQuitEvent event) {
		this.quit(event.getPlayer());
	}

	public void protect(final Player p) {
		if (!this.protection.contains(p.getUniqueId())) {
			this.protection.add(p.getUniqueId());
		}
	}

	public void teleport(final Player p) {
		Location loc = YPvP.getPlugin().getLocationManager().getLocation(this.name);
		if (loc != null) {
			p.teleport(loc);
			loc = null;
		}
	}

	public boolean isProtected(final Player p) {
		return this.protection.contains(p.getUniqueId());
	}

	public void unprotect(final Player p) {
		if (this.protection.contains(p.getUniqueId())) {
			this.protection.remove(p.getUniqueId());
		}
	}

	public void joinPlayer(final UUID uuid) {
		if (!this.players.contains(uuid)) {
			this.players.add(uuid);
		}
	}

	public void quitPlayer(final UUID uuid) {
		if (this.players.contains(uuid)) {
			this.players.remove(uuid);
		}
	}

	public int getPlaying() {
		return this.players.size();
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOtherNames(final String[] otherNames) {
		this.otherNames = otherNames;
	}

	public void setTopKillStreakMinigame(final boolean topKillStreakMinigame) {
		this.topKillStreakMinigame = topKillStreakMinigame;
	}

	public String getName() {
		return this.name;
	}

	public String[] getOtherNames() {
		return this.otherNames;
	}

	public Set<UUID> getProtection() {
		return this.protection;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}

	public UUID getTopperKs() {
		return this.topperKs;
	}

	public boolean isTopKillStreakMinigame() {
		return this.topKillStreakMinigame;
	}
}
