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

public class GiveCmd implements CommandClass {
	
	private CWCore cwc;
	
	public GiveCmd(CWCore cwc) {
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
		if (CmdUtils.hasModifier(args,"-h") || args.length < 2) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/give <player> <item[:data]> [amt] [optional args]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Spawn items for for other players");
			sender.sendMessage(pf + "Optional args: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "name:<name>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set display name of item");
			sender.sendMessage(ChatColor.DARK_PURPLE + "lore:<lore>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set lore of item, _ for space, | for newline");
			sender.sendMessage(ChatColor.DARK_PURPLE + "dur:<durability>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the durability of items");
			sender.sendMessage(ChatColor.DARK_PURPLE + "e:<enchantment>:<level>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set enchantments like e:sharpness:10");
			sender.sendMessage(ChatColor.DARK_PURPLE + "player:<player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set player names for skulls Needs skull:3 item");
			sender.sendMessage(ChatColor.DARK_PURPLE + "color:<#RRGGBB>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set color for leather armor");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-d" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Drop items on ground instead of giving it");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-u" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "unstack items to max item stack like for soup");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-e" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Auto equip items if it's armor (WARNING: Will override!)");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s")) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s");
		}
		boolean drop = false;
		if (CmdUtils.hasModifier(args,"-d")) {
			drop = true;
			args = CmdUtils.modifiedArgs(args,"-d");
		}
		boolean unstack = false;
		if (CmdUtils.hasModifier(args,"-u")) {
			unstack = true;
			args = CmdUtils.modifiedArgs(args,"-u");
		}
		boolean equip = false;
		if (CmdUtils.hasModifier(args,"-e")) {
			equip = true;
			args = CmdUtils.modifiedArgs(args,"-e");
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
		if (drop && unstack) {
			sender.sendMessage(pf + ChatColor.RED + "You can't drop and unstack items!");
		 	return true;
		}
		
		/* Check for option args */
		item = new ItemStack(md.getItemType(), amt, md.getData());
		if (args.length >= 4) {
			item = ItemUtils.createItemFromCmd(args, md, amt, sender);
		}
		if (item == null)
		 	return true;
		
		/* Action */
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
		
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.give", "cwcore.cmd.*", "cwcore.*" };
	}
}