package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.EventPriority;
import br.com.weavenmc.ypvp.gamer.Gamer;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.Iterator;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.HashMap;
import br.com.weavenmc.ypvp.ability.Ability;

public class AjninAbility extends Ability
{
    private final HashMap<UUID, Player> ajnin;
    
    public AjninAbility() {
        this.ajnin = new HashMap<UUID, Player>();
        this.setName("Ajnin");
        this.setHasItem(false);
        this.setGroupToUse(Group.PREMIUM);
        this.setIcon(Material.NETHER_STAR);
        this.setDescription(new String[] { "§7Ao hitar seu oponente agache-se e", "§7teleporte ele para voc\u00ea." });
        this.setPrice(70000);
        this.setTempPrice(6000);
    }
    
    @Override
    public void eject(final Player p) {
        if (this.ajnin.containsKey(p.getUniqueId())) {
            this.ajnin.remove(p.getUniqueId());
        }
        for (final UUID uuid : this.ajnin.keySet()) {
            if (!this.ajnin.get(uuid).equals(p)) {
                continue;
            }
            this.ajnin.remove(uuid);
            break;
        }
    }
    
    @EventHandler
    public void onAjnin(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player e = (Player)event.getEntity();
            Player d = (Player)event.getDamager();
            if (this.hasKit(d) && !event.isCancelled()) {
                this.ajnin.put(d.getUniqueId(), e);
            }
            e = null;
            d = null;
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSneak(final PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        if (!event.isSneaking() && this.hasKit(p)) {
            if (this.ajnin.containsKey(p.getUniqueId())) {
                Player t = this.ajnin.get(p.getUniqueId());
                if (t != null && t.isOnline()) {
                    Gamer gamer = this.gamer(t);
                    if (!gamer.getWarp().isProtected(t)) {
                        if (!this.inCooldown(p)) {
                            if (p.getLocation().distance(t.getLocation()) <= 70.0) {
                                this.addCooldown(p, 14);
                                t.teleport((Entity)p);
                            }
                            else {
                                p.sendMessage("§5§lAJNIN§f O \u00faltimo jogador est\u00e1 §9§lMUITO LONGE");
                            }
                        }
                        else {
                            this.sendCooldown(p);
                        }
                    }
                    else {
                        p.sendMessage("§5§lAJNIN§f O \u00faltimo jogador est\u00e1 no §9§lSPAWN");
                    }
                    gamer = null;
                    t = null;
                }
                else {
                    p.sendMessage("§5§lAJNIN§f O \u00faltimo jogador est\u00e1 §9§lOFFLINE");
                }
            }
            else {
                p.sendMessage("§5§lAJNIN§f Voc\u00ea ainda n\u00e3o §9§lHITOU§f ningu\u00e9m!");
            }
        }
        p = null;
    }
}
