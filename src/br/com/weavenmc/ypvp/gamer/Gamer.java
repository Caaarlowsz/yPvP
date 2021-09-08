package br.com.weavenmc.ypvp.gamer;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.ypvp.minigame.Minigame;
import java.util.UUID;

public class Gamer
{
    private String name;
    private UUID uniqueId;
    private Minigame warp;
    private Sidebar sidebar;
    private Ability ability;
    private long lastCombatTime;
    private UUID lastCombat;
    private UUID spectator;
    
    public Gamer(final BukkitPlayer bP) {
        this.lastCombatTime = 0L;
        this.lastCombat = null;
        this.spectator = null;
        this.name = bP.getName();
        this.uniqueId = bP.getUniqueId();
    }
    
    public boolean hasSpectator() {
        return this.spectator != null;
    }
    
    public void resetCombat() {
        this.lastCombatTime = 0L;
        this.lastCombat = null;
    }
    
    public void addCombat(final UUID uuid, final int time) {
        this.lastCombatTime = time * 1000L + System.currentTimeMillis();
        this.lastCombat = uuid;
    }
    
    public boolean inCombat() {
        return this.lastCombatTime >= System.currentTimeMillis();
    }
    
    public void setWarp(final Minigame warp) {
        this.warp = warp;
    }
    
    public void setSidebar(final Sidebar sidebar) {
        this.sidebar = sidebar;
    }
    
    public void setAbility(final Ability ability) {
        this.ability = ability;
    }
    
    public void setLastCombat(final UUID lastCombat) {
        this.lastCombat = lastCombat;
    }
    
    public void setSpectator(final UUID spectator) {
        this.spectator = spectator;
    }
    
    public String getName() {
        return this.name;
    }
    
    public UUID getUniqueId() {
        return this.uniqueId;
    }
    
    public Minigame getWarp() {
        return this.warp;
    }
    
    public Sidebar getSidebar() {
        return this.sidebar;
    }
    
    public Ability getAbility() {
        return this.ability;
    }
    
    public long getLastCombatTime() {
        return this.lastCombatTime;
    }
    
    public UUID getLastCombat() {
        return this.lastCombat;
    }
    
    public UUID getSpectator() {
        return this.spectator;
    }
}
