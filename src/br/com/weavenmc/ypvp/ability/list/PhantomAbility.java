package br.com.weavenmc.ypvp.ability.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class PhantomAbility extends Ability {
	private ArrayList<UUID> flying;
	private HashMap<UUID, ItemStack[]> previous;

	public PhantomAbility() {
		this.flying = new ArrayList<UUID>();
		this.previous = new HashMap<UUID, ItemStack[]>();
		this.setName("Phantom");
		this.setHasItem(true);
		this.setGroupToUse(Group.LIGHT);
		this.setIcon(Material.FEATHER);
		this.setDescription(new String[] { "§7Tenha a habilidade de voar." });
		this.setPrice(60000);
		this.setTempPrice(3500);
	}

	@Override
	public void eject(final Player p) {
		if (this.flying.contains(p.getUniqueId())) {
			this.flying.contains(p.getUniqueId());
		}
		if (this.previous.containsKey(p.getUniqueId())) {
			this.previous.remove(p.getUniqueId());
		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		this.eject(event.getPlayer());
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		if (this.hasKit(p) && this.isItem(p.getItemInHand()) && event.getAction().name().contains("RIGHT")) {
			event.setCancelled(true);
			if (!this.flying.contains(p.getUniqueId())) {
				if (!this.inCooldown(p)) {
					this.addCooldown(p, 25);
					this.previous.put(p.getUniqueId(), p.getInventory().getArmorContents());
					this.flying.add(p.getUniqueId());
					p.setAllowFlight(true);
					p.setFlying(true);
					ItemBuilder builder = new ItemBuilder().type(Material.LEATHER_HELMET).color(Color.AQUA);
					p.getInventory().setHelmet(builder.build());
					builder = new ItemBuilder().type(Material.LEATHER_CHESTPLATE).color(Color.AQUA);
					p.getInventory().setChestplate(builder.build());
					builder = new ItemBuilder().type(Material.LEATHER_LEGGINGS).color(Color.AQUA);
					p.getInventory().setLeggings(builder.build());
					builder = new ItemBuilder().type(Material.LEATHER_BOOTS).color(Color.AQUA);
					p.getInventory().setBoots(builder.build());
					builder = null;
					p.updateInventory();
					for (int i = 0; i < 6; ++i) {
						final int current = i;
						new BukkitRunnable() {
							public void run() {
								if (current == 5) {
									if (PhantomAbility.this.flying.contains(p.getUniqueId())) {
										PhantomAbility.this.flying.remove(p.getUniqueId());
										p.sendMessage("§5§lPHANTON§f Acabou o tempo de §9§lV\u00d4O");
									}
									p.setAllowFlight(false);
									p.setFlying(false);
									if (PhantomAbility.this.previous.containsKey(p.getUniqueId())) {
										p.getInventory().setArmorContents(
												(ItemStack[]) PhantomAbility.this.previous.get(p.getUniqueId()));
										p.updateInventory();
										PhantomAbility.this.previous.remove(p.getUniqueId());
									}
								} else if (PhantomAbility.this.flying.contains(p.getUniqueId())) {
									p.sendMessage("§5§lPHANTOM§f Voc\u00ea n\u00e3o voar\u00e1 mais em §9§l"
											+ PhantomAbility.this.convert(current) + " SEGUNDOS...");
								}
							}
						}.runTaskLater((Plugin) yPvP.getPlugin(), (long) (i * 20));
					}
				} else {
					this.sendCooldown(p);
				}
			} else {
				p.sendMessage("§5§lPHANTOM§f Voc\u00ea j\u00e1 est\u00e1 §9§lVOANDO!");
			}
		}
	}

	@EventHandler
	public void onPhantonBug(final InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			final Player p = (Player) event.getWhoClicked();
			if ((this.flying.contains(p.getUniqueId()) || this.previous.containsKey(p.getUniqueId()))
					&& event.getSlotType() == InventoryType.SlotType.ARMOR) {
				event.setCancelled(true);
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
