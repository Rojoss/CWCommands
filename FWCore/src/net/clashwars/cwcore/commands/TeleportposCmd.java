package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportposCmd implements CommandClass {
	
	private CWCore cwc;
	
	public TeleportposCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		String pplayer = null;
		String locStr = "0,0,0";
		World world = null;
		String pworld = null;
		Location loc = null;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/teleportpos <x,y,z> [world] [player]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Teleport to a location or another world");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-f" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Force tp doesn't check for safe locations");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-*" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Teleport to a location on other servers.");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean force = false;
		if (CmdUtils.hasModifier(args,"-f", true)) {
			force = true;
			args = CmdUtils.modifiedArgs(args,"-f", true);
		}
		boolean bungee = false;
		if (CmdUtils.hasModifier(args,"-*", true)) {
			bungee = true;
			args = CmdUtils.modifiedArgs(args,"-*", true);
		}
		
		/* Console check */
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
		
		/* 1 arg (Location) */
		if (args.length >= 1) {
			locStr = args[0];
		}
		
		/* 2 args (world) */
		if (args.length >= 2) {
			pworld = args[1];
			world = cwc.getServer().getWorld(args[1]);
		}
		
		/* 3 args (player) */
		if (args.length >= 2) {
			pplayer = args[2];
			player = cwc.getServer().getPlayer(args[2]);
		}
		
		/* null checks */
		if (!bungee) {
			if (player == null && args.length >= 2) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
			if (world == null && args.length >= 1) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid world.");
				return true;
			}
			
			loc = LocationUtils.stringToLocation(locStr, world, player);
			if (loc == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid location syntax: x,y,z");
				return true;
			}
		}
		
		/* Action */
		if (bungee) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("TPPos");
				out.writeUTF(sender.getName());
				out.writeUTF(pplayer);
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
				player.sendMessage(pf + "You have been teleported to " + ChatColor.DARK_PURPLE + player.getLocation().getX() + ", " + player.getLocation().getY() + ", " + player.getLocation().getZ());
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have teleported " + ChatColor.DARK_PURPLE + player.getDisplayName()
						+ ChatColor.GOLD + " to " + ChatColor.DARK_PURPLE + player.getLocation().getX() + ", " + player.getLocation().getY() + ", " + player.getLocation().getZ());
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.teleportpos", "cwcore.cmd.*", "cwcore.*" };
	}
}