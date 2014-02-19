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

public class FlyCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public FlyCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
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
			if (cwp.getFlying()) {
				on = false;
			} else {
				on = true;
			}
		}

		//Action
		if (on) {
			if (cwp.getFlying() == false) {
				cwp.setFlying(true);
				player.setAllowFlight(true);
				player.setFlying(true);
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Player already has flying enabled");
				return true;
			}
		} else {
			if (cwp.getFlying() == true) {
				cwp.setFlying(false);
				player.setAllowFlight(false);
				player.setFlying(false);
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Player already has flying disabled");
				return true;
			}
		}

		if (!silent) {
			if (on) {
				player.sendMessage(pf + "Fly mode enabled!");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have given flymode to " + ChatColor.DARK_PURPLE + player.getDisplayName());
			} else {
				player.sendMessage(pf + "Fly mode disabled.");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have removed flymode from " + ChatColor.DARK_PURPLE + player.getDisplayName());
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.fly", "cwcore.cmd.*", "cwcore.*" };
	}
}
