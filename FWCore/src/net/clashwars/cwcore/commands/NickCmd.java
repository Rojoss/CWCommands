package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class NickCmd implements CommandExecutor {
	
	private CWCore cwc;
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
	
	public NickCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("nick")) {
			
			boolean reset = false;
			if (Utils.hasModifier(args,"-r")) {
				reset = true;
				args = Utils.modifiedArgs(args,"-r");
			}
			
			boolean silent = false;
			if (Utils.hasModifier(args,"-s")) {
				silent = true;
				args = Utils.modifiedArgs(args,"-s");
			}
			
			if (args.length < 1 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/nick <nick> [player]");
				sender.sendMessage(pf + ChatColor.DARK_GRAY + "Modifiers: " + ChatColor.GRAY + "-r (reset nick) - -s (no message)");
				return true;
			}
			
			Player player = (Player) sender;
			CWPlayer cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
			
			if (args.length == 1) {
				if (reset) {
					player.setDisplayName(player.getName());
					cwp.setNick(player.getName());
					if (!silent)
						sender.sendMessage(pf + "Your nickname has been reset!");
					return true;
				}
				
				player.setDisplayName(Utils.integrateColor(args[0]));
				cwp.setNick(args[0]);
				sender.sendMessage(pf + "Changed your nickname to: " + Utils.integrateColor(args[0]));
				return true;
			}
			
			if (args.length >= 2) {
				player = cwc.getPlugin().getServer().getPlayer(args[1]);
				
				if (player == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
					return true;
				}
				
				cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
				
				if (reset) {
					player.setDisplayName(player.getName());
					cwp.setNick(player.getName());
					if (!silent) {
						sender.sendMessage(pf + "Nickname of " + ChatColor.DARK_PURPLE + player.getName() + ChatColor.GOLD + " reset!");
						player.sendMessage(pf + "Your nickname has been reset!");
					}
					return true;
				}
				
				player.setDisplayName(Utils.integrateColor(args[0]));
				cwp.setNick(args[0]);
				if (!silent) { 
					sender.sendMessage(pf + "Nickname of " + ChatColor.DARK_PURPLE + player.getName() 
						+ ChatColor.GOLD + " changed to: " + Utils.integrateColor(args[0]));
					player.sendMessage(pf + "Your nickname has been changed to " + Utils.integrateColor(args[0]));
				}
				return true;
			}
			
			return true;
		}
		return true;
	}

}
