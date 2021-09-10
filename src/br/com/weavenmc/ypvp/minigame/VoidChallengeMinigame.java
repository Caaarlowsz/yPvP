package br.com.weavenmc.ypvp.minigame;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;

public class VoidChallengeMinigame extends Minigame {
	private HashMap<UUID, Integer> voidTimers;

	public VoidChallengeMinigame() {
		this.voidTimers = new HashMap<UUID, Integer>();
		this.setName("Void");
		this.setOtherNames(new String[0]);
		this.setTopKillStreakMinigame(false);
	}

	@Override
	public void join(final Player p) {
		BossBarAPI.removeBar(p);
		if (!TeleportManager.getInstance().canJoin(p, this)) {
			return;
		}
		if (p.getAllowFlight() && !AdminMode.getInstance().isAdmin(p)) {
			p.setAllowFlight(false);
		}
		p.sendMessage("§9§lTELEPORTE§f Voc\u00ea foi teleportado para §3§lVoid Challenge");
		final Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		gamer.resetCombat();
		if (gamer.getWarp() != null) {
			gamer.getWarp().quit(p);
		}
		this.joinPlayer(p.getUniqueId());
		yPvP.getPlugin().getCooldownManager().removeCooldown(p);
		yPvP.getPlugin().getAbilityManager().getAbilities().stream().forEach(ability -> ability.eject(p));
		gamer.setWarp(this);
		gamer.setAbility(yPvP.getPlugin().getAbilityManager().getNone());
		this.teleport(p);
		p.setHealth(20.0);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.getActivePotionEffects().clear();
		p.getInventory().setArmorContents((ItemStack[]) null);
		p.getInventory().clear();
		for (int i = 0; i < 36; ++i) {
			p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MUSHROOM_SOUP) });
		}
		p.updateInventory();
		yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
	}

	public String timerFormat(final int time) {
		if (time >= 3600) {
			final int hours = time / 3600;
			final int minutes = time % 3600 / 60;
			final int seconds = time % 3600 % 60;
			return String.valueOf((hours < 10) ? "0" : "") + hours + ":" + ((minutes < 10) ? "0" : "") + minutes + ":"
					+ ((seconds < 10) ? "0" : "") + seconds;
		}
		final int minutes2 = time / 60;
		final int seconds2 = time % 60;
		return String.valueOf(minutes2) + ":" + ((seconds2 < 10) ? "0" : "") + seconds2;
	}

	public String getTimeFormat(final int time) {
		if (time >= 3600) {
			final int hours = time / 3600;
			final int minutes = time % 3600 / 60;
			final int seconds = time % 3600 % 60;
			return String
					.valueOf((hours == 1) ? new StringBuilder(String.valueOf(hours)).append(" hora").toString()
							: new StringBuilder(String.valueOf(hours)).append(" horas").toString())
					+ " "
					+ ((minutes == 1) ? (String.valueOf(minutes) + " minuto") : (String.valueOf(minutes) + " minutos"))
					+ ((seconds <= 0) ? ""
							: (" e " + ((seconds == 1) ? (String.valueOf(seconds) + " segundo")
									: (String.valueOf(seconds) + " segundos"))));
		}
		if (time < 60) {
			return (time == 1) ? (String.valueOf(time) + " segundo") : (String.valueOf(time) + " segundos");
		}
		final int minutes2 = time / 60;
		final int seconds2 = time % 60;
		return String
				.valueOf((minutes2 == 1) ? new StringBuilder(String.valueOf(minutes2)).append(" minuto").toString()
						: new StringBuilder(String.valueOf(minutes2)).append(" minutos").toString())
				+ ((seconds2 <= 0) ? ""
						: (" e " + ((seconds2 == 1) ? (String.valueOf(seconds2) + " segundo")
								: (String.valueOf(seconds2) + " segundos"))));
	}

	public String getTimeSurviving(final Player p) {
		if (!this.voidTimers.containsKey(p.getUniqueId())) {
			return this.timerFormat(0);
		}
		return this.timerFormat(this.voidTimers.get(p.getUniqueId()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(final PlayerDeathEvent event) {
		final Player p = event.getEntity();
		if (this.voidTimers.containsKey(p.getUniqueId())) {
			final int survivalTime = this.voidTimers.get(p.getUniqueId());
			p.sendMessage("§5§lVOID CHALLENGE§f Voc\u00ea sobreviveu por §9§l"
					+ this.getTimeFormat(survivalTime).toUpperCase() + "§f!");
			if (survivalTime >= 1) {
				final BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
				bP.addMoney(survivalTime);
				p.sendMessage("§6§lMOEDAS§f Voc\u00ea recebeu §6§l" + survivalTime + " MOEDAS");
				bP.save(new DataCategory[] { DataCategory.BALANCE });
			}
			this.voidTimers.remove(p.getUniqueId());
		}
	}

	@EventHandler
	@Override
	public void onUpdate(final UpdateEvent event) {
		if (event.getType() != UpdateEvent.UpdateType.SECOND) {
			return;
		}
		for (final Player o : Bukkit.getOnlinePlayers()) {
			final Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(o.getUniqueId());
			if (gamer.getWarp() != this) {
				continue;
			}
			if (o.getLocation().getY() > -64.0) {
				continue;
			}
			if (o.isDead()) {
				continue;
			}
			if (o.getHealth() <= 0.0) {
				continue;
			}
			if (!this.voidTimers.containsKey(o.getUniqueId())) {
				this.voidTimers.put(o.getUniqueId(), 0);
			}
			this.voidTimers.put(o.getUniqueId(), this.voidTimers.get(o.getUniqueId()) + 1);
		}
	}

	@EventHandler
	public void onDamage(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(((Player) event.getEntity()).getUniqueId());
			if (gamer.getWarp() == this) {
				EntityDamageEvent.DamageCause cause = event.getCause();
				if (cause != EntityDamageEvent.DamageCause.VOID) {
					event.setCancelled(true);
				}
				cause = null;
			}
			gamer = null;
		}
	}

	@Override
	public void quit(final Player p) {
		if (this.voidTimers.containsKey(p.getUniqueId())) {
			this.voidTimers.remove(p.getUniqueId());
		}
		this.quitPlayer(p.getUniqueId());
		this.unprotect(p);
	}
}
