package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCmd implements CommandClass {
	
	private CWCore cwc;
	
	public SpeedCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		int amt = -1;
		float famt = .2F;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h") || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/speed <amt(0<>100)> [player]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Change a player his speed");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-r" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Reset speed");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-w" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set walk speed");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-w" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set fly speed");
			return true;
		}
		boolean walk = true;
		boolean fly = true;
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s")) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s");
		}
		boolean reset = false;
		if (CmdUtils.hasModifier(args,"-r")) {
			reset = true;
			args = CmdUtils.modifiedArgs(args,"-r");
		}

		if (CmdUtils.hasModifier(args,"-w")) {
			walk = true;
			fly = false;
			args = CmdUtils.modifiedArgs(args,"-w");
		}
		if (CmdUtils.hasModifier(args,"-f")) {
			fly = true;
			walk = false;
			args = CmdUtils.modifiedArgs(args,"-f");
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		/* 1 arg (amount) */
		if (args.length >= 1) {
			try {
	            amt = Integer.parseInt(args[0]);
	        } catch (NumberFormatException e) {
	        	sender.sendMessage(pf + ChatColor.RED + "Invalid amount.");
	            return true;
	        }
		}
		
		/* 2 args (Player) */
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
		}

		/* null checks */
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (amt < 0 || amt > 100) {
			sender.sendMessage(pf + ChatColor.RED + "Amount must be between 0 and 100");
			return true;
		}
		famt = (float) amt / 100;
		
		
		/* Action */
		cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
		player.sendMessage(pf + "amt: " + amt + " , famt: " + famt);
		if (reset) {
			famt = .2F;
		}
		
		String type = "";
		String r = "set";
		if (walk) {
			player.setWalkSpeed(famt);
			cwp.setWalkSpeed(famt);
			type = "Walk";
		}
		if (fly) {
			player.setFlySpeed(famt);
			cwp.setFlySpeed(famt);
			type = "Fly";
		}
		if (fly && walk)
			type = "Walk and fly";
		if (reset)
			r = "reset";
		
		if (!silent) { 
			player.sendMessage(pf + type + "speed has been " + r + " to: "
					+ ChatColor.DARK_PURPLE + amt);
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + type + "speed from " + ChatColor.DARK_PURPLE + player.getName() 
					+ ChatColor.GOLD + " has been " + r +  " to: "  + ChatColor.DARK_PURPLE + amt);
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.speed", "cwcore.cmd.*", "cwcore.*" };
	}
}