package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WarpdelCmd implements CommandClass {
	
	private CWCore cwc;
	
	public WarpdelCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		String name = "";
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/delwarp <name>");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Delete a warp.");
			return true;
		}
		
		/* 1 arg (Name) */
		if (args.length >= 1) {
			name = args[0].toLowerCase();
		}
		
		/* null checks */
		if (name == "" || name == " " || name == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid name");
			return true;
		}
		if (!cwc.getWarpsConfig().getWarpNames().contains(name)) {
			sender.sendMessage(pf + ChatColor.RED + "Warp " + ChatColor.GRAY + name + ChatColor.RED + " not found.");
			return true;
		}
		
		/* Action */
		cwc.getWarpsConfig().deleteWarp(name);
		sender.sendMessage(pf + "Warp " + ChatColor.DARK_PURPLE + name + ChatColor.GOLD + " deleted!");
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.delwarp", "cwcore.cmd.*", "cwcore.*" };
	}
}