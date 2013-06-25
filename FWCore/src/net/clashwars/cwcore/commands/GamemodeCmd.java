package net.clashwars.cwcore.commands;

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
	
	public GamemodeCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		GameMode mode = null;
		Player player = null;
		CWPlayer cwp = null;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/gamemode <mode(0|1|2)> [player]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Change a player his gamemode to 0=survival 1=creative 2=adventure");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		/* 1 arg (Gamemode) */
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("0")) {
				mode = GameMode.SURVIVAL;
			} else if (args[0].equalsIgnoreCase("1")) {
				mode = GameMode.CREATIVE;
			} else if (args[0].equalsIgnoreCase("2")) {
				mode = GameMode.ADVENTURE;
			}
		}
		
		/* 2 args (Player) */
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
		}
		
		/* null checks */
		if (mode == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid gamemode.");
			return true;
		}
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		
		/* Action */
		cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
		
		player.setGameMode(mode);
		cwp.setGamemode(mode.getValue());
		if (player.getGameMode().getValue() != 1) {
			if (cwp.getFlying() == true) {
				player.setAllowFlight(true);
				player.setFlying(true);
				cwp.setFlying(true);
			} else {
				LocationUtils.tpToTop(cwc, player);
			}
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
