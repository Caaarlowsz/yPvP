package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import br.com.weavenmc.ypvp.yPvP;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.block.BlockFace;
import java.util.HashSet;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class ThorAbility extends Ability
{
    public ThorAbility() {
        this.setName("Thor");
        this.setHasItem(true);
        this.setGroupToUse(Group.LIGHT);
        this.setIcon(Material.GOLD_AXE);
        this.setDescription(new String[] { "§7Tenha o pr\u00f3prio Mjolnir de Thor", "§7em sua vers\u00e3o mais poderosa." });
        this.setPrice(65000);
        this.setTempPrice(2500);
    }
    
    @Override
    public void eject(final Player p) {
    }
    
    @EventHandler
    public void onThor(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (this.hasKit(p) && this.isItem(p.getItemInHand())) {
            event.setCancelled(true);
            if (!this.inCooldown(p)) {
                this.addCooldown(p, 10);
                final Block target = p.getTargetBlock((HashSet)null, 200).getRelative(BlockFace.UP);
                for (int i = 0; i < 2; ++i) {
                    new BukkitRunnable() {
                        public void run() {
                            p.getWorld().strikeLightning(target.getLocation());
                        }
                    }.runTaskLater((Plugin)yPvP.getPlugin(), i * 10L);
                }
            }
            else {
                this.sendCooldown(p);
            }
        }
    }
}
