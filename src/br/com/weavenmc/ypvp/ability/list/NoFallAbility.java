package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class NoFallAbility extends Ability
{
    public NoFallAbility() {
        this.setName("NoFall");
        this.setHasItem(false);
        this.setGroupToUse(Group.LIGHT);
        this.setIcon(Material.IRON_BOOTS);
        this.setDescription(new String[] { "§7N\u00e3o receba dano de queda." });
        this.setPrice(45000);
        this.setTempPrice(3500);
    }
    
    @Override
    public void eject(final Player p) {
    }
    
    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && this.hasKit((Player)event.getEntity()) && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }
}
