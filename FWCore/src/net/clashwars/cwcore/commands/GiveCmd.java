package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.components.CustomItem;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
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

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public GiveCmd(CWCore cwc) {
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
		modifiers.put("*", "Look for players on other servers.");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		String pplayer = null;
		String pitem = null;
		ItemStack item = null;
		CustomItem ci = null;
		MaterialData md = null;
		int amt = 64;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-h", false) || args.length < 2) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean drop = CmdUtils.hasModifier(cmdArgs, "d");
		boolean unstack = CmdUtils.hasModifier(cmdArgs, "u");
		boolean equip = CmdUtils.hasModifier(cmdArgs, "e");
		boolean hat = CmdUtils.hasModifier(cmdArgs, "e");
		boolean bungee = CmdUtils.hasModifier(cmdArgs, "*");
		String name = CmdUtils.getOptionalArg(cmdArgs, "name:");
		String lore = CmdUtils.getOptionalArg(cmdArgs, "lore:");
		String dur = CmdUtils.getOptionalArg(cmdArgs, "dur:");
		String enchant = CmdUtils.getOptionalArg(cmdArgs, "e:");
		String effect = CmdUtils.getOptionalArg(cmdArgs, "pe:");
		String head = CmdUtils.getOptionalArg(cmdArgs, "head:");
		String color = CmdUtils.getOptionalArg(cmdArgs, "color:");

		//Args
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
			pplayer = args[0];
			if (player == null && !bungee) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}

		if (args.length >= 2) {
			pitem = args[1];
			md = AliasUtils.getFullData(args[1]);
			if (md == null) {
				sender.sendMessage(pf + ChatColor.RED + "Item " + ChatColor.GRAY + args[1] + ChatColor.RED + " was not recognized!");
				return true;
			}
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
		item = new ItemStack(md.getItemType(), amt, md.getData());

		ci = new CustomItem(item);

		if (name != null)
			ci.setName(name);
		if (lore != null)
			ci.setLore(lore);
		if (dur != null)
			ci.setDurability(dur);
		if (enchant != null)
			ci.setEnchants(enchant);
		if (effect != null)
			ci.setPotionEffects(effect);
		if (head != null)
			ci.setHead(head);
		if (color != null)
			ci.setLeatherColor(color);
		item = ci.getItem();

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
					sender.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + args[1] + str1 + ChatColor.GOLD + " to "
							+ ChatColor.DARK_PURPLE + player.getDisplayName());
					player.sendMessage(pf + "You received " + ChatColor.DARK_PURPLE + amt + " " + args[1] + str1 + ChatColor.GOLD + " from "
							+ ChatColor.DARK_PURPLE + sender.getName());
				} else {
					sender.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + Utils.integrateColor(name) + str1 + ChatColor.GOLD + str1
							+ " to " + ChatColor.DARK_PURPLE + player.getDisplayName());
					player.sendMessage(pf + "You received " + ChatColor.DARK_PURPLE + amt + " " + Utils.integrateColor(name) + str1 + ChatColor.GOLD
							+ " from " + ChatColor.DARK_PURPLE + sender.getName());
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
