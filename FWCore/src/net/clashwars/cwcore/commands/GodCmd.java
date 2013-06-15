package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCmd implements CommandClass {
	
	private CWCore cwc;
	
	public GodCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		Boolean on = null;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h")) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/god [player] [on|off]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Godmode a player so he can't take damage");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			args = CmdUtils.modifiedArgs(args,"-h");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s")) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s");
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			player = (Player) sender;
			cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
		}
		
		/* 1 arg (Player) */
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
		}
		
		/* 2 args (on/off) */
		if (args.length >= 2) {
			if (args[1].toLowerCase().startsWith("on")) {
				on = true;
			} else {
				on = false;
			}
		}
		
		/* null checks */
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (on == null) {
			if (cwp.getGod() == 1) {
				cwp.setGod(0);
			} else {
				cwp.setGod(1);
			}
		} else {
		/* Action */
			if (on) {
				if (cwp.getGod() == 0)
					cwp.setGod(1);
				else {
					sender.sendMessage(pf + ChatColor.RED + "Player already has godemode");
					return true;
				}
			} else {
				if (cwp.getGod() == 1)
					cwp.setGod(0);
				else {
					sender.sendMessage(pf + ChatColor.RED + "Player doesn't have godmode");
					return true;
				}
			}
		}
		
		if (cwp.getGod() == 1) {
			if (!silent) {
				player.sendMessage(pf + "God mode enabled!");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have given godmode to " + ChatColor.DARK_PURPLE + player.getDisplayName());
			}
		} else {
			if (!silent) {
				player.sendMessage(pf + "God mode disabled.");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have removed godmode from " + ChatColor.DARK_PURPLE + player.getDisplayName());
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.god", "cwcore.cmd.*", "cwcore.*" };
	}
}
