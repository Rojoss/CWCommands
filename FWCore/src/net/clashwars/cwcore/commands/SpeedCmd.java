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

public class SpeedCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public SpeedCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		modifiers.put("r", "Reset speed to default 0.2=walk 0.1=fly.");
		modifiers.put("w", "Set walk speed only.");
		modifiers.put("f", "Set fly speed only.");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		int amt = -1;
		float famt = .2F;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean reset = CmdUtils.hasModifier(cmdArgs, "r");
		boolean walk = CmdUtils.hasModifier(cmdArgs, "w");
		boolean fly = CmdUtils.hasModifier(cmdArgs, "f");
		if (!walk && !fly) {
			walk = true;
			fly = true;
		}

		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
		}

		//Args
		if (args.length < 1 && !reset) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		if (args.length >= 1) {
			try {
				amt = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid amount.");
				return true;
			}
			if (amt < 0 || amt > 100) {
				sender.sendMessage(pf + ChatColor.RED + "Amount must be between 0 and 100");
				return true;
			}
			famt = (float) amt / 100;
		}

		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}

		//Action
		cwp = cwc.getPlayerManager().getOrCreatePlayer(player);

		if (reset) {
			player.setWalkSpeed(0.2F);
			cwp.setWalkSpeed(0.2F);
			player.setFlySpeed(0.1F);
			cwp.setFlySpeed(0.1F);
			if (!silent) {
				player.sendMessage(pf + "your speed has been reset.");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "Speed from " + ChatColor.DARK_PURPLE + player.getName() + ChatColor.GOLD + " has been reset.");
			}
			return true;
		}

		String type = "";
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

		if (!silent) {
			player.sendMessage(pf + type + " speed has been set to: " + ChatColor.DARK_PURPLE + amt);
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + type + " speed from " + ChatColor.DARK_PURPLE + player.getName() + ChatColor.GOLD + " has been set to: "
						+ ChatColor.DARK_PURPLE + amt);
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.speed", "cwcore.cmd.*", "cwcore.*" };
	}
}