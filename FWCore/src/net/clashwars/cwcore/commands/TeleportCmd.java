package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCmd implements CommandClass {
	
	private CWCore cwc;
	
	public TeleportCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		Player target = null;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h") || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/teleport <target-player> [player]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Teleport yourself or a given player to <target-player>");
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
				sender.sendMessage(pf + ChatColor.RED + "You need to specify 2 players to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		/* 1 arg (Teleport Target) */
		if (args.length >= 1) {
			target = cwc.getServer().getPlayer(args[0]);
		}
		
		/* 2 args (Player) */
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
		}

		/* null checks */
		if (target == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player target.");
			return true;
		}
		if (player == null && args.length >= 2) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		
		/* Action */
		player.teleport(target);
		if (!silent) { 
			player.sendMessage(pf + "You have been teleported to " + target.getDisplayName());
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "You have teleported " + ChatColor.DARK_PURPLE + player.getDisplayName()
					+ ChatColor.GOLD + " to " + ChatColor.DARK_PURPLE + target.getDisplayName());
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.teleport", "cwcore.cmd.*", "cwcore.*" };
	}
}