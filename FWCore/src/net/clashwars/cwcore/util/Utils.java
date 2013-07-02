package net.clashwars.cwcore.util;

import java.util.Random;
import java.util.regex.Pattern;

import net.clashwars.cwcore.entity.CWPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Color;


public class Utils {
	
	/**
	 * Implode a StringList to a single string and specify the character between strings and specify at what string index to start and end
	 * @param arr (The string list to implode)
	 * @param glue (The string to put between 2 strings)
	 * @param start (The index of the string to start with)
	 * @param end (The index of the string to end at)
	 * @return String (String with all StringList strings separated by the given char)
	 */
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
	
	/**
	 * Add color to a string for like nicknames and itemnames etc.
	 * It will replace all color codes like &1<>9 &a<>f And the format codes.
	 * @param str (The string to add the color to)
	 * @return String (String with integrated colors)
	 */
	public static String integrateColor(String str) {
		for (ChatColor c : ChatColor.values()) {
			str = str.replaceAll("&" + c.getChar() + "|&" + Character.toUpperCase(c.getChar()), c.toString());
		}
		return str;
	}
	
	/**
	 * Remove color codes like &6 from a string
	 * @param str (The string to remove the color from)
	 * @return String (String without colors)
	 */
	public static String stripColorCodes(String str) {
		return Pattern.compile("&([0-9a-fk-orA-FK-OR])").matcher(str).replaceAll("");
	}
	
	/**
	 * Check if a string is a number or not.
	 * @param str (The string to check)
	 * @return Boolean
	 */
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
				return implode(CmdUtils.modifiedArgs(ptools, ptool[0], false), "»");
			}
		}
		return implode(ptools, "»");
	}
	
	public static Color getRandomColor() {
		Random rand = new Random();
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);

		return Color.fromRGB(r, g, b);
	}
}
