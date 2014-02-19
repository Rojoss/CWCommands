package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpsetCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public WarpsetCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		String name = "";

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		//Console
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
		}

		//Args
		if (args.length >= 1) {
			name = args[0].toLowerCase();
			if (name == "" || name == " " || name == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid name");
				return true;
			}
			if (name.length() > 25) {
				sender.sendMessage(pf + ChatColor.RED + "Warpname can't be more then 25 characters.");
				return true;
			}
		}

		//Action
		cwc.getWarpsConfig().createWarp(name, player.getLocation());
		player.sendMessage(pf + "Warp " + ChatColor.DARK_PURPLE + name + ChatColor.GOLD + " set!");
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.setwarp", "cwcore.cmd.*", "cwcore.*" };
	}
}