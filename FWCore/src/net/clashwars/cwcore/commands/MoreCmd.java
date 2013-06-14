package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MoreCmd implements CommandClass {
	
	private CWCore cwc;
	
	public MoreCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		int amt = 0;
		ItemStack item = null;
		MaterialData md = null;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h")) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/more [amt]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Create more items of the item in hand.");
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
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
			if (player.getItemInHand().getData().getItemType() != Material.AIR) {
				md = player.getItemInHand().getData();
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Can't give more air!");
				return true;
			}
		}
		
		/* No args (get max stacksize)*/
		if (args.length < 1) {
			amt = (player.getItemInHand().getMaxStackSize() - player.getItemInHand().getAmount());
		}
		
		/* 1 arg (amount) */
		if (args.length >= 1) {
			player.sendMessage(pf + amt);
			try {
			 	amt = Integer.parseInt(args[0]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid amount.");
			 	return true;
			 }
			player.sendMessage(pf + amt);
		}
		
		/* Action */
		item = md.toItemStack(amt);
		player.getInventory().addItem(item);
		String type = item.getType().name().toLowerCase().replace("_", "");
		if (!silent) {
			player.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + ChatColor.GOLD + " more " 
			+ ChatColor.DARK_PURPLE + type);
		}
		
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.more", "cwcore.cmd.*", "cwcore.*" };
	}
}