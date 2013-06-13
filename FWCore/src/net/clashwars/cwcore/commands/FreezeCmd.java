package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class FreezeCmd implements CommandExecutor {
	
	private CWCore cwc;
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
	
	public FreezeCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("freeze")) {
			
			boolean silent = false;
			if (Utils.hasModifier(args,"-s")) {
				silent = true;
				args = Utils.modifiedArgs(args,"-s");
			}
			
			if (!(sender instanceof Player) && args.length < 1) {
				sender.sendMessage(pf + ChatColor.RED + "`You need to specify a name to use this on the console!");
				return true;
			}
			
			Player player = null;
			if (args.length < 1) {
				player = (Player) sender;
				if (cwc.getFrozenPlayers().contains(player.getName())) {
					cwc.getFrozenPlayers().remove(player.getName());
					if (!silent)
						player.sendMessage(pf + "You are no longer frozen!");
				} else {
					cwc.getFrozenPlayers().add(player.getName());
					if (!silent)
						player.sendMessage(pf + "You have been frozen!");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/freeze [player] [on|off]");
				sender.sendMessage(pf + ChatColor.DARK_GRAY + "Modifiers: " + ChatColor.GRAY + "-s (no messages)");
				return true;
			}
			
			if (args.length >= 1) {
				player = cwc.getPlugin().getServer().getPlayer(args[0]);
				
				if (player == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
					return true;
				}
			}
			
			if (args.length == 1) {
				
				if (cwc.getFrozenPlayers().contains(player.getName())) {
					cwc.getFrozenPlayers().remove(player.getName());
					if (!silent) {
						player.sendMessage(pf + "You are no longer frozen!");
						sender.sendMessage(pf + "You have unfreezed " + ChatColor.DARK_PURPLE + player.getDisplayName());
					}
				} else {
					cwc.getFrozenPlayers().add(player.getName());
					if (!silent) {
						player.sendMessage(pf + "You have been frozen!");
						sender.sendMessage(pf + "You have frozen " + ChatColor.DARK_PURPLE + player.getDisplayName());
					}
				}
				return true;
			}
			
			if (args.length >= 1) {
				
				if (args[1].toLowerCase().startsWith("on")) {
					if (!cwc.getFrozenPlayers().contains(player.getName())) {
						cwc.getFrozenPlayers().add(player.getName());
						if (!silent) {
							player.sendMessage(pf + "You have been frozen!");
							sender.sendMessage(pf + "You have frozen " + ChatColor.DARK_PURPLE + player.getDisplayName());
						}
					} else {
						if (!silent)
							sender.sendMessage(pf + ChatColor.RED + "Player " + player.getDisplayName() + " is already frozen!");
						return true;
					}
				} else {
					if (cwc.getFrozenPlayers().contains(player.getName())) {
						cwc.getFrozenPlayers().remove(player.getName());
						if (!silent) {
							player.sendMessage(pf + "You are no longer frozen!");
							sender.sendMessage(pf + "You have unfreezed " + ChatColor.DARK_PURPLE + player.getDisplayName());
						}
					} else {
						if (!silent)
							sender.sendMessage(pf + ChatColor.RED + "Player " + player.getDisplayName() + " is not frozen!");
						return true;
					}
				}
			}
		}
		return true;
	}

}
