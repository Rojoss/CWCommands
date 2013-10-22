package net.clashwars.cwcore.commands;

import java.util.HashMap;

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
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public RealnameCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		String nick = null;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(cmdArgs,"-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		

		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		
		//Args
		if (args.length < 1) {
			sender.sendMessage(pf + "Your real name is: " + player.getName());
			return true;
		}
		
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
		
		
		//Action
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