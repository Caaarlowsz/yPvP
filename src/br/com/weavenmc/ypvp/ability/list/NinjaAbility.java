package br.com.weavenmc.ypvp.ability.list;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;

public class NinjaAbility extends Ability {
	private HashMap<UUID, UUID> ninjaMap;

	public NinjaAbility() {
		this.ninjaMap = new HashMap<UUID, UUID>();
		this.setName("Ninja");
		this.setHasItem(false);
		this.setGroupToUse(Group.MEMBRO);
		this.setIcon(Material.EMERALD);
		this.setDescription(new String[] { "§7Ao hitar seu oponente agache-se e", "§7teleporte-se para ele." });
		this.setPrice(70000);
		this.setTempPrice(0);
	}

	@Override
	public void eject(final Player p) {
		if (this.ninjaMap.containsKey(p.getUniqueId())) {
			this.ninjaMap.remove(p.getUniqueId());
		}
	}

	@EventHandler
	public void onEntityDamageListener(final EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			final Player player = (Player) event.getEntity();
			if (!this.hasKit(player)) {
				return;
			}
			if (event.isCancelled()) {
				return;
			}
			this.ninjaMap.put(player.getUniqueId(), ((Player) event.getDamager()).getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onToggleSneak(final PlayerToggleSneakEvent event) {
		if (event.isSneaking()) {
			final Player p = event.getPlayer();
			if (!this.hasKit(p)) {
				return;
			}
			if (!this.ninjaMap.containsKey(p.getUniqueId())) {
				p.sendMessage("§5§lNINJA§f Voc\u00ea ainda n\u00e3o §9§lHITOU§f ningu\u00e9m");
				return;
			}
			final Player last = Bukkit.getPlayer((UUID) this.ninjaMap.get(p.getUniqueId()));
			if (last == null) {
				p.sendMessage("§5§lNINJA§f O player n\u00e3o est\u00e1 §9§lONLINE!");
				return;
			}
			final Gamer gamer = this.gamer(last);
			if (gamer.getWarp().isProtected(last)) {
				p.sendMessage("§5§lNINJA§f O player est\u00e1 no §9§lSPAWN!");
				return;
			}
			final double distance = p.getLocation().distance(last.getLocation());
			if (distance > 70.0) {
				p.sendMessage("§5§lNINJA§f O player est\u00e1 §9§lMUITO LONGE! §b(" + (int) distance
						+ " blocos §3[minimo 70]§b)");
				return;
			}
			if (!this.inCooldown(p)) {
				this.addCooldown(p, 10);
				p.teleport((Entity) last);
			} else {
				this.sendCooldown(p);
			}
		}
	}
}
