package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FreezeCmd implements CommandExecutor {
	
	private CWCore cwc;
	
	public FreezeCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("freeze")) {
			String pf = cwc.getPrefix();
			Player player = null;
			Boolean on = null;
			
			/* Modifiers */
			if (Utils.hasModifier(args,"-h")) {
				sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + lbl + ChatColor.DARK_GRAY + "  =====");
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/freeze [player] [on|off]");
				sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Freeze a player so he can't move/(jump) anymore.");
				sender.sendMessage(pf + "Modifiers: ");
				sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
				sender.sendMessage(ChatColor.DARK_PURPLE + "-j" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Allow jumping while frozen");
				args = Utils.modifiedArgs(args,"-h");
				return true;
			}
			boolean silent = false;
			if (Utils.hasModifier(args,"-s")) {
				silent = true;
				args = Utils.modifiedArgs(args,"-s");
			}
			boolean jump = false;
			if (Utils.hasModifier(args,"-j")) {
				jump = true;
				args = Utils.modifiedArgs(args,"-j");
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
				if (cwc.getFrozenPlayers().contains(player.getName())) {
					cwc.getFrozenPlayers().remove(player.getName());
				} else {
					cwc.getFrozenPlayers().add(player.getName());
				}
			} else {
			/* Action */
				if (on) {
					if (!(cwc.getFrozenPlayers().contains(player.getName())))
						cwc.getFrozenPlayers().add(player.getName());
					else {
						sender.sendMessage(pf + ChatColor.RED + "Player is already frozen");
						return true;
					}
				} else {
					if (cwc.getFrozenPlayers().contains(player.getName()))
						cwc.getFrozenPlayers().remove(player.getName());
					else {
						sender.sendMessage(pf + ChatColor.RED + "Player is not frozen");
						return true;
					}
				}
			}
			
			if (cwc.getFrozenPlayers().contains(player.getName())) {
				player.setWalkSpeed(0);
				if (!jump)
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 128));
				if (!silent) {
					player.sendMessage(pf + "You have been frozen!");
					if (sender.getName() != player.getName())
						sender.sendMessage(pf + "You have frozen " + ChatColor.DARK_PURPLE + player.getDisplayName());
				}
			} else {
				// TODO: Reset speed to stored speed in MySQL
				// TODO: Reset frozen players on server restart and when they join another server
				player.setWalkSpeed(.2F);
				if (!jump)
					player.removePotionEffect(PotionEffectType.JUMP);
				if (!silent) {
					player.sendMessage(pf + "You are no longer frozen!");
					if (sender.getName() != player.getName())
						sender.sendMessage(pf + "You have unfrozen " + ChatColor.DARK_PURPLE + player.getDisplayName());
				}
			}
		}
		return true;
	}
}
