package br.com.weavenmc.ypvp;

import java.io.File;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.weavenmc.commons.bukkit.command.BukkitCommandFramework;
import br.com.weavenmc.commons.core.command.CommandFramework;
import br.com.weavenmc.commons.core.command.CommandLoader;
import br.com.weavenmc.ypvp.listeners.DamagerFixer;
import br.com.weavenmc.ypvp.listeners.PlayerListener;
import br.com.weavenmc.ypvp.listeners.SignListener;
import br.com.weavenmc.ypvp.managers.AbilityManager;
import br.com.weavenmc.ypvp.managers.CooldownManager;
import br.com.weavenmc.ypvp.managers.GamerManager;
import br.com.weavenmc.ypvp.managers.ScoreboardManager;
import br.com.weavenmc.ypvp.managers.WarpManager;
import br.com.weavenmc.ypvp.minigame.LocationManager;
import br.com.weavenmc.ypvp.tournament.Tournament;

public class yPvP extends JavaPlugin {
	private static yPvP plugin;
	private PvPType pvpType;
	private Tournament tournament;
	private GamerManager gamerManager;
	private WarpManager warpManager;
	private LocationManager locationManager;
	private AbilityManager abilityManager;
	private ScoreboardManager scoreboardManager;
	private CooldownManager cooldownManager;

	public void onLoad() {
		(yPvP.plugin = this).saveDefaultConfig();
	}

	public void onEnable() {
		this.pvpType = PvPType.valueOf(this.getConfig().getString("type"));
		this.getLogger().info("PvP Type: " + this.pvpType.name());
		this.tournament = new Tournament();
		this.registerManagements();
		this.enableManagements();
		this.getServer().getPluginManager().registerEvents((Listener) new DamagerFixer(), (Plugin) this);
		this.getServer().getPluginManager().registerEvents((Listener) new PlayerListener(), (Plugin) this);
		this.getServer().getPluginManager().registerEvents((Listener) new SignListener(), (Plugin) this);
		new CommandLoader((CommandFramework) new BukkitCommandFramework((JavaPlugin) this))
				.loadCommandsFromPackage("br.com.weavenmc.ypvp.commands");
	}

	public void onDisable() {
		this.disableManagements();
	}

	private void registerManagements() {
		this.gamerManager = new GamerManager(this);
		this.warpManager = new WarpManager(this);
		this.locationManager = new LocationManager(this);
		this.abilityManager = new AbilityManager(this);
		this.scoreboardManager = new ScoreboardManager(this);
		this.cooldownManager = new CooldownManager(this);
	}

	private void enableManagements() {
		this.gamerManager.enable();
		this.warpManager.enable();
		this.locationManager.enable();
		this.abilityManager.enable();
		this.scoreboardManager.enable();
		this.cooldownManager.enable();
	}

	private void disableManagements() {
		this.gamerManager.disable();
		this.warpManager.disable();
		this.locationManager.disable();
		this.abilityManager.disable();
		this.scoreboardManager.disable();
		this.cooldownManager.disable();
	}

	public File getFile() {
		return super.getFile();
	}

	public static yPvP getPlugin() {
		return yPvP.plugin;
	}

	public PvPType getPvpType() {
		return this.pvpType;
	}

	public Tournament getTournament() {
		return this.tournament;
	}

	public GamerManager getGamerManager() {
		return this.gamerManager;
	}

	public WarpManager getWarpManager() {
		return this.warpManager;
	}

	public LocationManager getLocationManager() {
		return this.locationManager;
	}

	public AbilityManager getAbilityManager() {
		return this.abilityManager;
	}

	public ScoreboardManager getScoreboardManager() {
		return this.scoreboardManager;
	}

	public CooldownManager getCooldownManager() {
		return this.cooldownManager;
	}

	public enum PvPType {
		SIMULATOR("SIMULATOR", 0), FULLIRON("FULLIRON", 1);

		private PvPType(final String s, final int n) {
		}
	}
}
