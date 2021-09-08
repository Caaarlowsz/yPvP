package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class FiremanAbility extends Ability
{
    public FiremanAbility() {
        this.setName("Fireman");
        this.setHasItem(false);
        this.setGroupToUse(Group.MEMBRO);
        this.setIcon(Material.LAVA_BUCKET);
        this.setDescription(new String[] { "§7N\u00e3o receba dano para nenhum elemento", "§7relacionado \u00e1 fogo." });
        this.setPrice(60000);
        this.setTempPrice(4000);
    }
    
    @Override
    public void eject(final Player p) {
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
