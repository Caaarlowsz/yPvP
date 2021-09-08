package br.com.weavenmc.ypvp.minigame;

import br.com.weavenmc.ypvp.ability.Ability;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import br.com.weavenmc.ypvp.gamer.Gamer;
import org.bukkit.enchantments.Enchantment;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.ypvp.managers.TeleportManager;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import org.bukkit.entity.Player;

public class FramesMinigame extends Minigame
{
    public FramesMinigame() {
        this.setName("Fps");
        this.setOtherNames(new String[] { "Frames" });
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
        p.sendMessage("§9§lTELEPORTE§f Voc\u00ea foi teleportado para §3§lFPS");
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
        this.teleport(p);
        this.protect(p);
        yPvP.getPlugin().getTournament().quitPlayer(p);
        p.getInventory().clear();
        p.getInventory().setArmorContents((ItemStack[])null);
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
            builder.enchantment(Enchantment.DAMAGE_ALL, Integer.valueOf(1));
            p.getInventory().setItem(0, builder.build());
            builder = null;
        }
        else {
            ItemBuilder builder = new ItemBuilder().type(Material.STONE_SWORD);
            builder.enchantment(Enchantment.DAMAGE_ALL, Integer.valueOf(1));
            p.getInventory().setItem(0, builder.build());
            builder = null;
        }
        ItemBuilder builder = new ItemBuilder().type(Material.BOWL).amount(64);
        p.getInventory().setItem(13, builder.build());
        builder = new ItemBuilder().type(Material.RED_MUSHROOM).amount(64);
        p.getInventory().setItem(14, builder.build());
        builder = new ItemBuilder().type(Material.BROWN_MUSHROOM).amount(64);
        p.getInventory().setItem(15, builder.build());
        p.updateInventory();
        yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
        builder = null;
    }
    
    @Override
    public void quit(final Player p) {
        this.quitPlayer(p.getUniqueId());
        this.unprotect(p);
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
        if (gamer.getWarp() == this && this.isProtected(p)) {
            Location fps = yPvP.getPlugin().getLocationManager().getLocation("fps");
            if (fps != null) {
                if (p.getLocation().distance(fps) > 5.0) {
                    this.unprotect(p);
                    p.sendMessage("§8§lPROTE\u00c7\u00c3O§f Voc\u00ea §7§lPERDEU§f sua prote\u00e7\u00e3o de spawn");
                }
                fps = null;
            }
        }
        gamer = null;
        p = null;
    }
}
