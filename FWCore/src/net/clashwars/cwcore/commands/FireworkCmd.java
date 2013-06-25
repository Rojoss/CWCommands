package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.ItemUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class FireworkCmd implements CommandClass {
	
	private CWCore cwc;
	
	public FireworkCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		ItemStack item = null;
		MaterialData md = null;
		int amt = 1;
		String name = null;
		boolean equiped = false;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 2) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/firework <amt> [player]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Spawn items for for other players");
			sender.sendMessage(pf + "Optional args: ");
			sender.sendMessage(pf + ChatColor.GRAY + "If any of the optional args are missing it will randomize it! :)");
			sender.sendMessage(ChatColor.DARK_PURPLE + "p:<power>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the power: 0, 1, 2, 3");
			sender.sendMessage(ChatColor.DARK_PURPLE + "e:<effect>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the effect type: sball, bball, creep, burst, star");
			sender.sendMessage(ChatColor.DARK_PURPLE + "a:<trail|spark>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the after effect: trail or spark");
			sender.sendMessage(ChatColor.DARK_PURPLE + "c:<#RRGGBB>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Add colors can add multiple times.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "fc:<#RRGGBB>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Add a fade color can add multiple times.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-l" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Launch the rocket instead of giving it.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-e" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Create the firework effect doesn't give or launch it just effect.");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean launch = false;
		if (CmdUtils.hasModifier(args,"-l", true)) {
			launch = true;
			args = CmdUtils.modifiedArgs(args,"-l", true);
		}
		boolean effectOnly = false;
		if (CmdUtils.hasModifier(args,"-e", true)) {
			effectOnly = true;
			args = CmdUtils.modifiedArgs(args,"-e", true);
		}
		/*
		boolean powerSet = false;
		if (CmdUtils.hasModifier(args,"p:", false)) {
			powerSet = true;
			CmdUtils.getArgIndex(args, "p:", false);
			
			String[] splt = args[CmdUtils.getArgIndex(args, "p:", false)].split(":");
        	if (splt.length > 1) {
				try {
				 	power = Integer.parseInt(splt[1]);
				 } catch (NumberFormatException e) {
				 	sender.sendMessage(pf + ChatColor.RED + "Invalid power amount, Must be a number.");
				 	return true;
				 }
        	}
			args = CmdUtils.modifiedArgs(args,"p:", false);
		}
		boolean effectSet = false;
		if (CmdUtils.hasModifier(args,"e:", false)) {
			effectSet = true;
			CmdUtils.getArgIndex(args, "e:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "e:", false)].split(":");
        	if (splt.length > 1) {
				 effect = splt[1];
        	}
			args = CmdUtils.modifiedArgs(args,"e:", false);
		}
		boolean afterEffectSet = false;
		if (CmdUtils.hasModifier(args,"a:", false)) {
			afterEffectSet = true;
			CmdUtils.getArgIndex(args, "a:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "a:", false)].split(":");
        	if (splt.length > 1) {
				 afterEffect = splt[1];
        	}
			args = CmdUtils.modifiedArgs(args,"a:", false);
		}
		
		/* 1 arg (Player) */
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
		}
		
		/* 2 args (Item:Data) */
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
		if (md == null) {
			sender.sendMessage(pf + ChatColor.RED + "Item " + ChatColor.GRAY + args[1] + ChatColor.RED + " was not recognized!");
		 	return true;
		}
		//if (drop && unstack) {
			sender.sendMessage(pf + ChatColor.RED + "You can't drop and unstack items!");
		 	return true;
		//}
		
		/* Check for option args
		item = new ItemStack(md.getItemType(), amt, md.getData());
		if (args.length >= 4) {
			item = ItemUtils.createItemFromCmd(args, md, amt, sender);
		}
		if (item == null)
		 	return true;
		 */
		/* Action
		if (equip) {
			if (item.getType().name().endsWith("HELMET")) {
				player.getInventory().setHelmet(item);
				equiped = true;
				drop = false;
				unstack = false;
			} else if (item.getType().name().endsWith("CHESTPLATE")) {
				player.getInventory().setChestplate(item);
				equiped = true;
				drop = false;
				unstack = false;
			} else if (item.getType().name().endsWith("LEGGINGS")) {
				player.getInventory().setLeggings(item);
				equiped = true;
				drop = false;
				unstack = false;
			} else if (item.getType().name().endsWith("BOOTS")) {
				player.getInventory().setBoots(item);
				equiped = true;
				drop = false;
				unstack = false;
			}
		}
		if (!drop && !unstack && !equiped)
			player.getInventory().addItem(item);
		if (drop && !equiped) {
			Location loc = player.getEyeLocation().add(player.getLocation().getDirection());
			loc.getWorld().dropItem(loc, item);
		}
		if (unstack && !equiped) {
			for (int i = 0; i < amt; i++) {
				ItemStack uItem = item;
				uItem.setAmount(1);
				player.getInventory().addItem(uItem);
			}
		}
		
		name = item.getItemMeta().getDisplayName();
		if (!silent) {
			if (name == null) {
				sender.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + args[1] 
				+ ChatColor.GOLD + " to " + ChatColor.DARK_PURPLE + player.getDisplayName());
				player.sendMessage(pf + "You received " + ChatColor.DARK_PURPLE + amt + " " + args[1] 
						+ ChatColor.GOLD + " from " + ChatColor.DARK_PURPLE + sender.getName());
			} else {
				sender.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + Utils.integrateColor(name) 
				+ ChatColor.GOLD + " to " + ChatColor.DARK_PURPLE + player.getDisplayName());
				player.sendMessage(pf + "You received " + ChatColor.DARK_PURPLE + amt + " " + Utils.integrateColor(name) 
				+ ChatColor.GOLD + " from " + ChatColor.DARK_PURPLE + sender.getName());
			}
		}
		*/
		//return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.firework", "cwcore.cmd.*", "cwcore.*" };
	}
}
