package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.Utils;

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
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public GuiCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		optionalArgs.put("p:<player>", "See enderchest of another player");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		Player ePlayer = null;
		String gui = null;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(cmdArgs,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		if (CmdUtils.getOptionalArg(cmdArgs, "p:") != null) {
			ePlayer = Utils.getFakeOfflinePlayer((CmdUtils.getOptionalArg(cmdArgs, "p:")));
		}
		
		
		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		
		//Args
		if (args.length >= 1) {
			if (args[0].startsWith("c") || args[0].startsWith("w")) {
				gui = "workbench";
			} else if (args[0].startsWith("enc")) {
				gui = "enchantment table";
			} else if (args[0].startsWith("end") || args[0].startsWith("ec") || args[0].startsWith("ch")) {
				gui = "ender chest";
			}
			if (gui == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid GUI.");
				return true;
			}
		}
		
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}
		
		
		/* Action */
		if (gui == "workbench") {
			player.openWorkbench(null, true);
		} else if (gui == "enchantment table") {
			player.openEnchanting(null, true);
		} else if (gui == "ender chest") {
			player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.5f, 1.5f);
			if (ePlayer != null) {
				if (!ePlayer.isOnline() && !ePlayer.hasPlayedBefore()) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid player for the enderchest.");
					return true;
				}
				if (ePlayer.isOnline()) {
					player.openInventory(ePlayer.getEnderChest());
			        cwc.getViewList().put(player.getName(), ePlayer);
				} else if (ePlayer.hasPlayedBefore()) {
					ItemStack[] items = ePlayer.getEnderChest().getContents();
					Inventory inventory = Bukkit.createInventory(player, InventoryType.ENDER_CHEST);
					inventory.setContents(items);
					player.openInventory(inventory);
					cwc.getViewList().put(player.getName(), ePlayer);
				}
				if (!silent) {
					player.sendMessage(pf + ChatColor.DARK_PURPLE + gui + ChatColor.GOLD + " from "
							+ ChatColor.DARK_PURPLE + ePlayer.getDisplayName() + ChatColor.GOLD + " opened.");
					if (sender.getName() != player.getName())
						sender.sendMessage(pf + "Opening the " + ChatColor.DARK_PURPLE + gui 
						+ ChatColor.GOLD + " from " + ChatColor.DARK_PURPLE + ePlayer.getDisplayName()
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
