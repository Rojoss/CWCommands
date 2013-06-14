package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.TimeUtils;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCmd implements CommandClass {
	
	private CWCore cwc;
	
	public TimeCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		String time = null;
		Boolean get = false;
		World world = null;
		Player player = null;
		
		/* Check if cmd is /day or /night and force time*/
		if (sender instanceof Player && args.length < 1) {
			if (lbl.equalsIgnoreCase("day")) {
				long ticks = TimeUtils.parse("day");
				((Player) sender).getWorld().setTime(ticks);
				sender.sendMessage(pf + "Time set to: "+ ChatColor.DARK_PURPLE + "day");
				return true;
			}
			if (lbl.equalsIgnoreCase("night")) {
				long ticks = TimeUtils.parse("night");
				((Player) sender).getWorld().setTime(ticks);
				sender.sendMessage(pf + "Time set to: "+ ChatColor.DARK_PURPLE + "night");
				return true;
			}
		}
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h") || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/time <get|500ticks|24:00|12:00|day|night> [world]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Set the time of the world or from the given world.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s")) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s");
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a world to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
			world = player.getWorld(); 
		}
		
		/* 1 arg (Time) */
		if (args.length >= 1) {
			if (args[0].toLowerCase().equals("get")) {
				get = true;
			} else {
				time = args[0];
			}
		}
		
		/* 2 args (world) */
		if (args.length >= 2) {
			world = (World) cwc.getPlugin().getServer().getWorld(args[1]);
		}
		
		/* null checks */
		if (world == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid world!");
			return true;
		}
		if (time == null && get == false) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid time!");
			return true;
		}
		
		/* Action */
		if (get) {
			sender.sendMessage(pf + "Current time in '" + world.getName() + "' is: " 
			+ ChatColor.DARK_PURPLE + TimeUtils.format24(world.getTime()) + ChatColor.GRAY + " - "
			+ ChatColor.DARK_PURPLE + TimeUtils.format12(world.getTime()) + ChatColor.GRAY + " - "
			+ ChatColor.DARK_PURPLE + TimeUtils.formatTicks(world.getTime()));
		} else {
			long ticks = TimeUtils.parse(time);
			world.setTime(ticks);
			if (!silent) {
				sender.sendMessage(pf + "Time set to: "
				+ ChatColor.DARK_PURPLE + TimeUtils.format24(world.getTime()) + ChatColor.GRAY + " - "
				+ ChatColor.DARK_PURPLE + TimeUtils.format12(world.getTime()) + ChatColor.GRAY + " - "
				+ ChatColor.DARK_PURPLE + TimeUtils.formatTicks(world.getTime()));
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.time", "cwcore.cmd.*", "cwcore.*" };
	}
}
