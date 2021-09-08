package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import br.com.weavenmc.ypvp.yPvP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import java.util.UUID;
import java.util.ArrayList;
import br.com.weavenmc.ypvp.ability.Ability;

public class ForcefieldAbility extends Ability
{
    private final ArrayList<UUID> ff;
    
    public ForcefieldAbility() {
        this.ff = new ArrayList<UUID>();
        this.setName("Forcefield");
        this.setHasItem(true);
        this.setGroupToUse(Group.BETA);
        this.setIcon(Material.GOLDEN_APPLE);
        this.setDescription(new String[] { "§7Ative seu campo de for\u00e7a e hite", "§7todos \u00e1 6 blocos de distancia." });
        this.setPrice(60000);
        this.setTempPrice(6500);
    }
    
    @Override
    public void eject(final Player p) {
        this.ff.remove(p.getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onForcefield(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        final ItemStack itemInHand = p.getItemInHand();
        if (this.hasKit(p)) {
            if (!this.isItem(itemInHand)) {
                if (this.ff.contains(p.getUniqueId())) {
                    final double damage = this.getDamage(itemInHand.getType());
                    for (final Entity nearby : p.getNearbyEntities(6.0, 6.0, 6.0)) {
                        ((Damageable)nearby).damage(damage, (Entity)p);
                        if (nearby instanceof Player) {
                            ((Player)nearby).sendMessage("§5§lFORCEFIELD§f Voc\u00ea est\u00e1 no campo de §9§l" + p.getName());
                        }
                    }
                }
            }
            else if (!this.inCooldown(p)) {
                if (!this.ff.contains(p.getUniqueId())) {
                    this.addCooldown(p, 50);
                    p.updateInventory();
                    this.ff.add(p.getUniqueId());
                    p.sendMessage("§5§lFORCEFIELD§f Voc\u00ea §9§lATIVOU§f o seu §9§lCAMPO DE FOR\u00c7A");
                    final Player player;
                    Bukkit.getScheduler().runTaskLater((Plugin)yPvP.getPlugin(), () -> {
                        player.updateInventory();
                        this.ff.remove(player.getUniqueId());
                    }, 320L);
                }
                else {
                    p.sendMessage("§5§lFORCEFIELD§f O seu §9§lCAMPO DE FOR\u00c7A§f j\u00e1 est\u00e1 §9§lATIVADO");
                }
            }
            else {
                this.sendCooldown(p);
            }
        }
    }
    
    private double getDamage(final Material type) {
        double damage = 1.0;
        if (type.toString().contains("DIAMOND_")) {
            damage = 8.0;
        }
        else if (type.toString().contains("IRON_")) {
            damage = 7.0;
        }
        else if (type.toString().contains("STONE_")) {
            damage = 6.0;
        }
        else if (type.toString().contains("WOOD_")) {
            damage = 5.0;
        }
        else if (type.toString().contains("GOLD_")) {
            damage = 5.0;
        }
        if (!type.toString().contains("_SWORD")) {
            --damage;
            if (!type.toString().contains("_AXE")) {
                --damage;
                if (!type.toString().contains("_PICKAXE")) {
                    --damage;
                    if (!type.toString().contains("_SPADE")) {
                        damage = 1.0;
                    }
                }
            }
        }
        return damage;
    }
}
