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

public class EffectCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public EffectCmd(CWCore cwc) {
		this.cwc = cwc;
		optionalArgs.put("p:<player>", "Apply effect on this player");
		modifiers.put("s", "No messages");
		modifiers.put("a", "Ambient effect (From  Beacons)");
		modifiers.put("r", "Restricted wont override previous effects");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		PotionEffectType effect = null;
		int secs = 60;
		int strength = 1;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean ambient = CmdUtils.hasModifier(cmdArgs, "a");
		boolean restricted = CmdUtils.hasModifier(cmdArgs, "r");
		boolean targetSet = CmdUtils.hasOptionalArg(cmdArgs, "p:");
		String ps = CmdUtils.getOptionalArg(cmdArgs, "p:");
		if (ps != null) {
			player = cwc.getServer().getPlayer(ps);
		}
		
		//Console
		if (!(sender instanceof Player)) {
			if (targetSet == false) {
				sender.sendMessage(pf + ChatColor.RED + "Specify a player to apply the effect on for console usage.");
				return true;
			}
		} else {
			if (player == null) {
				player = (Player) sender;
			}
		}
		
		//Args
		if (args.length >= 1) {
			effect = AliasUtils.findPotion(args[0]);
		}
		
		if (args.length >= 2) {
			try {
			 	secs = Integer.parseInt(args[1]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid duration, Must be a number.");
			 	return true;
			 }
		}
		
		if (args.length >= 3) {
			try {
			 	strength = Integer.parseInt(args[2]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid strength, Must be a number.");
			 	return true;
			 }
		}
		
		if (effect == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid effect.");
			return true;
		}
		

		//Action
		int s = strength;
		if (strength > 0) {
			s = strength - 1;
		}
		
		PotionEffect ef = new PotionEffect(effect, secs * 20, s, ambient);
		
		if (!restricted) {
			if (player.hasPotionEffect(effect)) {
				player.removePotionEffect(effect);
			}
		}
		player.addPotionEffect(ef);
		
		if (!silent) {
			if (player.getName().equalsIgnoreCase(sender.getName())) {
				player.sendMessage(pf + "Potion effect " + ChatColor.DARK_PURPLE + args[0] + " " + ChatColor.DARK_GRAY + strength 
						+ ChatColor.GOLD + " applied for " + ChatColor.GRAY + secs + ChatColor.GOLD + " seconds.");
			} else {
				sender.sendMessage(pf + "Potion effect " + ChatColor.DARK_PURPLE + args[0] + " " + ChatColor.DARK_GRAY + strength 
						+ ChatColor.GOLD + " applied for " + ChatColor.GRAY + secs + ChatColor.GOLD + " seconds on " + player.getDisplayName());
			}
			
		}
		
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.effect", "cwcore.cmd.*", "cwcore.*" };
	}
}
