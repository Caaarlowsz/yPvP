package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.event.HandlerList;
import java.util.Iterator;
import java.util.Queue;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.block.Block;
import java.util.HashMap;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Location;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.jnbt.DataException;
import java.io.IOException;
import java.io.File;
import br.com.weavenmc.ypvp.jnbt.Schematic;
import br.com.weavenmc.ypvp.yPvP;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import br.com.weavenmc.commons.core.permission.Group;
import java.util.UUID;
import java.util.ArrayList;
import br.com.weavenmc.commons.bukkit.worldedit.AsyncWorldEdit;
import br.com.weavenmc.ypvp.ability.Ability;

public class GladiatorAbility extends Ability
{
    private static AsyncWorldEdit asyncWorldEdit;
    private ArrayList<UUID> callingToBattle;
    
    static {
        GladiatorAbility.asyncWorldEdit = AsyncWorldEdit.getInstance();
    }
    
    public GladiatorAbility() {
        this.callingToBattle = new ArrayList<UUID>();
        this.setName("Gladiator");
        this.setHasItem(true);
        this.setGroupToUse(Group.PREMIUM);
        this.setIcon(Material.IRON_FENCE);
        this.setDescription(new String[] { "§7Puxe seu inimigo para um duelo", "§71v1 em uma arena nos c\u00e9us." });
        this.setPrice(80000);
        this.setTempPrice(8000);
    }
    
    @Override
    public void eject(final Player p) {
        if (this.callingToBattle.contains(p.getUniqueId())) {
            this.callingToBattle.remove(p.getUniqueId());
        }
    }
    
    @EventHandler
    public void onGladiatorListener(final PlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Player gladiator = event.getPlayer();
        if (this.hasKit(gladiator) && event.getRightClicked() instanceof Player && this.isItem(gladiator.getItemInHand())) {
            event.setCancelled(true);
            final Player target = (Player)event.getRightClicked();
            final Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(target.getUniqueId());
            if (gamer.getWarp().isProtected(target)) {
                gladiator.sendMessage("§5§lGLADIATOR§f O jogador est\u00e1 com prote\u00e7\u00e3o de spawn!");
                return;
            }
            if (this.callingToBattle.contains(target.getUniqueId()) || this.callingToBattle.contains(gladiator.getUniqueId())) {
                return;
            }
            this.callingToBattle.add(gladiator.getUniqueId());
            this.callingToBattle.add(target.getUniqueId());
            Schematic gladiatorArena = null;
            try {
                gladiatorArena = Schematic.getInstance().loadSchematic(new File(yPvP.getPlugin().getDataFolder(), "gladiator.schematic"));
            }
            catch (IOException | DataException ex3) {
                final Exception ex2;
                final Exception ex = ex2;
                ex.printStackTrace();
                gladiator.sendMessage("§5§lGLADIATOR§f N\u00e3o foi possivel criar a §9§lARENA!");
                this.eject(target);
                this.eject(gladiator);
                return;
            }
            new GladiatorBattle(gladiator, target, gladiatorArena, gladiator.getLocation().add(0.0, 100.0, 0.0));
            this.eject(target);
            this.eject(gladiator);
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        if (this.callingToBattle.contains(event.getPlayer().getUniqueId())) {
            this.callingToBattle.remove(event.getPlayer().getUniqueId());
        }
    }
    
    public void lookAt(final Player p, final Player target) {
        ((CraftPlayer)p).getHandle().setSpectatorTarget((Entity)((CraftPlayer)target).getHandle());
    }
    
    public class GladiatorBattle
    {
        private boolean started;
        private Player gladiator;
        private Player target;
        private Location gladiatorLocation;
        private Location targetLocation;
        private BukkitTask whiterTask;
        private BukkitTask endTask;
        private HashMap<Schematic.GladiatorBlock, Block> gladiatorBlocks;
        private Listener gladiatorListener;
        
        public GladiatorBattle(final Player gladiator, final Player target, final Schematic schematic, final Location loc) {
            this.started = false;
            this.gladiatorBlocks = new HashMap<Schematic.GladiatorBlock, Block>();
            this.gladiator = gladiator;
            this.target = target;
            this.gladiatorLocation = gladiator.getLocation();
            this.targetLocation = target.getLocation();
            Bukkit.getPluginManager().registerEvents(this.gladiatorListener = (Listener)new Listener() {
                @EventHandler(priority = EventPriority.LOWEST)
                public void onEntityDamage(final EntityDamageByEntityEvent event) {
                    if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                        final Player entity = (Player)event.getEntity();
                        final Player damager = (Player)event.getDamager();
                        if (GladiatorBattle.this.inBattle(entity) && GladiatorBattle.this.inBattle(damager)) {
                            return;
                        }
                        if (GladiatorBattle.this.inBattle(entity) && !GladiatorBattle.this.inBattle(damager)) {
                            event.setCancelled(true);
                        }
                        else if (!GladiatorBattle.this.inBattle(entity) && GladiatorBattle.this.inBattle(damager)) {
                            event.setCancelled(true);
                        }
                    }
                }
                
                @EventHandler
                public void onScape(final PlayerCommandPreprocessEvent event) {
                    if (GladiatorBattle.this.inBattle(event.getPlayer())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§5§lGLADIATOR§f Voc\u00ea n\u00e3o pode usar comandos durante uma §9§lBATALHA!");
                    }
                }
                
                @EventHandler(priority = EventPriority.LOWEST)
                public void onGladiatorAgain(final PlayerInteractEntityEvent event) {
                    if (GladiatorBattle.this.inBattle(event.getPlayer())) {
                        event.setCancelled(true);
                    }
                }
                
                @EventHandler
                public void onDeath(final PlayerDeathEvent event) {
                    if (!GladiatorBattle.this.inBattle(event.getEntity())) {
                        return;
                    }
                    GladiatorBattle.this.gladiator.teleport(GladiatorBattle.this.gladiatorLocation);
                    GladiatorBattle.this.target.teleport(GladiatorBattle.this.targetLocation);
                    GladiatorBattle.this.gladiator.removePotionEffect(PotionEffectType.WITHER);
                    GladiatorBattle.this.target.removePotionEffect(PotionEffectType.WITHER);
                    GladiatorBattle.this.destroy();
                }
                
                @EventHandler
                public void onQuit(final PlayerQuitEvent event) {
                    if (!GladiatorBattle.this.inBattle(event.getPlayer())) {
                        return;
                    }
                    GladiatorBattle.this.gladiator.teleport(GladiatorBattle.this.gladiatorLocation);
                    GladiatorBattle.this.target.teleport(GladiatorBattle.this.targetLocation);
                    GladiatorBattle.this.gladiator.removePotionEffect(PotionEffectType.WITHER);
                    GladiatorBattle.this.target.removePotionEffect(PotionEffectType.WITHER);
                    GladiatorBattle.this.destroy();
                }
                
                @EventHandler(priority = EventPriority.LOWEST)
                public void onPlayerMove(final PlayerMoveEvent event) {
                    if (!GladiatorBattle.this.started) {
                        return;
                    }
                    if (!GladiatorBattle.this.inBattle(event.getPlayer())) {
                        return;
                    }
                    if (!GladiatorBattle.this.gladiatorBlocks.values().contains(event.getTo().getBlock())) {
                        GladiatorBattle.this.gladiator.teleport(GladiatorBattle.this.gladiatorLocation);
                        GladiatorBattle.this.target.teleport(GladiatorBattle.this.targetLocation);
                        GladiatorBattle.this.destroy();
                    }
                }
            }, (Plugin)yPvP.getPlugin());
            this.callBattle(schematic, loc);
        }
        
        public void timeOut() {
            this.gladiator.teleport(this.gladiatorLocation);
            this.target.teleport(this.targetLocation);
            this.gladiator.removePotionEffect(PotionEffectType.WITHER);
            this.target.removePotionEffect(PotionEffectType.WITHER);
            this.destroy();
        }
        
        public Object callBattle(final Schematic schematic, Location loc) {
            final World world = loc.getWorld();
            final short[] blocks = schematic.getBlocks();
            final byte[] blockData = schematic.getData();
            final short length = schematic.getLenght();
            final short width = schematic.getWidth();
            final short height = schematic.getHeight();
            final Queue<Schematic.GladiatorBlock> queue = new ConcurrentLinkedQueue<Schematic.GladiatorBlock>();
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    for (int z = 0; z < length; ++z) {
                        final int index = y * width * length + z * width + x;
                        final Block block = new Location(world, x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
                        this.gladiatorBlocks.put(new Schematic.GladiatorBlock(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ(), blocks[index], blockData[index]), block);
                        if (block.getType() != Material.AIR) {
                            this.gladiatorBlocks.clear();
                            return this.callBattle(schematic, loc = loc.add(1.0, 0.0, 1.0));
                        }
                    }
                }
            }
            for (final Map.Entry<Schematic.GladiatorBlock, Block> entrie : this.gladiatorBlocks.entrySet()) {
                final Schematic.GladiatorBlock a = entrie.getKey();
                final Block b = entrie.getValue();
                b.setTypeIdAndData(a.getId(), a.getData(), true);
            }
            this.gladiator.setFallDistance(0.0f);
            this.gladiator.teleport(new Location(loc.getWorld(), (double)(loc.getBlockX() + 4), (double)(loc.getBlockY() + 2), (double)(loc.getBlockZ() + 4)));
            this.target.setFallDistance(0.0f);
            this.target.teleport(new Location(loc.getWorld(), (double)(loc.getBlockX() + 16), (double)(loc.getBlockY() + 2), (double)(loc.getBlockZ() + 16)));
            this.gladiator.sendMessage("§5§lGLADIATOR§f Voc\u00ea desafiou §9§l" + this.target.getName() + "§f para uma batalha!");
            this.target.sendMessage("§5§lGLADIATOR§f Voc\u00ea foi desafiado por §9§l" + this.gladiator.getName() + "§f para uma batalha!");
            this.whiterTask = new BukkitRunnable() {
                public void run() {
                    GladiatorBattle.this.gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 5));
                    GladiatorBattle.this.target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 5));
                }
            }.runTaskLater((Plugin)yPvP.getPlugin(), 2400L);
            this.endTask = new BukkitRunnable() {
                public void run() {
                    GladiatorBattle.this.timeOut();
                }
            }.runTaskLater((Plugin)yPvP.getPlugin(), 3600L);
            final boolean started = true;
            this.started = started;
            return started;
        }
        
        public boolean inBattle(final Player player) {
            return this.gladiator == player || this.target == player;
        }
        
        public void destroy() {
            HandlerList.unregisterAll(this.gladiatorListener);
            for (final Block b : this.gladiatorBlocks.values()) {
                b.setType(Material.AIR);
            }
            this.gladiatorBlocks.clear();
            if (this.whiterTask != null) {
                this.whiterTask.cancel();
            }
            if (this.endTask != null) {
                this.endTask.cancel();
            }
        }
    }
}
