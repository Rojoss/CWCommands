package net.clashwars.cwcore.config;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

public class AliasesConfig extends Config {
	private YamlConfiguration cfg;
	private ConfigUtil cu;
	private final File			dir		= new File("plugins/CWCore/");
	private final File			file	= new File(dir + "/Aliases.yml");
	
	public static Map<Material, Set<String>>                        searchMaterials = new HashMap<Material, Set<String>>();
    public static Map<EntityType, Set<String>>                      searchEntities  = new HashMap<EntityType, Set<String>>();
    public static Map<Enchantment, Set<String>>                     searchEnchants  = new HashMap<Enchantment, Set<String>>();
    public static Map<PotionEffectType, Set<String>>        		searchPotions   = new HashMap<PotionEffectType, Set<String>>();

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
            
            searchMaterials.clear();
            searchEntities.clear();
            searchPotions.clear();
            
            for (Material m : Material.values()) {
                searchMaterials.put(m, new HashSet<String>(cu.getStringList("search-aliases.materials." + m.name(), m.name())));
            }

            for (EntityType e : EntityType.values()) {
                searchEntities.put(e, new HashSet<String>(cu.getStringList("search-aliases.entities." + e.name(), e.name())));
            }
            
            for (Enchantment en : Enchantment.values()) {
            	searchEnchants.put(en, new HashSet<String>(cu.getStringList("search-aliases.enchants." + en.getName(), en.getName())));
            }
            
            try {
                for (PotionEffectType p : PotionEffectType.values()) {
                	searchPotions.put(p, new HashSet<String>(cu.getStringList("search-aliases.potions." + p.getName(), p.getName())));
                }
            } catch (Throwable e) {

            }
			
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
