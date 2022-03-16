package br.com.weavenmc.ypvp.minigame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.api.vanish.VanishAPI;
import br.com.weavenmc.commons.bukkit.event.admin.PlayerAdminModeEnterEvent;
import br.com.weavenmc.commons.bukkit.event.vanish.PlayerHideEvent;
import br.com.weavenmc.commons.bukkit.event.vanish.PlayerShowEvent;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.event.SpectatorBattleEndEvent;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class BattleMinigame extends Minigame {
	private final HashMap<UUID, UUID> battle;
	private final HashMap<UUID, List<UUID>> normalChallenge;
	private final HashMap<UUID, UUID> customChallenge;
	private UUID nextBattle;
	private final HashMap<UUID, Material> armourType;
	private final HashMap<UUID, Material> swordType;
	private final HashMap<UUID, Material> recraftType;
	private final HashMap<UUID, Boolean> recraftOption;
	private final HashMap<UUID, Boolean> sharpOption;
	private final HashMap<UUID, Boolean> fullSoupOption;
	private final HashMap<UUID, UUID> customCalling;

	public BattleMinigame() {
		this.battle = new HashMap<UUID, UUID>();
		this.normalChallenge = new HashMap<UUID, List<UUID>>();
		this.customChallenge = new HashMap<UUID, UUID>();
		this.nextBattle = null;
		this.armourType = new HashMap<UUID, Material>();
		this.swordType = new HashMap<UUID, Material>();
		this.recraftType = new HashMap<UUID, Material>();
		this.recraftOption = new HashMap<UUID, Boolean>();
		this.sharpOption = new HashMap<UUID, Boolean>();
		this.fullSoupOption = new HashMap<UUID, Boolean>();
		this.customCalling = new HashMap<UUID, UUID>();
		this.setName("1v1");
		this.setOtherNames(new String[] { "OnevsOne", "Battle" });
		this.setTopKillStreakMinigame(true);
	}

	@Override
	public void join(final Player p) {
		if (!TeleportManager.getInstance().canJoin(p, this)) {
			return;
		}
		if (p.getAllowFlight() && !AdminMode.getInstance().isAdmin(p)) {
			p.setAllowFlight(false);
		}
		p.sendMessage("�9�lTELEPORTE�f Voc\u00ea foi teleportado para �3�l1v1");
		VanishAPI.getInstance().updateVanishToPlayer(p);
		final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		gamer.resetCombat();
		if (gamer.getWarp() != null) {
			gamer.getWarp().quit(p);
		}
		this.joinPlayer(p.getUniqueId());
		YPvP.getPlugin().getCooldownManager().removeCooldown(p);
		YPvP.getPlugin().getAbilityManager().getAbilities().stream().forEach(ability -> ability.eject(p));
		gamer.setWarp(this);
		gamer.setAbility(YPvP.getPlugin().getAbilityManager().getNone());
		p.setHealth(20.0);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.getActivePotionEffects().clear();
		p.getInventory().clear();
		this.teleport(p);
		p.getInventory().setArmorContents((ItemStack[]) null);
		ItemBuilder builder = new ItemBuilder().type(Material.BLAZE_ROD).name("�6�l1v1 Normal");
		p.getInventory().setItem(3, builder.build());
		builder = new ItemBuilder().type(Material.IRON_FENCE).name("�b�l1v1 Customizado");
		p.getInventory().setItem(4, builder.build());
		builder = new ItemBuilder().type(Material.INK_SACK).durability(8).name("�e�l1v1 R\u00e1pido");
		p.getInventory().setItem(5, builder.build());
		p.updateInventory();
		YPvP.getPlugin().getScoreboardManager().createScoreboard(p);
	}

	public void callBattleEnd(final Player player1, final Player player2) {
		Bukkit.getPluginManager().callEvent((Event) new SpectatorBattleEndEvent(player1, player2));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onShow(final PlayerShowEvent event) {
		final Player show = event.getToShow();
		final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(show.getUniqueId());
		if (gamer.hasSpectator()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onShow(final PlayerHideEvent event) {
		final Player hide = event.getToHide();
		final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(hide.getUniqueId());
		if (gamer.hasSpectator()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSpectatorBattleEnd(final SpectatorBattleEndEvent event) {
		for (final Gamer gamer : YPvP.getPlugin().getGamerManager().getGamers()) {
			if (!gamer.hasSpectator()) {
				continue;
			}
			if (!gamer.getSpectator().equals(event.getPlayer1().getUniqueId())
					&& !gamer.getSpectator().equals(event.getPlayer2().getUniqueId())) {
				continue;
			}
			final Player player = Bukkit.getPlayer(gamer.getUniqueId());
			if (player == null) {
				continue;
			}
			gamer.setSpectator(null);
			this.teleport(player);
			player.sendMessage("�b�lESPECTAR�f O player acabou a luta!");
			VanishAPI.getInstance().updateVanishToPlayer(player);
		}
	}

	public boolean isBattling(final Player player) {
		return this.battle.containsKey(player.getUniqueId());
	}

	public Player getCurrentBattlePlayer(final Player player) {
		return Bukkit.getPlayer((UUID) this.battle.get(player.getUniqueId()));
	}

	@EventHandler
	public void onAdminEnter(final PlayerAdminModeEnterEvent event) {
		if (this.battle.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
			event.getPlayer()
					.sendMessage("�4�lADMIN�f Voc\u00ea n\u00e3o pode entrar no Modo Admin durante uma batalha");
		}
	}

	@EventHandler
	public void onPlayerHideListener(final PlayerHideEvent event) {
		final Player hide = event.getToHide();
		if (this.battle.containsKey(hide.getUniqueId())
				&& this.battle.get(hide.getUniqueId()) == event.getPlayer().getUniqueId()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerShowListener(final PlayerShowEvent event) {
		final Player show = event.getToShow();
		if (this.battle.containsKey(show.getUniqueId())
				&& this.battle.get(show.getUniqueId()) != event.getPlayer().getUniqueId()) {
			event.setCancelled(true);
		}
	}

	public String getBattlePlayer(final Player p) {
		if (this.battle.containsKey(p.getUniqueId())) {
			final Player battlePlayer = Bukkit.getPlayer((UUID) this.battle.get(p.getUniqueId()));
			if (battlePlayer != null) {
				return battlePlayer.getName();
			}
		}
		return "Ningu\u00e9m";
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		if (gamer.getWarp() == this) {
			final ItemStack itemInHand = p.getItemInHand();
			if (itemInHand.getType() == Material.INK_SACK) {
				event.setCancelled(true);
				this.callFastBattle(p);
			}
		}
	}

	public synchronized void callFastBattle(final Player p) {
		final ItemStack itemInHand = p.getItemInHand();
		if (itemInHand.getType() == Material.INK_SACK && itemInHand.hasItemMeta()) {
			if (itemInHand.getItemMeta().getDisplayName().equals("�e�l1v1 R\u00e1pido")) {
				if (this.nextBattle == null) {
					this.nextBattle = p.getUniqueId();
					p.setItemInHand(new ItemBuilder().type(Material.INK_SACK).durability(10)
							.name("�e�lProcurando partidas").build());
					p.updateInventory();
					p.sendMessage("�eO 1v1 R\u00e1pido est\u00e1 procurando algu\u00e9m para voc\u00ea batalhar!");
				} else {
					final Player finded = Bukkit.getPlayer(this.nextBattle);
					if (finded != null) {
						if (finded.getUniqueId() != p.getUniqueId()) {
							this.nextBattle = null;
							this.clearRequests(finded, p);
							this.startNormalBattle(p, finded);
							this.clearCustom(p, finded);
							finded.sendMessage(
									"�9O 1v1 R\u00e1pido encontrou algu\u00e9m para voc\u00ea lutar! O player escolhido foi �e"
											+ p.getName());
							p.sendMessage(
									"�9O 1v1 R\u00e1pido encontrou algu\u00e9m para voc\u00ea lutar! O player escolhido foi �e"
											+ finded.getName());
						} else {
							this.nextBattle = p.getUniqueId();
							p.setItemInHand(new ItemBuilder().type(Material.INK_SACK).durability(10)
									.name("�e�lProcurando partidas").build());
							p.updateInventory();
							p.sendMessage(
									"�eO 1v1 R\u00e1pido est\u00e1 procurando algu\u00e9m para voc\u00ea batalhar!");
						}
					} else {
						this.nextBattle = p.getUniqueId();
						p.setItemInHand(new ItemBuilder().type(Material.INK_SACK).durability(10)
								.name("�e�lProcurando partidas").build());
						p.updateInventory();
						p.sendMessage("�eO 1v1 R\u00e1pido est\u00e1 procurando algu\u00e9m para voc\u00ea batalhar!");
					}
				}
			} else {
				if (this.nextBattle == p.getUniqueId()) {
					this.nextBattle = null;
				}
				p.setItemInHand(
						new ItemBuilder().type(Material.INK_SACK).durability(8).name("�e�l1v1 R\u00e1pido").build());
				p.updateInventory();
				p.sendMessage("�eO 1v1 R\u00e1pido parou de procurar algu\u00e9m para voc\u00ea batalhar!");
			}
		}
	}

	@EventHandler
	public void onChallenge(final PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {
			final Player p = event.getPlayer();
			final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			final ItemStack itemInHand = p.getItemInHand();
			if (gamer.getWarp() == this && !this.battle.containsKey(p.getUniqueId())) {
				if (!YPvP.getPlugin().getCooldownManager().hasCooldown(p)) {
					final Player challenged = (Player) event.getRightClicked();
					final Gamer g = YPvP.getPlugin().getGamerManager().getGamer(challenged.getUniqueId());
					if (g.getWarp() == this && !this.battle.containsKey(challenged.getUniqueId())) {
						if (itemInHand.getType() == Material.BLAZE_ROD) {
							event.setCancelled(true);
							if (this.normalChallenge.containsKey(p.getUniqueId())
									&& this.normalChallenge.get(p.getUniqueId()).contains(challenged.getUniqueId())) {
								this.clearRequests(p, challenged);
								this.startNormalBattle(challenged, p);
								this.clearCustom(challenged, p);
								challenged.sendMessage("�b" + p.getName() + "�2 aceitou seu desafio");
								p.sendMessage("�2Voc\u00ea aceitou o desafio de �b" + challenged.getName());
							} else {
								YPvP.getPlugin().getCooldownManager().addCooldown(p, 5);
								p.sendMessage(
										"�7Voc\u00ea enviou um desafio de 1v1 normal para �b" + challenged.getName());
								challenged.sendMessage("�eVoc\u00ea recebeu desafio de 1v1 normal de �7" + p.getName());
								final List<UUID> challengers = this.normalChallenge.containsKey(
										challenged.getUniqueId()) ? this.normalChallenge.get(challenged.getUniqueId())
												: new ArrayList<UUID>();
								challengers.add(p.getUniqueId());
								this.normalChallenge.put(challenged.getUniqueId(), challengers);
								new BukkitRunnable() {
									public void run() {
										if (BattleMinigame.this.normalChallenge.containsKey(challenged.getUniqueId())) {
											BattleMinigame.this.normalChallenge.get(challenged.getUniqueId())
													.remove(p.getUniqueId());
										}
									}
								}.runTaskLater((Plugin) YPvP.getPlugin(), 100L);
							}
						} else if (itemInHand.getType() == Material.IRON_FENCE) {
							if (this.customChallenge.containsKey(p.getUniqueId())
									&& this.customChallenge.get(p.getUniqueId()).equals(challenged.getUniqueId())) {
								this.clearRequests(challenged, p);
								this.startCustomBattle(challenged, p);
								challenged.sendMessage("�b" + p.getName() + "�2 aceitou seu desafio");
								p.sendMessage("�2Voc\u00ea aceitou o desafio de �b" + challenged.getName());
							} else {
								this.customCalling.put(p.getUniqueId(), challenged.getUniqueId());
								this.customCalling.put(challenged.getUniqueId(), p.getUniqueId());
								this.openCustomInventoryFor(p, challenged);
							}
						}
					}
				} else {
					final String millis = StringTimeUtils
							.toMillis(YPvP.getPlugin().getCooldownManager().getCooldown(p));
					p.sendMessage("�cAguarde " + millis + " para desafiar novamente");
				}
			}
		}
	}

	public void defautCustom(final Player p) {
		this.armourType.put(p.getUniqueId(), Material.LEATHER_CHESTPLATE);
		this.swordType.put(p.getUniqueId(), Material.WOOD_SWORD);
		this.recraftType.put(p.getUniqueId(), Material.RED_MUSHROOM);
		this.recraftOption.put(p.getUniqueId(), false);
		this.sharpOption.put(p.getUniqueId(), true);
		this.fullSoupOption.put(p.getUniqueId(), false);
	}

	public void openCustomInventoryFor(final Player p, final Player challenged) {
		this.defautCustom(p);
		final Inventory menu = Bukkit.createInventory((InventoryHolder) p, 54, "�c1v1 contra " + challenged.getName());
		for (int i = 0; i < 54; ++i) {
			menu.setItem(i, new ItemBuilder().type(Material.STAINED_GLASS_PANE).name("�b�l-").durability(8).build());
		}
		ItemBuilder builder = new ItemBuilder().type(Material.WOOL).name("�a�lDesafiar Jogador").durability(5);
		menu.setItem(43, builder.build());
		builder = new ItemBuilder().type(Material.WOOL).name("�a�lDesafiar Jogador").durability(5);
		menu.setItem(44, builder.build());
		builder = new ItemBuilder().type(Material.WOOL).name("�a�lDesafiar Jogador").durability(5);
		menu.setItem(52, builder.build());
		builder = new ItemBuilder().type(Material.WOOL).name("�a�lDesafiar Jogador").durability(5);
		menu.setItem(53, builder.build());
		if (this.swordType.containsKey(p.getUniqueId())) {
			final Material sword = this.swordType.get(p.getUniqueId());
			builder = new ItemBuilder().type(sword);
			if (sword == Material.WOOD_SWORD) {
				builder.name("�6Espada de Madeira")
						.lore(new String[] { "�3Clique aqui para mudar", "�3o tipo de sua espada!", "" });
			} else if (sword == Material.STONE_SWORD) {
				builder.name("�6Espada de Pedra")
						.lore(new String[] { "�3Clique aqui para mudar", "�3o tipo de sua espada!", "" });
			} else if (sword == Material.IRON_SWORD) {
				builder.name("�6Espada de Ferro")
						.lore(new String[] { "�3Clique aqui para mudar", "�3o tipo de sua espada!", "" });
			} else if (sword == Material.DIAMOND_SWORD) {
				builder.name("�6Espada de Diamante")
						.lore(new String[] { "�3Clique aqui para mudar", "�3o tipo de sua espada!", "" });
			}
			menu.setItem(20, builder.build());
		}
		if (this.armourType.containsKey(p.getUniqueId())) {
			final Material armour = this.armourType.get(p.getUniqueId());
			builder = new ItemBuilder().type(armour);
			if (armour == Material.LEATHER_CHESTPLATE) {
				builder.name("�eArmadura de Couro")
						.lore(new String[] { "�3Clique aqui para mudar", "�3o tipo de sua armadura!", "" });
			} else if (armour == Material.IRON_CHESTPLATE) {
				builder.name("�eArmadura de Ferro")
						.lore(new String[] { "�3Clique aqui para mudar", "�3o tipo de sua armadura!", "" });
			} else if (armour == Material.DIAMOND_CHESTPLATE) {
				builder.name("�eArmadura de Diamente")
						.lore(new String[] { "�3Clique aqui para mudar", "�3o tipo de sua armadura!", "" });
			} else if (armour == Material.GOLD_HELMET) {
				builder.name("�eSem armadura")
						.lore(new String[] { "�3Clique aqui para mudar", "�3o tipo de sua armadura!", "" });
			}
			menu.setItem(21, builder.build());
		}
		if (this.recraftType.containsKey(p.getUniqueId())) {
			final Material recraft = this.recraftType.get(p.getUniqueId());
			builder = new ItemBuilder().type(recraft);
			if (recraft == Material.RED_MUSHROOM) {
				builder.name("�bRecrafts de Cogumelo")
						.lore(new String[] { "�3Clique aqui para mudar", "�3o tipo de seu recraft!", "" });
			} else if (recraft == Material.COCOA) {
				builder.name("�bRecrafts de Cocoabean")
						.lore(new String[] { "�3Clique aqui para mudar", "�3o tipo de seu recraft!", "" });
			}
			menu.setItem(22, builder.build());
		}
		if (this.recraftOption.containsKey(p.getUniqueId())) {
			if (this.recraftOption.get(p.getUniqueId())) {
				builder = new ItemBuilder().type(Material.BROWN_MUSHROOM).name("�aCom Recraft")
						.lore(new String[] { "�3Clique aqui para", "�3desativar o recraft!", "" });
			} else {
				builder = new ItemBuilder().type(Material.MAGMA_CREAM).name("�cSem Recraft")
						.lore(new String[] { "�3Clique aqui para", "�3ativar o recraft!", "" });
			}
			menu.setItem(23, builder.build());
		}
		if (this.sharpOption.containsKey(p.getUniqueId())) {
			if (this.sharpOption.get(p.getUniqueId())) {
				builder = new ItemBuilder().type(Material.ENCHANTED_BOOK).name("�3Com Sharpness")
						.lore(new String[] { "�3Clique aqui para", "�3tirar a afia\u00e7ao da espada!", "" });
			} else {
				builder = new ItemBuilder().type(Material.BOOK).name("�3Sem Sharpness")
						.lore(new String[] { "�3Clique aqui para", "�3colocar afia\u00e7ao na espada!", "" });
			}
			menu.setItem(24, builder.build());
		}
		if (this.fullSoupOption.containsKey(p.getUniqueId())) {
			if (this.fullSoupOption.get(p.getUniqueId())) {
				builder = new ItemBuilder().type(Material.MUSHROOM_SOUP).name("�2Full Sopa")
						.lore(new String[] { "�3Clique aqui para", "�3usar 1 hotbar apenas", "" });
			} else {
				builder = new ItemBuilder().type(Material.BOWL).name("�21 Hotbar")
						.lore(new String[] { "�3Clique aqui para", "�3usar full sopa", "" });
			}
			menu.setItem(29, builder.build());
		}
		p.openInventory(menu);
	}

	@EventHandler
	public void onDamage(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player p = (Player) event.getEntity();
			final Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (gamer.getWarp() == this) {
				if (this.battle.containsKey(p.getUniqueId())) {
					if (event.getCause() == EntityDamageEvent.DamageCause.FIRE
							|| event.getCause() == EntityDamageEvent.DamageCause.LAVA
							|| event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onDamageHit(final EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			final Player e = (Player) event.getEntity();
			final Player d = (Player) event.getDamager();
			final Gamer entity = YPvP.getPlugin().getGamerManager().getGamer(e.getUniqueId());
			final Gamer damager = YPvP.getPlugin().getGamerManager().getGamer(d.getUniqueId());
			if (entity.getWarp() == this || damager.getWarp() == this) {
				if (!this.battle.containsKey(e.getUniqueId())) {
					event.setCancelled(true);
				} else if (!this.battle.containsKey(d.getUniqueId())) {
					event.setCancelled(true);
				} else if (this.battle.containsKey(e.getUniqueId()) && this.battle.containsKey(d.getUniqueId())
						&& this.battle.get(d.getUniqueId()) != e.getUniqueId()
						&& this.battle.get(e.getUniqueId()) != d.getUniqueId()) {
					event.setCancelled(true);
				}
			}
		}
	}

	public int itemsInInventory(final Inventory inventory, final Material... search) {
		final List<Material> wanted = Arrays.asList(search);
		int found = 0;
		ItemStack[] arrayOfItemStack;
		for (int j = (arrayOfItemStack = inventory.getContents()).length, i = 0; i < j; ++i) {
			final ItemStack item = arrayOfItemStack[i];
			if (item != null && wanted.contains(item.getType())) {
				found += item.getAmount();
			}
		}
		return found;
	}

	@EventHandler
	public void onCombatCommand(final PlayerCommandPreprocessEvent event) {
		final Player p = event.getPlayer();
		if (this.battle.containsKey(p.getUniqueId())) {
			event.setCancelled(true);
			p.sendMessage("�b�l1V1�f Voc\u00ea n\u00e3o pode executar comandos durante a batalha!");
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBattleLogout(final PlayerQuitEvent event) {
		final Player logout = event.getPlayer();
		if (this.battle.containsKey(logout.getUniqueId())) {
			final BukkitPlayer bPLoser = (BukkitPlayer) WeavenMC.getAccountCommon()
					.getWeavenPlayer(logout.getUniqueId());
			int deaths = bPLoser.getData(DataType.PVP_DEATHS).asInt();
			bPLoser.getData(DataType.PVP_DEATHS).setValue((Object) (++deaths));
			final Player winner = Bukkit.getPlayer((UUID) this.battle.get(logout.getUniqueId()));
			this.callBattleEnd(logout, winner);
			winner.sendMessage("�c" + logout.getName() + " deslogou.");
			this.battle.remove(logout.getUniqueId());
			this.battle.remove(winner.getUniqueId());
			this.checkLostKs(logout, winner, bPLoser.getData(DataType.PVP_KILLSTREAK).asInt());
			bPLoser.getData(DataType.PVP_KILLSTREAK).setValue((Object) 0);
			final BukkitPlayer bPWinner = (BukkitPlayer) WeavenMC.getAccountCommon()
					.getWeavenPlayer(winner.getUniqueId());
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
			bPLoser.save(new DataCategory[] { DataCategory.KITPVP });
			bPWinner.save(new DataCategory[] { DataCategory.BALANCE, DataCategory.KITPVP });
			this.join(winner);
		}
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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLose(final PlayerDeathEvent event) {
		final Player loser = event.getEntity();
		final EntityPlayer l = ((CraftPlayer) loser).getHandle();
		if (this.battle.containsKey(loser.getUniqueId())) {
			event.getDrops().clear();
			final Player winner = Bukkit.getPlayer((UUID) this.battle.get(loser.getUniqueId()));
			this.callBattleEnd(loser, winner);
			l.killer = (EntityHuman) ((CraftPlayer) winner).getHandle();
			final String restingLife = StringTimeUtils.toMillis(winner.getHealth() / 2.0);
			final int restingSoups = this.itemsInInventory((Inventory) winner.getInventory(), Material.MUSHROOM_SOUP);
			winner.sendMessage("�cVoc\u00ea venceu o 1v1 contra " + loser.getName() + " com " + restingLife
					+ " cora\u00e7oes e " + restingSoups + " sopas restantes");
			loser.sendMessage("�c" + winner.getName() + " venceu o 1v1 com " + restingLife + " cora\u00e7oes e "
					+ restingSoups + " sopas restantes");
			this.battle.remove(winner.getUniqueId());
			this.battle.remove(loser.getUniqueId());
			this.join(winner);
		}
	}

	@EventHandler
	public void onJoinListener(final PlayerJoinEvent event) {
		Player joined = event.getPlayer();
		for (final Player o : Bukkit.getOnlinePlayers()) {
			if (!this.battle.containsKey(o.getUniqueId())) {
				continue;
			}
			o.hidePlayer(joined);
		}
		joined = null;
	}

	@EventHandler
	public void onQuitListener(final PlayerQuitEvent event) {
		Player quited = event.getPlayer();
		this.clearRequests(quited);
		this.clearCustom(quited);
		quited = null;
	}

	@EventHandler
	public void onInventoryClose(final InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			final Player p = (Player) event.getPlayer();
			if (this.customCalling.containsKey(p.getUniqueId())) {
				this.customCalling.remove(p.getUniqueId());
			}
		}
	}

	@EventHandler
	public void onInventoryClickListener(final InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			final Player p = (Player) event.getWhoClicked();
			if (this.customCalling.containsKey(p.getUniqueId())) {
				final ItemStack currentItem = event.getCurrentItem();
				if (currentItem != null) {
					final int currentSlot = event.getSlot();
					final Player t = Bukkit.getPlayer((UUID) this.customCalling.get(p.getUniqueId()));
					if (t != null) {
						final Inventory menu = event.getInventory();
						if (menu.getName().equalsIgnoreCase("�c1v1 contra " + t.getName())) {
							event.setCancelled(true);
							if (currentItem.getType() == Material.WOOD_SWORD) {
								this.swordType.put(p.getUniqueId(), Material.STONE_SWORD);
								menu.setItem(
										currentSlot, new ItemBuilder().type(Material.STONE_SWORD)
												.name("�6Espada de Pedra").lore(new String[] {
														"�3Clique aqui para mudar", "�3o tipo de sua espada!", "" })
												.build());
							} else if (currentItem.getType() == Material.STONE_SWORD) {
								this.swordType.put(p.getUniqueId(), Material.IRON_SWORD);
								menu.setItem(currentSlot,
										new ItemBuilder()
												.type(Material.IRON_SWORD).name("�6Espada de Ferro").lore(new String[] {
														"�3Clique aqui para mudar", "�3o tipo de sua espada!", "" })
												.build());
							} else if (currentItem.getType() == Material.IRON_SWORD) {
								this.swordType.put(p.getUniqueId(), Material.DIAMOND_SWORD);
								menu.setItem(
										currentSlot, new ItemBuilder().type(Material.DIAMOND_SWORD)
												.name("�6Espada de Diamente").lore(new String[] {
														"�3Clique aqui para mudar", "�3o tipo de sua espada!", "" })
												.build());
							} else if (currentItem.getType() == Material.DIAMOND_SWORD) {
								this.swordType.put(p.getUniqueId(), Material.WOOD_SWORD);
								menu.setItem(
										currentSlot, new ItemBuilder().type(Material.WOOD_SWORD)
												.name("�6Espada de Madeira").lore(new String[] {
														"�3Clique aqui para mudar", "�3o tipo de sua espada!", "" })
												.build());
							} else if (currentItem.getType() == Material.LEATHER_CHESTPLATE) {
								this.armourType.put(p.getUniqueId(), Material.IRON_CHESTPLATE);
								menu.setItem(
										currentSlot, new ItemBuilder().type(Material.IRON_CHESTPLATE)
												.name("�eArmadura de Ferro").lore(new String[] {
														"�3Clique aqui para mudar", "�3o tipo de sua armadura!", "" })
												.build());
							} else if (currentItem.getType() == Material.IRON_CHESTPLATE) {
								this.armourType.put(p.getUniqueId(), Material.DIAMOND_CHESTPLATE);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.DIAMOND_CHESTPLATE)
												.name("�eArmadura de Diamante").lore(new String[] {
														"�3Clique aqui para mudar", "�3o tipo de sua armadura!", "" })
												.build());
							} else if (currentItem.getType() == Material.DIAMOND_CHESTPLATE) {
								this.armourType.put(p.getUniqueId(), Material.GOLD_HELMET);
								menu.setItem(currentSlot,
										new ItemBuilder()
												.type(Material.GOLD_HELMET).name("�eSem Armadura").lore(new String[] {
														"�3Clique aqui para mudar", "�3o tipo de sua armadura!", "" })
												.build());
							} else if (currentItem.getType() == Material.GOLD_HELMET) {
								this.armourType.put(p.getUniqueId(), Material.LEATHER_CHESTPLATE);
								menu.setItem(
										currentSlot, new ItemBuilder().type(Material.LEATHER_CHESTPLATE)
												.name("�eArmadura de Couro").lore(new String[] {
														"�3Clique aqui para mudar", "�3o tipo de sua armadura!", "" })
												.build());
							} else if (currentItem.getType() == Material.RED_MUSHROOM) {
								this.recraftType.put(p.getUniqueId(), Material.CACTUS);
								menu.setItem(
										currentSlot, new ItemBuilder().type(Material.CACTUS)
												.name("�bRecrafts de Cocoabean").lore(new String[] {
														"�3Clique aqui para mudar", "�3o tipo de seu recraft!", "" })
												.build());
							} else if (currentItem.getType() == Material.CACTUS) {
								this.recraftType.put(p.getUniqueId(), Material.RED_MUSHROOM);
								menu.setItem(
										currentSlot, new ItemBuilder().type(Material.RED_MUSHROOM)
												.name("�bRecrafts de Cogumelo").lore(new String[] {
														"�3Clique aqui para mudar", "�3o tipo de seu recraft!", "" })
												.build());
							} else if (currentItem.getType() == Material.BROWN_MUSHROOM) {
								this.recraftOption.put(p.getUniqueId(), false);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.MAGMA_CREAM).name("�cSem Recraft")
												.lore(new String[] { "�3Clique aqui para", "�3ativar o recraft!", "" })
												.build());
							} else if (currentItem.getType() == Material.MAGMA_CREAM) {
								this.recraftOption.put(p.getUniqueId(), true);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.BROWN_MUSHROOM).name("�aCom Recraft").lore(
												new String[] { "�3Clique aqui para", "�3desativar o recraft!", "" })
												.build());
							} else if (currentItem.getType() == Material.ENCHANTED_BOOK) {
								this.sharpOption.put(p.getUniqueId(), false);
								menu.setItem(currentSlot,
										new ItemBuilder()
												.type(Material.BOOK).name("�3Sem Sharpness").lore(new String[] {
														"�3Clique aqui para", "�3colocar afia\u00e7ao na espada!", "" })
												.build());
							} else if (currentItem.getType() == Material.BOOK) {
								this.sharpOption.put(p.getUniqueId(), true);
								menu.setItem(currentSlot, new ItemBuilder()
										.type(Material.ENCHANTED_BOOK).name("�3Com Sharpness").lore(new String[] {
												"�3Clique aqui para", "�3tirar a afia\u00e7ao da espada!", "" })
										.build());
							} else if (currentItem.getType() == Material.MUSHROOM_SOUP) {
								this.fullSoupOption.put(p.getUniqueId(), false);
								menu.setItem(currentSlot, new ItemBuilder().type(Material.BOWL).name("�21 Hotbar")
										.lore(new String[] { "�3Clique aqui para", "�3usar full sopa", "" }).build());
							} else if (currentItem.getType() == Material.BOWL) {
								this.fullSoupOption.put(p.getUniqueId(), true);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.MUSHROOM_SOUP).name("�2Full Sopa").lore(
												new String[] { "�3Clique aqui para", "�3usar 1 hotbar apenas", "" })
												.build());
							} else if (currentItem.getType() == Material.WOOL) {
								p.closeInventory();
								YPvP.getPlugin().getCooldownManager().addCooldown(p, 5);
								this.customChallenge.put(t.getUniqueId(), p.getUniqueId());
								p.sendMessage("�7Voc\u00ea enviou um desafio de 1v1 customizado para �b" + t.getName());
								t.sendMessage("�eVoc\u00ea recebeu desafio de 1v1 customizado de �7" + p.getName());
								new BukkitRunnable() {
									public void run() {
										if (BattleMinigame.this.customChallenge.containsKey(t.getUniqueId())
												&& BattleMinigame.this.customChallenge.get(t.getUniqueId()) == p
														.getUniqueId()) {
											BattleMinigame.this.customChallenge.remove(t.getUniqueId());
										}
									}
								}.runTaskLater((Plugin) YPvP.getPlugin(), 100L);
							}
						}
					} else {
						p.closeInventory();
						this.customCalling.remove(p.getUniqueId());
						p.sendMessage("�cO jogador desafiado n\u00e3o foi encontrado.");
					}
				}
			}
		}
	}

	public void startCustomBattle(final Player custommer, final Player challenged) {
		this.battle.put(custommer.getUniqueId(), challenged.getUniqueId());
		this.battle.put(challenged.getUniqueId(), custommer.getUniqueId());
		final Location pos1 = YPvP.getPlugin().getLocationManager().getLocation("1v1loc1");
		final Location pos2 = YPvP.getPlugin().getLocationManager().getLocation("1v1loc2");
		if (pos1 != null && pos2 != null) {
			custommer.teleport(pos1);
			challenged.teleport(pos2);
		}
		this.prepareCustomBattle(custommer, challenged);
		for (final Player o : Bukkit.getOnlinePlayers()) {
			if (o.getUniqueId() != custommer.getUniqueId()) {
				if (o.getUniqueId() == challenged.getUniqueId()) {
					continue;
				}
				custommer.hidePlayer(o);
				challenged.hidePlayer(o);
			}
		}
		custommer.showPlayer(challenged);
		challenged.showPlayer(custommer);
	}

	public void startNormalBattle(final Player p1, final Player p2) {
		this.battle.put(p1.getUniqueId(), p2.getUniqueId());
		this.battle.put(p2.getUniqueId(), p1.getUniqueId());
		final Location pos1 = YPvP.getPlugin().getLocationManager().getLocation("1v1loc1");
		final Location pos2 = YPvP.getPlugin().getLocationManager().getLocation("1v1loc2");
		if (pos1 != null && pos2 != null) {
			p1.teleport(pos1);
			p2.teleport(pos2);
		}
		this.prepareNormalBattle(p1);
		this.prepareNormalBattle(p2);
		for (final Player o : Bukkit.getOnlinePlayers()) {
			if (o.getUniqueId() != p1.getUniqueId()) {
				if (o.getUniqueId() == p2.getUniqueId()) {
					continue;
				}
				p1.hidePlayer(o);
				p2.hidePlayer(o);
			}
		}
		p1.showPlayer(p2);
		p2.showPlayer(p1);
	}

	private void prepareCustomBattle(final Player customer, final Player challenged) {
		customer.getInventory().clear();
		challenged.getInventory().clear();
		customer.getInventory().setArmorContents((ItemStack[]) null);
		challenged.getInventory().setArmorContents((ItemStack[]) null);
		if (this.fullSoupOption.get(customer.getUniqueId())) {
			for (int i = 0; i < 36; ++i) {
				customer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MUSHROOM_SOUP) });
			}
			for (int i = 0; i < 36; ++i) {
				challenged.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MUSHROOM_SOUP) });
			}
		} else {
			for (int i = 1; i < 9; ++i) {
				customer.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
			}
			for (int i = 1; i < 9; ++i) {
				challenged.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
			}
		}
		final ItemBuilder builder = new ItemBuilder().type((Material) this.swordType.get(customer.getUniqueId()));
		if (this.sharpOption.get(customer.getUniqueId())) {
			builder.enchantment(Enchantment.DAMAGE_ALL, Integer.valueOf(1));
		}
		final ItemStack itemStack = builder.build();
		customer.getInventory().setItem(0, itemStack);
		challenged.getInventory().setItem(0, itemStack);
		final Material armour = this.armourType.get(customer.getUniqueId());
		if (armour == Material.LEATHER_CHESTPLATE) {
			customer.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
			customer.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			customer.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			customer.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
			challenged.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
			challenged.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			challenged.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			challenged.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		} else if (armour == Material.IRON_CHESTPLATE) {
			customer.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
			customer.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			customer.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
			customer.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
			challenged.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
			challenged.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			challenged.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
			challenged.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
		} else if (armour == Material.DIAMOND_CHESTPLATE) {
			customer.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
			customer.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			customer.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
			customer.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
			challenged.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
			challenged.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			challenged.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
			challenged.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		}
		if (this.recraftOption.get(customer.getUniqueId())) {
			if (this.recraftType.get(customer.getUniqueId()) == Material.RED_MUSHROOM) {
				customer.getInventory().setItem(13, new ItemStack(Material.BOWL, 64));
				customer.getInventory().setItem(14, new ItemStack(Material.RED_MUSHROOM, 64));
				customer.getInventory().setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 64));
				challenged.getInventory().setItem(13, new ItemStack(Material.BOWL, 64));
				challenged.getInventory().setItem(14, new ItemStack(Material.RED_MUSHROOM, 64));
				challenged.getInventory().setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 64));
			} else if (this.recraftType.get(customer.getUniqueId()) == Material.CACTUS) {
				customer.getInventory().setItem(13, new ItemStack(Material.BOWL, 64));
				customer.getInventory().setItem(14, new ItemStack(Material.CACTUS, 64));
				customer.getInventory().setItem(15, new ItemStack(Material.CACTUS, 64));
				challenged.getInventory().setItem(13, new ItemStack(Material.BOWL, 64));
				challenged.getInventory().setItem(14, new ItemStack(Material.CACTUS, 64));
				challenged.getInventory().setItem(15, new ItemStack(Material.CACTUS, 64));
			}
		}
		customer.updateInventory();
		challenged.updateInventory();
	}

	private void prepareNormalBattle(final Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents((ItemStack[]) null);
		for (int i = 1; i < 9; ++i) {
			p.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
		}
		if (YPvP.getPlugin().getPvpType() == YPvP.PvPType.FULLIRON) {
			ItemBuilder builder = new ItemBuilder().type(Material.IRON_HELMET);
			p.getInventory().setHelmet(builder.build());
			builder = new ItemBuilder().type(Material.IRON_CHESTPLATE);
			p.getInventory().setChestplate(builder.build());
			builder = new ItemBuilder().type(Material.IRON_LEGGINGS);
			p.getInventory().setLeggings(builder.build());
			builder = new ItemBuilder().type(Material.IRON_BOOTS);
			p.getInventory().setBoots(builder.build());
			builder = new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL,
					Integer.valueOf(1));
			p.getInventory().setItem(0, builder.build());
			builder = null;
		} else {
			ItemBuilder builder = new ItemBuilder().type(Material.WOOD_SWORD).enchantment(Enchantment.DAMAGE_ALL,
					Integer.valueOf(1));
			p.getInventory().setItem(0, builder.build());
			builder = null;
		}
		p.updateInventory();
	}

	public void clearRequests(final Player... players) {
		for (final Player p : players) {
			if (this.nextBattle == p.getUniqueId()) {
				this.nextBattle = null;
			}
			if (this.normalChallenge.containsKey(p.getUniqueId())) {
				this.normalChallenge.remove(p.getUniqueId());
			}
			if (this.customChallenge.containsKey(p.getUniqueId())) {
				this.customChallenge.remove(p.getUniqueId());
			}
		}
	}

	public void clearCustom(final Player... players) {
		for (final Player p : players) {
			this.armourType.remove(p.getUniqueId());
			this.swordType.remove(p.getUniqueId());
			this.recraftType.remove(p.getUniqueId());
			this.recraftOption.remove(p.getUniqueId());
			this.sharpOption.remove(p.getUniqueId());
			this.fullSoupOption.remove(p.getUniqueId());
		}
	}

	@Override
	public void quit(final Player p) {
		this.clearRequests(p);
		this.clearCustom(p);
		this.quitPlayer(p.getUniqueId());
	}
}
