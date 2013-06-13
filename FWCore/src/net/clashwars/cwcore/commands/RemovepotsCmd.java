package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;


public class RemovepotsCmd implements CommandExecutor {
	
	private CWCore cwc;
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
	
	public RemovepotsCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("removepots")) {
			
			boolean silent = false;
			if (Utils.hasModifier(args,"-s")) {
				silent = true;
				args = Utils.modifiedArgs(args,"-s");
			}
			
			Player player = (Player) sender;
			if (args.length < 1) {
				for (PotionEffect effect : player.getActivePotionEffects()) {
					player.removePotionEffect(effect.getType());
				}
				if (!silent)
					sender.sendMessage(pf + "Your potion effects have been removed!");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/removepots [player]");
				sender.sendMessage(pf + ChatColor.DARK_GRAY + "Modifiers: " + ChatColor.GRAY + "-s (no messages)");
				return true;
			}
			
			if (args.length >= 1) {
				
				player = cwc.getPlugin().getServer().getPlayer(args[0]);
				
				if (player == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
					return true;
				}
				
				for (PotionEffect effect : player.getActivePotionEffects()) {
					player.removePotionEffect(effect.getType());
				}
				
				if (!silent) {
					sender.sendMessage(pf + "All potion effects from " + ChatColor.DARK_PURPLE + player.getDisplayName() 
							+ ChatColor.GOLD + " have been removed!");
					player.sendMessage(pf + "All your potion effects have been removed!");
				}
				return true;
			}
			return true;
		}
		return true;
	}
}
