package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.components.CustomItem;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
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
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public ItemCmd(CWCore cwc) {
		this.cwc = cwc;
		optionalArgs.put("name:<name>", "Set display name of item");
		optionalArgs.put("lore:<lore>", "Set lore of item, _ for space, | for newline");
		optionalArgs.put("dur:<durability>", "Set the durability of items");
		optionalArgs.put("e:<enchant>.<lvl>[,<e>.<lvl>]", "Set enchantments like e:sharp.10,dur.1");
		optionalArgs.put("pe:<effect>.<dur>.<lvl>[,<e>.<d>.<l>]", "Set potion effects like pe:speed.10.1,jump.10.2");
		optionalArgs.put("head:<player>", "Set player names for heads Needs 144:3 item");
		optionalArgs.put("color:<#RRGGBB>", "Set color for leather armor");
		modifiers.put("s", "No messages");
		modifiers.put("d", "Drop items on ground instead of giving it");
		modifiers.put("u", "unstack items to max item stack like for soup");
		modifiers.put("e", "Auto equip items if it's armor (WARNING: Will override!");
		modifiers.put("h", "Force set item as hat (helmet)");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		ItemStack item = null;
		CustomItem ci = null;
		MaterialData md = null;
		int amt = 64;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(cmdArgs,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean drop = CmdUtils.hasModifier(cmdArgs, "d");
		boolean unstack = CmdUtils.hasModifier(cmdArgs, "u");
		boolean equip = CmdUtils.hasModifier(cmdArgs, "e");
		boolean hat = CmdUtils.hasModifier(cmdArgs, "e");
		String name = CmdUtils.getOptionalArg(cmdArgs, "name:");
		String lore = CmdUtils.getOptionalArg(cmdArgs, "lore:");
		String dur = CmdUtils.getOptionalArg(cmdArgs, "dur:");
		String enchant = CmdUtils.getOptionalArg(cmdArgs, "e:");
		String effect = CmdUtils.getOptionalArg(cmdArgs, "pe:");
		String head = CmdUtils.getOptionalArg(cmdArgs, "head:");
		String color = CmdUtils.getOptionalArg(cmdArgs, "color:");
		
		
		//Console
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command. Use /give instead.");
			return true;
		} else {
			player = (Player) sender;
		}
		
		
		//Args
		if (args.length >= 1) {
			md = AliasUtils.getFullData(args[0]);
			if (md == null) {
				sender.sendMessage(pf + ChatColor.RED + "Item " + ChatColor.GRAY + args[0] + ChatColor.RED + " was not recognized!");
			 	return true;
			}
		}
		
		if (args.length >= 2) {
			try {
			 	amt = Integer.parseInt(args[1]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid amount, Must be a number.");
			 	return true;
			 }
		}
		
		
		//Action
		item = new ItemStack(md.getItemType(), amt, md.getData());
		
		ci = new CustomItem(item);
		
		if (name != null) ci.setName(name);
		if (lore != null) ci.setLore(lore);
		if (dur != null) ci.setDurability(dur);
		if (enchant != null) ci.setEnchants(enchant);
		if (effect != null) ci.setPotionEffects(effect);
		if (head != null) ci.setHead(head);
		if (color != null) ci.setLeatherColor(color);
		item = ci.getItem();
		
		/* Action */
		if (equip) {
			if (item.getType().name().endsWith("HELMET")) {
				player.getInventory().setHelmet(item);
			} else if (item.getType().name().endsWith("CHESTPLATE")) {
				player.getInventory().setChestplate(item);
			} else if (item.getType().name().endsWith("LEGGINGS")) {
				player.getInventory().setLeggings(item);
			} else if (item.getType().name().endsWith("BOOTS")) {
				player.getInventory().setBoots(item);
			}
		} else if (hat) {
			player.getInventory().setHelmet(item);
		} else if (drop) {
			Location loc = player.getEyeLocation().add(player.getLocation().getDirection());
			loc.getWorld().dropItem(loc, item);
		} else if (unstack) {
			for (int i = 0; i < amt; i++) {
				ItemStack uItem = item;
				uItem.setAmount(1);
				player.getInventory().addItem(uItem);
			}
		} else {
			player.getInventory().addItem(item);
		}	
		
		
		if (!silent) {
			name = item.getItemMeta().getDisplayName();
			String str1 = hat == true ? " hat" : "";
			if (name == null) {
				player.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + args[0] + str1);
			} else {
				player.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + Utils.integrateColor(name) + str1);
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.item", "cwcore.cmd.*", "cwcore.*" };
	}
}
