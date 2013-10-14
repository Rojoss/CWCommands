package net.clashwars.cwcore.util;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CmdUtils {
	
	//Send command help.
	public static void commandHelp(CommandSender sender, String lbl, HashMap<String,String> optionalArgs, HashMap<String, String> modifiers) {
		sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
		sender.sendMessage(CWCore.pf + "Usage: " + ChatColor.DARK_PURPLE + CmdUtils.syntaxFromName(lbl).replace("<command>", lbl));
		sender.sendMessage(CWCore.pf + "Desc: " + ChatColor.GRAY + CmdUtils.descFromName(lbl));
		sender.sendMessage(CWCore.pf + "Aliases: " + ChatColor.GRAY + CmdUtils.aliasesFromName(lbl));
		if (optionalArgs.size() > 0) {
			sender.sendMessage(CWCore.pf + "Optional args: ");
			for (String arg : optionalArgs.keySet()) {
				sender.sendMessage(ChatColor.DARK_PURPLE + arg + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + optionalArgs.get(arg));
			}
		}
		if (modifiers.size() > 0) {
			sender.sendMessage(CWCore.pf + "Modifiers: ");
			for (String modifier : modifiers.keySet()) {
				sender.sendMessage(ChatColor.DARK_PURPLE + modifier + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + modifiers.get(modifier));
			}
		}
	}
	
	
	
	//Remove all modifiers and optionalArgs from args
	public static String[] getCmdArgs(String[] args, HashMap<String, String> optionalArgs, HashMap<String, String> modifiers) {
		String cmd = "";
		
		label: for (String arg : args) {
			for (String optArg : optionalArgs.keySet()) {
				String[] splt1 = arg.split(":");
				String[] splt2 = optArg.split(":");
				if (splt1.length > 1 && splt2.length > 1) {
					if (splt1[0].equalsIgnoreCase(splt2[0])) {
						continue label;
					}
				}
			}
			
			for (String modifier : modifiers.keySet()) {
				if (arg.equalsIgnoreCase("-" + modifier)) {
					continue label;
				}
				
			}
			if (cmd != "") {
				cmd += " " + arg;
			} else {
				cmd += arg;
			}
		}
		
		if (cmd.isEmpty()) {
			return new String[0];
		} else {
			String[] cmdArgs = cmd.split(" ");
			return cmdArgs;
		}
	}
	
	
	
	//Check if args contain modifier
	public static boolean hasModifier(String[] args, String mod) {
		return hasModifier(args, mod, true);
	}
	
	public static boolean hasModifier(String[] args, String mod, boolean exact) {
		for (String arg : args) {
			if (!mod.contains("-")) {
				mod = "-" + mod;
			}
			if (exact) {
				if (arg.equalsIgnoreCase(mod))
					return true;
			} else {
				if (arg.toLowerCase().startsWith(mod))
					return true;
			}
		}
		return false;
	}
	
	
	
	//Check if args contain optional arg
	public static boolean hasOptionalArg(String[] args, String optArg) {
		for (String arg : args) {
			if (arg.toLowerCase().startsWith(optArg))
				return true;
		}
		return false;
	}
	
	
	
	//Get data from optional arg
	public static String getOptionalArg(String[] args, String optArg) {
		for (String arg : args) {
			if (arg.toLowerCase().startsWith(optArg)) {
				String[] splt = arg.split(":");
				if (splt.length > 1) {
					return splt[1];
				}
			}
		}
		return null;
	}
	
	
	
	//Get commands information
	public static String descFromName(String str) {	
		for (Command cmd : CWCore.cmdList){
			if (cmd.getName().equalsIgnoreCase(str)) {
				return cmd.getDescription();
			} else {
				for (String alias : cmd.getAliases()) {
					if (alias.equalsIgnoreCase(str)) {
						return cmd.getDescription();
					}
				}
			}
		}
		return "";
	}
	
	public static String syntaxFromName(String str) {	
		for (Command cmd : CWCore.cmdList){
			if (cmd.getName().equalsIgnoreCase(str)) {
				return cmd.getUsage();
			} else {
				for (String alias : cmd.getAliases()) {
					if (alias.equalsIgnoreCase(str)) {
						return cmd.getUsage();
					}
				}
			}
		}
		return "";
	}
	
	public static String aliasesFromName(String str) {	
		for (Command cmd : CWCore.cmdList){
			if (cmd.getName().equalsIgnoreCase(str)) {
				return cmd.getAliases().toString();
			} else {
				for (String alias : cmd.getAliases()) {
					if (alias.equalsIgnoreCase(str)) {
						return cmd.getAliases().toString().replace("]", ", " + cmd.getName() + "]");
					}
				}
			}
		}
		return "";
	}
}
