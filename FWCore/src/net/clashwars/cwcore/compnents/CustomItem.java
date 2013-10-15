package net.clashwars.cwcore.compnents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.ItemUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomItem extends ItemStack {
	
	ItemStack i = null;
	ItemMeta im = null;
	PotionMeta pmeta = null;
	LeatherArmorMeta lmeta = null;
	EnchantmentStorageMeta emeta = null;
	SkullMeta smeta = null;

	public CustomItem(ItemStack item) {
		i = item;
		im = i.getItemMeta();
		
		if (i.getType() == Material.POTION) {
			pmeta = (PotionMeta) i.getItemMeta();
		}
		if (i.getType() == Material.LEATHER_HELMET || i.getType() == Material.LEATHER_CHESTPLATE || i.getType() == Material.LEATHER_LEGGINGS || i.getType() == Material.LEATHER_BOOTS) {
			lmeta = (LeatherArmorMeta) i.getItemMeta();
		}
		if (i.getType() == Material.SKULL_ITEM) {
			smeta = (SkullMeta) i.getItemMeta();
		}
		if (i.getType() == Material.BOOK || i.getType() == Material.ENCHANTED_BOOK) {
			i.setType(Material.ENCHANTED_BOOK);
			emeta = (EnchantmentStorageMeta) i.getItemMeta();
		}
	}
	
	
	public ItemStack getItem() {
		if (im != null) {
			i.setItemMeta(im);
		}
		if (pmeta != null) {
			i.setItemMeta(pmeta);
		}
		if (lmeta != null) {
			i.setItemMeta(lmeta);
		}
		if (smeta != null) {
			i.setItemMeta(smeta);
		}
		if (emeta != null) {
			i.setItemMeta(emeta);
		}
		return i;
	}
	
	
	public void setName(String name) {
		im.setDisplayName(Utils.integrateColor(name));
		if (pmeta != null)
			pmeta.setDisplayName(Utils.integrateColor(name));
		if (lmeta != null)
			lmeta.setDisplayName(Utils.integrateColor(name));
		if (smeta != null)
			smeta.setDisplayName(Utils.integrateColor(name));
		if (emeta != null)
			emeta.setDisplayName(Utils.integrateColor(name));
	}
	
	
	public void setLore(String lore) {
		List<String> l = new ArrayList<String>();
		if (lore.contains("|")) {
			String[] lines = lore.split("\\|");
			for (String line : lines) {
				l.add(Utils.integrateColor(line.replace("_", " ")));
			}
		} else {
			l.add(Utils.integrateColor(lore.replace("_", " ")));
		}
		im.setLore(l);
		if (pmeta != null)
			pmeta.setLore(l);
		if (lmeta != null)
			lmeta.setLore(l);
		if (smeta != null)
			smeta.setLore(l);
		if (emeta != null)
			emeta.setLore(l);
	}
	
	
	public void setDurability(String dur) {
		short durability = 1;
		try {
		 	durability = Short.parseShort(dur);
		} catch (NumberFormatException e) {
		}
		i.setDurability((short) (i.getData().getItemType().getMaxDurability() - durability));
	}
	
	
	//Set enchants for a input string like <enchant>[.<lvl>][,<enchant>[.<lvl>]],etc
	//And set enchantments for enchanted books if the item is a book.
	public void setEnchants(String str) {
		HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
		
		if (str.contains(",")) {
			String[] enchants = str.split(",");
			if (enchants.length > 1) {
				for (String e : enchants) {
					String[] enchant = e.split("\\.");
					if (enchant.length > 1) {
						enchantments.put(AliasUtils.findEnchantment(enchant[0]), Utils.getInt(enchant[1]));
					} else {
						enchantments.put(AliasUtils.findEnchantment(enchant[0]), 1);
					}
				}
			}
		} else {
			String[] enchant = str.split("\\.");
			if (enchant.length > 1) {
				enchantments.put(AliasUtils.findEnchantment(enchant[0]), Utils.getInt(enchant[1]));
			} else {
				enchantments.put(AliasUtils.findEnchantment(enchant[0]), 1);
			}
		}
		if (emeta != null) {
			for (Enchantment enchant : enchantments.keySet()) {
				emeta.addStoredEnchant(enchant, enchantments.get(enchant), true);
			}
		} else {
			for (Enchantment e : enchantments.keySet()) {
				i.addUnsafeEnchantment(e, enchantments.get(e));
				im.addEnchant(e, enchantments.get(e), true);
				if (pmeta != null)
					pmeta.addEnchant(e, enchantments.get(e), true);
				if (lmeta != null)
					lmeta.addEnchant(e, enchantments.get(e), true);
				if (smeta != null)
					smeta.addEnchant(e, enchantments.get(e), true);
			}
		}
	}
	
	
	public void setHead(String player) {
		if (i.getType() == Material.SKULL_ITEM) {
			i.setDurability((short) 3);
			smeta.setOwner(player);
		}
	}
	
	
	public void setLeatherColor(String color) {
		if (i.getType() == Material.LEATHER_HELMET || i.getType() == Material.LEATHER_CHESTPLATE || i.getType() == Material.LEATHER_LEGGINGS || i.getType() == Material.LEATHER_BOOTS) {
			int c = 0;
			
			if (color.contains("#")) {
                String[] str = color.split("#");
                if (str[1].matches("[0-9A-Fa-f]+")) {
                	c = Integer.parseInt(str[1], 16);
                }
			} else {
				if (color.matches("[0-9A-Fa-f]+")) {
                    c = Integer.parseInt(color, 16);
				}
			}
			
			lmeta.setColor(Color.fromRGB(c));
		}
	}
	
	
	//Set potion effects for a input string like <potion>[.<dur>.<lvl>][,<potion>[.<dur>.<lvl>]],etc
		public void setPotionEffects(String str) {
			List<PotionEffect> effects = ItemUtils.getPotionEffects(str);
			
			if (effects != null && !effects.contains(null)) {
				i.setType(Material.POTION);
				if (i.getDurability() == 0) {
					i.setDurability((short) 64);
				}
				for (PotionEffect effect : effects) {
					pmeta.addCustomEffect(effect, true);
				}
			}
		}
	
	
}
