package br.com.weavenmc.ypvp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.api.title.TitleAPI;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.minigame.BattleMinigame;
import br.com.weavenmc.ypvp.minigame.FramesMinigame;
import br.com.weavenmc.ypvp.minigame.LavaChallengeMinigame;
import br.com.weavenmc.ypvp.minigame.Minigame;
import br.com.weavenmc.ypvp.minigame.SpawnMinigame;

public class GameCommand implements CommandClass {
	@CommandFramework.Command(name = "espectar", aliases = { "spec" }, groupToUse = Group.INVESTIDOR)
	public void espectar(final BukkitCommandSender sender, final String label, final String[] args) {
		if (sender.isPlayer()) {
			final Player p = sender.getPlayer();
			final Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (args.length == 0) {
				p.sendMessage("§b§lESPECTAR§f Utilize: /espectar <player>");
			} else {
				final Player t = Bukkit.getPlayer(args[0]);
				if (t != null) {
					if (!t.getUniqueId().equals(p.getUniqueId())) {
						if (AdminMode.getInstance().isAdmin(p)) {
							final Gamer tGamer = yPvP.getPlugin().getGamerManager().getGamer(t.getUniqueId());
							if (tGamer.getWarp().getName().equals("1v1")) {
								final BattleMinigame battle = (BattleMinigame) tGamer.getWarp();
								if (battle.isBattling(t)) {
									gamer.setSpectator(t.getUniqueId());
									final Player battling = battle.getCurrentBattlePlayer(t);
									for (final Player o : Bukkit.getOnlinePlayers()) {
										if (p.getUniqueId().equals(o.getUniqueId())) {
											continue;
										}
										p.hidePlayer(o);
									}
									p.teleport((Entity) t);
									p.showPlayer(t);
									p.showPlayer(battling);
									p.sendMessage("§b§lESPECTAR§f Voc\u00ea agora est\u00e1 espectando a luta.");
								} else {
									p.sendMessage("§b§lESPECTAR§f O player n\u00e3o est\u00e1 em 1v1.");
								}
							} else {
								p.sendMessage("§b§lESPECTAR§f Voce so pode espectar jogadores em 1v1.");
							}
						} else {
							p.sendMessage("§b§lESPECTAR§f Voce precisa estar no Modo Admin.");
						}
					} else {
						p.sendMessage("§b§lESPECTAR§f Indique outro player.");
					}
				} else {
					p.sendMessage("§b§lESPECTAR§f O player " + args[0] + " nao esta online.");
				}
			}
		}
	}

	@CommandFramework.Command(name = "kit")
	public void kit(final BukkitCommandSender sender, final String label, final String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (args.length == 0) {
				p.sendMessage("§b§lKITS§f Utilize: /kit <kit>");
			} else if (gamer.getWarp().getName().equals("Spawn")) {
				if (gamer.getAbility().getName().equals("Nenhum")) {
					final Ability ability = yPvP.getPlugin().getAbilityManager().getAbility(args[0]);
					if (ability != null) {
						if (bP.hasGroupPermission(Group.COPA) || bP.hasGroupPermission(ability.getGroupToUse())
								|| this.hasKit(bP, ability)) {
							gamer.setAbility(ability);
							this.construct(p, ability);
							p.sendMessage("§b§lKITS§f Voc\u00ea selecionou o kit §3§l" + ability.getName());
							TitleAPI.setTitle(p, "§bKit " + ability.getName(), "§fescolhido com sucesso");
						} else {
							p.sendMessage("§b§lKITS§f Voc\u00ea n\u00e3o possui o kit §3§l"
									+ ability.getName().toUpperCase() + "§f!");
						}
					} else {
						p.sendMessage("§b§lKITS§f O kit " + args[0] + " n\u00e3o existe!");
					}
				} else {
					p.sendMessage("§b§lKITS§f Voc\u00ea j\u00e1 est\u00e1 usando um kit!");
				}
			} else {
				p.sendMessage("§b§lKITS§f Voc\u00ea s\u00f3 pode usar kits no Spawn!");
			}
			gamer = null;
			bP = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	public boolean hasKit(final BukkitPlayer bP, final Ability ability) {
		return bP.hasPermission("pvpkit." + ability.getName().toLowerCase());
	}

	@CommandFramework.Command(name = "set", aliases = { "setwarp" }, groupToUse = Group.DIRETOR)
	public void set(final BukkitCommandSender sender, final String label, final String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			if (args.length == 0) {
				p.sendMessage("§3§lWARPS§f Utilize: /setwarp <warp>");
			} else {
				yPvP.getPlugin().getLocationManager().saveLocation(args[0], p.getLocation());
				p.sendMessage("§3§lWARPS§f Voc\u00ea setou a warp §b§l" + args[0] + "§f.");
			}
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@CommandFramework.Command(name = "spawn")
	public void spawn(final BukkitCommandSender sender, final String label, final String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			yPvP.getPlugin().getWarpManager().getWarp(SpawnMinigame.class).join(p);
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@CommandFramework.Command(name = "score", aliases = { "sidebar", "scoreboard" })
	public void score(final BukkitCommandSender sender, final String label, final String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			Sidebar sidebar = gamer.getSidebar();
			if (sidebar != null) {
				if (sidebar.isHided()) {
					sidebar.show();
					yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
					p.sendMessage("§6§lSCOREBOARD§f Voc\u00ea §e§lATIVOU§f a Scoreboard!");
				} else {
					sidebar.hide();
					p.sendMessage("§6§lSCOREBOARD§f Voc\u00ea §e§lDESATIVOU§f a Scoreboard!");
				}
			} else {
				yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
				p.sendMessage("§6§lSCOREBOARD§f Voc\u00ea §e§lATIVOU§f a Scoreboard!");
			}
			sidebar = null;
			gamer = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@CommandFramework.Command(name = "fps")
	public void frames(final BukkitCommandSender sender, final String label, final String[] args) {
		if (sender.isPlayer()) {
			yPvP.getPlugin().getWarpManager().getWarp(FramesMinigame.class).join(sender.getPlayer());
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@CommandFramework.Command(name = "1v1")
	public void battle(final BukkitCommandSender sender, final String label, final String[] args) {
		if (sender.isPlayer()) {
			yPvP.getPlugin().getWarpManager().getWarp(BattleMinigame.class).join(sender.getPlayer());
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@CommandFramework.Command(name = "lava")
	public void lava(final BukkitCommandSender sender, final String label, final String[] args) {
		if (sender.isPlayer()) {
			yPvP.getPlugin().getWarpManager().getWarp(LavaChallengeMinigame.class).join(sender.getPlayer());
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@CommandFramework.Command(name = "warp", aliases = { "minigame" })
	public void warp(final BukkitCommandSender sender, final String label, final String[] args) {
		if (sender.isPlayer()) {
			final Player p = sender.getPlayer();
			if (args.length == 0) {
				p.sendMessage("§3§lWARPS§f Utilize: §b§l/warp§f <nome>");
			} else {
				Minigame minigame = yPvP.getPlugin().getWarpManager().getWarp(args[0]);
				if (minigame != null) {
					minigame.join(p);
					minigame = null;
				} else {
					p.sendMessage("§9§lTELEPORTE§f Esta warp n\u00e3o existe!");
				}
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	public void construct(final Player p, final Ability ability) {
		p.getInventory().clear();
		p.getInventory().setArmorContents((ItemStack[]) null);
		for (int i = 0; i < 36; ++i) {
			p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MUSHROOM_SOUP) });
		}
		if (yPvP.getPlugin().getPvpType() == yPvP.PvPType.FULLIRON) {
			ItemBuilder builder = new ItemBuilder().type(Material.IRON_HELMET);
			p.getInventory().setHelmet(builder.build());
			builder = new ItemBuilder().type(Material.IRON_CHESTPLATE);
			p.getInventory().setChestplate(builder.build());
			builder = new ItemBuilder().type(Material.IRON_LEGGINGS);
			p.getInventory().setLeggings(builder.build());
			builder = new ItemBuilder().type(Material.IRON_BOOTS);
			p.getInventory().setBoots(builder.build());
			builder = new ItemBuilder().type(Material.DIAMOND_SWORD);
			if (ability.getName().equals("PvP")) {
				builder.enchantment(Enchantment.DAMAGE_ALL, Integer.valueOf(1));
			}
			p.getInventory().setItem(0, builder.build());
			if (ability.isHasItem()) {
				builder = new ItemBuilder().name("§e§l" + ability.getName()).type(ability.getIcon());
				p.getInventory().setItem(1, builder.build());
			}
			builder = null;
		} else {
			ItemBuilder builder = new ItemBuilder().type(Material.STONE_SWORD);
			if (ability.getName().equals("PvP")) {
				builder.enchantment(Enchantment.DAMAGE_ALL, Integer.valueOf(1));
			}
			p.getInventory().setItem(0, builder.build());
			if (ability.isHasItem()) {
				builder = new ItemBuilder().name("§e§l" + ability.getName()).type(ability.getIcon());
				p.getInventory().setItem(1, builder.build());
			}
			builder = null;
		}
		ItemBuilder builder = new ItemBuilder().type(Material.BOWL).amount(32);
		p.getInventory().setItem(13, builder.build());
		builder = new ItemBuilder().type(Material.RED_MUSHROOM).amount(32);
		p.getInventory().setItem(14, builder.build());
		builder = new ItemBuilder().type(Material.BROWN_MUSHROOM).amount(32);
		p.getInventory().setItem(15, builder.build());
		builder = new ItemBuilder().type(Material.COMPASS).name("§3§lBussola");
		p.getInventory().setItem(8, builder.build());
		p.updateInventory();
		builder = null;
	}

	@CommandFramework.Completer(name = "kit")
	public List<String> kitcompleter(final BukkitCommandSender sender, final String label, final String[] args) {
		final List<String> list = new ArrayList<String>();
		Player p = sender.getPlayer();
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		if (args.length == 1) {
			for (final Ability ability : yPvP.getPlugin().getAbilityManager().getAbilities()) {
				if (bP.hasGroupPermission(ability.getGroupToUse()) || bP.getData(DataType.PLAYER_PERMISSIONS).asList()
						.contains("pvpkit." + ability.getName().toLowerCase())) {
					if (args[0].toLowerCase().startsWith(ability.getName().substring(0, 1))) {
						list.add(ability.getName());
					} else {
						list.add(ability.getName());
					}
				}
			}
		}
		bP = null;
		p = null;
		return list;
	}
}
