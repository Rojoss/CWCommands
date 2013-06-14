package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class RemovepotsCmd implements CommandClass {
	
	private CWCore cwc;
	
	public RemovepotsCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h")) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/removepots [player]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Remove all potion effects from a player.");
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
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		if (!silent) {
			player.sendMessage(pf + "All your potion effects have been removed!");
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "All potion effects from " + ChatColor.DARK_PURPLE + player.getDisplayName() 
				+ ChatColor.GOLD + " have been removed!");
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.removepots", "cwcore.cmd.*", "cwcore.*" };
	}
}