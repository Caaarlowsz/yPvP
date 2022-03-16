package br.com.weavenmc.ypvp;

import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Management {
	private YPvP plugin;

	public Management(final YPvP plugin) {
		this.plugin = plugin;
	}

	public abstract void enable();

	public abstract void disable();

	public void registerListener(final Listener listener) {
		this.getServer().getPluginManager().registerEvents(listener, (Plugin) this.getPlugin());
	}

	public Server getServer() {
		return this.plugin.getServer();
	}

	public YPvP getPlugin() {
		return this.plugin;
	}
}
