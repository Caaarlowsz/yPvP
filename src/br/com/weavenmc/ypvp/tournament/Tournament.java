package br.com.weavenmc.ypvp.tournament;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;

public class Tournament {
	private boolean running;
	private boolean joinEnabled;
	private boolean isLastOfDay;
	private int timeToStart;
	private Set<UUID> players;
	private Set<UUID> spectators;
	private Queue<UUID> waitingForBattle;
	private UUID lastWinner;
	private UUID nextBattlePlayer;
	private TournamentBattle battle;

	public Tournament() {
		this.isLastOfDay = false;
		this.timeToStart = 300;
		this.players = new HashSet<UUID>();
		this.spectators = new HashSet<UUID>();
		this.waitingForBattle = new ConcurrentLinkedQueue<UUID>();
		this.running = false;
		this.joinEnabled = false;
	}

	public boolean isParticipating(final Player p) {
		return this.players.contains(p.getUniqueId());
	}

	public boolean isLastWinner(final Player p) {
		return this.lastWinner.equals(p.getUniqueId());
	}

	public boolean isNextBattlePlayer(final Player p) {
		return this.nextBattlePlayer.equals(p.getUniqueId());
	}

	public void joinPlayer(final Player p) {
		if (!this.players.contains(p.getUniqueId())) {
			this.players.add(p.getUniqueId());
			p.getInventory().clear();
			p.getInventory().setArmorContents((ItemStack[]) null);
			p.setGameMode(GameMode.SPECTATOR);
		}
	}

	public void quitPlayer(final Player p) {
		if (this.waitingForBattle.contains(p.getUniqueId())) {
			this.waitingForBattle.remove(p.getUniqueId());
		}
		if (this.players.contains(p.getUniqueId())) {
			this.players.remove(p.getUniqueId());
		}
		if (this.lastWinner == p.getUniqueId()) {
			this.lastWinner = null;
		}
		if (this.nextBattlePlayer == p.getUniqueId()) {
			this.nextBattlePlayer = null;
		}
	}

	public void begin() {
		this.joinEnabled = false;
		this.timeToStart = 300;
		this.running = true;
	}

	public void getNextPlayer() {
		if (this.lastWinner == null) {
			this.lastWinner = this.waitingForBattle.poll();
		}
		this.nextBattlePlayer = this.waitingForBattle.poll();
	}

	public void checkWinner() {
		if (this.players.size() <= 1) {
			this.running = false;
			if (this.players.size() == 1) {
				final Player winner = Bukkit.getPlayer(this.players.toArray(new UUID[0])[0]);
				if (winner != null) {
					Bukkit.broadcastMessage(
							"§3§lTOURNAMENT§f O jogador §a§l" + winner.getName() + "§f foi o §2§lVENCEDOR§f!");
					final BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon()
							.getWeavenPlayer(winner.getUniqueId());
					bP.addMoney(7000);
					bP.addXp(25);
					bP.addDoubleXpMultiplier(4);
					bP.addTickets(1);
					bP.save(new DataCategory[] { DataCategory.BALANCE });
					winner.sendMessage("§3§lTOURNAMENT§f Parab\u00e9s! Voc\u00ea §a§lVENCEU§f o §1§lTORNEIO!");
					winner.sendMessage("§6§lMONEY§f Voc\u00ea recebeu §6§l7000");
					winner.sendMessage("§9§lXP§f Voc\u00ea recebeu §9§l25");
					winner.sendMessage("§3§lDOUBLEXP§f Voc\u00ea recebeu §3§l4");
					winner.sendMessage("§b§lTICKET§f Voc\u00ea recebeu §b§l1");
					String tournamentTime = "4h";
					if (this.isLastOfDay) {
						tournamentTime = "16h";
					}
					Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(),
							"group " + winner.getName() + " add torneio " + tournamentTime);
				} else {
					this.stop();
				}
			} else {
				this.stop();
			}
		}
	}

	public void stop() {
	}

	public TournamentBattle nextBattle() {
		if (this.players.size() <= 1) {
			return null;
		}
		if (this.lastWinner == null) {
			this.lastWinner = this.waitingForBattle.poll();
		}
		while (!this.validate(this.lastWinner)) {
			this.players.remove(this.lastWinner);
			if (this.players.size() <= 1) {
				return null;
			}
			this.lastWinner = this.waitingForBattle.poll();
		}
		this.nextBattlePlayer = this.waitingForBattle.poll();
		while (!this.validate(this.nextBattlePlayer)) {
			this.players.remove(this.nextBattlePlayer);
			if (this.players.size() <= 1) {
				return null;
			}
			this.nextBattlePlayer = this.waitingForBattle.poll();
		}
		final Player player1 = Bukkit.getPlayer(this.lastWinner);
		final Player player2 = Bukkit.getPlayer(this.nextBattlePlayer);
		return this.battle = new TournamentBattle(player1, player2);
	}

	public boolean validate(final UUID uuid) {
		return Bukkit.getPlayer(uuid) != null;
	}

	public boolean isRunning() {
		return this.running;
	}

	public boolean isJoinEnabled() {
		return this.joinEnabled;
	}

	public boolean isLastOfDay() {
		return this.isLastOfDay;
	}

	public int getTimeToStart() {
		return this.timeToStart;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}

	public Set<UUID> getSpectators() {
		return this.spectators;
	}

	public Queue<UUID> getWaitingForBattle() {
		return this.waitingForBattle;
	}

	public UUID getLastWinner() {
		return this.lastWinner;
	}

	public UUID getNextBattlePlayer() {
		return this.nextBattlePlayer;
	}

	public TournamentBattle getBattle() {
		return this.battle;
	}

	public void setRunning(final boolean running) {
		this.running = running;
	}

	public void setJoinEnabled(final boolean joinEnabled) {
		this.joinEnabled = joinEnabled;
	}

	public void setLastOfDay(final boolean isLastOfDay) {
		this.isLastOfDay = isLastOfDay;
	}

	public void setTimeToStart(final int timeToStart) {
		this.timeToStart = timeToStart;
	}

	public void setPlayers(final Set<UUID> players) {
		this.players = players;
	}

	public void setSpectators(final Set<UUID> spectators) {
		this.spectators = spectators;
	}

	public void setWaitingForBattle(final Queue<UUID> waitingForBattle) {
		this.waitingForBattle = waitingForBattle;
	}

	public void setLastWinner(final UUID lastWinner) {
		this.lastWinner = lastWinner;
	}

	public void setNextBattlePlayer(final UUID nextBattlePlayer) {
		this.nextBattlePlayer = nextBattlePlayer;
	}

	public void setBattle(final TournamentBattle battle) {
		this.battle = battle;
	}
}
