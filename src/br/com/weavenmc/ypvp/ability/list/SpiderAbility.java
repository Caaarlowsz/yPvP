package br.com.weavenmc.ypvp.ability.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class SpiderAbility extends Ability {
	public SpiderAbility() {
		this.setName("Spider");
		this.setHasItem(true);
		this.setGroupToUse(Group.LIGHT);
		this.setIcon(Material.SNOW_BALL);
		this.setDescription(new String[] { "§7Lan\u00e7e sua teia e prenda seus", "§7inimigos nela." });
		this.setPrice(55000);
		this.setTempPrice(5000);
	}

	@Override
	public void eject(final Player p) {
	}

	@EventHandler
	public void onSpiderCatch(final ProjectileHitEvent event) {
		if (event.getEntity().hasMetadata("Spiderball")) {
			final List<Block> webs = new ArrayList<Block>();
			final Location loc = event.getEntity().getLocation();
			final int x = new Random().nextInt(2) - 1;
			final int z = new Random().nextInt(2) - 1;
			for (int y = 0; y < 2; ++y) {
				for (int xx = 0; xx < 2; ++xx) {
					for (int zz = 0; zz < 2; ++zz) {
						final Block b = loc.clone().add((double) (x + xx), (double) y, (double) (z + zz)).getBlock();
						if (b.getType() == Material.AIR) {
							b.setType(Material.WEB);
							webs.add(b);
						}
					}
				}
			}
			event.getEntity().remove();
			new BukkitRunnable() {
				public void run() {
					for (final Block web : webs) {
						web.setType(Material.AIR);
					}
				}
			}.runTaskLater((Plugin) yPvP.getPlugin(), 200L);
		}
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		if (this.hasKit(p)) {
			final ItemStack itemInHand = p.getItemInHand();
			if (itemInHand.getType() == this.getIcon() && itemInHand.hasItemMeta()
					&& itemInHand.getItemMeta().getDisplayName().equals("§e§l" + this.getName())) {
				event.setCancelled(true);
				if (!this.inCooldown(p)) {
					this.addCooldown(p, 25);
					p.throwSnowball().setMetadata("Spiderball",
							(MetadataValue) new FixedMetadataValue((Plugin) yPvP.getPlugin(), (Object) "Spiderball"));
				} else {
					this.sendCooldown(p);
				}
				p.updateInventory();
			}
		}
	}
}
