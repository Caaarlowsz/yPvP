package br.com.weavenmc.ypvp.tournament;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;

public class TournamentBattle {
	private UUID player1;
	private UUID player2;

	public TournamentBattle(final Player player1, final Player player2) {
		this.player1 = player1.getUniqueId();
		this.player2 = player2.getUniqueId();
		this.prepareToBattle(player1);
		this.prepareToBattle(player2);
	}

	public Player getBattlePlayer1() {
		return Bukkit.getPlayer(this.player1);
	}

	public Player getBattlePlayer2() {
		return Bukkit.getPlayer(this.player2);
	}

	public boolean isBattlePlayer(final UUID uuid) {
		return this.player1 == uuid || this.player2 == uuid;
	}

	private void prepareToBattle(final Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents((ItemStack[]) null);
		p.setHealth(20.0);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.getActivePotionEffects().clear();
		for (int i = 0; i < 36; ++i) {
			p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MUSHROOM_SOUP) });
		}
		ItemBuilder builder = new ItemBuilder().type(Material.DIAMOND_SWORD).name("§1§lTournament Battle")
				.enchantment(Enchantment.DAMAGE_ALL, Integer.valueOf(1)).unbreakable();
		p.getInventory().setItem(0, builder.build());
		builder = new ItemBuilder().type(Material.IRON_HELMET);
		p.getInventory().setHelmet(builder.build());
		builder = new ItemBuilder().type(Material.IRON_CHESTPLATE);
		p.getInventory().setChestplate(builder.build());
		builder = new ItemBuilder().type(Material.IRON_LEGGINGS);
		p.getInventory().setLeggings(builder.build());
		builder = new ItemBuilder().type(Material.IRON_BOOTS);
		p.getInventory().setBoots(builder.build());
		p.updateInventory();
		builder = null;
	}
}
