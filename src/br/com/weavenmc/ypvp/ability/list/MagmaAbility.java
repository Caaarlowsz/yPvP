package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.entity.EntityDamageEvent;
import java.util.Random;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import java.util.Iterator;
import org.bukkit.Bukkit;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class MagmaAbility extends Ability
{
    public MagmaAbility() {
        this.setName("Magma");
        this.setHasItem(false);
        this.setGroupToUse(Group.LIGHT);
        this.setIcon(Material.WATER_BUCKET);
        this.setDescription(new String[] { "§7N\u00e3o receba dano para nenhum elemento", "§7relacionado \u00e1 fogo, a hitar seus", "§7oponentes h\u00e1 30% de chance de eles", "§7pegarem fogo, por\u00e9m receba dano na \u00e1gua." });
        this.setPrice(45000);
        this.setTempPrice(5500);
    }
    
    @Override
    public void eject(final Player p) {
    }
    
    @EventHandler
    public void onUpdate(final UpdateEvent event) {
        if (event.getType() != UpdateEvent.UpdateType.SECOND) {
            return;
        }
        for (final Player o : Bukkit.getOnlinePlayers()) {
            if (!this.hasKit(o)) {
                continue;
            }
            Location playerLoc = o.getLocation();
            if (playerLoc.add(0.0, -1.0, 0.0).getBlock().getType().equals((Object)Material.STATIONARY_WATER) || playerLoc.add(0.0, -1.0, 0.0).getBlock().getType().equals((Object)Material.WATER)) {
                o.damage(6.0);
            }
            playerLoc = null;
        }
    }
    
    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player e = (Player)event.getEntity();
            Player d = (Player)event.getDamager();
            if (this.hasKit(d) && !event.isCancelled()) {
                Integer chance = new Random().nextInt(100);
                if (chance > 0 && chance <= 30) {
                    e.setFireTicks(150);
                }
                chance = null;
            }
            e = null;
            d = null;
        }
    }
    
    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            if (this.hasKit(p)) {
                EntityDamageEvent.DamageCause cause = event.getCause();
                if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.FIRE_TICK || cause == EntityDamageEvent.DamageCause.LAVA) {
                    event.setCancelled(true);
                    p.setFireTicks(0);
                }
                cause = null;
            }
            p = null;
        }
    }
}
