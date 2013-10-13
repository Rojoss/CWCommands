package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

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
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public ClearinvCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		modifiers.put("a", "Clear armor slots");
		modifiers.put("i", "Clear inventory slots");
		modifiers.put("b", "Clear hotbar slots");
		modifiers.put("f", "Clear items in first");
		modifiers.put("e", "Clear items in enderchest");
		modifiers.put("*", "Look for players on other servers.");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		String pplayer = null;
		String pitem = null;
		MaterialData md = null;
		int amt = -1;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(cmdArgs,"-h", true)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean armor = CmdUtils.hasModifier(cmdArgs, "a");
		boolean inventory = CmdUtils.hasModifier(cmdArgs, "i");
		boolean bar = CmdUtils.hasModifier(cmdArgs, "b");
		boolean hand = CmdUtils.hasModifier(cmdArgs, "f");
		boolean echest = CmdUtils.hasModifier(cmdArgs, "e");
		boolean bungee = CmdUtils.hasModifier(cmdArgs, "*");
		boolean all = true;
		if (armor || inventory || bar || hand || echest) {
			all = false;
		}
		
		
		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			pplayer = sender.getName();
			player = (Player) sender;
		}
		
		
		//Args
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
			pplayer = args[0];
		}
		
		if (args.length >= 2) {
			md = AliasUtils.getFullData(args[1]);
			pitem = args[1];
		}
		
		if (args.length >= 3) {
			try {
			 	amt = Integer.parseInt(args[2]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid amount, Must be a number.");
			 	return true;
			 }
		}
		
		
		//Action
		if (player == null && !bungee) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (md == null && args.length >= 2) {
			sender.sendMessage(pf + ChatColor.RED + "Item " + ChatColor.GRAY + args[1] + ChatColor.RED + " was not recognized!");
		 	return true;
		}
		
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