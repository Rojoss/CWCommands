package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCmd implements CommandExecutor {
	
	private CWCore cwc;
	
	public BroadcastCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("bc")) {
			String pf = cwc.getPrefix();
			
			/* Modifiers + No args */
			if (Utils.hasModifier(args,"-h") || args.length < 1) {
				sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + lbl + ChatColor.DARK_GRAY + "  =====");
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/bc <msg>");
				sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Broadcast a message to the server.");
				sender.sendMessage(pf + "Modifiers: ");
				sender.sendMessage(ChatColor.DARK_PURPLE + "-p" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No prefix");
				return true;
			}
			boolean prefix = true;
			if (Utils.hasModifier(args,"-p")) {
				prefix = false;
				args = Utils.modifiedArgs(args,"-p");
			}
			
			/* args */
			String message = Utils.implode(args, " ", 0);
			if (prefix) {
				cwc.getPlugin().getServer().broadcastMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "[" 
				+ ChatColor.DARK_RED + ChatColor.BOLD + "CW BC" + ChatColor.DARK_GRAY + ChatColor.BOLD + "] " 
				+ ChatColor.GOLD + ChatColor.BOLD + Utils.integrateColor(message));
			} else {
				cwc.getPlugin().getServer().broadcastMessage(Utils.integrateColor(message));
			}
		}
		return true;
	}
}
