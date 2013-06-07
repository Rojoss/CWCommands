package com.pqqqqq.cwcore.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.pqqqqq.cwcore.CWCore;

public class PluginConfig extends Config {
	private CWCore				cwc;
	private YamlConfiguration	cfg;
	private final File			dir		= new File("plugins/CWCore/");
	private final File			file	= new File(dir + "/CWCore.yml");

	public PluginConfig(CWCore cwc) {
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

			cwc.setMailDelay(ConfigUtil.getInt(cfg, file, "mail.delay", 30));
			cwc.setStormPercent(ConfigUtil.getInt(cfg, file, "storm-probability", 100));
			//fwc.setChestDelay(ConfigUtil.getInt(cfg, file, "dungeon-chests.open-delay", 60));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
