package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import br.com.weavenmc.ypvp.yPvP;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class UrgalAbility extends Ability
{
    public UrgalAbility() {
        this.setName("Urgal");
        this.setHasItem(true);
        this.setGroupToUse(Group.BETA);
        this.setIcon(Material.POTION);
        this.setDescription(new String[] { "§7Receba for\u00e7a para matar seus", "§7inimigos mais r\u00e1pido." });
        this.setPrice(100000);
        this.setTempPrice(12000);
    }
    
    @Override
    public void eject(final Player p) {
        p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }
    
    @EventHandler
    public void onUrgal(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (this.hasKit(p) && this.isItem(p.getItemInHand()) && event.getAction().name().contains("RIGHT")) {
            event.setCancelled(true);
            if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                if (!this.inCooldown(p)) {
                    this.addCooldown(p, 60);
                    if (yPvP.getPlugin().getPvpType() == yPvP.PvPType.FULLIRON) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 1));
                    }
                    else {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 0));
                    }
                    p.sendMessage("§5§lURGAL§f Voc\u00ea recebeu §9§lFOR\u00c7A!");
                }
                else {
                    this.sendCooldown(p);
                }
            }
            else {
                p.sendMessage("§5§lURGAL§f Voc\u00ea j\u00e1 est\u00e1 com §9§lFOR\u00c7A!");
            }
        }
    }
}
