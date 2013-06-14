package net.clashwars.cwcore.util;

import java.util.regex.Pattern;

import org.bukkit.ChatColor;


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

	/**
	 * Implode a StringList to a single string and specify the character between strings and specify at what string index to start
	 * @param arr (The string list to implode)
	 * @param glue (The string to put between 2 strings)
	 * @param start (The index of the string to start with)
	 * @return String (String with all StringList strings separated by the given char)
	 */
	public static String implode(String[] arr, String glue, int start) {
		return implode(arr, glue, start, arr.length - 1);
	}

	/**
	 * Implode a StringList to a single string and specify the character between strings.
	 * @param arr (The string list to implode)
	 * @param glue (The string to put between 2 strings)
	 * @return String (String with all StringList strings separated by the given char)
	 */
	public static String implode(String[] arr, String glue) {
		return implode(arr, glue, 0);
	}

	/**
	 * Implode a StringList to a single string.
	 * @param arr (The string list to implode)
	 * @return String (String with all StringList strings separated by spaces)
	 */
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
	
	public static String stripColorCodes(String str) {
		return Pattern.compile("&([0-9a-fk-orA-FK-OR])").matcher(str).replaceAll("");
	}
}
