package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.HashMap;

public class TimelordAbility
{
    private HashMap<UUID, Long> frozenTime;
    
    public TimelordAbility() {
        this.frozenTime = new HashMap<UUID, Long>();
    }
    
    public void eject(final Player p) {
        this.frozenTime.remove(p.getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMoveTimelord(final PlayerMoveEvent event) {
        final Player p = event.getPlayer();
        if (this.frozenTime.containsKey(p.getUniqueId()) && this.frozenTime.get(p.getUniqueId()) >= System.currentTimeMillis()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onTimeLord(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
    }
}
