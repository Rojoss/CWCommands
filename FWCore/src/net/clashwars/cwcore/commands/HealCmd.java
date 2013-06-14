package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCmd implements CommandClass {
	
	private CWCore cwc;
	
	public HealCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		Boolean setHealth = false;
		int amt = 0;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h")) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/heal [player] [amt]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Heal a player or set a player his max health.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-m" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set Max health only, doesn't heal player. (need amt)");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-a" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Also resets hunger/saturation/fireticks/exhaustion");
			args = CmdUtils.modifiedArgs(args,"-h");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s")) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s");
		}
		boolean all = false;
		if (CmdUtils.hasModifier(args,"-a")) {
			all = true;
			args = CmdUtils.modifiedArgs(args,"-a");
		}
		boolean maxOnly = false;
		if (CmdUtils.hasModifier(args,"-m")) {
			maxOnly = true;
			args = CmdUtils.modifiedArgs(args,"-m");
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
		
		/* 1 arg (player) */
		if (args.length >= 1) {
			player = cwc.getPlugin().getServer().getPlayer(args[0]);
		}
		
		/* 2 args (amount) */
		if (args.length >= 2) {
			 try {
			 	amt = Integer.parseInt(args[1]);
			 	setHealth = true;
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid amount.");
			 	return true;
			 }
		}
		
		/* null checks */
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (amt < 2) {
			if (setHealth) {
				sender.sendMessage(pf + ChatColor.RED + "Health must be more then 1.");
				return true;
			}
		}
		
		/* Action */
		CWPlayer cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
		if (setHealth) {
			player.resetMaxHealth();
			player.setMaxHealth(amt);
			cwp.setMaxHealth(amt);
		}
		if (!maxOnly) {
			player.setHealth(player.getMaxHealth());
			if (all) {
				player.setFireTicks(0);
				player.setSaturation(5.0F);
				player.setFoodLevel(20);
				player.setExhaustion(0.0F);
			}
		}
		if (!silent) {
			if (maxOnly)
				player.sendMessage(pf + "your max health has been set to: " + ChatColor.DARK_RED + (player.getMaxHealth() / 2) + "❤");
			if (!maxOnly)
				player.sendMessage(pf + "You have been healed! " + ChatColor.DARK_RED + (player.getHealth() / 2) + "❤");
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "You set " + ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.GOLD + " his health to: "
				+ ChatColor.DARK_RED + (player.getMaxHealth() / 2) + "❤");
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.heal", "cwcore.cmd.*", "cwcore.*" };
	}
}