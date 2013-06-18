package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BroadcastCmd implements CommandClass {
	
	private CWCore cwc;
	
	public BroadcastCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", true) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/broadcast <msg>");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Broadcast a message to the server.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-p" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No prefix");
			return true;
		}
		boolean prefix = true;
		if (CmdUtils.hasModifier(args,"-p", true)) {
			prefix = false;
			args = CmdUtils.modifiedArgs(args,"-p", true);
		}
		
		/* args */
		String message = Utils.implode(args, " ", 0);
		if (prefix) {
			cwc.getPlugin().getServer().broadcastMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "[" 
			+ ChatColor.DARK_RED + ChatColor.BOLD + "CW BC" + ChatColor.DARK_GRAY + ChatColor.BOLD + "] " 
			+ ChatColor.GOLD + ChatColor.BOLD + Utils.integrateColor(message));
		} else {
			cwc.getPlugin().getServer().broadcastMessage(Utils.integrateColor(message));
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.broadcast", "cwcore.cmd.*", "cwcore.*" };
	}
}
