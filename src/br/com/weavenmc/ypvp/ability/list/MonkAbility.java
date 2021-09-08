package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import br.com.weavenmc.ypvp.gamer.Gamer;
import java.util.Random;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class MonkAbility extends Ability
{
    public MonkAbility() {
        this.setName("Monk");
        this.setHasItem(true);
        this.setGroupToUse(Group.MEMBRO);
        this.setIcon(Material.BLAZE_POWDER);
        this.setDescription(new String[] { "§7Bagun\u00e7e o invent\u00e1ro de seu inimigo", "§7e tenha chance de mat\u00e1-lo." });
        this.setPrice(35000);
        this.setTempPrice(3500);
    }
    
    @Override
    public void eject(final Player p) {
    }
    
    @EventHandler
    public void onMonk(final PlayerInteractEntityEvent event) {
        final Player p = event.getPlayer();
        if (this.hasKit(p) && this.isItem(p.getItemInHand()) && event.getRightClicked() instanceof Player) {
            final Player target = (Player)event.getRightClicked();
            final Gamer gamer = this.gamer(target);
            if (!gamer.getWarp().isProtected(target)) {
                if (!this.inCooldown(p)) {
                    this.addCooldown(p, 12);
                    final int random = new Random().nextInt(target.getInventory().getSize() - 10 + 1 + 10);
                    final ItemStack selected = target.getInventory().getItem(random);
                    final ItemStack ItemMudado = target.getItemInHand();
                    target.setItemInHand(selected);
                    target.getInventory().setItem(random, ItemMudado);
                    target.updateInventory();
                    p.sendMessage("§5§lMONK§f Voc\u00ea monkou o jogador §9§l" + target.getName());
                    target.sendMessage("§5§lMONK§f Voc\u00ea foi monkado pelo §9§l" + p.getName());
                }
                else {
                    this.sendCooldown(p);
                }
            }
            else {
                p.sendMessage("§5§lMONK§f Este jogador est\u00e1 com prote\u00e7\u00e3o de spawn.");
            }
        }
    }
}
