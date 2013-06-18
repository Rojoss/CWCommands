package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiCmd implements CommandClass {
	
	private CWCore cwc;
	
	public GuiCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		String gui = null;
		Player target = null;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/gui <gui(craft|enderchest|enchant)> [player]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Show a GUI to a player.");
			sender.sendMessage(pf + "Optional arguments: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "player:<player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "See enderchest of another player");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean targetSet = false;
		boolean onlineTarget = false;
		if (CmdUtils.hasModifier(args,"player:", false)) {
			targetSet = true;
			CmdUtils.getArgIndex(args, "player:", false);
			target = CmdUtils.getOfflinePlayerFromArgs(args, "player:", cwc);
			if (target.isOnline()) {
				target = CmdUtils.getPlayerFromArgs(args, "player:", cwc);
				onlineTarget = true;
			}
			args = CmdUtils.modifiedArgs(args,"player:", false);
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
		
		/* 1 arg (Gui) */
		if (args.length >= 1) {
			if (args[0].startsWith("c") || args[0].startsWith("w")) {
				gui = "workbench";
			} else if (args[0].startsWith("end") || args[0].startsWith("ec") || args[0].startsWith("ch")) {
				gui = "enderchest";
			} else if (args[0].startsWith("enc")) {
				gui = "enchantment table";
			}
		}
		
		/* 2 args (Player) */
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
		}
		
		/* null checks */
		if (gui == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid GUI.");
			return true;
		}
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (target == null || target.hasPlayedBefore() == false) {
			if (targetSet) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid target player.");
				return true;
			}
		}
		
		/* Action */
		if (gui == "workbench") {
			player.openWorkbench(null, true);
		} else if (gui == "enchantment table") {
			player.openEnchanting(null, true);
		} else if (gui == "enderchest") {
			player.playSound(player.getLocation(), Sound.CHEST_OPEN, 5.0f, 1.0f);
			if (targetSet) {
				if (onlineTarget) {
					player.openInventory(target.getEnderChest());
			        cwc.getViewList().put(player.getName(), target);
				} else {
					ItemStack[] items = target.getEnderChest().getContents();
					Inventory inventory = Bukkit.createInventory(player, InventoryType.ENDER_CHEST);
					inventory.setContents(items);
					player.openInventory(inventory);
					cwc.getViewList().put(player.getName(), target);
				}
				if (!silent) {
					player.sendMessage(pf + ChatColor.DARK_PURPLE + gui + ChatColor.GOLD + " from "
							+ ChatColor.DARK_PURPLE + target.getDisplayName() + ChatColor.GOLD + " opened.");
					if (sender.getName() != player.getName())
						sender.sendMessage(pf + "Opening the " + ChatColor.DARK_PURPLE + gui 
						+ ChatColor.GOLD + " from " + ChatColor.DARK_PURPLE + target.getDisplayName()
						+ ChatColor.GOLD + " for " + ChatColor.DARK_PURPLE + player.getDisplayName());
				}
				return true;
			} else {
				player.openInventory(player.getEnderChest());
			}
		}
		if (!silent) {
			player.sendMessage(pf + ChatColor.DARK_PURPLE + gui + ChatColor.GOLD + " opened.");
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "Opening a " + ChatColor.DARK_PURPLE + gui 
				+ ChatColor.GOLD + " for " + ChatColor.DARK_PURPLE + player.getDisplayName());
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.gui", "cwcore.cmd.*", "cwcore.*" };
	}
}
