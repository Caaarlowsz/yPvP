package br.com.weavenmc.ypvp.minigame;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import br.com.weavenmc.ypvp.Management;
import com.github.caaarlowsz.ymc.kitpvp.YPvP;

public class LocationManager extends Management {
	private FileConfiguration config;

	public LocationManager(final YPvP plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		this.config = this.getPlugin().getConfig();
	}

	public void saveLocation(final String name, final Location loc) {
		this.config.set("Locations." + name.toLowerCase() + ".x", (Object) loc.getX());
		this.config.set("Locations." + name.toLowerCase() + ".y", (Object) loc.getY());
		this.config.set("Locations." + name.toLowerCase() + ".z", (Object) loc.getZ());
		this.config.set("Locations." + name.toLowerCase() + ".yaw", (Object) loc.getYaw());
		this.config.set("Locations." + name.toLowerCase() + ".pitch", (Object) loc.getPitch());
		this.getPlugin().saveConfig();
	}

	public Location getLocation(final String name) {
		if (this.config.get("Locations." + name.toLowerCase()) != null) {
			final double x = this.config.getDouble("Locations." + name.toLowerCase() + ".x");
			final double y = this.config.getDouble("Locations." + name.toLowerCase() + ".y");
			final double z = this.config.getDouble("Locations." + name.toLowerCase() + ".z");
			final float yaw = (float) this.config.getInt("Locations." + name.toLowerCase() + ".yaw");
			final float pitch = (float) this.config.getInt("Locations." + name.toLowerCase() + ".pitch");
			return new Location((World) this.getServer().getWorlds().get(0), x, y, z, yaw, pitch);
		}
		return null;
	}

	@Override
	public void disable() {
		this.config = null;
	}

	public FileConfiguration getConfig() {
		return this.config;
	}
}
