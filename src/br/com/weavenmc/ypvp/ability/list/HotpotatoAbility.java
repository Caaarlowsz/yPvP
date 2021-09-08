package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.HashMap;

public class HotpotatoAbility
{
    private HashMap<UUID, Player> u;
    private HashMap<UUID, ItemStack> last;
    
    public HotpotatoAbility() {
        this.u = new HashMap<UUID, Player>();
        this.last = new HashMap<UUID, ItemStack>();
    }
    
    public void eject(final Player p) {
        if (this.u.containsKey(p.getUniqueId())) {
            this.u.remove(p.getUniqueId());
        }
        if (this.last.containsKey(p.getUniqueId())) {
            this.last.remove(p.getUniqueId());
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        this.eject(e.getPlayer());
    }
    
    @EventHandler
    public void onHotpotato(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onHotpotatoClick(final InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            final Player player = (Player)event.getWhoClicked();
            if (this.u.containsKey(player.getUniqueId()) && event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 103) {
                event.setCancelled(true);
                event.getInventory().setItem(103, (ItemStack)null);
                player.sendMessage("§5§lHOTPOTATO§f Voc\u00ea removeu a §9§lTNT");
                final Player target = this.u.get(player.getUniqueId());
                if (target != null && target.isOnline()) {
                    target.sendMessage("§5§lHOTPOTATO§f O jogador §9§l" + player.getName() + "§f desarmou a §9§lHOTPOTATO§f!");
                }
                target.updateInventory();
                this.u.remove(target.getUniqueId());
                target.getInventory().setHelmet((ItemStack)this.last.get(player.getUniqueId()));
                this.last.remove(target.getUniqueId());
            }
        }
    }
    
    public int convert(final int a) {
        if (a == 0) {
            return 5;
        }
        if (a == 1) {
            return 4;
        }
        if (a == 2) {
            return 3;
        }
        if (a == 3) {
            return 2;
        }
        if (a == 4) {
            return 1;
        }
        return a;
    }
}
