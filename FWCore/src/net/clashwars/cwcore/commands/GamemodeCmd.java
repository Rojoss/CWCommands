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
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
	
	public GamemodeCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("gm")) {
			
			boolean silent = false;
			if (Utils.hasModifier(args,"-s")) {
				silent = true;
				args = Utils.modifiedArgs(args,"-s");
			}
			
			if (!(sender instanceof Player)) {
				if (args.length < 2) {
					sender.sendMessage(pf + ChatColor.RED + "`You need to specify a name to use this on the console!");
					return true;
				}
			}
			
			if (args.length < 1 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/gm <0|1|2> [player]");
				sender.sendMessage(pf + ChatColor.DARK_GRAY + "Modifiers: " + ChatColor.GRAY + "-s (no messages)");
				return true;
			}
			
			GameMode mode = null;
			if (args[0].equalsIgnoreCase("0")) {
				mode = GameMode.SURVIVAL;
			} else if (args[0].equalsIgnoreCase("1")) {
				mode = GameMode.CREATIVE;
			} else if (args[0].equalsIgnoreCase("2")) {
				mode = GameMode.ADVENTURE;
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Invalid gamemode.");
				return true;
			}
			
			Player player = null;
			//CWPlayer cwp = null;
			if (args.length > 1) {
				player = cwc.getPlugin().getServer().getPlayer(args[1]);
				
				if (player == null) {
			    	sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			    	return true;
				}
				
				//cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
				
			} else {
				player = (Player) sender;
				//cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
				player.setGameMode(mode);
				//cwp.setGamemode(mode.getValue());
				if (!silent)
					sender.sendMessage(pf + "Changed your gamemode to " + ChatColor.DARK_PURPLE + mode.name().toLowerCase());
				return true;
			}
			
			player.setGameMode(mode);
			//cwp.setGamemode(mode.getValue());
			if (!silent) {
				sender.sendMessage(pf + "You have set " + ChatColor.DARK_PURPLE + player.getDisplayName() 
					+ ChatColor.GOLD + " his gamemode to " + ChatColor.DARK_PURPLE + mode.name().toLowerCase());
				player.sendMessage(pf + "Your gamemode is changed to " + ChatColor.DARK_PURPLE + mode.name().toLowerCase());
			}
			return true;
		}
		return true;
	}

}
