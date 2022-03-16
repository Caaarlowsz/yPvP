package br.com.weavenmc.ypvp.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.api.title.TitleAPI;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;
import br.com.weavenmc.ypvp.minigame.Minigame;
import br.com.weavenmc.ypvp.minigame.SpawnMinigame;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class PlayerListener implements Listener {
	public PlayerListener() {
		final ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
		this.newShapelessRecipe(soup,
				Arrays.asList(new MaterialData(Material.CACTUS), new MaterialData(Material.BOWL)));
		this.newShapelessRecipe(soup,
				Arrays.asList(new MaterialData(Material.NETHER_STALK), new MaterialData(Material.BOWL)));
		this.newShapelessRecipe(soup,
				Arrays.asList(new MaterialData(Material.INK_SACK, (byte) 3), new MaterialData(Material.BOWL)));
		this.newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.SUGAR), new MaterialData(Material.BOWL)));
		this.newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.PUMPKIN_SEEDS),
				new MaterialData(Material.PUMPKIN_SEEDS), new MaterialData(Material.BOWL)));
		this.newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.CARROT_ITEM),
				new MaterialData(Material.POTATO_ITEM), new MaterialData(Material.BOWL)));
	}

	public void newShapelessRecipe(final ItemStack result, final List<MaterialData> materials) {
		final ShapelessRecipe recipe = new ShapelessRecipe(result);
		for (final MaterialData mat : materials) {
			recipe.addIngredient(mat);
		}
		Bukkit.addRecipe((Recipe) recipe);
	}

	@EventHandler
	public void onLoginListener(final PlayerLoginEvent event) {
		final Player p = event.getPlayer();
		final BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		if (bP != null) {
			final Gamer gamer;
			YPvP.getPlugin().getGamerManager().loadGamer(bP.getUniqueId(), gamer = new Gamer(bP));
			gamer.setAbility(YPvP.getPlugin().getAbilityManager().getNone());
			WeavenMC.getAsynchronousExecutor()
					.runAsync(() -> bP.load(new DataCategory[] { DataCategory.KITPVP, DataCategory.LAVA_CHALLENGE }));
		} else {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
					"�4�lERRO�f Ocorreu um erro ao tentar carregar sua conta.");
		}
	}

	@EventHandler
	public void onJoinListener(final PlayerJoinEvent event) {
		event.setJoinMessage((String) null);
		final Player p = event.getPlayer();
		YPvP.getPlugin().getWarpManager().getWarp(SpawnMinigame.class).join(p);
		p.sendMessage("");
		p.sendMessage("�2�lNESTY�f�lPVP");
		p.sendMessage("");
		p.sendMessage("�fEscolha seu kit clicando no �e�lBAU�f da sua �e�lMAO");
		p.sendMessage("");
		p.sendMessage("�9�lTENHA UM BOM JOGO!");
		TitleAPI.setTitle(p, "�2�lNesty�f�lPvP", "�7Conectado ao " + YPvP.getPlugin().getPvpType().name());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuitListener(final PlayerQuitEvent event) {
		event.setQuitMessage((String) null);
		YPvP.getPlugin().getGamerManager().unloadGamer(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCombatLogout(final PlayerQuitEvent event) {
		final Player logout = event.getPlayer();
		final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(logout.getUniqueId());
		if (gamer.inCombat() && !gamer.getWarp().getName().equalsIgnoreCase("1v1")) {
			final Player winner = Bukkit.getPlayer(gamer.getLastCombat());
			if (winner != null) {
				final BukkitPlayer bPLoser = (BukkitPlayer) WeavenMC.getAccountCommon()
						.getWeavenPlayer(logout.getUniqueId());
				final BukkitPlayer bPWinner = (BukkitPlayer) WeavenMC.getAccountCommon()
						.getWeavenPlayer(winner.getUniqueId());
				int deaths = bPLoser.getData(DataType.PVP_DEATHS).asInt();
				bPLoser.getData(DataType.PVP_DEATHS).setValue((Object) (++deaths));
				winner.sendMessage("�c" + logout.getName() + " deslogou.");
				this.checkLostKs(logout, winner, bPLoser.getData(DataType.PVP_KILLSTREAK).asInt());
				bPLoser.getData(DataType.PVP_KILLSTREAK).setValue((Object) 0);
				final int streak = bPWinner.getData(DataType.PVP_KILLSTREAK).asInt() + 1;
				bPWinner.getData(DataType.PVP_KILLSTREAK).setValue((Object) streak);
				final int maxStreak = bPWinner.getData(DataType.PVP_GREATER_KILLSTREAK).asInt();
				if (streak > maxStreak) {
					bPWinner.getData(DataType.PVP_GREATER_KILLSTREAK).setValue((Object) streak);
				}
				final int xp = this.calculateXp((WeavenPlayer) bPWinner, (WeavenPlayer) bPLoser);
				bPWinner.addXp(xp);
				bPWinner.addMoney(80);
				winner.sendMessage("�e�lKILL�f Voc\u00ea matou �e�l" + logout.getName());
				winner.sendMessage("�6�lMONEY�f Voc\u00ea recebeu �6�l80 MOEDAS");
				winner.sendMessage("�9�lXP�f Voc\u00ea recebeu �9�l" + xp + " XPs"
						+ (bPWinner.isDoubleXPActived() ? " �7(doublexp)" : ""));
				int kills = bPWinner.getData(DataType.PVP_KILLS).asInt();
				bPWinner.getData(DataType.PVP_KILLS).setValue((Object) (++kills));
				this.checkKs(winner, streak);
				bPLoser.save(new DataCategory[] { DataCategory.BALANCE, DataCategory.KITPVP });
				bPWinner.save(new DataCategory[] { DataCategory.BALANCE, DataCategory.KITPVP });
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventory(final PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {
			final Player player = event.getPlayer();
			if (AdminMode.getInstance().isAdmin(player)) {
				player.openInventory((Inventory) ((Player) event.getRightClicked()).getInventory());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void combat(final EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player && !event.isCancelled()) {
			final Player damaged = (Player) event.getEntity();
			final Player damager = (Player) event.getDamager();
			final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(damaged.getUniqueId());
			if (gamer.getWarp().getName().equalsIgnoreCase("spawn")
					|| gamer.getWarp().getName().equalsIgnoreCase("fps")) {
				gamer.addCombat(damager.getUniqueId(), 9);
				BossBarAPI.setBar(damager, String.valueOf(damaged.getName()) + " - " + gamer.getAbility().getName(), 5);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onProjectile(final ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			event.getEntity().remove();
		}
	}

	@EventHandler
	public void onFood(final FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawn(final CreatureSpawnEvent event) {
		if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.DISPENSE_EGG
				&& event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM
				&& event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWeather(final WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void explode(final EntityExplodeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPortal(final PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPickUp(final PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		if (AdminMode.getInstance().isAdmin(p)) {
			event.setCancelled(true);
			return;
		}
		Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		Material material = event.getItem().getItemStack().getType();
		if (gamer.getWarp().getName().equalsIgnoreCase("1v1")) {
			event.setCancelled(true);
		} else if (!material.toString().contains("MUSHROOM") && material != Material.BOWL
				&& material != Material.ENDER_PEARL && material != Material.EXP_BOTTLE
				&& material != Material.GOLDEN_APPLE && material != Material.CACTUS && material != Material.COCOA
				&& material != Material.GLASS_BOTTLE) {
			event.setCancelled(true);
		}
		material = null;
		gamer = null;
		p = null;
	}

	public void repair(final Player player) {
		ItemStack[] armorContents;
		for (int length = (armorContents = player.getInventory().getArmorContents()).length, i = 0; i < length; ++i) {
			final ItemStack armour = armorContents[i];
			if (armour != null) {
				armour.setDurability((short) 0);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSpongeJump(final PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Block block = event.getTo().getBlock();
		Location loc = block.getLocation();
		loc.setY(loc.getY() - 1.0);
		Block block2 = loc.getBlock();
		if (block2.getType() == Material.SPONGE) {
			p.setFallDistance(-50.0f);
			p.setVelocity(new Vector(0, 5, 0));
			p.setFallDistance(-50.0f);
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
		}
		loc = null;
		block = null;
		block2 = null;
		p = null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onIgnite(final BlockIgniteEvent event) {
		if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawnListener(final PlayerRespawnEvent event) {
		final Player p = event.getPlayer();
		TeleportManager.getInstance().allowJoin(p);
		p.setFireTicks(0);
		p.setHealth(p.getMaxHealth());
		final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		final Minigame minigame = gamer.getWarp();
		minigame.join(p);
		p.sendMessage("�6�lRESPAWN�f Voc\u00ea morreu e resnaceu na Warp �e" + minigame.getName());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStopDeath(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.isCancelled()) {
				return;
			}
			final Player p = (Player) event.getEntity();
			final EntityPlayer handle = ((CraftPlayer) p).getHandle();
			if (p.getHealth() - event.getFinalDamage() <= 0.0) {
				event.setCancelled(true);
				p.setHealth(20.0);
				final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
				final UUID lastCombatUUID = gamer.getLastCombat();
				Player killer = null;
				if (lastCombatUUID != null && (killer = Bukkit.getPlayer(lastCombatUUID)) != null) {
					final EntityPlayer entityhuman = ((CraftPlayer) killer).getHandle();
					handle.killer = (EntityHuman) entityhuman;
				} else {
					handle.killer = null;
				}
				final List<ItemStack> items = new ArrayList<ItemStack>();
				ItemStack[] contents;
				for (int length = (contents = p.getInventory().getContents()).length, i = 0; i < length; ++i) {
					final ItemStack content = contents[i];
					if (content != null) {
						if (content.getType() != Material.AIR) {
							items.add(content);
						}
					}
				}
				ItemStack[] armorContents;
				for (int length2 = (armorContents = p.getInventory().getArmorContents()).length,
						j = 0; j < length2; ++j) {
					final ItemStack content = armorContents[j];
					if (content != null) {
						if (content.getType() != Material.AIR) {
							items.add(content);
						}
					}
				}
				Bukkit.getPluginManager().callEvent((Event) new PlayerDeathEvent(p, items, 0, 0, (String) null));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeathListener(final PlayerDeathEvent event) {
		final Player p = event.getEntity();
		final BukkitPlayer player = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		if (p.getKiller() != null && p.getKiller() instanceof Player) {
			final Player killer = p.getKiller();
			this.repair(killer);
			event.getDrops().stream().forEach(drop -> killer.getWorld().dropItem(p.getLocation(), drop));
			event.getDrops().clear();
			this.checkLostKs(p, killer, player.getData(DataType.PVP_KILLSTREAK).asInt());
			final BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(killer.getUniqueId());
			final int xp = this.calculateXp((WeavenPlayer) bP, (WeavenPlayer) player);
			bP.addXp(xp);
			bP.addMoney(80);
			int kills = bP.getData(DataType.PVP_KILLS).asInt();
			bP.getData(DataType.PVP_KILLS).setValue((Object) (++kills));
			final int killStreak = bP.getData(DataType.PVP_KILLSTREAK).asInt() + 1;
			final int maxStreak = bP.getData(DataType.PVP_GREATER_KILLSTREAK).asInt();
			if (killStreak > maxStreak) {
				bP.getData(DataType.PVP_GREATER_KILLSTREAK).asInt();
			}
			bP.getData(DataType.PVP_KILLSTREAK).setValue((Object) killStreak);
			killer.sendMessage("�e�lKILL�f Voc\u00ea matou �e�l" + p.getName());
			killer.sendMessage("�6�lMONEY�f Voc\u00ea recebeu �6�l80 MOEDAS");
			killer.sendMessage(
					"�9�lXP�f Voc\u00ea recebeu �9�l" + xp + " XPs" + (bP.isDoubleXPActived() ? " �7(doublexp)" : ""));
			int deaths = player.getData(DataType.PVP_DEATHS).asInt();
			player.getData(DataType.PVP_DEATHS).setValue((Object) (++deaths));
			player.getData(DataType.PVP_KILLSTREAK).setValue((Object) 0);
			player.removeMoney(1);
			p.sendMessage("�c�lMORTE�f Voc\u00ea morreu para �e�l" + killer.getName());
			p.sendMessage("�4�lMONEY�f Voc\u00ea perdeu �4�l1 MOEDA");
			this.checkKs(killer, killStreak);
			player.save(new DataCategory[] { DataCategory.KITPVP, DataCategory.BALANCE });
			bP.save(new DataCategory[] { DataCategory.BALANCE, DataCategory.KITPVP });
		} else {
			event.getDrops().clear();
			p.sendMessage("�c�lMORTE�f Voc\u00ea morreu");
			player.getData(DataType.PVP_KILLSTREAK).setValue((Object) 0);
			player.save(new DataCategory[] { DataCategory.KITPVP });
		}
		Bukkit.getPluginManager().callEvent((Event) new PlayerRespawnEvent(p, p.getLocation(), false));
	}

	protected void forceRespawn(final Player player) {
		final EntityPlayer handle = ((CraftPlayer) player).getHandle();
		handle.u().getTracker().untrackEntity((Entity) handle);
		((CraftServer) Bukkit.getServer()).getServer().getPlayerList().moveToWorld(handle, 0, false);
	}

	public void checkKs(final Player p, final int ks) {
		if (ks < 10) {
			return;
		}
		if (String.valueOf(ks).endsWith("0") || String.valueOf(ks).endsWith("5")) {
			Bukkit.broadcastMessage("�4�lKILLSTREAK �1�l" + p.getName() + " �fconseguiu um �6�lKILLSTREAK DE " + ks);
		}
	}

	public void checkLostKs(final Player p, final Player k, final int ks) {
		if (ks < 10) {
			return;
		}
		Bukkit.broadcastMessage("�4�lKILLSTREAK �1�l" + p.getName() + "�f perdeu seu �6�lKILLSTREAK DE " + ks
				+ " PARA �c�l" + k.getName());
	}

	public int calculateXp(final WeavenPlayer receiver, final WeavenPlayer wP) {
		double result = 5.0;
		final int kills = wP.getData(DataType.PVP_KILLS).asInt();
		final int deaths = wP.getData(DataType.PVP_DEATHS).asInt();
		if (kills != 0 && deaths != 0) {
			result += kills / deaths;
		}
		final int battleWins = wP.getData(DataType.PVP_1V1_KILLS).asInt();
		final int battleLoses = wP.getData(DataType.PVP_1V1_DEATHS).asInt();
		if (battleWins != 0 && battleLoses != 0) {
			result += battleWins / battleLoses;
		}
		result += wP.getLeague().ordinal() / 2;
		final int hgWins = wP.getData(DataType.HG_WINS).asInt();
		final int hgDeaths = wP.getData(DataType.HG_DEATHS).asInt();
		if (hgWins != 0 && hgDeaths != 0) {
			result += hgWins / hgDeaths;
		}
		final int gladWins = wP.getData(DataType.GLADIATOR_WINS).asInt();
		final int gladDeaths = wP.getData(DataType.GLADIATOR_LOSES).asInt();
		if (gladWins != 0 && gladDeaths != 0) {
			result += gladWins / gladDeaths;
		}
		if ((int) result <= 0) {
			result = 5.0;
		}
		if (receiver.isDoubleXPActived()) {
			result *= 2.0;
		}
		return (int) result;
	}

	@EventHandler
	public void onBucket(final PlayerBucketEmptyEvent event) {
		final Player p = event.getPlayer();
		final BukkitPlayer bP = BukkitPlayer.getPlayer(p.getUniqueId());
		if (!bP.hasGroupPermission(Group.GERENTE)) {
			final Material bucket = event.getBucket();
			if (bucket.toString().contains("LAVA")) {
				event.setCancelled(true);
			} else if (bucket.toString().contains("WATER")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onRepair(final PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack itemStack = p.getItemInHand();
		if (itemStack != null) {
			Action action = event.getAction();
			if (action.name().contains("LEFT")) {
				if (itemStack.getType() == Material.DIAMOND_SWORD || itemStack.getType() == Material.STONE_SWORD
						|| itemStack.getType() == Material.WOOD_SWORD || itemStack.getType() == Material.STONE_SWORD
						|| itemStack.getType() == Material.IRON_SWORD || itemStack.getType() == Material.GOLD_SWORD
						|| itemStack.getType() == Material.DIAMOND_AXE || itemStack.getType() == Material.GOLD_AXE
						|| itemStack.getType() == Material.STONE_AXE || itemStack.getType() == Material.WOOD_AXE
						|| itemStack.getType() == Material.IRON_AXE || itemStack.getType() == Material.FISHING_ROD) {
					itemStack.setDurability((short) 0);
					p.updateInventory();
				}
			} else if (itemStack.getType() == Material.FISHING_ROD) {
				itemStack.setDurability((short) 0);
				p.updateInventory();
			}
			action = null;
			itemStack = null;
		}
		p = null;
	}

	@EventHandler
	public void onDropItem(final PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(player.getUniqueId());
		if (gamer.getWarp().getName().equals("1v1")) {
			event.setCancelled(true);
		} else {
			Material material = event.getItemDrop().getItemStack().getType();
			if (!material.toString().contains("MUSHROOM") && material != Material.CACTUS && material != Material.BOWL
					&& material != Material.ENDER_PEARL && material != Material.EXP_BOTTLE
					&& material != Material.GOLDEN_APPLE && material != Material.GLASS_BOTTLE
					&& !material.toString().contains("_BOOTS") && !material.toString().contains("_LEGGINGS")
					&& !material.toString().contains("_CHESTPLATE") && !material.toString().contains("_HELMET")) {
				event.setCancelled(true);
			}
			material = null;
		}
	}

	@EventHandler
	public void onPlace(final BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreak(final BlockBreakEvent event) {
		final Player p = event.getPlayer();
		if (p.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onRegain(final EntityRegainHealthEvent event) {
		if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
				|| event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCompass(final PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		if (!gamer.getAbility().getName().equals("Nenhum")) {
			Material material = p.getItemInHand().getType();
			if (material == null || material != Material.COMPASS) {
				return;
			}
			event.setCancelled(true);
			Player target = null;
			double distance = 500.0;
			for (final Player players : Bukkit.getOnlinePlayers()) {
				if (AdminMode.getInstance().isAdmin(players)) {
					continue;
				}
				final double distanceToVictim = p.getLocation().distance(players.getLocation());
				if (distanceToVictim >= distance || distanceToVictim <= 10.0) {
					continue;
				}
				distance = distanceToVictim;
				target = players;
			}
			if (target == null) {
				p.sendMessage("�6�lBUSSOLA�f Nenhum player foi encontrado");
				p.setCompassTarget(p.getWorld().getSpawnLocation());
			} else {
				p.setCompassTarget(target.getLocation());
				p.sendMessage("�6�lBUSSOLA�f Apontando para �e�l" + target.getName());
				target = null;
			}
			material = null;
		}
		gamer = null;
		p = null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSoup(final PlayerInteractEvent event) {
		Player p = event.getPlayer();
		final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		Material material = p.getItemInHand().getType();
		if (material == null || material != Material.MUSHROOM_SOUP) {
			return;
		}
		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
				&& p.getHealth() < p.getMaxHealth()) {
			final int restores = 7;
			event.setCancelled(true);
			if (p.getHealth() + restores <= p.getMaxHealth()) {
				p.setHealth(p.getHealth() + restores);
			} else {
				p.setHealth(p.getMaxHealth());
			}
			p.setItemInHand(new ItemBuilder().type(Material.BOWL).build());
			if (gamer.getAbility().getName().toLowerCase().equals("quickdrop")) {
				p.setItemInHand(new ItemBuilder().type(Material.AIR).build());
			}
		}
		material = null;
		p = null;
	}

	@EventHandler
	public void onItemSpawn(final ItemSpawnEvent event) {
		final Item localItem = event.getEntity();
		Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) YPvP.getPlugin(), () -> localItem.remove(), 200L);
	}
}
