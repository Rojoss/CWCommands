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

public class NickCmd implements CommandClass {
	
	private CWCore cwc;
	
	public NickCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		String nick = "";
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-r" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Reset nickname (Needs all args filled)");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean reset = false;
		if (CmdUtils.hasModifier(args,"-r", true)) {
			reset = true;
			args = CmdUtils.modifiedArgs(args,"-r", true);
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
			cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
		}
		
		/* 1 arg (Nickname) */
		if (args.length >= 1) {
			nick = args[0];
		}
		
		/* 2 args (Player) */
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
		}

		/* null checks */
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (nick == null || nick == "" || nick == " " || nick == "  " || nick == "   ") {
			sender.sendMessage(pf + ChatColor.RED + "Invalid nickname.");
			return true;
		}
		//TODO: Check for other nicknames/players so there are no duplicates.
		
		/* Action */
		cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
			
		if (reset) {
			player.setDisplayName(player.getName());
			cwp.setNick(player.getName());
			if (!silent) {
				player.sendMessage(pf + "Your nickname has been reset!");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "Nickname of " + ChatColor.DARK_PURPLE + player.getName() + ChatColor.GOLD + " has been reset!");
			}
			return true;
		}
			
		player.setDisplayName(Utils.integrateColor(nick));
		cwp.setNick(nick);
		if (!silent) { 
			player.sendMessage(pf + "Your nickname has been changed to: " + Utils.integrateColor(nick));
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "Nickname of " + ChatColor.DARK_PURPLE + player.getName() 
					+ ChatColor.GOLD + " changed to: " + Utils.integrateColor(nick));
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.nick", "cwcore.cmd.*", "cwcore.*" };
	}
}