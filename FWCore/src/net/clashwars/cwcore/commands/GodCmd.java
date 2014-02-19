package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				cmdArgs;

	public GodCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		boolean on = false;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(args, "-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");

		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			player = (Player) sender;
		}

		//Args
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}
		cwp = cwc.getPlayerManager().getOrCreatePlayer(player);

		if (args.length >= 2) {
			if (args[1].toLowerCase().startsWith("on")) {
				on = true;
			} else {
				on = false;
			}
		} else {
			if (cwp.getGod() == true) {
				cwp.setGod(false);
			} else {
				cwp.setGod(true);
			}
		}

		/* Action */
		if (on) {
			if (cwp.getGod() == false) {
				cwp.setGod(true);
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Player already has godemode enabled");
				return true;
			}
		} else {
			if (cwp.getGod() == true) {
				cwp.setGod(false);
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Player already has godmode disabled");
				return true;
			}
		}

		if (!silent) {
			player.sendMessage(pf + "God mode enabled!");
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "You have given godmode to " + ChatColor.DARK_PURPLE + player.getDisplayName());
		} else {
			player.sendMessage(pf + "God mode disabled.");
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "You have removed godmode from " + ChatColor.DARK_PURPLE + player.getDisplayName());
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.god", "cwcore.cmd.*", "cwcore.*" };
	}
}
