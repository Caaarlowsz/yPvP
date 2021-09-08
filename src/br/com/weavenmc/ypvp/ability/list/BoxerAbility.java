package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class BoxerAbility extends Ability
{
    public BoxerAbility() {
        this.setName("Boxer");
        this.setHasItem(false);
        this.setGroupToUse(Group.LIGHT);
        this.setIcon(Material.IRON_SWORD);
        this.setDescription(new String[] { "§7D\u00ea 0.50 \u00e1 mais de dano e receba", "§70.50 \u00e1 menos de dano." });
        this.setPrice(45000);
        this.setTempPrice(3000);
    }
    
    @Override
    public void eject(final Player p) {
    }
    
    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player e = (Player)event.getEntity();
            Player d = (Player)event.getDamager();
            if (this.hasKit(d)) {
                event.setDamage(event.getDamage() + 0.5);
            }
            if (this.hasKit(e)) {
                event.setDamage(event.getDamage() - 0.5);
            }
            e = null;
            d = null;
        }
    }
}
