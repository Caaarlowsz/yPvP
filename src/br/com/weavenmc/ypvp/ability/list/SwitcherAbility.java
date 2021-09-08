package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Location;
import br.com.weavenmc.ypvp.gamer.Gamer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.metadata.FixedMetadataValue;
import br.com.weavenmc.ypvp.yPvP;
import org.bukkit.entity.Egg;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class SwitcherAbility extends Ability
{
    public SwitcherAbility() {
        this.setName("Switcher");
        this.setHasItem(true);
        this.setGroupToUse(Group.MEMBRO);
        this.setIcon(Material.EGG);
        this.setDescription(new String[] { "§7Lan\u00e7e sua bolinha em seu oponente", "§7e troque de lugar com ele." });
        this.setPrice(45000);
        this.setTempPrice(2500);
    }
    
    @Override
    public void eject(final Player p) {
    }
    
    @EventHandler
    public void onSwticher(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (this.hasKit(p) && this.isItem(p.getItemInHand()) && event.getAction().name().contains("RIGHT")) {
            event.setCancelled(true);
            p.updateInventory();
            if (!this.inCooldown(p)) {
                this.addCooldown(p, 12);
                final Egg egg = (Egg)p.launchProjectile((Class)Egg.class);
                egg.setMetadata("Switcher", (MetadataValue)new FixedMetadataValue((Plugin)yPvP.getPlugin(), (Object)p.getUniqueId()));
                egg.setShooter((ProjectileSource)p);
                egg.setVelocity(egg.getVelocity().multiply(2));
            }
            else {
                this.sendCooldown(p);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getDamager().hasMetadata("Switcher") && event.getDamager() instanceof Egg) {
            final Egg egg = (Egg)event.getDamager();
            if (egg.getShooter() != null && egg.getShooter() instanceof Player) {
                final Player shooter = (Player)egg.getShooter();
                if (event.getEntity() instanceof Player) {
                    final Player entity = (Player)event.getEntity();
                    final Gamer gamer = this.gamer(entity);
                    if (!gamer.getWarp().isProtected(entity)) {
                        final Location comeHereBaby = shooter.getLocation();
                        final Location iBeThere = entity.getLocation();
                        shooter.teleport(iBeThere);
                        entity.teleport(comeHereBaby);
                    }
                }
            }
        }
    }
}
