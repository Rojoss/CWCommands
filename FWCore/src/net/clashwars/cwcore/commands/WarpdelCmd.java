package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WarpdelCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public WarpdelCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		String name = "";

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		//Args
		if (args.length >= 1) {
			name = args[0].toLowerCase();
			if (name == "" || name == " " || name == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid name");
				return true;
			}
			if (!cwc.getWarpsConfig().getWarpNames().contains(name)) {
				sender.sendMessage(pf + ChatColor.RED + "Warp " + ChatColor.GRAY + name + ChatColor.RED + " not found.");
				return true;
			}
		}

		//Action
		cwc.getWarpsConfig().deleteWarp(name);
		sender.sendMessage(pf + "Warp " + ChatColor.DARK_PURPLE + name + ChatColor.GOLD + " deleted!");
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.delwarp", "cwcore.cmd.*", "cwcore.*" };
	}
}