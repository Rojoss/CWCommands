package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.ItemUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.Bukkit;
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
		String pplayer = null;
		String pitem = null;
		ItemStack item = null;
		MaterialData md = null;
		int amt = 64;
		String name = null;
		boolean equiped = false;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 2) {
			CmdUtils.commandHelp(sender, lbl);
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
			sender.sendMessage(ChatColor.DARK_PURPLE + "-*" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Give items to players on other servers.");
			return true;
		}
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
		boolean bungee = false;
		if (CmdUtils.hasModifier(args,"-*", true)) {
			bungee = true;
			args = CmdUtils.modifiedArgs(args,"-*", true);
		}
		
		/* 1 arg (Player) */
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
			pplayer = args[0];
		}
		
		/* 2 args (Item:Data) */
		if (args.length >= 2) {
			pitem = args[1];
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
		if (player == null && !bungee) {
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
		if (args.length >= 4 && !bungee) {
			item = ItemUtils.createItemFromCmd(args, md, amt, sender);
		} else if (args.length >= 4 && bungee) {
			sender.sendMessage(pf + ChatColor.RED + "Can't add optional args when giving items to players in other servers.");
			return true;
		}
		if (item == null && !bungee) {
		 	return true;
		}
		
		/* Action */
		if (bungee) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("Give");
				out.writeUTF(sender.getName());
				out.writeUTF(pplayer);
				out.writeUTF(pitem);
				out.writeInt(amt);
				out.writeBoolean(silent);
				out.writeBoolean(drop);
				out.writeBoolean(unstack);
				out.writeBoolean(equip);

				Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
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
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.give", "cwcore.cmd.*", "cwcore.*" };
	}
}
