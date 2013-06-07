package com.pqqqqq.cwcore.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class PluginConfig extends Config {
	private YamlConfiguration	cfg;
	private final File			dir		= new File("plugins/CWCore/");
	private final File			file	= new File(dir + "/CWCore.yml");

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
