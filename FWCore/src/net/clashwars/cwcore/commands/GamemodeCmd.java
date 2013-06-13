package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCmd implements CommandExecutor {
	
	private CWCore cwc;
	
	public GamemodeCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("gm")) {
			String pf = cwc.getPrefix();
			GameMode mode = null;
			Player player = null;
			CWPlayer cwp = null;
			
			/* Modifiers + No args */
			if (Utils.hasModifier(args,"-h") || args.length < 1) {
				sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + lbl + ChatColor.DARK_GRAY + "  =====");
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/gamemode <mode(0|1|2)> [player]");
				sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Change a player his gamemode to 0=survival 1=creative 2=adventure");
				sender.sendMessage(pf + "Modifiers: ");
				sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
				return true;
			}
			boolean silent = false;
			if (Utils.hasModifier(args,"-s")) {
				silent = true;
				args = Utils.modifiedArgs(args,"-s");
			}
			
			/* Console check */
			if (!(sender instanceof Player)) {
				if (args.length < 2) {
					sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!");
					return true;
				}
			} else {
				player = (Player) sender;
				cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
			}
			
			/* 1 arg (Gamemode) */
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("0")) {
					mode = GameMode.SURVIVAL;
				} else if (args[0].equalsIgnoreCase("1")) {
					mode = GameMode.CREATIVE;
				} else if (args[0].equalsIgnoreCase("2")) {
					mode = GameMode.ADVENTURE;
				}
			}
			
			/* 2 args (Player) */
			if (args.length >= 2) {
				player = cwc.getServer().getPlayer(args[1]);
			}
			
			/* null checks */
			if (mode == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid gamemode.");
				return true;
			}
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
			
			/* Action */
			cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
			player.setGameMode(mode);
			cwp.setGamemode(mode.getValue());
			if (!silent) {
				player.sendMessage(pf + "Your gamemode is set to " + ChatColor.DARK_PURPLE + mode.name().toLowerCase());
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have set " + ChatColor.DARK_PURPLE + player.getDisplayName() 
					+ ChatColor.GOLD + " his gamemode to " + ChatColor.DARK_PURPLE + mode.name().toLowerCase());
			}
		}
		return true;
	}
}
