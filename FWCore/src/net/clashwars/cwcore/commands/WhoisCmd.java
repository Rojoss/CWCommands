package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhoisCmd implements CommandClass {
	
	private CWCore cwc;
	
	public WhoisCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/whois <player>");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Show detailed info about a player");
			return true;
		}
		
		/* 1 arg (Player) */
		if (args.length >= 1) {
			if (cwc.getServer().getPlayer(args[0]) == cwc.getServer().getPlayer(sender.getName())) {
				player = cwc.getServer().getPlayer(args[0]);
				cwp = cwc.getPlayerManager().getPlayer(player.getName());
			} else {
				if (sender.hasPermission("cwcore.cmd.whois.others")) {
					player = cwc.getServer().getPlayer(args[0]);
					cwp = cwc.getPlayerManager().getPlayer(player.getName());
				} else {
					sender.sendMessage(pf + ChatColor.RED + "insufficient permissions!" 
							+ ChatColor.GRAY + " - " + ChatColor.DARK_GRAY + "'" + ChatColor.DARK_RED + "cwcore.cmd.whois.others" + ChatColor.DARK_GRAY + "'");
					return true;
				}
			}
		}
		
		/* null checks */
		if (player == null || cwp == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		
		String yes = ChatColor.GREEN + "true";
		String no = ChatColor.DARK_RED + "false";
		
		/* Action */
		sender.sendMessage(ChatColor.DARK_GRAY + "========" + ChatColor.DARK_RED + "CW whois: " + ChatColor.GOLD + player.getName() + ChatColor.DARK_GRAY + "========");
		sender.sendMessage(ChatColor.DARK_PURPLE + "Nickname" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getDisplayName());
		sender.sendMessage(ChatColor.DARK_PURPLE + "Tag" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + cwp.getTag());
		sender.sendMessage(ChatColor.DARK_PURPLE + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD 
				+ player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ());
		sender.sendMessage(ChatColor.DARK_PURPLE + "Health" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getHealth() + "/" + player.getMaxHealth());
		sender.sendMessage(ChatColor.DARK_PURPLE + "Hunger" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getFoodLevel() + "/20" + " saturation: " + ChatColor.YELLOW + player.getSaturation());
		sender.sendMessage(ChatColor.DARK_PURPLE + "Gamemode" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getGameMode().toString().toLowerCase());
		sender.sendMessage(ChatColor.DARK_PURPLE + "Flymode" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + (cwp.getFlying() ? yes:no));
		sender.sendMessage(ChatColor.DARK_PURPLE + "Godmode" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + (cwp.getGod() ? yes:no));
		sender.sendMessage(ChatColor.DARK_PURPLE + "Vanished" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + (cwp.getVanished() ? yes:no));
		sender.sendMessage(ChatColor.DARK_PURPLE + "Frozen" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + (cwc.getFrozenPlayers().contains(player) ? yes:no));
		sender.sendMessage(ChatColor.DARK_PURPLE + "Speed" + ChatColor.DARK_GRAY + ": " 
				+ ChatColor.GOLD + "walk: " + ChatColor.YELLOW + player.getWalkSpeed() + ChatColor.GOLD + " fly: " + ChatColor.YELLOW + player.getFlySpeed());
		sender.sendMessage(ChatColor.DARK_PURPLE + "Experience" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "TODO");
		//TODO: Experience
		//TODO: Server
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.whois", "cwcore.cmd.*", "cwcore.*" };
		//Extra: cwcore.cmd.whois.others
	}
}