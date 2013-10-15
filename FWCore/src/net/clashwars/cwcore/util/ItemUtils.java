package net.clashwars.cwcore.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_6_R2.EntityFireworks;
import net.minecraft.server.v1_6_R2.NBTTagCompound;
import net.minecraft.server.v1_6_R2.NBTTagList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemUtils {
	
	//Get a custom item from a string like this: <item>[:ID]:<enchant>[.lvl],<e>[.lvl],etc
	public static CustomItem getItem(String str) {
		String[] splt = str.split(":");
		ItemStack i = null;
		CustomItem ci = null;
		if (splt.length > 0) {
			MaterialData md = AliasUtils.getFullData(splt[0]);
			i = md.toItemStack();
			ci = new CustomItem(i);
		}
		if (splt.length > 1) {
			ci.setEnchants(splt[1]);
		}
		return ci;
	}
	
	
	//Get potion effects for a input string like <potion>[.<dur>.<lvl>][,<potion>[.<dur>.<lvl>]],etc
	public static List<PotionEffect> getPotionEffects(String str) {
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		
		if (str.contains(",")) {
			String[] potionEffects = str.split(",");
			if (potionEffects.length > 1) {
				for (String e : potionEffects) {
					String[] potionEffect = e.split("\\.");
					PotionEffectType ef = AliasUtils.findPotion(potionEffect[0]);
					if (ef == null) {
						ef = PotionEffectType.SPEED;
					}
					if (potionEffect.length > 2) {
						int lvl = Utils.getInt(potionEffect[2]);
						if (lvl > 0) {
							lvl -= 1;
						}
						effects.add(new PotionEffect(ef, Utils.getInt(potionEffect[1]) * 20, lvl));
					} else if (potionEffect.length > 1) {
						effects.add(new PotionEffect(ef, Utils.getInt(potionEffect[1]) * 20, 0));
					} else {
						effects.add(new PotionEffect(ef, 600, 0));
					}
				}
			}
		} else {
			String[] potionEffect = str.split("\\.");
			PotionEffectType ef = AliasUtils.findPotion(potionEffect[0]);
			if (ef == null) {
				ef = PotionEffectType.SPEED;
			}
			if (potionEffect.length > 2) {
				int lvl = Utils.getInt(potionEffect[2]);
				if (lvl > 0) {
					lvl -= 1;
				}
				effects.add(new PotionEffect(ef, Utils.getInt(potionEffect[1]) * 20, lvl));
			} else if (potionEffect.length > 1) {
				effects.add(new PotionEffect(ef, Utils.getInt(potionEffect[1]) * 20, 0));
			} else {
				effects.add(new PotionEffect(ef, 600, 0));
			}
		}
		return effects;
	}
	
	
	public static void createFireworksExplosion(Location location, boolean flicker, boolean trail, Type type, int[] colors, int[] fadeColors, int flightDuration) {
        //Get type
		int typeID = 0;
		if (type == Type.BALL) {
			typeID = 0;
		} else if (type == Type.BALL_LARGE) {
			typeID = 1;
		} else if (type == Type.STAR) {
			typeID = 2;
		} else if (type == Type.CREEPER) {
			typeID = 3;
		} else if (type == Type.BURST) {
			typeID = 4;
		}
		
		// create item
        net.minecraft.server.v1_6_R2.ItemStack item = new net.minecraft.server.v1_6_R2.ItemStack(401, 1, 0);
        
        // get tag
        NBTTagCompound tag = item.tag;
        if (tag == null) {
                tag = new NBTTagCompound();
        }
        
        // create explosion tag
        NBTTagCompound explTag = new NBTTagCompound("Explosion");
        explTag.setByte("Flicker", flicker ? (byte)1 : (byte)0);
        explTag.setByte("Trail", trail ? (byte)1 : (byte)0);
        explTag.setByte("Type", (byte)typeID);
        explTag.setIntArray("Colors", colors);
        explTag.setIntArray("FadeColors", fadeColors);
        
        // create fireworks tag
        NBTTagCompound fwTag = new NBTTagCompound("Fireworks");
        fwTag.setByte("Flight", (byte)flightDuration);
        NBTTagList explList = new NBTTagList("Explosions");
        explList.add(explTag);
        fwTag.set("Explosions", explList);
        tag.setCompound("Fireworks", fwTag);
        
        // set tag
        item.tag = tag;
        
        // create fireworks entity
        EntityFireworks fireworks = new EntityFireworks(((CraftWorld)location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), item);
        ((CraftWorld)location.getWorld()).getHandle().addEntity(fireworks);
        
        // cause explosion
        if (flightDuration == 0) {
                ((CraftWorld)location.getWorld()).getHandle().broadcastEntityEffect(fireworks, (byte)17);
                fireworks.die();
        }
    }

}
