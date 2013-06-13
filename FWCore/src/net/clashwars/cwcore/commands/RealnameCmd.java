package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class RealnameCmd implements CommandExecutor {
	
	private CWCore cwc;
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
	
	public RealnameCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("realname")) {
			
			Player player = (Player) sender;
			if (args.length < 1) {
				sender.sendMessage(pf + "Your real name is: " + player.getName());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/realname [player]");
				return true;
			}
			
			if (args.length >= 1) {
				
				//TODO: Check for nickname as playername (mysql)
				player = cwc.getPlugin().getServer().getPlayer(args[0]);
				
				if (player == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
					return true;
				}
				
				sender.sendMessage(pf + "Real name of " + ChatColor.DARK_PURPLE + player.getDisplayName() 
						+ ChatColor.GOLD + " is: " + player.getName());
				return true;
			}
			return true;
		}
		return true;
	}
}
