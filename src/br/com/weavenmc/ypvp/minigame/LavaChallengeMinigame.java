package br.com.weavenmc.ypvp.minigame;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;

public class LavaChallengeMinigame extends Minigame {
	public LavaChallengeMinigame() {
		this.setName("Lava");
		this.setOtherNames(new String[] { "Challenge" });
		this.setTopKillStreakMinigame(false);
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
		p.sendMessage("�9�lTELEPORTE�f Voc\u00ea foi teleportado para �3�lLava Challenge");
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
		p.getInventory().setArmorContents((ItemStack[]) null);
		p.getInventory().clear();
		for (int i = 0; i < 36; ++i) {
			p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MUSHROOM_SOUP) });
		}
		p.getInventory().setItem(0, new ItemBuilder().type(Material.STONE_SWORD).name("�6�lLava Challenge").build());
		p.updateInventory();
		this.teleport(p);
		YPvP.getPlugin().getScoreboardManager().createScoreboard(p);
	}

	@EventHandler
	public void onDamage(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Gamer gamer = YPvP.getPlugin().getGamerManager().getGamer(((Player) event.getEntity()).getUniqueId());
			if (gamer.getWarp() == this) {
				EntityDamageEvent.DamageCause cause = event.getCause();
				if (cause != EntityDamageEvent.DamageCause.FIRE && cause != EntityDamageEvent.DamageCause.FIRE_TICK
						&& cause != EntityDamageEvent.DamageCause.LAVA) {
					event.setCancelled(true);
				}
				cause = null;
			}
			gamer = null;
		}
	}

	@Override
	public void quit(final Player p) {
		this.quitPlayer(p.getUniqueId());
		this.unprotect(p);
	}
}
