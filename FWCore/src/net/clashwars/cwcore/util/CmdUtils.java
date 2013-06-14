package net.clashwars.cwcore.util;

import net.clashwars.cwcore.CWCore;
import net.minecraft.server.v1_5_R3.DedicatedServer;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.MinecraftServer;
import net.minecraft.server.v1_5_R3.PlayerInteractManager;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CmdUtils {
	
	/**
	 * Check if  a command has modifiers in them like -s etc.
	 * @param args (All command arguments)
	 * @param mod (the modifier to check for like "-s")
	 * @return boolean (If it finds the modifier true else false)
	 */
	public static boolean hasModifier(String[] args, String mod) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].toLowerCase().startsWith(mod)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return arguments without the modifier
	 * This is used to remove the modifier so it can be used on any place in the command.
	 * @param args (All command arguments)
	 * @param mod (the modifier to remove like "-s")
	 * @return String[] (New args list without modifiers)
	 */
	public static String[] modifiedArgs(String[] args, String mod) {
		int i;
        int sloc = -1;
        String temp;
        for (i = 0; i < args.length; i++) {
                if (args[i].toLowerCase().startsWith(mod)) {
                        sloc = i;
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
	
	/**
	 * Return the index in the StringList of the argument given.
	 * This is used to check for optional arguments like "name:&6name"
	 * @param args (All command arguments)
	 * @param argument (The argument to check for like "name:")
	 * @return int (Index of string)
	 */
	public static int getArgIndex(String[] args, String argument) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].toLowerCase().startsWith(argument)) {
				return i;
			}
		}
		return 0;
	}
	
	public static Player getPlayerFromArgs(String[] args, String prefix, CWCore cwc) {
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
	
	public static Player getOfflinePlayerFromArgs(String[] args, String prefix, CWCore cwc) {
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
}
