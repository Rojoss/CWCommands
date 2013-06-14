package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RealnameCmd implements CommandClass {
	
	private CWCore cwc;
	
	public RealnameCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		String nick = null;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h")) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/realname [player]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Lookup a player his real name in case he has a nickname.");
			args = CmdUtils.modifiedArgs(args,"-h");
			return true;
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		/* No args (run as sender)*/
		if (args.length < 1) {
			sender.sendMessage(pf + "Your real name is: " + player.getName());
			return true;
		}
		
		/* 1 arg (Player) */
		if (args.length >= 1) {
			cwp = cwc.getPlayerManager().getPlayerFromNick(args[0]);
			if (cwp == null) {
				player = cwc.getServer().getPlayer(args[0]);
				if (player == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid player");
					return true;
				} else {
					nick = player.getDisplayName();
				}
			} else {
				nick = cwp.getNick();
			}
		}
		
		/* Action */
		if (cwp == null && player != null)
			sender.sendMessage(pf + "Real name of " + ChatColor.DARK_PURPLE + Utils.integrateColor(nick)
			+ ChatColor.GOLD + " is: " + player.getName());
		if (cwp != null)
			sender.sendMessage(pf + "Real name of " + ChatColor.DARK_PURPLE + Utils.integrateColor(nick)
			+ ChatColor.GOLD + " is: " + cwp.getName());
		
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.realname", "cwcore.cmd.*", "cwcore.*" };
	}
}