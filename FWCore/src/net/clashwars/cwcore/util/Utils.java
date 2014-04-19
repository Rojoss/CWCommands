package net.clashwars.cwcore.util;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

import net.clashwars.cwcore.entity.CWPlayer;
import net.minecraft.server.v1_7_R3.DedicatedServer;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Utils {

	public static String implode(String[] arr, String glue, int start, int end) {
		String ret = "";

		if (arr == null || arr.length <= 0)
			return ret;

		for (int i = start; i <= end && i < arr.length; i++) {
			ret += arr[i] + glue;
		}

		return ret.substring(0, ret.length() - glue.length());
	}

	public static String implode(String[] arr, String glue, int start) {
		return implode(arr, glue, start, arr.length - 1);
	}

	public static String implode(String[] arr, String glue) {
		return implode(arr, glue, 0);
	}

	public static String implode(String[] arr) {
		return implode(arr, " ");
	}

	//Integrate colors in a string
	public static String integrateColor(String str) {
		for (ChatColor c : ChatColor.values()) {
			str = str.replaceAll("&" + c.getChar() + "|&" + Character.toUpperCase(c.getChar()), c.toString());
		}
		return str;
	}

	public static String[] integrateColor(String[] str) {
		for (int i = 0; i < str.length; i++) {
			for (ChatColor c : ChatColor.values()) {
				str[i] = str[i].replaceAll("&" + c.getChar() + "|&" + Character.toUpperCase(c.getChar()), c.toString());
			}
		}
		return str;
	}

	//Remove color codes from a string.
	public static String stripColorCodes(String str) {
		return Pattern.compile("&([0-9a-fk-orA-FK-OR])").matcher(str).replaceAll("");
	}

	//Check if string is a number.
	public static boolean isNumber(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String[] getPowerToolsList(CWPlayer cwp) {
		String[] ptools = cwp.getPowertool().split("»");
		return ptools;
	}

	public static String getPowerToolCommandByID(String[] ptools, int id) {
		for (int i = 0; i < ptools.length; i++) {
			String[] ptool = ptools[i].split(":", 2);
			int ptID = -1;
			try {
				ptID = Integer.parseInt(ptool[0]);
			} catch (NumberFormatException e) {
				return "";
			}
			if (ptID == id) {
				return ptool[1];
			}
		}
		return "";
	}

	public static String removePowerToolCommandByID(String[] ptools, int id) {
		for (int i = 0; i < ptools.length; i++) {
			String[] ptool = ptools[i].split(":", 2);
			int ptID = Integer.parseInt(ptool[0]);
			if (ptID == id) {
				return implode(CmdUtils.removeFromStringArray(ptools, ptool[0]), "»");
			}
		}
		return implode(ptools, "»");
	}

	//Get a random Color
	public static Color getRandomColor() {
		Random rand = new Random();
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);

		return Color.fromRGB(r, g, b);
	}

	//Get int from string
	public static int getInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
		}
		return -1;
	}

	//Get float from string
	public static float getFloat(String str) {
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
		}
		return -1;
	}

	//Get colors or a color from string
	public static ArrayList<Color> getColors(String str) {
		ArrayList<Color> colors = new ArrayList<Color>();

		String[] clrs = str.split(",");
		if (clrs.length > 0) {
			for (String color : clrs) {
				colors.add(getColor(color));
			}
		} else {
			colors.add(getColor(str));
		}

		return colors;
	}

	public static Color getColor(String str) {
		int color = -1;

		if (str.contains("#")) {
			String[] clr = str.split("#");
			if (clr[1].matches("[0-9A-Fa-f]+")) {
				color = Integer.parseInt(clr[1], 16);
			}
		} else {
			if (str.matches("[0-9A-Fa-f]+")) {
				color = Integer.parseInt(str, 16);
			}
		}

		return Color.fromRGB(color);
	}

	//Get a firework type effect from string
	public static Type getFireworkEffect(String str) {
		Type effectType = null;
		if (str.startsWith("sb")) {
			effectType = Type.BALL;
		} else if (str.startsWith("bb")) {
			effectType = Type.BALL_LARGE;
		} else if (str.startsWith("c")) {
			effectType = Type.CREEPER;
		} else if (str.startsWith("bu")) {
			effectType = Type.BURST;
		} else if (str.startsWith("s")) {
			effectType = Type.STAR;
		}
		return effectType;
	}

	//Get a fake offline player which can be used as a regular player.
	public static Player getFakeOfflinePlayer(String player) {
		OfflinePlayer oPlayer = null;
		oPlayer = Bukkit.getServer().getOfflinePlayer(player);

		MinecraftServer minecraftServer = DedicatedServer.getServer();

		GameProfile gp = new GameProfile(/* I have no idea what id is*/null, player);
		EntityPlayer entityPlayer = new EntityPlayer(DedicatedServer.getServer(), minecraftServer.getWorldServer(0), /* GamProfile */gp,
				new PlayerInteractManager(minecraftServer.getWorldServer(0)));
		entityPlayer.getBukkitEntity().loadData();

		return entityPlayer.getBukkitEntity();
	}

	//Get a world from a string like <x>,<y>,<z>:<world>
	public static World getWorld(String str) {
		if (str != null) {
			String[] splt = str.split(":");
			if (splt.length > 1) {
				if (Bukkit.getServer().getWorld(splt[1]) != null) {
					return Bukkit.getServer().getWorld(splt[1]);
				}
			}
		}
		return Bukkit.getServer().getWorlds().get(0);
	}

	//Check if a string has a world specififed.
	public static boolean hasWorld(String str) {
		if (str != null) {
			String[] splt = str.split(":");
			if (splt.length > 1) {
				if (Bukkit.getServer().getWorld(splt[1]) != null) {
					return true;
				}
			}
		}
		return false;
	}
}
