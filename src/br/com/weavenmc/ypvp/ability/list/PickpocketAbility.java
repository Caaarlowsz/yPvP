package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.inventory.InventoryCloseEvent;
import java.util.Iterator;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import br.com.weavenmc.ypvp.yPvP;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Player;
import br.com.weavenmc.commons.core.permission.Group;
import java.util.ArrayList;
import org.bukkit.Material;
import java.util.UUID;
import java.util.List;
import br.com.weavenmc.ypvp.ability.Ability;

public class PickpocketAbility extends Ability
{
    private final List<UUID> open;
    private final List<Material> blocked_items;
    
    public PickpocketAbility() {
        this.open = new ArrayList<UUID>();
        this.blocked_items = new ArrayList<Material>();
        this.setName("Pickpocket");
        this.setHasItem(true);
        this.setGroupToUse(Group.LIGHT);
        this.setIcon(Material.BLAZE_ROD);
        this.setDescription(new String[] { "§7Abra o invent\u00e1rio de seus inimigos", "§7e roube items dele." });
        this.setPrice(80000);
        this.setTempPrice(8700);
        this.blocked_items.add(Material.DIAMOND_SWORD);
        this.blocked_items.add(Material.IRON_SWORD);
        this.blocked_items.add(Material.STONE_SWORD);
        this.blocked_items.add(Material.GOLD_SWORD);
        this.blocked_items.add(Material.WOOD_SWORD);
    }
    
    @Override
    public void eject(final Player p) {
        if (this.open.contains(p.getUniqueId())) {
            this.open.remove(p.getUniqueId());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(final PlayerInteractEntityEvent event) {
        final Player p = event.getPlayer();
        if (this.hasKit(p) && event.getRightClicked() instanceof Player && this.isItem(p.getItemInHand())) {
            if (!this.inCooldown(p)) {
                this.addCooldown(p, 40);
                final Player t = (Player)event.getRightClicked();
                p.openInventory((Inventory)t.getInventory());
                this.open.add(p.getUniqueId());
                t.sendMessage("§5§lPICKPOCKET§f O jogador §9§l" + p.getName() + "§f abriu o seu invent\u00e1rio!");
                p.sendMessage("§5§lPICKPOCKET§f Voc\u00ea tem §9§l1.5 SEGUNDOS§f para roubar os itens...");
                new BukkitRunnable() {
                    public void run() {
                        if (PickpocketAbility.this.open.contains(p.getUniqueId())) {
                            p.closeInventory();
                        }
                    }
                }.runTaskLater((Plugin)yPvP.getPlugin(), 30L);
            }
            else {
                this.sendCooldown(p);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(final InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            final Player p = (Player)event.getWhoClicked();
            if (event.getCurrentItem() != null && this.open.contains(p.getUniqueId())) {
                if (this.blocked_items.contains(event.getCurrentItem().getType())) {
                    event.setCancelled(true);
                }
                else {
                    for (final Ability ability : yPvP.getPlugin().getAbilityManager().getAbilities()) {
                        if (ability.getIcon() == null) {
                            continue;
                        }
                        if (event.getCurrentItem().getType().equals((Object)Material.BOWL)) {
                            continue;
                        }
                        if (!event.getCurrentItem().getType().equals((Object)ability.getIcon())) {
                            continue;
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(final InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player p = (Player)event.getPlayer();
            if (this.open.contains(p.getUniqueId())) {
                this.open.remove(p.getUniqueId());
            }
            p = null;
        }
    }
}
