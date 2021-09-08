package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import java.util.List;
import br.com.weavenmc.ypvp.ability.Ability;

public class KangarooAbility extends Ability
{
    private final List<Player> kang;
    
    public KangarooAbility() {
        this.kang = new ArrayList<Player>();
        this.setName("Kangaroo");
        this.setHasItem(true);
        this.setGroupToUse(Group.MEMBRO);
        this.setIcon(Material.FIREWORK);
        this.setDescription(new String[] { "§7Tenha a habilidade de double-jump", "§7e de se mover mais r\u00e1pido." });
        this.setPrice(70000);
        this.setTempPrice(0);
    }
    
    @Override
    public void eject(final Player p) {
        if (this.kang.contains(p)) {
            this.kang.remove(p);
        }
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if ((this.hasKit(p) & event.getAction() != Action.PHYSICAL) && p.getItemInHand().getType() == this.getIcon()) {
            event.setCancelled(true);
            if (!this.kang.contains(p)) {
                if (!this.inCooldown(p)) {
                    Vector velocity = p.getEyeLocation().getDirection();
                    if (p.isSneaking()) {
                        velocity = velocity.multiply(1.8f).setY(0.5f);
                    }
                    else {
                        velocity = velocity.multiply(0.5f).setY(1.0f);
                    }
                    p.setFallDistance(-1.0f);
                    p.setVelocity(velocity);
                    this.kang.add(p);
                    velocity = null;
                }
                else {
                    this.sendCooldown(p);
                }
            }
        }
        p = null;
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        if (this.kang.contains(event.getPlayer())) {
            this.kang.remove(event.getPlayer());
        }
    }
    
    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            if (this.hasKit(p) && !event.isCancelled() && event.getDamager() instanceof Player) {
                this.addCooldown(p, 7);
            }
            p = null;
        }
    }
    
    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        final Player p = event.getPlayer();
        if (!this.hasKit(p)) {
            return;
        }
        if (!this.kang.contains(p)) {
            return;
        }
        if (!p.isOnGround()) {
            return;
        }
        this.kang.remove(p);
    }
    
    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player p = (Player)event.getEntity();
            if (this.hasKit(p) && event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getDamage() > 12.0) {
                event.setDamage(12.0);
            }
        }
    }
}
