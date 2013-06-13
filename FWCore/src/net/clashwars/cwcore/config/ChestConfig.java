package net.clashwars.cwcore.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.LootChest;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChestConfig extends Config {
	private CWCore cwc;
	private YamlConfiguration cfg;
	private ConfigUtil cu;
	private final File			dir		= new File("plugins/CWCore/");
	private final File			file	= new File(dir + "/lootChests.yml");

	public ChestConfig(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public void init() {
		try {
			dir.mkdirs();
			file.createNewFile();
			cfg = new YamlConfiguration();
			cu = new ConfigUtil(cfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		try {
			cfg.load(file);
			cwc.getLootChests().clear();

			if (!cfg.isConfigurationSection("chests")) {
				cfg.createSection("chests");
				cfg.save(file);
			}

			for (String c : cfg.getConfigurationSection("chests").getKeys(false)) {
				String w = cu.getString("chests." + c + ".world", null);

				if (w == null)
					continue;

				World world = cwc.getPlugin().getServer().getWorld(w);

				if (world == null)
					continue;

				Integer x = cu.getObjectiveInt("chests." + c + ".x", null);
				Integer y = cu.getObjectiveInt("chests." + c + ".y", null);
				Integer z = cu.getObjectiveInt("chests." + c + ".z", null);

				if (x == null || y == null || z == null)
					continue;

				Block block = world.getBlockAt(x, y, z);

				if (block.getState() instanceof Chest) {
					LootChest lc = new LootChest(block);
					lc.setAcccessed(new HashSet<String>(cu.getStringList("chests." + c + ".accessed")));

					cwc.getLootChests().add(lc);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		try {
			cfg.set("chests", null);
			for (int i = 0; i < cwc.getLootChests().size(); i++) {
				LootChest lc = cwc.getLootChests().get(i);
				Chest c = lc.getChest();

				if (c == null)
					continue;

				Location loc = c.getLocation();

				cfg.set("chests." + Integer.toString(i) + ".world", loc.getWorld().getName());
				cfg.set("chests." + Integer.toString(i) + ".x", loc.getBlockX());
				cfg.set("chests." + Integer.toString(i) + ".y", loc.getBlockY());
				cfg.set("chests." + Integer.toString(i) + ".z", loc.getBlockZ());
				cfg.set("chests." + Integer.toString(i) + ".accessed", new ArrayList<String>(lc.getAccessed()));
			}

			cfg.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}