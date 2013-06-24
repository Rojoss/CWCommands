package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.ExperienceManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpCmd implements CommandClass {
	
	private CWCore cwc;
	
	public ExpCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		String type = null;
		int amt = -1;
		Boolean levels = false;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 2) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/exp <get|add|take|set> <player> [amount[L]]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Take, Give, Set or get a player his experience.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		
		/* 1 arg (Action Type) */
		if (args.length >= 1) {
			if (args[0].startsWith("ge") || args[0].startsWith("sh")) {
				type = "get";
			} else if (args[0].startsWith("ad") || args[0].startsWith("gi")) {
				type = "add";
			} else if (args[0].startsWith("ta") || args[0].startsWith("re")) {
				type = "take";
			} else if (args[0].startsWith("se")) {
				type = "set";
			}
		}
		
		/* 2 args (Player) */
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
		}
		
		/* 3 args (Amount) */
		if (args.length >= 3 && type != "get") {
			String sAmt = args[2];
			if (sAmt.toLowerCase().endsWith("l")) {
	            levels = true;
	            sAmt = sAmt.substring(0, sAmt.length() - 1);
			}
	    	try {
	            amt = Integer.parseInt(sAmt);
	    	} catch (NumberFormatException e) {
	            sender.sendMessage(pf + ChatColor.RED + "Invalid amount.");
	            return true;
	    	}
		}
		
		/* null checks */
		if (type == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid action. It can be get, add, take or set.");
			return true;
		}
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		
		/* Action */
		ExperienceManager expMan = new ExperienceManager(player);
		
		if (type == "get") {
			sender.sendMessage(pf + ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.GOLD + " has " + ChatColor.DARK_PURPLE + expMan.getCurrentExp() 
					+ ChatColor.GOLD + " xp. His level is " + ChatColor.DARK_PURPLE + expMan.getLevelForExp(expMan.getCurrentExp()));
			sender.sendMessage(pf + "There is " + ChatColor.DARK_PURPLE + expMan.getXpForLevel(expMan.getLevelForExp(expMan.getCurrentExp()) + 1) 
					+ ChatColor.GOLD + " xp needed for level " + ChatColor.DARK_PURPLE + (expMan.getLevelForExp(expMan.getCurrentExp()) + 1)
					+ ChatColor.DARK_GRAY + " (" + ChatColor.DARK_PURPLE + (expMan.getXpForLevel(expMan.getLevelForExp(expMan.getCurrentExp()) + 1) - expMan.getCurrentExp()) 
					+ ChatColor.GOLD + " more" + ChatColor.DARK_GRAY + ")");
			return true;
		}
		
		if (amt < 0) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid amount.");
			return true;
		}
		if (type == "add") {
			if (levels) {
				player.giveExpLevels(amt);
			} else {
				expMan.changeExp(amt);
			}
			if (!silent) {
				sender.sendMessage(pf + "You have given " + ChatColor.DARK_PURPLE + amt + ChatColor.GOLD + (levels ? " levels.":" xp.") + ChatColor.GOLD + " to "
						 + ChatColor.DARK_PURPLE + player.getDisplayName());
			}
			return true;
		} else if (type == "take") {
			if (!levels && amt > expMan.getCurrentExp()) {
				amt = expMan.getCurrentExp();
			}
			amt *= -1;
			if (levels) {
				player.giveExpLevels(amt);
			} else {
				expMan.changeExp(amt);
			}
			if (!silent) {
				sender.sendMessage(pf + "You have taken " + ChatColor.DARK_PURPLE + amt + ChatColor.GOLD + (levels ? " levels.":" xp.") + ChatColor.GOLD + " from "
						 + ChatColor.DARK_PURPLE + player.getDisplayName());
			}
			return true;
		} else if (type == "set") {
			if (amt < 0) {
				sender.sendMessage(pf + ChatColor.RED + "Can't set negative amount.");
				return true;
			}
			if (levels) {
				player.setLevel(amt);
			} else {
				expMan.setExp(amt);
			}
			if (!silent) {
				sender.sendMessage(pf + "Experience from " + ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.GOLD + " has been set to "
						+ ChatColor.DARK_PURPLE + amt + ChatColor.GOLD + (levels ? " levels.":" xp."));
			}
			return true;
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.exp", "cwcore.cmd.*", "cwcore.*" };
	}
}
