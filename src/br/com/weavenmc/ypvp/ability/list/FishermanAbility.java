package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import br.com.weavenmc.ypvp.gamer.Gamer;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class FishermanAbility extends Ability
{
    public FishermanAbility() {
        this.setName("Fisherman");
        this.setHasItem(true);
        this.setGroupToUse(Group.MEMBRO);
        this.setIcon(Material.FISHING_ROD);
        this.setDescription(new String[] { "§7Fisgue seus oponentes e traga-os", "§7at\u00e9 voc\u00ea." });
        this.setPrice(40000);
        this.setTempPrice(1500);
    }
    
    @Override
    public void eject(final Player p) {
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFish(final PlayerFishEvent event) {
        Player p = event.getPlayer();
        if (this.hasKit(p)) {
            Entity entity = event.getCaught();
            boolean cancel = false;
            if (entity instanceof Player) {
                Gamer gamer = this.gamer((Player)entity);
                if (gamer.getWarp().isProtected((Player)entity)) {
                    cancel = true;
                }
                gamer = null;
            }
            if (!cancel) {
                Block block = event.getHook().getLocation().getBlock();
                if (entity != null && entity != block) {
                    entity.teleport(p.getPlayer().getLocation());
                }
                block = null;
            }
            entity = null;
        }
        p = null;
    }
}
