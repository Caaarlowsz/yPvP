package br.com.weavenmc.ypvp.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;

public abstract class Ability implements Listener {
	private String name;
	private int price;
	private int tempPrice;
	private Group groupToUse;
	private double cooldownTime;
	private Material icon;
	private String[] description;
	private boolean hasItem;

	public abstract void eject(final Player p0);

	public boolean inCooldown(final Player p) {
		return yPvP.getPlugin().getCooldownManager().hasCooldown(p);
	}

	public void sendCooldown(final Player p) {
		final String millis = StringTimeUtils.toMillis(yPvP.getPlugin().getCooldownManager().getCooldown(p));
		p.sendMessage("§6§lCOOLDOWN§f Aguarde mais §e§l" + millis + "§f para usar novamente!");
	}

	public void addCooldown(final Player p, final int cooldown) {
		yPvP.getPlugin().getCooldownManager().addCooldown(p, cooldown);
	}

	public Gamer gamer(final Player p) {
		return yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
	}

	public boolean hasKit(final Player p) {
		final Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		return gamer.getAbility() == this;
	}

	public List<Location> circle(final Location loc, final int radius, final int height, final boolean hollow,
			final boolean sphere, final int plusY) {
		final List<Location> circleblocks = new ArrayList<Location>();
		final int cx = loc.getBlockX();
		final int cy = loc.getBlockY();
		final int cz = loc.getBlockZ();
		for (int x = cx - radius; x <= cx + radius; ++x) {
			for (int z = cz - radius; z <= cz + radius; ++z) {
				for (int y = sphere ? (cy - radius) : cy; y < (sphere ? (cy + radius) : (cy + height)); ++y) {
					final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z)
							+ (sphere ? ((cy - y) * (cy - y)) : 0);
					if (dist < radius * radius && (!hollow || dist >= (radius - 1) * (radius - 1))) {
						final Location l = new Location(loc.getWorld(), (double) x, (double) (y + plusY), (double) z);
						circleblocks.add(l);
					}
				}
			}
		}
		return circleblocks;
	}

	public boolean isItem(final ItemStack stack) {
		return stack != null && this.icon != null && stack.getType() == this.icon;
	}

	public String getName() {
		return this.name;
	}

	public int getPrice() {
		return this.price;
	}

	public int getTempPrice() {
		return this.tempPrice;
	}

	public Group getGroupToUse() {
		return this.groupToUse;
	}

	public double getCooldownTime() {
		return this.cooldownTime;
	}

	public Material getIcon() {
		return this.icon;
	}

	public String[] getDescription() {
		return this.description;
	}

	public boolean isHasItem() {
		return this.hasItem;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPrice(final int price) {
		this.price = price;
	}

	public void setTempPrice(final int tempPrice) {
		this.tempPrice = tempPrice;
	}

	public void setGroupToUse(final Group groupToUse) {
		this.groupToUse = groupToUse;
	}

	public void setCooldownTime(final double cooldownTime) {
		this.cooldownTime = cooldownTime;
	}

	public void setIcon(final Material icon) {
		this.icon = icon;
	}

	public void setDescription(final String[] description) {
		this.description = description;
	}

	public void setHasItem(final boolean hasItem) {
		this.hasItem = hasItem;
	}
}
