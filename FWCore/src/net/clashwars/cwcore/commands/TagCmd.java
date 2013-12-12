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
import org.kitteh.tag.TagAPI;

public class TagCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public TagCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		modifiers.put("r", "Reset tag");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		String tag = "";
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(cmdArgs,"-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean reset = CmdUtils.hasModifier(cmdArgs, "r");
		

		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
			cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
			if (reset) {
				resetNick(player, cwp, silent, pf);
				return true;
			}
		}
		
		if (args.length < 1) {
			player.sendMessage(pf + "Your tag is" + ChatColor.GRAY + ": " + ChatColor.WHITE + Utils.integrateColor(cwp.getNick()));
			return true;
		}
		
		if (args.length >= 1) {
			tag = args[0];
			if (tag == null || tag.isEmpty()) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid tag.");
				return true;
			}
		}
		
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
			cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
		}
		
		
		
		//Action
		if (reset) {
			cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
			return true;
		}
		cwp.setTag(tag);
		TagAPI.refreshPlayer(player);
		if (!silent) { 
			player.sendMessage(pf + "Your tag has been changed to" + ChatColor.GRAY + ": " + ChatColor.WHITE + Utils.integrateColor(tag));
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "Tag from " + ChatColor.DARK_PURPLE + player.getName() 
					+ ChatColor.GOLD + " changed to" + ChatColor.GRAY + ": " + ChatColor.WHITE + Utils.integrateColor(tag));
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.nick", "cwcore.cmd.*", "cwcore.*" };
	}
	
	
	private void resetNick(Player player, CWPlayer cwp, boolean silent, String pf) {
		player.setDisplayName(player.getName());
		cwp.setNick(player.getName());
		if (!silent) {
			player.sendMessage(pf + "Your nickname has been reset!");
		}
	}
}