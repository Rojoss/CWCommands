package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class BroadcastCmd implements CommandExecutor {
	
	private CWCore cwc;
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
	
	public BroadcastCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("bc")) {
			
			boolean prefix = true;
			if (Utils.hasModifier(args,"-p")) {
				prefix = false;
				args = Utils.modifiedArgs(args,"-p");
			}
			
			if (args.length < 1 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/bc <msg>");
				sender.sendMessage(pf + ChatColor.DARK_GRAY + "Modifiers: " + ChatColor.GRAY + "-p (no prefix)");
				return true;
			}
			
			String message = Utils.implode(args, " ", 0);
			if (prefix) {
				cwc.getPlugin().getServer().broadcastMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "[" 
					+ ChatColor.DARK_RED + ChatColor.BOLD + "CW BC" + ChatColor.DARK_GRAY + ChatColor.BOLD + "] " 
					+ ChatColor.GOLD + ChatColor.BOLD + Utils.integrateColor(message));
			} else {
				cwc.getPlugin().getServer().broadcastMessage(Utils.integrateColor(message));
			}
			return true;
		}
		return true;
	}

}
