package br.com.weavenmc.ypvp.minigame;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import java.util.Iterator;
import java.util.List;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;
import java.util.ArrayList;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import br.com.weavenmc.ypvp.gamer.Gamer;
import org.bukkit.Material;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.ypvp.managers.TeleportManager;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import org.bukkit.entity.Player;

public class SpawnMinigame extends Minigame
{
    public SpawnMinigame() {
        this.setName("Spawn");
        this.setOtherNames(new String[0]);
        this.setTopKillStreakMinigame(true);
    }
    
    @Override
    public void join(final Player p) {
        BossBarAPI.removeBar(p);
        if (!TeleportManager.getInstance().canJoin(p, this)) {
            return;
        }
        if (p.getAllowFlight() && !AdminMode.getInstance().isAdmin(p)) {
            p.setAllowFlight(false);
        }
        p.sendMessage("§9§lTELEPORTE§f Voc\u00ea foi teleportado para §3§lSpawn");
        final Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
        gamer.resetCombat();
        if (gamer.getWarp() != null) {
            gamer.getWarp().quit(p);
        }
        this.joinPlayer(p.getUniqueId());
        yPvP.getPlugin().getCooldownManager().removeCooldown(p);
        yPvP.getPlugin().getAbilityManager().getAbilities().stream().forEach(ability -> ability.eject(p));
        p.sendMessage("§8§lPROTE\u00c7\u00c3O§f Voc\u00ea §7§lRECEBEU§f sua prote\u00e7\u00e3o de spawn");
        gamer.setWarp(this);
        gamer.setAbility(yPvP.getPlugin().getAbilityManager().getNone());
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.setFireTicks(0);
        p.getActivePotionEffects().clear();
        p.getInventory().clear();
        this.teleport(p);
        this.protect(p);
        yPvP.getPlugin().getTournament().quitPlayer(p);
        p.getInventory().setArmorContents((ItemStack[])null);
        ItemBuilder builder = new ItemBuilder().type(Material.ENDER_CHEST).name("§b§lKits §7(Clique para Abrir)");
        p.getInventory().setItem(1, builder.build());
        builder = new ItemBuilder().type(Material.COMPASS).name("§e§lWarps §7(Clique para Abrir)");
        p.getInventory().setItem(2, builder.build());
        builder = new ItemBuilder().type(Material.SKULL_ITEM).durability(3).skin(p.getName()).name("§6§l" + gamer.getName() + " §7(Clique para Ver)");
        p.getInventory().setItem(4, builder.build());
        builder = new ItemBuilder().type(Material.DIAMOND).name("§b§lShop §7(Clique para Abrir)");
        p.getInventory().setItem(6, builder.build());
        builder = new ItemBuilder().type(Material.ENCHANTED_BOOK).name("§3§lTournament §7(Clique para Ver)");
        p.getInventory().setItem(7, builder.build());
        p.getInventory().setHeldItemSlot(1);
        p.updateInventory();
        yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        final Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
        if (gamer.getWarp() == this && gamer.getAbility().getName().equals("Nenhum")) {
            final ItemStack item = event.getItem();
            if (item != null) {
                if (item.getType() == Material.ENDER_CHEST) {
                    event.setCancelled(true);
                    this.openKitsMenu(p);
                }
                else if (item.getType() == Material.COMPASS) {
                    event.setCancelled(true);
                    this.openWarpsMenu(p);
                }
                else if (item.getType() == Material.SKULL_ITEM) {
                    event.setCancelled(true);
                    p.performCommand("account");
                }
                else if (item.getType() == Material.DIAMOND) {
                    event.setCancelled(true);
                    this.openStoreMenu(p);
                }
                else if (item.getType() == Material.ENCHANTED_BOOK) {
                    event.setCancelled(true);
                    p.performCommand("tournament");
                }
            }
        }
    }
    
    public void openWarpsMenu(final Player p) {
        final Inventory menu = Bukkit.createInventory((InventoryHolder)p, 9, "§bWarps");
        Minigame minigame = yPvP.getPlugin().getWarpManager().getWarp(FramesMinigame.class);
        ItemBuilder builder = new ItemBuilder().type(Material.GLASS).name("§b§lFPS").lore(new String[] { "§7Treine seu PvP com mais FPSs" }).amount(minigame.getPlaying());
        menu.setItem(0, builder.build());
        minigame = yPvP.getPlugin().getWarpManager().getWarp(BattleMinigame.class);
        builder = new ItemBuilder().type(Material.BLAZE_ROD).name("§b§l1v1").lore(new String[] { "§7Tire 1v1 justo com algu\u00e9m" }).amount(minigame.getPlaying());
        menu.setItem(1, builder.build());
        minigame = yPvP.getPlugin().getWarpManager().getWarp(LavaChallengeMinigame.class);
        builder = new ItemBuilder().type(Material.LAVA_BUCKET).name("§b§lLava Challenge").lore(new String[] { "§7Treine seus refils e recrafts", "§7completando os niveis do challenge." }).amount(minigame.getPlaying());
        menu.setItem(2, builder.build());
        minigame = yPvP.getPlugin().getWarpManager().getWarp(VoidChallengeMinigame.class);
        builder = new ItemBuilder().type(Material.BEDROCK).name("§b§lVoid Challenge").lore(new String[] { "§7Veja quanto tempo voc\u00ea tanka", "§7com o dano do void e receba", "§7moedas de acordo com o tempo." }).amount(minigame.getPlaying());
        menu.setItem(3, builder.build());
        builder = null;
        minigame = null;
        p.openInventory(menu);
    }
    
    public void openKitsMenu(final Player p) {
        BukkitPlayer bP = (BukkitPlayer)WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
        final Inventory menu = Bukkit.createInventory((InventoryHolder)p, 54, "§bKits");
        ItemBuilder builder = new ItemBuilder().type(Material.INK_SACK).name("§7P\u00e1gina anterior").durability(8);
        menu.setItem(0, builder.build());
        builder = new ItemBuilder().type(Material.WOOL).durability(3).name("§eSeus Kits").lore(new String[] { "Em breve!" });
        menu.setItem(2, builder.build());
        builder = new ItemBuilder().type(Material.WOOL).durability(5).name("§aKits Gratuitos do Mes").lore(new String[] { "Em breve!" });
        menu.setItem(3, builder.build());
        builder = new ItemBuilder().type(Material.DIAMOND).name("§bLoja de Kits");
        menu.setItem(4, builder.build());
        builder = new ItemBuilder().type(Material.WOOL).durability(14).name("§6Kits Favoritos").lore(new String[] { "Em breve!" });
        menu.setItem(5, builder.build());
        builder = new ItemBuilder().type(Material.WOOL).durability(11).name("§cTodos os Kits").lore(new String[] { "Em breve!" });
        menu.setItem(6, builder.build());
        builder = new ItemBuilder().type(Material.INK_SACK).durability(8).name("§7Pr\u00f3xima p\u00e1gina");
        menu.setItem(8, builder.build());
        for (int i = 9; i < 18; ++i) {
            builder = new ItemBuilder().type(Material.STAINED_GLASS_PANE).name("§e§l-").durability(4);
            menu.setItem(i, builder.build());
        }
        int i = 18;
        List<String> description = new ArrayList<String>();
        for (final Ability ability : yPvP.getPlugin().getAbilityManager().getAbilities()) {
            description.clear();
            if (bP.hasGroupPermission(Group.COPA) || bP.hasGroupPermission(ability.getGroupToUse()) || this.hasKit(bP, ability)) {
                description.add("§aVoc\u00ea possui este kit");
            }
            else {
                description.add("§cVoc\u00ea n\u00e3o possui este kit");
            }
            description.add("");
            String[] description2;
            for (int length = (description2 = ability.getDescription()).length, j = 0; j < length; ++j) {
                final String d = description2[j];
                description.add(d);
            }
            builder = new ItemBuilder().type(ability.getIcon()).name("§e§l" + ability.getName()).lore((List)description);
            menu.setItem(i, builder.build());
            ++i;
        }
        bP = null;
        builder = null;
        description = null;
        p.openInventory(menu);
    }
    
    public void openStoreMenu(final Player p) {
        BukkitPlayer bP = (BukkitPlayer)WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
        final Inventory menu = Bukkit.createInventory((InventoryHolder)p, 54, "§bLoja de Kits");
        ItemBuilder builder = new ItemBuilder().type(Material.INK_SACK).name("§7P\u00e1gina anterior").durability(8);
        menu.setItem(0, builder.build());
        builder = new ItemBuilder().type(Material.WOOL).durability(3).name("§eSeus Kits").lore(new String[] { "Em breve!" });
        menu.setItem(2, builder.build());
        builder = new ItemBuilder().type(Material.WOOL).durability(5).name("§aKits Gratuitos do Mes").lore(new String[] { "Em breve!" });
        menu.setItem(3, builder.build());
        builder = new ItemBuilder().type(Material.DIAMOND).name("§bLoja de Kits");
        menu.setItem(4, builder.build());
        builder = new ItemBuilder().type(Material.WOOL).durability(14).name("§6Kits Favoritos").lore(new String[] { "Em breve!" });
        menu.setItem(5, builder.build());
        builder = new ItemBuilder().type(Material.WOOL).durability(11).name("§cTodos os Kits").lore(new String[] { "Em breve!" });
        menu.setItem(6, builder.build());
        builder = new ItemBuilder().type(Material.INK_SACK).durability(8).name("§7Pr\u00f3xima p\u00e1gina");
        menu.setItem(8, builder.build());
        for (int i = 9; i < 18; ++i) {
            builder = new ItemBuilder().type(Material.STAINED_GLASS_PANE).name("§e§l-").durability(4);
            menu.setItem(i, builder.build());
        }
        for (int i = 0; i < 9; ++i) {
            builder = new ItemBuilder().type(Material.STAINED_GLASS_PANE).name("§e§l-").durability(4);
            menu.setItem(i, builder.build());
        }
        int i = 18;
        List<String> description = new ArrayList<String>();
        for (final Ability ability : yPvP.getPlugin().getAbilityManager().getAbilities()) {
            if (ability.getName().equalsIgnoreCase("pvp")) {
                continue;
            }
            description.clear();
            if (bP.hasGroupPermission(Group.COPA) || bP.hasGroupPermission(ability.getGroupToUse()) || this.hasKit(bP, ability)) {
                description.add("§aVoc\u00ea possui este kit.");
            }
            else {
                description.add("§cVoc\u00ea n\u00e3o possui este kit.");
            }
            description.add("");
            String[] description2;
            for (int length = (description2 = ability.getDescription()).length, j = 0; j < length; ++j) {
                final String d = description2[j];
                description.add(d);
            }
            description.add("");
            description.add("§b§lClique §besquerdo§b§l para alugar por §b3 dias");
            description.add("§b§lpor §b" + ability.getTempPrice() + " moedas!");
            description.add("");
            description.add("§e§lClique §edireito§e§l para comprar §epermanentemente");
            description.add("§e§lpor §e" + ability.getPrice() + " moedas!");
            builder = new ItemBuilder().type(ability.getIcon()).name("§e§l" + ability.getName()).lore((List)description);
            menu.setItem(i, builder.build());
            ++i;
        }
        bP = null;
        builder = null;
        description = null;
        p.openInventory(menu);
    }
    
    public boolean hasKit(final BukkitPlayer bP, final Ability ability) {
        return bP.hasPermission("pvpkit." + ability.getName().toLowerCase());
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player p = (Player)event.getWhoClicked();
            BukkitPlayer bP = (BukkitPlayer)WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
            ItemStack current = event.getCurrentItem();
            if (current != null) {
                if (event.getInventory().getName().equals("§bKits")) {
                    event.setCancelled(true);
                    if (current.getType() != Material.DIAMOND) {
                        for (final Ability ability : yPvP.getPlugin().getAbilityManager().getAbilities()) {
                            if (current.getType().equals((Object)ability.getIcon())) {
                                p.closeInventory();
                                p.performCommand("kit " + ability.getName());
                                return;
                            }
                        }
                    }
                    else {
                        this.openStoreMenu(p);
                    }
                }
                else if (event.getInventory().getName().equals("§bLoja de Kits")) {
                    event.setCancelled(true);
                    for (final Ability ability : yPvP.getPlugin().getAbilityManager().getAbilities()) {
                        if (current.getType().equals((Object)ability.getIcon())) {
                            if (event.getClick() == ClickType.LEFT) {
                                if (bP.getMoney() >= ability.getTempPrice()) {
                                    p.closeInventory();
                                    bP.removeMoney(ability.getTempPrice());
                                    bP.addPermission("pvpkit." + ability.getName().toLowerCase(), this.getTime("3d"));
                                    bP.save(new DataCategory[] { DataCategory.ACCOUNT, DataCategory.BALANCE });
                                    p.sendMessage("§b§lSHOP§f Parab\u00e9ns! Voc\u00ea §3§lALUGOU§f a habilidade §b§l" + ability.getName().toUpperCase() + "§f durante §3§l3 DIAS!");
                                }
                                else {
                                    p.closeInventory();
                                    p.sendMessage("§b§lSHOP§f Voc\u00ea precisa de mais §b§l" + (ability.getTempPrice() - bP.getMoney()) + " MOEDAS§f para §3§lALUGAR§f a habilidade §b§l" + ability.getName().toUpperCase());
                                }
                            }
                            else if (event.getClick() == ClickType.RIGHT) {
                                if (bP.getMoney() >= ability.getPrice()) {
                                    p.closeInventory();
                                    bP.removeMoney(ability.getPrice());
                                    bP.addPermission("pvpkit." + ability.getName().toLowerCase(), -1L);
                                    bP.save(new DataCategory[] { DataCategory.ACCOUNT, DataCategory.BALANCE });
                                    p.sendMessage("§b§lSHOP§f Parab\u00e9ns! Voc\u00ea §3§lCOMPROU§f a habilidade §b§l" + ability.getName().toUpperCase() + "§f com dura\u00e7\u00e3o §3§lETERNA!");
                                }
                                else {
                                    p.closeInventory();
                                    p.sendMessage("§b§lSHOP§f Voc\u00ea precisa de mais §b§l" + (ability.getPrice() - bP.getMoney()) + " MOEDAS§f para §3§lCOMPRAR§f a habilidade §b§l" + ability.getName().toUpperCase());
                                }
                            }
                            return;
                        }
                    }
                }
                else if (event.getInventory().getName().equals("§bWarps")) {
                    event.setCancelled(true);
                    if (current.getType() == Material.GLASS) {
                        p.closeInventory();
                        p.performCommand("fps");
                    }
                    else if (current.getType() == Material.BLAZE_ROD) {
                        p.closeInventory();
                        p.performCommand("1v1");
                    }
                    else if (current.getType() == Material.LAVA_BUCKET) {
                        p.closeInventory();
                        p.performCommand("warp lava");
                    }
                    else if (current.getType() == Material.BEDROCK) {
                        p.closeInventory();
                        p.performCommand("warp void");
                    }
                }
                current = null;
            }
            bP = null;
            p = null;
        }
    }
    
    public long getTime(final String time) {
        try {
            return StringTimeUtils.parseDateDiff(time, true);
        }
        catch (Exception ex) {
            return -1L;
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
            if (gamer.getWarp() == this && this.isProtected(p)) {
                event.setCancelled(true);
            }
            gamer = null;
            p = null;
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityAttack(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
            if (gamer.getWarp() == this) {
                if (!this.isProtected(p)) {
                    if (event.getDamager() instanceof Player) {
                        final Player t = (Player)event.getDamager();
                        final Gamer game = yPvP.getPlugin().getGamerManager().getGamer(t.getUniqueId());
                        if (game.getWarp() == this && this.isProtected(t)) {
                            event.setCancelled(false);
                            this.unprotect(t);
                            t.sendMessage("§8§lPROTE\u00c7\u00c3O§f Voc\u00ea §7§lPERDEU§f sua prote\u00e7\u00e3o de spawn");
                            if (game.getAbility().getName().equals("Nenhum")) {
                                t.performCommand("kit pvp");
                            }
                        }
                    }
                }
                else if (this.isProtected(p)) {
                    event.setCancelled(true);
                }
            }
            gamer = null;
            p = null;
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onSpawnMove(final PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
        if (gamer.getWarp() == this && this.isProtected(p) && p.getLocation().distance(yPvP.getPlugin().getLocationManager().getLocation("spawn")) > 15.0) {
            this.unprotect(p);
            p.sendMessage("§8§lPROTE\u00c7\u00c3O§f Voc\u00ea §7§lPERDEU§f sua prote\u00e7\u00e3o de spawn");
            if (gamer.getAbility().getName().equals("Nenhum")) {
                p.performCommand("kit pvp");
            }
        }
        gamer = null;
        p = null;
    }
    
    @Override
    public void quit(final Player p) {
        this.quitPlayer(p.getUniqueId());
        this.unprotect(p);
    }
}
