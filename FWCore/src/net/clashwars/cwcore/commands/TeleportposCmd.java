package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportposCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public TeleportposCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		modifiers.put("f", "Force tp doesn't check for safe locations");
		modifiers.put("*", "Teleport to location at other servers.");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		String pplayer = null;
		String locStr = "0,0,0";
		World world = null;
		String server = null;
		String pworld = null;
		Location loc = null;

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
			if (args.length < 3) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player and a world to use this in the console!");
				return true;
			}
		} else {
			player = (Player) sender;
			pplayer = sender.getName();
			world = player.getWorld();
			pworld = player.getWorld().getName();
		}

		//Args
		if (args.length >= 1) {
			locStr = args[0];
			if (Utils.hasWorld(locStr)) {
				world = Utils.getWorld(locStr);
			} else {
				if (player != null) {
					world = player.getWorld();
				} else {
					sender.sendMessage(pf + ChatColor.RED + "Invalid world.");
				}
			}
			loc = LocationUtils.getLocation(locStr, world);
			if (loc == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid location syntax: x,y,z");
				return true;
			}
			pworld = world.toString();
		}

		if (args.length >= 2) {
			pplayer = args[1];
			player = cwc.getServer().getPlayer(args[1]);
			if (player == null && !bungee) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}

		if (args.length >= 3) {
			if (bungee) {
				server = args[2];
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Can't teleport to another server without -*");
			}
		}

		//Action
		if (bungee) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("TPPos");
				out.writeUTF(sender.getName());
				out.writeUTF(pplayer);
				out.writeUTF(server);
				out.writeUTF(pworld);
				out.writeUTF(locStr);
				out.writeBoolean(silent);
				out.writeBoolean(force);

				Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			if (force) {
				player.teleport(loc);
			} else {
				player.teleport(LocationUtils.getSafeDestination(loc));
			}
			if (!silent) {
				player.sendMessage(pf + "You have been teleported to " + ChatColor.DARK_PURPLE + player.getLocation().getX() + ", "
						+ player.getLocation().getY() + ", " + player.getLocation().getZ());
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have teleported " + ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.GOLD + " to "
							+ ChatColor.DARK_PURPLE + player.getLocation().getX() + ", " + player.getLocation().getY() + ", "
							+ player.getLocation().getZ());
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.teleportpos", "cwcore.cmd.*", "cwcore.*" };
	}
}