package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class InvseeCmd implements CommandExecutor {
	
	private CWCore cwc;
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
	
	public InvseeCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("invsee")) {
			
			if (!(sender instanceof Player)) {
				sender.sendMessage(pf + ChatColor.RED + "Player only command!");
				return true;
			}
			
			if (args.length < 1 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/invsee <player>");
				return true;
			}
			
			if (args.length >= 1) {
				
				Player p = (Player) sender;
				Player player = cwc.getPlugin().getServer().getPlayer(args[0]);
				
				if (player == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
					return true;
				}
				
				p.openInventory(player.getInventory());
			}
			return true;
		}
		return true;
	}
}
