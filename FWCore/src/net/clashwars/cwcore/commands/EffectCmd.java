package net.clashwars.cwcore.commands;

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
	
	public EffectCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		PotionEffectType effect = null;
		int secs = 60;
		int strength = 1;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/effect <effect> [duration] [strength]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Apply a potion effect.");
			sender.sendMessage(pf + "Optional args: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "player:<player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Apply effect on this player.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-a" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Ambient effect (From  Beacons)");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-r" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Restricted wont override previous effects.");
			return true;
		}
		
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean ambient = false;
		if (CmdUtils.hasModifier(args,"-a", true)) {
			ambient = true;
			args = CmdUtils.modifiedArgs(args,"-a", true);
		}
		boolean restricted = false;
		if (CmdUtils.hasModifier(args,"-r", true)) {
			restricted = true;
			args = CmdUtils.modifiedArgs(args,"-r", true);
		}
		
		boolean targetSet = false;
		if (CmdUtils.hasModifier(args,"player:", false)) {
			targetSet = true;
			player = CmdUtils.getPlayer(args, "player:", cwc);
			args = CmdUtils.modifiedArgs(args,"player:", false);
		} else {
			
		}
		
		/* Console check */
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
		
		/* 1 arg (Effect) */
		if (args.length >= 1) {
			effect = AliasUtils.findPotion(args[0]);
		}
		
		/* 2 args (duration) */
		if (args.length >= 2) {
			try {
			 	secs = Integer.parseInt(args[1]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid duration, Must be a number.");
			 	return true;
			 }
		}
		
		/* 3 args (strength) */
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
		
		/* Action */
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
