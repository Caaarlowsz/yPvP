package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class AntiStomperAbility extends Ability
{
    public AntiStomperAbility() {
        this.setName("AntiStomper");
        this.setHasItem(false);
        this.setGroupToUse(Group.MEMBRO);
        this.setIcon(Material.DIAMOND_HELMET);
        this.setDescription(new String[] { "§7Receba dano reduzido de stompers." });
        this.setPrice(30000);
        this.setTempPrice(3500);
    }
    
    @Override
    public void eject(final Player p) {
    }
}
