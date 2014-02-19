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

public class HealCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public HealCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		modifiers.put("m", "Set Max health only, doesn't heal player. (need amt)");
		modifiers.put("n", "No hunger/saturation/fireticks/exhaustion");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		int amt = 0;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean maxOnly = CmdUtils.hasModifier(cmdArgs, "m");
		boolean noExtra = CmdUtils.hasModifier(cmdArgs, "n");

		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			player = (Player) sender;
		}

		if (args.length >= 1) {
			player = cwc.getPlugin().getServer().getPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}
		CWPlayer cwp = cwc.getPlayerManager().getOrCreatePlayer(player);

		if (args.length >= 2) {
			try {
				amt = Integer.parseInt(args[1]);
				if (amt < 2) {
					sender.sendMessage(pf + ChatColor.RED + "Health must be more then 1.");
					return true;
				}
				player.resetMaxHealth();
				player.setMaxHealth(amt);
				cwp.setMaxHealth(amt);
			} catch (NumberFormatException e) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid amount.");
				return true;
			}
		}

		//Action
		if (!maxOnly) {
			player.setHealth(player.getMaxHealth());
			if (!noExtra) {
				player.setFireTicks(0);
				player.setSaturation(5.0F);
				player.setFoodLevel(20);
				player.setExhaustion(0.0F);
			}
		}
		if (!silent) {
			if (maxOnly)
				player.sendMessage(pf + "your max health has been set to: " + ChatColor.DARK_RED + (player.getMaxHealth() / 2) + "❤");
			if (!maxOnly)
				player.sendMessage(pf + "You have been healed! " + ChatColor.DARK_RED + (player.getHealth() / 2) + "❤");
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "You set " + ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.GOLD + " his health to: "
						+ ChatColor.DARK_RED + (player.getMaxHealth() / 2) + "❤");
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.heal", "cwcore.cmd.*", "cwcore.*" };
	}
}