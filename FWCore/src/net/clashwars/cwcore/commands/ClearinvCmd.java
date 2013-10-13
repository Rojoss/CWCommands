package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.InvUtils;

import org.bukkit.Bukkit;
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
		String pplayer = null;
		String pitem = null;
		MaterialData md = null;
		int amt = -1;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h", false)) {
			CmdUtils.commandHelp(sender, lbl);
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-a" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Clear armor");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-i" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Clear inventory");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-b" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Clear hotbar");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-f" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Clear item in fist/hand");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-e" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Clear items in enderchest");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-*" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Clear items from players on other servers.");
			return true;
		}
		boolean all = true;
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean armor = false;
		if (CmdUtils.hasModifier(args,"-a", true)) {
			armor = true;
			args = CmdUtils.modifiedArgs(args,"-a", true);
		}
		boolean inventory = false;
		if (CmdUtils.hasModifier(args,"-i", true)) {
			inventory = true;
			args = CmdUtils.modifiedArgs(args,"-i", true);
		}
		boolean bar = false;
		if (CmdUtils.hasModifier(args,"-b", true)) {
			bar = true;
			args = CmdUtils.modifiedArgs(args,"-b", true);
		}
		boolean hand = false;
		if (CmdUtils.hasModifier(args,"-f", true)) {
			hand = true;
			args = CmdUtils.modifiedArgs(args,"-f", true);
		}
		boolean echest = false;
		if (CmdUtils.hasModifier(args,"-e", true)) {
			echest = true;
			args = CmdUtils.modifiedArgs(args,"-e", true);
		}
		boolean bungee = false;
		if (CmdUtils.hasModifier(args,"-*", true)) {
			bungee = true;
			args = CmdUtils.modifiedArgs(args,"-*", true);
		}
		if (armor || inventory || bar || hand || echest) {
			all = false;
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			pplayer = sender.getName();
			player = (Player) sender;
		}
		
		/* 1 arg (Player) */
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
			pplayer = args[0];
		}
		
		/* 2 args material:data) */
		if (args.length >= 2) {
			md = AliasUtils.getFullData(args[1]);
			pitem = args[1];
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
		if (player == null && !bungee) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (md == null && args.length >= 2) {
			sender.sendMessage(pf + ChatColor.RED + "Item " + ChatColor.GRAY + args[1] + ChatColor.RED + " was not recognized!");
		 	return true;
		}
		
		/* Action */
		if (bungee) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("ClearInv");
				out.writeUTF(sender.getName());
				out.writeUTF(pplayer);
				out.writeUTF(pitem);
				out.writeInt(amt);
				out.writeBoolean(silent);
				out.writeBoolean(armor);
				out.writeBoolean(inventory);
				out.writeBoolean(bar);
				out.writeBoolean(hand);
				out.writeBoolean(echest);
				out.writeBoolean(all);

				Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			if (all) {
				InvUtils.clearInventorySlots(player, false, 0, -1, md, amt);
				player.getInventory().setArmorContents(null);
			} else {
				if (armor)
					player.getInventory().setArmorContents(null);
				if (inventory)
					InvUtils.clearInventorySlots(player, false, 9, 36, md, amt);
				if (bar)
					InvUtils.clearInventorySlots(player, false, 0, 9, md, amt);
				if (hand)
					InvUtils.clearInventorySlots(player, false, player.getInventory().getHeldItemSlot(), player.getInventory().getHeldItemSlot() + 1, md, amt);
				if (echest)
					InvUtils.clearInventorySlots(player, true, 0, -1, md, amt);
			}
			
			if (!silent) {
				player.sendMessage(pf + "Items in your " + (echest ? "enderchest" : "inventory") + " have been cleared!");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have cleared items in " + ChatColor.DARK_PURPLE + player.getDisplayName() 
					+ ChatColor.GOLD + " his " + (echest ? "enderchest" : "inventory") + ".");
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.clearinv", "cwcore.cmd.*", "cwcore.*" };
	}
}