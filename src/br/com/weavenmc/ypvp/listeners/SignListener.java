package br.com.weavenmc.ypvp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;

public class SignListener implements Listener {
	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null
				&& (event.getClickedBlock().getType() == Material.WALL_SIGN
						|| event.getClickedBlock().getType() == Material.SIGN_POST)) {
			final Sign s = (Sign) event.getClickedBlock().getState();
			final String[] lines = s.getLines();
			if (lines.length > 3) {
				if (lines[0].equals("§2Nesty§fMC") && lines[2].equals("§6§m>-----<") && lines[3].equals(" ")) {
					if (lines[1].equals("§bSopas")) {
						event.setCancelled(true);
						final Inventory soup = this.inv(54, "§bSopas");
						for (int i = 0; i < 54; ++i) {
							soup.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
						}
						player.openInventory(soup);
					} else if (lines[1].equals("§eRecraft")) {
						event.setCancelled(true);
						final Inventory recraft = this.inv(9, "§eRecraft");
						recraft.setItem(3, new ItemStack(Material.BOWL, 64));
						recraft.setItem(4, new ItemStack(Material.RED_MUSHROOM, 64));
						recraft.setItem(5, new ItemStack(Material.BROWN_MUSHROOM, 64));
						player.openInventory(recraft);
					} else if (lines[1].equals("§cCocoabean")) {
						event.setCancelled(true);
						final Inventory cocoa = this.inv(9, "§cCocoabean");
						cocoa.setItem(3, new ItemStack(Material.BOWL, 64));
						cocoa.setItem(4, new ItemStack(Material.getMaterial(351), 64, (short) 3));
						cocoa.setItem(5, new ItemStack(Material.getMaterial(351), 64, (short) 3));
						player.openInventory(cocoa);
					} else if (lines[1].equals("§aCactus")) {
						event.setCancelled(true);
						final Inventory cactu = this.inv(9, "§aCactus");
						cactu.setItem(3, new ItemStack(Material.BOWL, 64));
						cactu.setItem(4, new ItemStack(Material.CACTUS, 64));
						cactu.setItem(5, new ItemStack(Material.CACTUS, 64));
						player.openInventory(cactu);
					}
				} else if (lines[2].equals(" ") && lines[3].equals("§a§lClique!")) {
					final BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
					if (lines[0].equals("§6§lMOEDAS")) {
						final String input = ChatColor.stripColor(lines[1]);
						try {
							final Integer quantity = Integer.valueOf(input);
							event.setCancelled(true);
							event.getClickedBlock().breakNaturally();
							bP.addMoney((int) quantity);
							bP.save(new DataCategory[] { DataCategory.BALANCE });
							player.sendMessage("§6§lMONEY§f Voc\u00ea recebeu §6§l" + quantity + " MOEDAS");
						} catch (NumberFormatException ex) {
							event.setCancelled(true);
							event.getClickedBlock().breakNaturally();
							player.sendMessage("§cEsta placa n\u00e3o possuia validade ou estava com erro!");
						}
					} else if (lines[0].equals("§b§lTICKET")) {
						final String input = ChatColor.stripColor(lines[1]);
						try {
							final Integer quantity = Integer.valueOf(input);
							event.setCancelled(true);
							event.getClickedBlock().breakNaturally();
							bP.addTickets((int) quantity);
							bP.save(new DataCategory[] { DataCategory.BALANCE });
							player.sendMessage("§3§lTICKETS§f Voc\u00ea recebeu §3§l" + quantity + " TICKETS");
						} catch (NumberFormatException ex) {
							event.setCancelled(true);
							event.getClickedBlock().breakNaturally();
							player.sendMessage("§cEsta placa n\u00e3o possuia validade ou estava com erro!");
						}
					} else if (lines[0].equals("§3§lDOUBLEXP")) {
						final String input = ChatColor.stripColor(lines[1]);
						try {
							final Integer quantity = Integer.valueOf(input);
							event.setCancelled(true);
							event.getClickedBlock().breakNaturally();
							bP.addDoubleXpMultiplier((int) quantity);
							bP.save(new DataCategory[] { DataCategory.BALANCE });
							player.sendMessage("§3§lDOUBLEXP§f Voc\u00ea recebeu §3§l" + quantity + " DOUBLEXPS");
						} catch (NumberFormatException ex) {
							event.setCancelled(true);
							event.getClickedBlock().breakNaturally();
							player.sendMessage("§cEsta placa n\u00e3o possuia validade ou estava com erro!");
						}
					}
				}
			}
		}
	}

	protected Inventory inv(final int size, final String title) {
		return Bukkit.createInventory((InventoryHolder) null, size, title);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignChange(final SignChangeEvent event) {
		if (event.getLine(0).contains("&")) {
			event.setLine(0, event.getLine(0).replace("&", "§"));
		}
		if (event.getLine(1).contains("&")) {
			event.setLine(1, event.getLine(1).replace("&", "§"));
		}
		if (event.getLine(2).contains("&")) {
			event.setLine(2, event.getLine(2).replace("&", "§"));
		}
		if (event.getLine(3).contains("&")) {
			event.setLine(3, event.getLine(3).replace("&", "§"));
		}
		final String line = event.getLine(0);
		if (line.equalsIgnoreCase("sopa") || line.equalsIgnoreCase("sopas")) {
			event.setLine(0, "§2Nesty§fMC");
			event.setLine(1, "§bSopas");
			event.setLine(2, "§6§m>-----<");
			event.setLine(3, " ");
		} else if (line.equalsIgnoreCase("recraft") || line.equalsIgnoreCase("recrafts")) {
			event.setLine(0, "§2Nesty§fMC");
			event.setLine(1, "§eRecraft");
			event.setLine(2, "§6§m>-----<");
			event.setLine(3, " ");
		} else if (line.equalsIgnoreCase("cocoa") || line.equalsIgnoreCase("cocoabean")) {
			event.setLine(0, "§2Nesty§fMC");
			event.setLine(1, "§cCocoabean");
			event.setLine(2, "§6§m>-----<");
			event.setLine(3, " ");
		} else if (line.equalsIgnoreCase("cactu") || line.equalsIgnoreCase("cactus")) {
			event.setLine(0, "§2Nesty§fMC");
			event.setLine(1, "§aCactus");
			event.setLine(2, "§6§m>-----<");
			event.setLine(3, " ");
		} else if (line.contains(":")) {
			final String[] code = line.split(":");
			if (code.length > 1) {
				if (code[0].equalsIgnoreCase("money")) {
					try {
						final int quantity = Integer.valueOf(code[1]);
						event.setLine(0, "§6§lMOEDAS");
						event.setLine(1, "§e§l" + quantity);
						event.setLine(2, " ");
						event.setLine(3, "§a§lClique!");
					} catch (NumberFormatException ex) {
					}
				} else if (code[0].equalsIgnoreCase("ticket")) {
					try {
						final int quantity = Integer.valueOf(code[1]);
						event.setLine(0, "§b§lTICKET");
						event.setLine(1, "§3§l" + quantity);
						event.setLine(2, " ");
						event.setLine(3, "§a§lClique!");
					} catch (NumberFormatException ex2) {
					}
				} else if (code[0].equalsIgnoreCase("doublexp")) {
					try {
						final int quantity = Integer.valueOf(code[1]);
						event.setLine(0, "§3§lDOUBLEXP");
						event.setLine(1, "§b§l" + quantity);
						event.setLine(2, " ");
						event.setLine(3, "§a§lClique!");
					} catch (NumberFormatException ex3) {
					}
				}
			}
		}
	}
}
