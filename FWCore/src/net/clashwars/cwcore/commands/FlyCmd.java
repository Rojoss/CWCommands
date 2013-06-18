package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCmd implements CommandClass {
	
	private CWCore cwc;
	
	public FlyCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		Boolean on = null;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h", false)) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/fly [player] [on|off]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Set fly mode for a player");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
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
			if (cwp.getFlying() == true) {
				cwp.setFlying(false);
				player.setAllowFlight(false);
				player.setFlying(false);
			} else {
				cwp.setFlying(true);
				player.setAllowFlight(true);
				player.setFlying(true);
			}
		} else {
		/* Action */
			if (on) {
				if (cwp.getFlying() == false) {
					cwp.setFlying(true);
					player.setFlying(true);
					player.setAllowFlight(true);
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
					sender.sendMessage(pf + ChatColor.RED + "Player doesn't have flying enabled");
					return true;
				}
			}
		}
		
		if (cwp.getFlying() == true) {
			if (!silent) {
				player.sendMessage(pf + "Fly mode enabled!");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have given flymode to " + ChatColor.DARK_PURPLE + player.getDisplayName());
			}
		} else {
			LocationUtils.tpToTop(cwc, player);
			if (!silent) {
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
