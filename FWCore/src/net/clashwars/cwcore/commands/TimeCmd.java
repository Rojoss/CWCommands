package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.util.TimeUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCmd implements CommandExecutor {
	
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("time")) {
			
			boolean silent = false;
			if (Utils.hasModifier(args,"-s")) {
				silent = true;
				args = Utils.modifiedArgs(args,"-s");
			}
			
			if (args.length < 1 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/time <get|500ticks|24:00|12:00|day|night>");
				sender.sendMessage(pf + ChatColor.DARK_GRAY + "Modifiers: " + ChatColor.GRAY + "-s (no messages)");
				return true;
			}
			Player player = (Player) sender;
			
			String time;
			if (args.length >= 1) {
				if (args[0].toLowerCase().equals("get")) {
					sender.sendMessage(pf + "Current time in '" + player.getWorld().getName() + "' is: " 
						+ ChatColor.DARK_PURPLE + TimeUtils.format24(player.getWorld().getTime()) + ChatColor.GRAY + " - "
						+ ChatColor.DARK_PURPLE + TimeUtils.format12(player.getWorld().getTime()) + ChatColor.GRAY + " - "
						+ ChatColor.DARK_PURPLE + TimeUtils.formatTicks(player.getWorld().getTime()));
					return true;
				} else if (args[0].toLowerCase().equals("day")) {
					time = "day";
				} else if (args[0].toLowerCase().equals("night")) {
					time = "night";
				} else {
					time = args[0];
				}
				
				long ticks = TimeUtils.parse(time);
				
				player.getWorld().setTime(ticks);
				if (!silent) {
					sender.sendMessage(pf + "Time set to: "
						+ ChatColor.DARK_PURPLE + TimeUtils.format24(player.getWorld().getTime()) + ChatColor.GRAY + " - "
						+ ChatColor.DARK_PURPLE + TimeUtils.format12(player.getWorld().getTime()) + ChatColor.GRAY + " - "
						+ ChatColor.DARK_PURPLE + TimeUtils.formatTicks(player.getWorld().getTime()));
				}
				
				return true;
			}
			
			return true;
		}
		return true;
	}

}
