package br.com.weavenmc.ypvp;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;

public abstract class Management
{
    private yPvP plugin;
    
    public Management(final yPvP plugin) {
        this.plugin = plugin;
    }
    
    public abstract void enable();
    
    public abstract void disable();
    
    public void registerListener(final Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, (Plugin)this.getPlugin());
    }
    
    public Server getServer() {
        return this.plugin.getServer();
    }
    
    public yPvP getPlugin() {
        return this.plugin;
    }
}
