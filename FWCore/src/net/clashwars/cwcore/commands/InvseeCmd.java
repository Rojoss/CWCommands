package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCmd implements CommandExecutor {
	
	private CWCore cwc;
	
	public InvseeCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("invsee")) {
			String pf = cwc.getPrefix();
			Player player = null;
			
			/* Modifiers + No args */
			if (Utils.hasModifier(args,"-h") || args.length < 1) {
				sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + lbl + ChatColor.DARK_GRAY + "  =====");
				sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Look into a player his inventory and edit it.");
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/invsee <player>");
				return true;
			}
			//TODO: Maybe add -a to see armor as well and -e to see a player his enderchest.
			
			/* Console check */
			if (!(sender instanceof Player)) {
				sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
				return true;
			}
			
			/* 1 arg (Player) */
			if (args.length >= 1) {
				player = cwc.getServer().getPlayer(args[0]);
			}
			
			/* null checks */
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
			
			/* Action */
			Player sendp = (Player) sender;
			sendp.openInventory(player.getInventory());
			sendp.sendMessage(pf + "Editing " + ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.GOLD + " his inventory.");
		}
		return true;
	}
}