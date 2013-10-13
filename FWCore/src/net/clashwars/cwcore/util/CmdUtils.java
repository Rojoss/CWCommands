package net.clashwars.cwcore.util;

import net.clashwars.cwcore.CWCore;
import net.minecraft.server.v1_6_R2.DedicatedServer;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.MinecraftServer;
import net.minecraft.server.v1_6_R2.PlayerInteractManager;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdUtils {
	
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
	
	public static void commandHelp(CommandSender sender, String lbl) {
		sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
		sender.sendMessage(CWCore.pf + "Usage: " + ChatColor.DARK_PURPLE + CmdUtils.syntaxFromName(lbl).replace("<command>", lbl));
		sender.sendMessage(CWCore.pf + "Desc: " + ChatColor.GRAY + CmdUtils.descFromName(lbl));
		sender.sendMessage(CWCore.pf + "Aliases: " + ChatColor.GRAY + CmdUtils.aliasesFromName(lbl));
	}
	
	
	public static boolean hasModifier(String[] args, String mod, boolean exact) {
		for (int i = 0; i < args.length; i++) {
			if (exact) {
				if (args[i].equalsIgnoreCase(mod)) {
					return true;
				}
			} else {
				if (args[i].toLowerCase().startsWith(mod)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static String[] modifiedArgs(String[] args, String mod, boolean exact) {
		int i;
        int sloc = -1;
        String temp;
        for (i = 0; i < args.length; i++) {
        	if (exact) {
        		if (args[i].equalsIgnoreCase(mod)) {
                    sloc = i;
        		}
        	} else {
        		if (args[i].toLowerCase().startsWith(mod)) {
                    sloc = i;
        		}
        	}
        }
        if (sloc != -1) {
                for (i = sloc; i < args.length -1; i++) {
                        temp = args[i];
                    args[i] = args[i + 1];
                    args[i + 1] = temp;
                }
        }
       
        String[] args2 = new String[args.length - 1];
        for (i = 0; i < args2.length; i++) {
                args2[i] = args[i];
        }
        return args2;
	}
	
	public static int getArgIndex(String[] args, String argument, boolean exact) {
		for (int i = 0; i < args.length; i++) {
			if (exact) {
				if (args[i].equalsIgnoreCase(argument)) {
					return i;
				}
			} else {
				if (args[i].toLowerCase().startsWith(argument)) {
					return i;
				}
			}
		}
		return 0;
	}
	
	public static Player getPlayer(String[] args, String prefix, CWCore cwc) {
		Player player = null;
		for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().startsWith(prefix)) {
            	String[] splt = args[i].split(":");
            	if (splt.length > 1) {
                    player = cwc.getServer().getPlayer(splt[1]);
                    return player;
            	}
            }
		}
		return null;
	}
	
	public static Player getOfflinePlayer(String[] args, String prefix, CWCore cwc) {
		OfflinePlayer player = null;
		for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().startsWith(prefix)) {
            	String[] splt = args[i].split(":");
            	if (splt.length > 1) {
                    player = cwc.getServer().getOfflinePlayer(splt[1]);
                    MinecraftServer minecraftServer = DedicatedServer.getServer();
    				EntityPlayer entityPlayer = new EntityPlayer(DedicatedServer.getServer(), minecraftServer.getWorldServer(0), player.getName(), new PlayerInteractManager(minecraftServer.getWorldServer(0)));
                    entityPlayer.getBukkitEntity().loadData();
                    Player p = entityPlayer.getBukkitEntity();
                    return p;
            	}
            }
		}
		return null;
	}
	
	public static String getString(String[] args, String prefix) {
		for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().startsWith(prefix)) {
            	String[] splt = args[i].split(":");
            	if (splt.length > 2) {
            		String ret = splt[1];
                    for (int j = 2; j < splt.length; j++) {
                    	ret += ":" + splt[j];
                    }
                    return ret;
            	} else {
            		return splt[1];
            	}
            }
		}
		return null;
	}
	
	public static int getInt(String[] args, String prefix) {
		for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().startsWith(prefix)) {
            	String[] splt = args[i].split(":");
            	if (splt.length > 1) {
            		try {
    				 	int value = Integer.parseInt(splt[1]);
    				 	return value;
    				 } catch (NumberFormatException e) {
    				 }
            	}
            }
		}
		return -1;
	}
	
	public static float getFloat(String[] args, String prefix) {
		for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().startsWith(prefix)) {
            	String[] splt = args[i].split(":");
            	if (splt.length > 1) {
            		try {
    				 	float value = Float.parseFloat(splt[1]);
    				 	return value;
    				 } catch (NumberFormatException e) {
    				 }
            	}
            }
		}
		return -1;
	}
	
	public static boolean getBool(String[] args, String prefix) {
		for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().startsWith(prefix)) {
            	String[] splt = args[i].split(":");
            	if (splt.length > 1) {
            		if (splt[1].equalsIgnoreCase("true")) {
            			return true;
            		}
            	}
            }
		}
		return false;
	}
}
