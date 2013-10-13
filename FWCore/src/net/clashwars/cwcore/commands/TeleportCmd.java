package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

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

	public TeleportCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		Player target = null;
		String pplayer = null;
		String ptarget = null;

		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args, "-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-f" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Force tp doesn't check for safe locations");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-*" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Teleport to players at other servers.");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args, "-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args, "-s", true);
		}
		boolean force = false;
		if (CmdUtils.hasModifier(args, "-f", true)) {
			force = true;
			args = CmdUtils.modifiedArgs(args, "-f", true);
		}
		boolean bungee = false;
		if (CmdUtils.hasModifier(args,"-*", true)) {
			bungee = true;
			args = CmdUtils.modifiedArgs(args,"-*", true);
		}

		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify 2 players to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
			pplayer = sender.getName();
		}

		/* 1 arg (Teleport Target) */
		if (args.length >= 1) {
			target = cwc.getServer().getPlayer(args[0]);
			ptarget = args[0];
		}

		/* 2 args (Player) */
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
			pplayer = args[1];
		}

		/* null checks */
		if (target == null && !bungee) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player target.");
			return true;
		}
		if (player == null && args.length >= 2 && !bungee) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		
		
		/* Action */
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