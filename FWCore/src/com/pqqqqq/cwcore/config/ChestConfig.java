package com.pqqqqq.cwcore.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;

import com.pqqqqq.cwcore.DungeonChest;
import com.pqqqqq.cwcore.CWCore;

public class ChestConfig extends Config {
	private CWCore				cwc;
	private YamlConfiguration	cfg;
	private final File			dir		= new File("plugins/CWCore/");
	private final File			file	= new File(dir + "/dungeon-chests.yml");

	public ChestConfig(CWCore cwc) {
		this.cwc = cwc;
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
			cwc.getDungeonChests().clear();

			if (!cfg.isConfigurationSection("chests")) {
				cfg.createSection("chests");
				cfg.save(file);
			}

			for (String c : cfg.getConfigurationSection("chests").getKeys(false)) {
				String w = ConfigUtil.getString(cfg, file, "chests." + c + ".world", null);

				if (w == null)
					continue;

				World world = cwc.getPlugin().getServer().getWorld(w);

				if (world == null)
					continue;

				Integer x = ConfigUtil.getObjectiveInt(cfg, file, "chests." + c + ".x", null);
				Integer y = ConfigUtil.getObjectiveInt(cfg, file, "chests." + c + ".y", null);
				Integer z = ConfigUtil.getObjectiveInt(cfg, file, "chests." + c + ".z", null);

				if (x == null || y == null || z == null)
					continue;

				Block block = world.getBlockAt(x, y, z);

				if (block.getState() instanceof Chest) {
					DungeonChest dc = new DungeonChest(block);
					dc.setAcccessed(new HashSet<String>(ConfigUtil.getStringList(cfg, file, "chests." + c + ".accessed")));

					cwc.getDungeonChests().add(dc);
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
			for (int i = 0; i < cwc.getDungeonChests().size(); i++) {
				DungeonChest dc = cwc.getDungeonChests().get(i);
				Chest c = dc.getChest();

				if (c == null)
					continue;

				Location loc = c.getLocation();

				cfg.set("chests." + Integer.toString(i) + ".world", loc.getWorld().getName());
				cfg.set("chests." + Integer.toString(i) + ".x", loc.getBlockX());
				cfg.set("chests." + Integer.toString(i) + ".y", loc.getBlockY());
				cfg.set("chests." + Integer.toString(i) + ".z", loc.getBlockZ());
				cfg.set("chests." + Integer.toString(i) + ".accessed", new ArrayList<String>(dc.getAccessed()));
			}

			cfg.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}