package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class HealCmd implements CommandClass {
	
	private CWCore cwc;
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
	
	public HealCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(lbl.equalsIgnoreCase("heal")) {
			
			/* Modifiers */
			if (Utils.hasModifier(args,"-h")) {
				sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/heal [player] [amt]");
				sender.sendMessage(pf + ChatColor.DARK_GRAY + "Modifiers: " + ChatColor.GRAY + "-s (no messages) , -a (Also hunger/Saturation/fire etc)");
				args = Utils.modifiedArgs(args,"-h");
				return true;
			}
			boolean silent = false;
			if (Utils.hasModifier(args,"-s")) {
				silent = true;
				args = Utils.modifiedArgs(args,"-s");
			}
			boolean all = false;
			if (Utils.hasModifier(args,"-a")) {
				all = true;
				args = Utils.modifiedArgs(args,"-a");
			}
			
			/* No args */
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					player.setHealth(player.getMaxHealth());
					if (all) {
						player.setFireTicks(0);
						player.setSaturation(5.0F);
						player.setFoodLevel(20);
						player.setExhaustion(0.0F);
					}
					if (!silent)
						player.sendMessage(pf + "Healed! " + ChatColor.DARK_RED + (player.getHealth() / 2) + "❤");
					return true;
				}
			}
			
			/* 1 arg (player) */
			if (args.length >= 1) {
				Player player = cwc.getPlugin().getServer().getPlayer(args[0]);
				int amt = 0;
				if (player == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
					return true;
				}
				CWPlayer cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
				
				/* 2 args (amount) */
				if (args.length == 2) {
					 try {
					 	amt = Integer.parseInt(args[1]);
					 } catch (NumberFormatException e) {
					 	sender.sendMessage(pf + ChatColor.RED + "Invalid amount.");
					 	return true;
					 }
				}
				
				/* Heal the other player */
				
				if (amt != 0) {
					player.resetMaxHealth();
					player.setMaxHealth(amt);
					cwp.setMaxHealth(amt);
				}
				player.setHealth(player.getMaxHealth());
				if (all) {
					player.setFireTicks(0);
					player.setSaturation(5.0F);
					player.setFoodLevel(20);
					player.setExhaustion(0.0F);
				}
				if (!silent) {
					sender.sendMessage(pf + "You set " + ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.GOLD + " his health to: "
						+ ChatColor.DARK_RED + (player.getHealth() / 2) + "❤");
					player.sendMessage(pf + "Your health was set to: " + ChatColor.DARK_RED + (player.getHealth() / 2) + "❤");
				}
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "some.permission" };
	}

	@Override
	public String[] aliases() {
		return new String[] { "heal" };
	}

}
