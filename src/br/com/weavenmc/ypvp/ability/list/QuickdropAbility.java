package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class QuickdropAbility extends Ability
{
    public QuickdropAbility() {
        this.setName("Quickdrop");
        this.setHasItem(false);
        this.setGroupToUse(Group.MEMBRO);
        this.setIcon(Material.BOWL);
        this.setDescription(new String[] { "§7Ao tomar sopa drope os potes automaticamente." });
        this.setPrice(45000);
        this.setTempPrice(4000);
    }
    
    @Override
    public void eject(final Player p) {
    }
}
