package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCmd implements CommandClass {

	private CWCore	cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;

	public TeleportCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		modifiers.put("f", "Force tp doesn't check for safe locations");
		modifiers.put("*", "Teleport to players at other servers.");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		Player target = null;
		String pplayer = null;
		String ptarget = null;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(cmdArgs, "-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean force = CmdUtils.hasModifier(cmdArgs, "f");
		boolean bungee = CmdUtils.hasModifier(cmdArgs, "*");

		
		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify 2 players to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
			pplayer = sender.getName();
		}

		
		//Args
		if (args.length >= 1) {
			target = cwc.getServer().getPlayer(args[0]);
			ptarget = args[0];
			if (target == null && !bungee) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player target.");
				return true;
			}
		}

		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
			pplayer = args[1];
			if (player == null && args.length >= 2 && !bungee) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}
		
		
		//Action
		if (bungee) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("TP");
				out.writeUTF(pplayer);
				out.writeUTF(ptarget);
				out.writeBoolean(silent);
				out.writeBoolean(force);

				Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			if (force) {
                player.teleport(target);
			} else {
                player.teleport(LocationUtils.getSafeDestination(target.getLocation()));
			}
			
			if (!silent) { 
                player.sendMessage(pf + "You have been teleported to " + target.getDisplayName());
                if (sender.getName() != player.getName())
                        sender.sendMessage(pf + "You have teleported " + ChatColor.DARK_PURPLE + player.getDisplayName()
                                + ChatColor.GOLD + " to " + ChatColor.DARK_PURPLE + target.getDisplayName());
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.teleport", "cwcore.cmd.*", "cwcore.*" };
	}
}