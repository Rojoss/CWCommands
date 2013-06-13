package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RealnameCmd implements CommandExecutor {
	
	private CWCore cwc;
	
	public RealnameCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("realname")) {
			String pf = cwc.getPrefix();
			Player player = null;
			
			/* Modifiers */
			if (Utils.hasModifier(args,"-h")) {
				sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + lbl + ChatColor.DARK_GRAY + "  =====");
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/realname [player]");
				sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Lookup a player his real name in case he has a nickname.");
				args = Utils.modifiedArgs(args,"-h");
				return true;
			}
			
			/* Console check */
			if (!(sender instanceof Player)) {
				if (args.length < 1) {
					sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
					return true;
				}
			} else {
				player = (Player) sender;
			}
			
			/* No args (run as sender)*/
			if (args.length < 1) {
				sender.sendMessage(pf + "Your real name is: " + player.getName());
				return true;
			}
			
			/* 1 arg (Player) */
			if (args.length >= 1) {
				
				//TODO: Check for nickname as playername (scan through mysql nicknames)
				player = cwc.getServer().getPlayer(args[0]);
			}
			
			/* null checks */
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
			
			/* Action */
			sender.sendMessage(pf + "Real name of " + ChatColor.DARK_PURPLE + player.getDisplayName() 
			+ ChatColor.GOLD + " is: " + player.getName());
		}
		return true;
	}
}