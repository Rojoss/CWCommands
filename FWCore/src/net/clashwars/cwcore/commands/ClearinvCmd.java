package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.InvUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class ClearinvCmd implements CommandClass {
	
	private CWCore cwc;
	
	public ClearinvCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		MaterialData md = null;
		int amt = -1;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h")) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/" + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/clearinv [player] [item[:data]] [amt]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Clear a player his inventory.");
			sender.sendMessage(pf + "Example: " + ChatColor.GRAY + "/ci -a -h - will clear all items at armor/fist");
			sender.sendMessage(pf + "Example2: " + ChatColor.GRAY + "/ci worst dia 30 -h - Will clear 30 dia in hotbar");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-a" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Clear armor");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-i" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Clear inventory");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-b" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Clear hotbar");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-f" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Clear item in fist/hand");
			return true;
		}
		boolean all = true;
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s")) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s");
		}
		boolean armor = false;
		if (CmdUtils.hasModifier(args,"-a")) {
			armor = true;
			args = CmdUtils.modifiedArgs(args,"-a");
		}
		boolean inventory = false;
		if (CmdUtils.hasModifier(args,"-i")) {
			inventory = true;
			args = CmdUtils.modifiedArgs(args,"-i");
		}
		boolean bar = false;
		if (CmdUtils.hasModifier(args,"-b")) {
			bar = true;
			args = CmdUtils.modifiedArgs(args,"-b");
		}
		boolean hand = false;
		if (CmdUtils.hasModifier(args,"-f")) {
			hand = true;
			args = CmdUtils.modifiedArgs(args,"-f");
		}
		if (armor || inventory || bar || hand) {
			all = false;
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
		
		/* 2 args material:data) */
		if (args.length >= 2) {
			md = AliasUtils.getFullData(args[1]);
		}
		
		/* 3 args (Amount) */
		if (args.length >= 3) {
			try {
			 	amt = Integer.parseInt(args[2]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid amount, Must be a number.");
			 	return true;
			 }
		}
		
		/* null checks */
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (md == null && args.length >= 2) {
			sender.sendMessage(pf + ChatColor.RED + "Item " + ChatColor.GRAY + args[1] + ChatColor.RED + " was not recognized!");
		 	return true;
		}
		
		/* Action without material */
		if (all) {
			InvUtils.clearInventorySlots(player, 0, -1, md, amt);
			player.getInventory().setArmorContents(null);
		} else {
			if (armor)
				player.getInventory().setArmorContents(null);
			if (inventory)
				InvUtils.clearInventorySlots(player, 9, 36, md, amt);
			if (bar)
				InvUtils.clearInventorySlots(player, 0, 9, md, amt);
			if (hand)
				InvUtils.clearInventorySlots(player, player.getInventory().getHeldItemSlot(), player.getInventory().getHeldItemSlot() + 1, md, amt);
		}
		
		if (!silent) {
			player.sendMessage(pf + "Items in your inventory have been cleared!");
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "You have cleared items in " + ChatColor.DARK_PURPLE + player.getDisplayName() 
				+ ChatColor.GOLD + " his inventory!");
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.clearinv", "cwcore.cmd.*", "cwcore.*" };
	}
}