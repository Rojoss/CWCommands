package com.pqqqqq.fwcore.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.pqqqqq.fwcore.FWCore;

public class PluginConfig extends Config {
	private FWCore				fwc;
	private YamlConfiguration	cfg;
	private final File			dir		= new File("plugins/FWCore/");
	private final File			file	= new File(dir + "/config.yml");

	public PluginConfig(FWCore fwc) {
		this.fwc = fwc;
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

			fwc.setMailDelay(ConfigUtil.getInt(cfg, file, "mail.delay", 30));
			fwc.setStormPercent(ConfigUtil.getInt(cfg, file, "storm-probability", 100));
			//fwc.setChestDelay(ConfigUtil.getInt(cfg, file, "dungeon-chests.open-delay", 60));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
