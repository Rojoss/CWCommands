package net.clashwars.cwcore.util;

import net.minecraft.server.v1_6_R2.EntityFireworks;
import net.minecraft.server.v1_6_R2.NBTTagCompound;
import net.minecraft.server.v1_6_R2.NBTTagList;

import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ItemUtils {
	
	public static ItemStack createItem(String itemStr) {
		ItemStack item = null;
		MaterialData md = null;
		
		String[] splt = itemStr.split(":");
		
		if (splt.length > 0) {
			String iStr = splt[0];
			if (splt.length > 1) {
				if (!splt[1].equals("")) {
					iStr += ":" + splt[1];
				}
			}
			md = AliasUtils.getFullData(iStr);
			item = new ItemStack(md.getItemType(), 1, md.getData());
			
			if (splt.length > 2) { // ID:DATA:ENCHANTS  -  Sharpness:1,Knockback:5,etc
				if (!splt[2].equals("")) {
					String[] enchants = splt[2].split(",");
					String[] e = null;
					for (int i = 0; i < enchants.length; i++) {
						e = enchants[0].split("-");
						item.addUnsafeEnchantment(AliasUtils.findEnchantment(e[0]), Integer.parseInt(e[1]));
					}
				}
			}
			
			if (splt.length > 3) { // ID:DATA:ENCHANTS:NAME
				if (!splt[3].equals("")) {
					item.getItemMeta().setDisplayName(splt[3]);
				}
			}
			
			return item;
		}
		return new ItemStack(0);
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
