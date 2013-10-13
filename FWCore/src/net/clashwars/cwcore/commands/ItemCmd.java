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

public class ItemCmd implements CommandClass {
	
	private CWCore cwc;
	
	public ItemCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		ItemStack item = null;
		MaterialData md = null;
		int amt = 64;
		String name = null;
		boolean equiped = false;
		
		/* Modifiers + No args */
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean drop = false;
		if (CmdUtils.hasModifier(args,"-d", true)) {
			drop = true;
			args = CmdUtils.modifiedArgs(args,"-d", true);
		}
		boolean unstack = false;
		if (CmdUtils.hasModifier(args,"-u", true)) {
			unstack = true;
			args = CmdUtils.modifiedArgs(args,"-u", true);
		}
		boolean equip = false;
		if (CmdUtils.hasModifier(args,"-e", true)) {
			equip = true;
			args = CmdUtils.modifiedArgs(args,"-e", true);
		}
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
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
		
		/* Console check */
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command. Use /give instead.");
			return true;
		} else {
			player = (Player) sender;
		}
		
		/* 1 arg (Item:Data) */
		if (args.length >= 1) {
			md = AliasUtils.getFullData(args[0]);
		}
		
		/* 2 args (Amount) */
		if (args.length >= 2) {
			try {
			 	amt = Integer.parseInt(args[1]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid amount, Must be a number.");
			 	return true;
			 }
		}
		
		/* null checks */
		if (md == null) {
			sender.sendMessage(pf + ChatColor.RED + "Item " + ChatColor.GRAY + args[0] + ChatColor.RED + " was not recognized!");
		 	return true;
		}
		if (drop && unstack) {
			sender.sendMessage(pf + ChatColor.RED + "You can't drop and unstack items!");
		 	return true;
		}
		
		/* Check for option args */
		item = new ItemStack(md.getItemType(), amt, md.getData());
		if (args.length >= 3) {
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
			if (name == null)
				player.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + args[0]);
			else
				player.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + Utils.integrateColor(name));
		}
		
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.item", "cwcore.cmd.*", "cwcore.*" };
	}
}
