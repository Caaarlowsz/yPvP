package br.com.weavenmc.ypvp.managers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.minigame.Minigame;

public class TeleportManager implements Listener {
	private static final TeleportManager instance;
	private boolean listenerRegistered;
	private final HashMap<UUID, BukkitTask> teleportTask;

	static {
		instance = new TeleportManager();
	}

	public TeleportManager() {
		this.listenerRegistered = false;
		this.teleportTask = new HashMap<UUID, BukkitTask>();
		if (!this.listenerRegistered) {
			Bukkit.getPluginManager().registerEvents((Listener) this, (Plugin) YPvP.getPlugin());
			this.listenerRegistered = true;
		}
	}

	public void allowJoin(final Player p) {
		final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		gamer.resetCombat();
		final BukkitTask currentTask = this.teleportTask.get(p.getUniqueId());
		if (currentTask != null) {
			currentTask.cancel();
			this.teleportTask.remove(p.getUniqueId());
		}
	}

	public boolean canJoin(final Player p, final Minigame minigame) {
		final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		final BukkitTask currentTask = this.teleportTask.get(p.getUniqueId());
		if (currentTask != null) {
			currentTask.cancel();
			this.teleportTask.remove(p.getUniqueId());
			p.sendMessage("�9�lTELEPORTE�f Voc\u00ea �3�lCANCELOU�f o teleporte!");
			return false;
		}
		if (!gamer.inCombat()) {
			return true;
		}
		if (!p.isOnGround()) {
			p.sendMessage("�9�lTELEPORTE�f Voc\u00ea precisa estar no �3�lCHAO�f para teleportar!");
			return false;
		}
		p.sendMessage("�9�lTELEPORTE�f Voce sera teleportado em �3�l5 SEGUNDOS�f. N\u00e3o se mexa!");
		this.teleportTask.put(p.getUniqueId(), new BukkitRunnable() {
			public void run() {
				final BukkitTask task = TeleportManager.this.teleportTask.get(p.getUniqueId());
				if (task != null) {
					gamer.resetCombat();
					minigame.join(p);
					TeleportManager.this.teleportTask.remove(p.getUniqueId());
				}
			}
		}.runTaskLater((Plugin) YPvP.getPlugin(), 100L));
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTeleportMove(final PlayerMoveEvent event) {
		final Player p = event.getPlayer();
		if (!this.teleportTask.containsKey(p.getUniqueId())) {
			return;
		}
		final BukkitTask currentTask = this.teleportTask.get(p.getUniqueId());
		currentTask.cancel();
		this.teleportTask.remove(p.getUniqueId());
		p.sendMessage("�9�lTELEPORTE�f Seu teleporte foi �3�lCANCELADO�f pois voce se mexeu!");
	}

	public static TeleportManager getInstance() {
		return TeleportManager.instance;
	}
}
