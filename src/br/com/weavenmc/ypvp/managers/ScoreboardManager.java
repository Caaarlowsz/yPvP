package br.com.weavenmc.ypvp.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.player.PingAPI;
import br.com.weavenmc.commons.bukkit.api.tablist.TabListAPI;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.commons.bukkit.scoreboard.StringScroller;
import br.com.weavenmc.commons.core.account.League;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.ypvp.Management;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.minigame.BattleMinigame;
import br.com.weavenmc.ypvp.minigame.Minigame;
import br.com.weavenmc.ypvp.minigame.VoidChallengeMinigame;

public class ScoreboardManager extends Management implements Listener {
	private StringScroller scrollerSpawn;
	private StringScroller scroller1v1;
	private StringScroller scrollerFps;
	private StringScroller scrollerChallenge;
	private StringScroller scrollerVoid;

	public ScoreboardManager(final YPvP plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		this.scrollerSpawn = new StringScroller("NestyPvP - Spawn - " + this.getPlugin().getPvpType().name() + " -", 12,
				1);
		this.scrollerSpawn.next();
		this.scroller1v1 = new StringScroller("NestyPvP - 1v1 - " + this.getPlugin().getPvpType().name() + " -", 12, 1);
		this.scroller1v1.next();
		this.scrollerFps = new StringScroller("NestyPvP - Fps - " + this.getPlugin().getPvpType().name() + " -", 12, 1);
		this.scrollerFps.next();
		this.scrollerChallenge = new StringScroller(
				"NestyPvP - Lava Challenge - " + this.getPlugin().getPvpType().name() + " -", 12, 1);
		this.scrollerChallenge.next();
		this.scrollerVoid = new StringScroller(
				"NestyPvP - Void Challenge - " + this.getPlugin().getPvpType().name() + " -", 12, 1);
		this.scrollerVoid.next();
		this.registerListener((Listener) this);
	}

	public void createScoreboard(final Player p) {
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		Gamer gamer = this.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		Sidebar sidebar = gamer.getSidebar();
		if (sidebar == null) {
			gamer.setSidebar(sidebar = new Sidebar(p.getScoreboard()));
			sidebar.show();
		}
		if (sidebar.isHided()) {
			return;
		}
		sidebar.hide();
		sidebar.show();
		Minigame minigame = gamer.getWarp();
		if (minigame.getName().equalsIgnoreCase("spawn")) {
			final League league = bP.getLeague();
			sidebar.setTitle("�2�lPVP");
			sidebar.setText(12, "    �7pvp.nestymc.com.br     ");
			sidebar.setText(11, "");
			sidebar.setText(10, "�fKills: �a" + bP.getData(DataType.PVP_KILLS).asInt());
			sidebar.setText(9, "�fDeaths: �c" + bP.getData(DataType.PVP_DEATHS).asInt());
			sidebar.setText(8, "�fKillStreak: �6" + bP.getData(DataType.PVP_KILLSTREAK).asInt());
			sidebar.setText(7, "");
			sidebar.setText(6, "�fRank: " + league.getColor() + league.getSymbol() + " " + league.name());
			sidebar.setText(5, "�fXP: �b" + bP.getXp());
			sidebar.setText(4, "");
			sidebar.setText(3, "�fKit: �b" + gamer.getAbility().getName());
			sidebar.setText(2, "");
			sidebar.setText(1, "    �awww.nestymc.com.br     ");
		} else if (minigame.getName().equalsIgnoreCase("fps")) {
			final League league = bP.getLeague();
			sidebar.setTitle("�2�lPVP");
			sidebar.setText(12, "    �7pvp.nestymc.com.br     ");
			sidebar.setText(11, "");
			sidebar.setText(10, "�fKills: �a" + bP.getData(DataType.PVP_KILLS).asInt());
			sidebar.setText(9, "�fDeaths: �c" + bP.getData(DataType.PVP_DEATHS).asInt());
			sidebar.setText(8, "�fKillStreak: �6" + bP.getData(DataType.PVP_KILLSTREAK).asInt());
			sidebar.setText(7, "");
			sidebar.setText(6, "�fRank: " + league.getColor() + league.getSymbol() + " " + league.name());
			sidebar.setText(5, "�fXP: �b" + bP.getXp());
			sidebar.setText(4, "");
			sidebar.setText(3, "�fKit: �b" + gamer.getAbility().getName());
			sidebar.setText(2, "");
			sidebar.setText(1, "    �awww.nestymc.com.br     ");
		} else if (minigame.getName().equalsIgnoreCase("1v1")) {
			final League league = bP.getLeague();
			sidebar.setTitle("�2�lPVP");
			sidebar.setText(13, "    �7pvp.nestymc.com.br     ");
			sidebar.setText(12, "");
			sidebar.setText(11, "�fKills: �a" + bP.getData(DataType.PVP_KILLS).asInt());
			sidebar.setText(10, "�fDeaths: �c" + bP.getData(DataType.PVP_DEATHS).asInt());
			sidebar.setText(9, "�fKillStreak: �6" + bP.getData(DataType.PVP_KILLSTREAK).asInt());
			sidebar.setText(8, "");
			sidebar.setText(7, "�fRank: " + league.getColor() + league.getSymbol() + " " + league.name());
			sidebar.setText(6, "�fXP: �b" + bP.getXp());
			sidebar.setText(5, "");
			sidebar.setText(4, "�fBatalhando contra:");
			sidebar.setText(3, "�3" + ((BattleMinigame) minigame).getBattlePlayer(p));
			sidebar.setText(2, "");
			sidebar.setText(1, "    �awww.nestymc.com.br     ");
		} else if (minigame.getName().equalsIgnoreCase("lava")) {
			final League league = bP.getLeague();
			sidebar.setTitle("�2�lPVP");
			sidebar.setText(6, "    �7pvp.nestymc.com.br     ");
			sidebar.setText(5, "");
			sidebar.setText(4, "�fXP: �b" + bP.getXp());
			sidebar.setText(3, "�fRank: " + league.getColor() + league.getSymbol() + " " + league.name());
			sidebar.setText(2, "");
			sidebar.setText(1, "    �awww.nestymc.com.br    ");
		} else if (minigame.getName().equalsIgnoreCase("void")) {
			final League league = bP.getLeague();
			sidebar.setTitle("�2�lPVP");
			sidebar.setText(9, "    �7pvp.nestymc.com.br     ");
			sidebar.setText(8, "");
			sidebar.setText(7, "�fXP: �b" + bP.getXp());
			sidebar.setText(6, "�fRank: " + league.getColor() + league.getSymbol() + " " + league.name());
			sidebar.setText(5, "");
			if (p.getLocation().getY() <= -64.0) {
				sidebar.setText(4, "�fSobreviveu:");
				sidebar.setText(3, "�a" + ((VoidChallengeMinigame) minigame).getTimeSurviving(p));
			} else {
				sidebar.setText(4, "�fPara �ainiciar�7 o desafio");
				sidebar.setText(3, "�fdesafio pule no �5void�7!");
			}
			sidebar.setText(2, "");
			sidebar.setText(1, "    �awww.nestymc.com.br   ");
		}
		minigame = null;
		sidebar = null;
		gamer = null;
		bP = null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onUpdate(final UpdateEvent event) {
		if (event.getCurrentTick() % 10L != 0L) {
			return;
		}
		this.scrollerSpawn.next();
		this.scroller1v1.next();
		this.scrollerFps.next();
		this.scrollerChallenge.next();
		this.scrollerVoid.next();
		final int count = Bukkit.getOnlinePlayers().size();
		for (final Player o : Bukkit.getOnlinePlayers()) {
			Gamer gamer = this.getPlugin().getGamerManager().getGamer(o.getUniqueId());
			Sidebar sidebar = gamer.getSidebar();
			if (sidebar == null) {
				continue;
			}
			if (sidebar.isHided()) {
				continue;
			}
			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(o.getUniqueId());
			Minigame minigame = gamer.getWarp();
			if (minigame.getName().equalsIgnoreCase("spawn")) {
				final League league = bP.getLeague();
				sidebar.setTitle("�2�lPVP");
				sidebar.setText(12, "    �7pvp.nestymc.com.br     ");
				sidebar.setText(11, "");
				sidebar.setText(10, "�fKills: �a" + bP.getData(DataType.PVP_KILLS).asInt());
				sidebar.setText(9, "�fDeaths: �c" + bP.getData(DataType.PVP_DEATHS).asInt());
				sidebar.setText(8, "�fKillStreak: �6" + bP.getData(DataType.PVP_KILLSTREAK).asInt());
				sidebar.setText(7, "");
				sidebar.setText(6, "�fRank: " + league.getColor() + league.getSymbol() + " " + league.name());
				sidebar.setText(5, "�fXP: �b" + bP.getXp());
				sidebar.setText(4, "");
				sidebar.setText(3, "�fKit: �b" + gamer.getAbility().getName());
				sidebar.setText(2, "");
				sidebar.setText(1, "    �awww.nestymc.com.br     ");
			} else if (minigame.getName().equalsIgnoreCase("fps")) {
				final League league = bP.getLeague();
				sidebar.setTitle("�2�lPVP");
				sidebar.setText(12, "    �7pvp.nestymc.com.br     ");
				sidebar.setText(11, "");
				sidebar.setText(10, "�fKills: �a" + bP.getData(DataType.PVP_KILLS).asInt());
				sidebar.setText(9, "�fDeaths: �c" + bP.getData(DataType.PVP_DEATHS).asInt());
				sidebar.setText(8, "�fKillStreak: �6" + bP.getData(DataType.PVP_KILLSTREAK).asInt());
				sidebar.setText(7, "");
				sidebar.setText(6, "�fRank: " + league.getColor() + league.getSymbol() + " " + league.name());
				sidebar.setText(5, "�fXP: �b" + bP.getXp());
				sidebar.setText(4, "");
				sidebar.setText(3, "�fKit: �b" + gamer.getAbility().getName());
				sidebar.setText(2, "");
				sidebar.setText(1, "    �awww.nestymc.com.br     ");
			} else if (minigame.getName().equalsIgnoreCase("1v1")) {
				final League league = bP.getLeague();
				sidebar.setTitle("�2�lPVP");
				sidebar.setText(11, "�fKills: �a" + bP.getData(DataType.PVP_KILLS).asInt());
				sidebar.setText(10, "�fDeaths: �c" + bP.getData(DataType.PVP_DEATHS).asInt());
				sidebar.setText(9, "�fKillStreak: �6" + bP.getData(DataType.PVP_KILLSTREAK).asInt());
				sidebar.setText(7, "�fRank: " + league.getColor() + league.getSymbol() + " " + league.name());
				sidebar.setText(6, "�fXP: �b" + bP.getXp());
				sidebar.setText(3, "�3" + ((BattleMinigame) minigame).getBattlePlayer(o));
			} else if (minigame.getName().equalsIgnoreCase("lava")) {
				final League league = bP.getLeague();
				sidebar.setTitle("�2�lPVP");
				sidebar.setText(4, "�fXP: �b" + bP.getXp());
				sidebar.setText(3, "�fRank: " + league.getColor() + league.getSymbol() + " " + league.name());
			} else if (minigame.getName().equalsIgnoreCase("void")) {
				final League league = bP.getLeague();
				sidebar.setTitle("�2�lPVP");
				sidebar.setText(7, "�fXP: �b" + bP.getXp());
				sidebar.setText(6, "�fRank: " + league.getColor() + league.getSymbol() + " " + league.name());
				if (o.getLocation().getY() <= -64.0) {
					sidebar.setText(4, "�fSobreviveu:");
					sidebar.setText(3, "�a" + ((VoidChallengeMinigame) minigame).getTimeSurviving(o));
				} else {
					sidebar.setText(4, "�fPara �ainiciar�7 o desafio");
					sidebar.setText(3, "�fdesafio pule no �5void�7!");
				}
			}
			this.updateTab(o, count);
			minigame = null;
			sidebar = null;
			gamer = null;
			bP = null;
		}
	}

	public void updateTab(final Player p, final int onlineCount) {
		final BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		final int ping = PingAPI.getPing(p);
		TabListAPI.setHeaderAndFooter(p,
				"�6�lNESTY�f�lMC �e�lKITPVP\n�eMoedas: �f" + bP.getMoney() + " �9- �eTickets: �f" + bP.getTickets()
						+ " �9- �ePing: �f" + ping + "\n" + "�eTemos �f" + onlineCount + "�e jogadores online!",
				"�bNick: �f" + bP.getName() + " �9- �bLiga: �f" + bP.getLeague().name() + " �9- �bXP: �f" + bP.getXp()
						+ "\n�bMais informa\u00e7\u00f5es em: �fwww.nestymc.com.br");
	}

	@Override
	public void disable() {
		this.scrollerSpawn = null;
	}
}
