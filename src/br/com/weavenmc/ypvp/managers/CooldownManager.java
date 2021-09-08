package br.com.weavenmc.ypvp.managers;

import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;
import br.com.weavenmc.ypvp.yPvP;
import java.util.UUID;
import java.util.Map;
import br.com.weavenmc.ypvp.Management;

public class CooldownManager extends Management
{
    private Map<UUID, Long> C;
    
    public CooldownManager(final yPvP plugin) {
        super(plugin);
    }
    
    @Override
    public void enable() {
        this.C = new ConcurrentHashMap<UUID, Long>();
    }
    
    public void addCooldown(final Player player, final int time) {
        this.C.put(player.getUniqueId(), System.currentTimeMillis() + time * 1000L);
    }
    
    public double getCooldown(final Player p) {
        return this.C.containsKey(p.getUniqueId()) ? ((this.C.get(p.getUniqueId()) - System.currentTimeMillis()) / 10.0 / 100.0) : 0.0;
    }
    
    public boolean hasCooldown(final Player p) {
        return this.C.containsKey(p.getUniqueId()) && this.C.get(p.getUniqueId()) >= System.currentTimeMillis();
    }
    
    public void removeCooldown(final Player p) {
        this.C.remove(p.getUniqueId());
    }
    
    @Override
    public void disable() {
        this.C.clear();
        this.C = null;
    }
}
