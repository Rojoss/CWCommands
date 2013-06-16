package net.clashwars.cwcore.util;

import java.util.Map;
import java.util.Set;

import net.clashwars.cwcore.config.AliasesConfig;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

public class AliasUtils {
	
	/**
	 * Find a material in the Aliases file with the given string
	 * @param str (The string with the material)
	 * @return Material (The material if the material is found)
	 */
	public static Material findMaterial(String str) {
        for (Map.Entry<Material, Set<String>> entry : AliasesConfig.searchMaterials.entrySet()) {
                Material m = entry.getKey();
                Set<String> aliases = entry.getValue();

                for (String alias : aliases) {
                        if (Integer.toString(m.getId()).equals(str) || str.equalsIgnoreCase(alias) || str.replaceAll("_| ", "").equalsIgnoreCase(alias.replaceAll("_| ", ""))) {
                                return m;
                        }
                }
        }
        return null;
	}
	
	/**
	 * Find a entityType in the Aliases file with the given string
	 * @param str (The string with the entity)
	 * @return EntityType (The type of entity if the entity is found)
	 */
	public static EntityType findEntityType(String str) {
        for (Map.Entry<EntityType, Set<String>> entry : AliasesConfig.searchEntities.entrySet()) {
                EntityType m = entry.getKey();
                Set<String> aliases = entry.getValue();

                for (String alias : aliases) {
                        if (str.equalsIgnoreCase(alias) || str.replaceAll("_| ", "").equalsIgnoreCase(alias.replaceAll("_| ", ""))) {
                                return m;
                        }
                }
        }
        return null;
	}
	
	/**
	 * Find a Enchantment in the Aliases file with the given string
	 * @param str (The string with the enchantment)
	 * @return Enchantment (The type of enchantment if the enchantment is found)
	 */
	public static Enchantment findEnchanttype(String str) {
        for (Map.Entry<Enchantment, Set<String>> entry : AliasesConfig.searchEnchants.entrySet()) {
        		Enchantment en = entry.getKey();
                Set<String> aliases = entry.getValue();

                for (String alias : aliases) {
                        if (str.equalsIgnoreCase(alias) || str.replaceAll("_| ", "").equalsIgnoreCase(alias.replaceAll("_| ", ""))) {
                                return en;
                        }
                }
        }
        return null;
	}
	
	/**
	 * Find a PottionEffect in the Aliases file with the given string
	 * @param str (The string with the potionEffect)
	 * @return PotionEffectType (The type of potionEffect if the effect is found)
	 */
	 public static PotionEffectType findPotions(String str) {
         for (Map.Entry<PotionEffectType, Set<String>> entry : AliasesConfig.searchPotions.entrySet()) {
                 PotionEffectType m = entry.getKey();
                 Set<String> aliases = entry.getValue();

                 for (String alias : aliases) {
                         if (str.equalsIgnoreCase(alias) || str.replaceAll("_| ", "").equalsIgnoreCase(alias.replaceAll("_| ", ""))) {
                                 return m;
                         }
                 }
         }
         return null;
	 }
	 
	 /**
	 * Create MaterialData with a Material from findMaterial() and data like stone:1
	 * @param str (The string with the material:data)
	 * @return MaterialData (The MaterialData with the material if found and the given data if specified)
	 */
	 public static MaterialData getFullData(String str) {
		 
         String[] splt = str.split(":");
         byte data = 0;
         
         if (splt.length > 1) {
         	data = (byte) Integer.parseInt(splt[1]);
         }
         
         Material iMat = findMaterial(splt[0]);
         if (iMat == null) {
        	 return null;
         }
         
         return new MaterialData(iMat, data);
	 }
	
	
}
