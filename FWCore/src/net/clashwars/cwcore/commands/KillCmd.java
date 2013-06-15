package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCmd implements CommandClass {
	
	private CWCore cwc;
	
	public KillCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h") || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/kill <player>");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Kill a player");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-f" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Force kill");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s")) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s");
		}
		boolean force = false;
		if (CmdUtils.hasModifier(args,"-f")) {
			force = true;
			args = CmdUtils.modifiedArgs(args,"-f");
		}
		
		/* 1 arg (Player) */
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
			cwp = cwc.getPlayerManager().getPlayer(player);
		}
		
		/* null checks */
		if (player == null || cwp == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (!force) {
			if (cwp.getGamemode() == 1 || cwp.getGod() == true) {
				sender.sendMessage(pf + ChatColor.RED + "You can't kill this player.");
				return true;
			}
		}
			
		
		/* Action */
		player.setHealth(0);
		if (!silent) {
			player.sendMessage(pf + "You where killed by " + ChatColor.DARK_PURPLE + sender.getName());
			if (sender.getName() != player.getName()) {
				sender.sendMessage(pf + "You have killed " + ChatColor.DARK_PURPLE + player.getDisplayName());
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.kill", "cwcore.cmd.*", "cwcore.*" };
	}
}