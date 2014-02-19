package net.clashwars.cwcore.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class WarpsConfig extends Config {
	private YamlConfiguration	cfg;
	private final File			dir		= new File("plugins/CWCore/");
	private final File			file	= new File(dir + "/warps.yml");

	public WarpsConfig() {
	}

	@Override
	public void init() {
		try {
			dir.mkdirs();
			file.createNewFile();
			cfg = new YamlConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		try {
			cfg.load(file);

			cfg.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		try {
			cfg.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createWarp(String name, Location location) {
		cfg.set("Warps." + name + ".Location.World", location.getWorld().getName());
		cfg.set("Warps." + name + ".Location.X", location.getBlockX());
		cfg.set("Warps." + name + ".Location.Y", location.getBlockY());
		cfg.set("Warps." + name + ".Location.Z", location.getBlockZ());
		cfg.set("Warps." + name + ".Location.Yaw", location.getYaw());
		cfg.set("Warps." + name + ".Location.Pitch", location.getPitch());
		save();
	}

	public void deleteWarp(String name) {
		cfg.set("Warps." + name, null);
		save();
	}

	public List<String> getWarpNames() {
		return new ArrayList<String>(cfg.getConfigurationSection("Warps").getKeys(false));
	}

	public ConfigurationSection getWarps() {
		return cfg.getConfigurationSection("Warps");
	}

	public ConfigurationSection getWarp(String name) {
		return cfg.getConfigurationSection("Warps." + name);
	}

}
