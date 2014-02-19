package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RemovepotsCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public RemovepotsCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		PotionEffectType effect = null;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-h", false)) {
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

		if (args.length >= 2) {
			effect = AliasUtils.findPotion(args[1]);
			if (effect == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid potion effect type.");
				return true;
			}
		}

		//Action
		if (effect != null) {
			player.removePotionEffect(effect);
			if (!silent) {
				player.sendMessage(pf + "Potion effect " + ChatColor.DARK_PURPLE + args[1] + ChatColor.GOLD + " removed.");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "Potion effect " + ChatColor.DARK_PURPLE + args[1] + ChatColor.GOLD + " removed from "
							+ ChatColor.DARK_PURPLE + player.getDisplayName());
			}
		} else {
			for (PotionEffect e : player.getActivePotionEffects()) {
				player.removePotionEffect(e.getType());
			}
			if (!silent) {
				player.sendMessage(pf + "All your potion effects have been removed!");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "All potion effects from " + ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.GOLD
							+ " removed!");
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.removepots", "cwcore.cmd.*", "cwcore.*" };
	}
}