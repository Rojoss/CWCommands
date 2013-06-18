package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleporthereCmd implements CommandClass {
	
	private CWCore cwc;
	
	public TeleporthereCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		Player player2 = null;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/teleporthere <player>");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Teleport player to yourself");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-f" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Force tp doesn't check for safe locations");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-a" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Teleport all players!");
			sender.sendMessage(ChatColor.RED + "To teleport all players you also need to add" + ChatColor.DARK_RED + " -confirm");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean all = false;
		if (CmdUtils.hasModifier(args,"-a", true)) {
			all = true;
			args = CmdUtils.modifiedArgs(args,"-a", true);
		}
		boolean force = false;
		if (CmdUtils.hasModifier(args,"-f", true)) {
			force = true;
			args = CmdUtils.modifiedArgs(args,"-f", true);
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		}
		player = (Player) sender;
		
		/* 1 arg (player2) */
		if (args.length >= 1) {
			player2 = cwc.getServer().getPlayer(args[0]);
		}

		/* null checks */
		if (player2 == null && all == false) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		
		/* Action */
		if (all) {
			if (CmdUtils.hasModifier(args,"-confirm", true)) {
				for (Player p : cwc.getServer().getOnlinePlayers()) {
					if (p != player) {
						if (force) {
							p.teleport(player.getLocation());
						} else {
							p.teleport(LocationUtils.getSafeDestination(player.getLocation()));
						}
						if (!silent) {
							p.sendMessage(pf + "You where teleported to " + player.getDisplayName());
						}
					}
				}
				player.sendMessage(pf + "All players have been teleported to you!");
			} else {
				player.sendMessage(pf + ChatColor.RED + "To teleport all players you need to add -confirm to confirm you want to teleport everyone!");
			}
		} else {
			if (force) {
				player2.teleport(player.getLocation());
			} else {
				player2.teleport(LocationUtils.getSafeDestination(player.getLocation()));
			}
			if (!silent) {
				player.sendMessage(pf + "You have teleported " + player2.getDisplayName() + ChatColor.GOLD + " to you.");
				player2.sendMessage(pf + "You where teleported to " + player.getDisplayName());
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.teleporthere", "cwcore.cmd.*", "cwcore.*" };
	}
}