package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.ItemUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class HatCmd implements CommandClass {
	
	private CWCore cwc;
	
	public HatCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		MaterialData md = null;
		ItemStack item = null;
		int amt = 1;
		
		/* Modifiers + No args */
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
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		/* 1 arg (material:data) */
		if (args.length >= 1) {
			md = AliasUtils.getFullData(args[0]);
		}
		
		/* 2 args (Player) */
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
		}

		/* null checks */
		if (md == null) {
			sender.sendMessage(pf + ChatColor.RED + "Item " + ChatColor.GRAY + args[0] + ChatColor.RED + " was not recognized!");
		 	return true;
		}
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
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
		if (item.getType().getId() == 0) {
			player.getInventory().setHelmet(item);
			player.sendMessage(pf + "Your hat has been removed.");
			return true;
		}
		
		player.getInventory().setHelmet(item);
		if (!silent) { 
			String name = item.getItemMeta().getDisplayName();
			if (name == null)
				player.sendMessage(pf + "Your hat has been set to " + ChatColor.DARK_PURPLE + args[0]);
			else
				player.sendMessage(pf + "Your hat has been set to " + ChatColor.DARK_PURPLE + Utils.integrateColor(name));
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.hat", "cwcore.cmd.*", "cwcore.*" };
	}
}