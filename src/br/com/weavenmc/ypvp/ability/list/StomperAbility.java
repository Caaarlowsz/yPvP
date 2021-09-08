package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventHandler;
import br.com.weavenmc.ypvp.gamer.Gamer;
import java.util.Iterator;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import br.com.weavenmc.ypvp.yPvP;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class StomperAbility extends Ability
{
    public StomperAbility() {
        this.setName("Stomper");
        this.setHasItem(false);
        this.setGroupToUse(Group.BETA);
        this.setIcon(Material.DIAMOND_BOOTS);
        this.setDescription(new String[] { "§7Pule de uma altura e fa\u00e7a os inimigos", "§7abaixo receberem o seu dano de queda." });
        this.setPrice(90000);
        this.setTempPrice(9000);
    }
    
    @Override
    public void eject(final Player p) {
    }
    
    @EventHandler
    public void onStomper(final EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Entity entityStomper = event.getEntity();
        if (!(entityStomper instanceof Player)) {
            return;
        }
        final Player stomper = (Player)entityStomper;
        if (!this.hasKit(stomper)) {
            return;
        }
        final EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        final double dmg = event.getDamage();
        boolean hasPlayer = false;
        for (final Player stompado : Bukkit.getOnlinePlayers()) {
            final Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(stompado.getUniqueId());
            if (stompado.getUniqueId() == stomper.getUniqueId()) {
                continue;
            }
            if (stompado.getLocation().distance(stomper.getLocation()) > 6.0) {
                continue;
            }
            double dmg2 = dmg * 1.0;
            if ((stompado.isSneaking() || gamer.getAbility().getName().equals("AntiStomper")) && dmg2 > 4.0) {
                dmg2 = 4.0;
            }
            stomper.sendMessage("§5§lSTOMPER§f Voc\u00ea §9§lSTOMPOU§f o §9§l" + stompado.getName());
            stompado.sendMessage("§5§lSTOMPER§f Voc\u00ea foi §9§lSTOMPADO§f pelo §9§l" + stomper.getName());
            gamer.setLastCombat(stomper.getUniqueId());
            stompado.damage(dmg2, (Entity)stomper);
            hasPlayer = true;
        }
        if (hasPlayer) {
            stomper.getWorld().playSound(stomper.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
        }
        if (event.getDamage() > 4.0) {
            event.setDamage(4.0);
        }
    }
}
