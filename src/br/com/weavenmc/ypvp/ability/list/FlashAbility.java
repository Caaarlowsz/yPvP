package br.com.weavenmc.ypvp.ability.list;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class FlashAbility extends Ability {
	public FlashAbility() {
		this.setName("Flash");
		this.setHasItem(true);
		this.setGroupToUse(Group.LIGHT);
		this.setIcon(Material.REDSTONE_TORCH_ON);
		this.setDescription(new String[] { "§7Mire para um lugar com seu item", "§7e teleporte-se para l\u00e1." });
		this.setPrice(35000);
		this.setTempPrice(2000);
	}

	@Override
	public void eject(final Player p) {
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(final PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (this.hasKit(p) && this.isItem(p.getItemInHand())) {
			event.setCancelled(true);
			if (!this.inCooldown(p)) {
				this.addCooldown(p, 18);
				p.getWorld().strikeLightningEffect(p.getLocation());
				Block target = ((LivingEntity) p).getTargetBlock(new HashSet<Byte>(), 200).getRelative(BlockFace.UP);
				p.teleport(target.getLocation());
				p.sendMessage("§5§lFLASH§f Voc\u00ea §9§lTELEPORTOU§f para o bloco §9§l" + target.getType().name());
				target = null;
			} else {
				this.sendCooldown(p);
			}
		}
		p = null;
	}
}
