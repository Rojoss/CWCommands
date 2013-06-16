package net.clashwars.cwcore.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.material.Colorable;

public class EntityUtils {
	
	private static String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;

	public static List<String> splitMobs(String str) {
		
		String[] mobStr = str.split(",");
		List<String> mobs = new ArrayList<String>();
		for (String mobPart : mobStr) {
			String[] mobDatas = mobPart.split(":");
			mobs.add(mobDatas[0]);
		}
		return mobs;
	}
	
	public static List<String> splitData(String str)
	{
		String[] mobStr = str.split(",");
		List<String> mobData = new ArrayList<String>();
		for (String mobPart : mobStr) {
			String[] mobDatas = mobPart.split(":");
			if (mobDatas.length == 1) {
				mobData.add(null);
			} else {
				mobData.add(mobDatas[1]);
			}
		}
		return mobData;
	}
	
	public static void changeMobData(EntityType type, Entity spawned, String data, Player player) {

		data = data.toLowerCase();
		
		if (spawned instanceof Slime) {
			try {
				((Slime)spawned).setSize(Integer.parseInt(data));
			} catch (Exception e) {
				player.sendMessage(pf + ChatColor.RED + "Invalid slime size.");
			}
		}

		if ((spawned instanceof Ageable) && data.contains("baby")) {
			((Ageable)spawned).setBaby();
			data = data.replace("baby", "");
		}

		if (spawned instanceof Colorable) {
			final String color = data.toUpperCase();
			try {
				if (color.length() > 1) {
					((Colorable)spawned).setColor(DyeColor.valueOf(color));
				}
			} catch (Exception e) {
				player.sendMessage(pf + ChatColor.RED + "Invalid dye color.");
			}
		}

		if (spawned instanceof Tameable && data.contains("tamed") && player != null) {
			Tameable tameable = ((Tameable)spawned);
			tameable.setTamed(true);
			AnimalTamer tamer = player;
			tameable.setOwner(tamer);
			data = data.replace("tamed", "");
		}

		if (type == EntityType.WOLF) {
			if (data.contains("angry")) {
				((Wolf)spawned).setAngry(true);
			}
		}

		if (type == EntityType.CREEPER && data.contains("powered")) {
			((Creeper)spawned).setPowered(true);
		}

		if (type == EntityType.OCELOT) {
			if (data.contains("siamese") || data.contains("white")) {
				((Ocelot)spawned).setCatType(Ocelot.Type.SIAMESE_CAT);
			} else if (data.contains("red") || data.contains("orange") || data.contains("tabby")) {
				((Ocelot)spawned).setCatType(Ocelot.Type.RED_CAT);
			} else if (data.contains("black") || data.contains("tuxedo")) {
				((Ocelot)spawned).setCatType(Ocelot.Type.BLACK_CAT);
			}
		}

		if (type == EntityType.VILLAGER) {
			for (Villager.Profession prof : Villager.Profession.values()) {
				if (data.contains(prof.toString().toLowerCase())) {
					((Villager)spawned).setProfession(prof);
				}
			}
		}

		if (spawned instanceof Zombie) {
			if (data.contains("villager")) {
				((Zombie)spawned).setVillager(true);
			} if (data.contains("baby")) {
				((Zombie)spawned).setBaby(true);
			}
		}

		if (type == EntityType.SKELETON) {
			if (data.contains("wither")) {
				((Skeleton)spawned).setSkeletonType(SkeletonType.WITHER);
			}
		}

		if (type == EntityType.EXPERIENCE_ORB) {
			if (Utils.isNumber(data)) {
				((ExperienceOrb)spawned).setExperience(Integer.parseInt(data));
			}
		}
	}
	

}
