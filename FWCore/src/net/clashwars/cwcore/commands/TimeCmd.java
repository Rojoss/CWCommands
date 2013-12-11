package net.clashwars.cwcore.commands;

import java.util.HashMap;

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
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public TimeCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		String time = null;
		Boolean get = false;
		World world = null;
		Player player = null;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		// Check if cmd is /day or /night and force time
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
		
		if (CmdUtils.hasModifier(cmdArgs,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		

		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a world to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
			world = player.getWorld(); 
		}
		

		//Args
		if (args.length >= 1) {
			if (args[0].toLowerCase().equals("get")) {
				get = true;
			} else {
				time = args[0];
			}
			if (time == null && get == false) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid time!");
				return true;
			}
		}
		
		if (args.length >= 2) {
			world = (World) cwc.getPlugin().getServer().getWorld(args[1]);
			if (world == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid world!");
				return true;
			}
		}
		
		
		//Action
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
