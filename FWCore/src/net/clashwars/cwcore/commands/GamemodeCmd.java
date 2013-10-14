package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public GamemodeCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		GameMode mode = null;
		Player player = null;
		CWPlayer cwp = null;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		
		
		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		//Args
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("0") || args[0].toLowerCase().startsWith("s")) {
				mode = GameMode.SURVIVAL;
			} else if (args[0].equalsIgnoreCase("1") || args[0].toLowerCase().startsWith("c")) {
				mode = GameMode.CREATIVE;
			} else if (args[0].equalsIgnoreCase("2") || args[0].toLowerCase().startsWith("a")) {
				mode = GameMode.ADVENTURE;
			}
			if (mode == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid gamemode.");
				return true;
			}
		}
		
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}
		cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
		
		
		//Action
		player.setGameMode(mode);
		cwp.setGamemode(mode.getValue());
		if (player.getGameMode() != GameMode.CREATIVE) {
			if (cwp.getFlying() == true) {
				player.setAllowFlight(true);
				player.setFlying(true);
			} else {
				LocationUtils.tpToTop(cwc, player);
				player.setAllowFlight(false);
				player.setFlying(false);
			}
		} else {
			player.setAllowFlight(true);
			player.setFlying(true);
		}
		
		if (!silent) {
			player.sendMessage(pf + "Your gamemode is set to " + ChatColor.DARK_PURPLE + mode.name().toLowerCase());
			if (sender.getName() != player.getName())
				sender.sendMessage(pf + "You have set " + ChatColor.DARK_PURPLE + player.getDisplayName() 
				+ ChatColor.GOLD + " his gamemode to " + ChatColor.DARK_PURPLE + mode.name().toLowerCase());
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.gamemode", "cwcore.cmd.*", "cwcore.*" };
	}
}
