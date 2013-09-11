package net.clashwars.cwcore.commands;

import java.util.Map.Entry;
import java.util.Set;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.config.AliasesConfig;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IteminfoCmd implements CommandClass {
	
	private CWCore cwc;
	
	public IteminfoCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h", false)) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/iteminfo of /ii");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Show information about the item your holding.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-d" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Show extra details (durability, names, lore, enchants, aliases)");
			return true;
		}
		boolean details = false;
		if (CmdUtils.hasModifier(args,"-d", true)) {
			details = true;
			args = CmdUtils.modifiedArgs(args,"-d", true);
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
		}
		
		ItemStack item = player.getItemInHand();
		if (item == null || item.getType() == Material.AIR) {
			sender.sendMessage(pf + ChatColor.RED + "You need to hold an item.");
			return true;
		}
		
		/* action */
		player.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "Item info for: " + ChatColor.GOLD + item.getType() + ChatColor.DARK_GRAY + "  =====");
		
		if (item.getData().getData() > 0) {
			player.sendMessage(ChatColor.GOLD + "Item ID:data" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + item.getTypeId() + ChatColor.DARK_GRAY + ":" + ChatColor.GRAY + item.getData().getData());
		} else {
			player.sendMessage(ChatColor.GOLD + "Item ID" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + item.getTypeId());
		}
		
		player.sendMessage(ChatColor.GOLD + "Item Name" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + item.getType());
		
		if (!details) {
			return true;
		}
		
		//Durability
		player.sendMessage("");
		if (item.getType().getMaxDurability() > 0) {
			player.sendMessage(ChatColor.GOLD + "Item Durability used" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + item.getDurability() + ChatColor.GOLD + " from the" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + item.getType().getMaxDurability());
		} else {
			player.sendMessage(ChatColor.GOLD + "Item Durability used" + ChatColor.GRAY + ": " + ChatColor.DARK_RED + "No durability"); 
		}
		
		//DisplayName
		if (item.getItemMeta().getDisplayName() != null) {
			player.sendMessage(ChatColor.GOLD + "Custom Name" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + item.getItemMeta().getDisplayName());
		} else {
			player.sendMessage(ChatColor.GOLD + "Custom Name" + ChatColor.GRAY + ": " + ChatColor.DARK_RED + "No custom name");
		}
		
		//Lore
		if (item.getItemMeta().getLore() != null) {
			String lore = item.getItemMeta().getLore().toString();
			lore = lore.replace(",", "\n").replace("[", "").replace("]", "");
			player.sendMessage(ChatColor.GOLD + "Custom Lore" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + "See below\n " + lore);
		} else {
			player.sendMessage(ChatColor.GOLD + "Custom Lore" + ChatColor.GRAY + ": " + ChatColor.DARK_RED + "No lore");
		}
		
		//Enchantments
		if (!item.getEnchantments().isEmpty()) {
			String enchants = "";
			for(Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
				   enchants += "" + entry.getKey().getName() + ChatColor.DARK_GRAY + ":" + ChatColor.LIGHT_PURPLE + entry.getValue() + ChatColor.GRAY + ", " + ChatColor.DARK_PURPLE;
			}
			player.sendMessage(ChatColor.GOLD + "Enchantments" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + enchants.toLowerCase());
		} else {
			player.sendMessage(ChatColor.GOLD + "Enchantments" + ChatColor.GRAY + ": " + ChatColor.DARK_RED + "No enchantments");
		}
		
		//Aliases
		Set<String> a;
		String[] aliases;
		String al = "";
		if (AliasesConfig.searchMaterials.containsKey(item.getType())) {
			a = AliasesConfig.searchMaterials.get(item.getType());
			aliases = a.toArray(new String[0]);
		} else {
			player.sendMessage(ChatColor.GOLD + "Name alliases" + ChatColor.GRAY + ": " + ChatColor.DARK_RED + "none");
			return true;
		}
		
		if (aliases.length > 0) {
			for (int i = 0; i < aliases.length; i++) {
				al += "" + aliases[i].toString() + ChatColor.GRAY + ", " + ChatColor.DARK_PURPLE;
			}
			player.sendMessage(ChatColor.GOLD + "Name alliases" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + al.toLowerCase());
		} else {
			player.sendMessage(ChatColor.GOLD + "Name alliases" + ChatColor.GRAY + ": " + ChatColor.DARK_RED + "none");
		}
		
		//ID:data once more.
		if (item.getData().getData() > 0) {
			player.sendMessage(ChatColor.GOLD + "Item ID:data" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + item.getTypeId() + ChatColor.DARK_GRAY + ":" + ChatColor.GRAY + item.getData().getData());
		} else {
			player.sendMessage(ChatColor.GOLD + "Item ID" + ChatColor.GRAY + ": " + ChatColor.DARK_PURPLE + item.getTypeId());
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.iteminfo", "cwcore.cmd.*", "cwcore.*" };
	}
}
