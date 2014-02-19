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

public class TeleporthereCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public TeleporthereCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		modifiers.put("f", "Force tp doesn't check for safe locations");
		modifiers.put("a", "Teleport all players.");
		modifiers.put("confirm", "Confirm to teleport all players");
		modifiers.put("*", "Teleports players from other servers.");

	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		String pplayer = null;
		Player player2 = null;
		String pplayer2 = null;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean force = CmdUtils.hasModifier(cmdArgs, "f");
		boolean all = CmdUtils.hasModifier(cmdArgs, "a");
		boolean confirmed = CmdUtils.hasModifier(cmdArgs, "confirm");
		boolean bungee = CmdUtils.hasModifier(cmdArgs, "*");

		//Console
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
			pplayer = sender.getName();
		}

		//Args
		if (args.length >= 1) {
			player2 = cwc.getServer().getPlayer(args[0]);
			pplayer2 = args[0];
			if (player2 == null && !all && !bungee) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}

		//Action
		if (all && bungee) {
			sender.sendMessage(pf + ChatColor.RED + "Can't teleport all players from other servers.");
			return true;
		}
		if (bungee) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("TPHere");
				out.writeUTF(pplayer2);
				out.writeUTF(pplayer);
				out.writeBoolean(silent);
				out.writeBoolean(force);

				Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			if (all) {
				if (confirmed == true) {
					for (Player p : cwc.getServer().getOnlinePlayers()) {
						if (p != player) {
							if (force) {
								p.teleport(player.getLocation());
							} else {
								p.teleport(LocationUtils.getSafeDestination(player.getLocation()));
							}
							if (!silent) {
								p.sendMessage(pf + "You where teleported to " + player.getDisplayName());
							}
						}
					}
					player.sendMessage(pf + "All players have been teleported to you!");
				} else {
					player.sendMessage(pf + ChatColor.RED
							+ "To teleport all players you need to add -confirm to confirm you want to teleport everyone!");
				}
			} else {
				if (force) {
					player2.teleport(player.getLocation());
				} else {
					player2.teleport(LocationUtils.getSafeDestination(player.getLocation()));
				}
				if (!silent) {
					player.sendMessage(pf + "You have teleported " + player2.getDisplayName() + ChatColor.GOLD + " to you.");
					player2.sendMessage(pf + "You where teleported to " + player.getDisplayName());
				}
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.teleporthere", "cwcore.cmd.*", "cwcore.*" };
	}
}