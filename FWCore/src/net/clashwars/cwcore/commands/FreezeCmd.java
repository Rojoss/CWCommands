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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FreezeCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public FreezeCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		modifiers.put("j", "Allow jumping while frozen");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		boolean on = false;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(args,"-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean jump = CmdUtils.hasModifier(cmdArgs, "j");
		
		
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
			if (cwp.getFrozen()) {
				on = false;
			} else {
				on = true;
			}
		}
		

		//Action
		if (on) {
			if (cwp.getFrozen() == false) {
				cwp.setFrozen(true);
				player.setWalkSpeed(0);
				if (!jump) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 128));
				}
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Player is already frozen");
				return true;
			}
		} else {
			if (cwp.getFrozen() == true) {
				cwp.setFrozen(false);
				player.setWalkSpeed(.2F);
				player.removePotionEffect(PotionEffectType.JUMP);
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Player is already unfrozen");
				return true;
			}
		}
		
		
		if (!silent) {
			if (on) {
				player.sendMessage(pf + "You have been frozen!");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have frozen " + ChatColor.DARK_PURPLE + player.getDisplayName());
			} else {
				player.sendMessage(pf + "You are no longer frozen!");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have unfrozen " + ChatColor.DARK_PURPLE + player.getDisplayName());
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.freeze", "cwcore.cmd.*", "cwcore.*" };
	}
}
