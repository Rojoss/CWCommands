package net.clashwars.cwcore.commands;

import java.util.Arrays;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.Aliases;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Skull;

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
		int amt = 1;
		String name = null;
		String[] lore = null;
		String phead = null;
		int color = -1;
		Enchantment enchant = null;
		int lvl = -1;
		
		/* Modifiers + No args */
		if (Utils.hasModifier(args,"-h") || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/item <item[:data]> [amt] [optional args]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Spawn items for yourself");
			sender.sendMessage(pf + "Optional args: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "name:<name>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set display name of item");
			sender.sendMessage(ChatColor.DARK_PURPLE + "lore:<lore>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set lore of item, _ for space, | for newline");
			sender.sendMessage(ChatColor.DARK_PURPLE + "e:<enchantment>:<level>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set enchantments like e:sharpness:10");
			sender.sendMessage(ChatColor.DARK_PURPLE + "player:<player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set player names for skulls Needs skull:3 item");
			sender.sendMessage(ChatColor.DARK_PURPLE + "color:<#RRGGBB>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set color for leather armor");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			//TODO: Add modifier (-d) for drop items if inv is full.
			//TODO: Add modifier (-u) for unstack all items
			//TODO: Add modifier (-m) for max items for the given stack.
			return true;
		}
		boolean silent = false;
		if (Utils.hasModifier(args,"-s")) {
			silent = true;
			args = Utils.modifiedArgs(args,"-s");
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
			
			md = Aliases.getFullData(args[0]);
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
		
		item = new ItemStack(md.getItemType(), amt, md.getData());
		
		if (args.length >= 3) {
			/* Optional arg (name:<Name>) */
			int nameArg = Utils.getArgIndex(args,"name:");
			if (nameArg != 0) {
				String str = args[nameArg];
				String[] splt = str.split(":");
				
				if (splt.length > 1) {
					name = splt[1];
				}
			}
			
			/* Optional arg (lore:<Lore>) */
			String s = null;
			int loreArg = Utils.getArgIndex(args,"lore:");
			if (loreArg != 0) {
				String str = args[loreArg];
				String[] splt = str.split(":");
				
				if (splt.length > 1) {
					s = splt[1];
					
					if (s.contains("|")) {
						String[] temp = s.split("\\|");
						s = temp[0];
						if (temp.length > 0) {
							lore = Arrays.copyOfRange(temp, 0, temp.length);
							
							for (int i = 0; i < lore.length; i++) {
		                        lore[i] = ChatColor.translateAlternateColorCodes('&', lore[i].replace("_", " "));
							}
						}
					}
				}
			}
				
			/* Optional arg (color:<#RRGGBB>) */
			int clrArg = Utils.getArgIndex(args,"color:");
			if (clrArg != 0) {
				String str = args[clrArg];
				String[] splt = str.split(":");
				
				if (splt.length > 1) {
					if (splt[1].contains("#")) {
                        String[] temp = splt[1].split("#");
                        splt[1] = temp[0];
                        if (temp[1].matches("[0-9A-Fa-f]+")) {
                                color = Integer.parseInt(temp[1], 16);
                        }
					} else {
						if (splt[1].matches("[0-9A-Fa-f]+")) {
                            color = Integer.parseInt(splt[1], 16);
						}
					}
				}
			}
			
			/* Optional arg (player:<player>) */
			int pArg = Utils.getArgIndex(args,"player:");
			if (pArg != 0) {
				String str = args[pArg];
				String[] splt = str.split(":");
				
				if (splt.length > 1) {
					phead = splt[1];
				}
			}
			
			/* Optional arg (e:<enchantment>:<power>) */
			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().startsWith("e:")) {
					String[] splt = args[i].split(":");
					
					if (splt.length > 1) {
						
						enchant = Aliases.findEnchanttype(splt[1]);
						
						try {
						 	lvl = Integer.parseInt(splt[2]);
						} catch (NumberFormatException e) {
						 	sender.sendMessage(pf + ChatColor.RED + "Invalid level.");
							return true;
						}
							
						if (enchant == null) {
							player.sendMessage(pf + ChatColor.RED + "Enchantment not found. " + ChatColor.GRAY + splt[1]);
							return true;
						}
						
						item.addUnsafeEnchantment(enchant, lvl);
					}
				}
			}
		}

		/* Add meta if specified */
		ItemMeta meta = item.getItemMeta();
		if (name != null) {
			name = name.replace("_", " ");
			meta.setDisplayName(Utils.integrateColor(name));
		}
		if (lore != null) {
			meta.setLore(Arrays.asList(lore));
		}
		if (color >= 0 && meta instanceof LeatherArmorMeta) {
			((LeatherArmorMeta)meta).setColor(Color.fromRGB(color));
		}
		item.setItemMeta(meta);
		
		player.sendMessage(pf + phead);
		if (phead != null && item.getType() == Material.SKULL_ITEM) {
			if (item.getDurability() == 3) {
				player.sendMessage(pf + phead);
				SkullMeta smeta = (SkullMeta) item.getItemMeta();
				smeta.setOwner(phead);
				item.setItemMeta(smeta);
			}
		}
		
		/* Action */
		player.getInventory().addItem(item);
		if (!silent)
			if (name == null)
				player.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + args[0]);
			else
				player.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + Utils.integrateColor(name));
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.item", "cwcore.cmd.*", "cwcore.*" };
	}
}
