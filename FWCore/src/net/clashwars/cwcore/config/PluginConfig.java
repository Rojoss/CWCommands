package net.clashwars.cwcore.config;

import java.io.File;
import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.sql.SqlInfo;

import org.bukkit.configuration.file.YamlConfiguration;

public class PluginConfig extends Config {
	private CWCore cwc;
	private YamlConfiguration cfg;
	private ConfigUtil cu;
	private final File			dir		= new File("plugins/CWCore/");
	private final File			file	= new File(dir + "/CWCore.yml");
	
	
	public PluginConfig (CWCore cwc) {
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
			
			// Sql
            String address = cu.getString("sql.address", "37.26.106.5:3306");
            String username = cu.getString("sql.username", "clashwar_main");
            String password = cu.getString("sql.password", "pass");
            String database = cu.getString("sql.database", "clashwar_main");
            cwc.setSqlInfo(new SqlInfo(address, username, password, database));
			
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
}
