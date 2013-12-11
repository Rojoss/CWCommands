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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class WarpCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public WarpCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		modifiers.put("f", "Force tp doesn't check for safe locations");
		modifiers.put("*", "Teleport to warps at other servers.");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		String pplayer = null;
		String name = "";
		
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
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
			pplayer = sender.getName();
		}
		
		
		//Args
		if (args.length >= 1) {
			name = args[0].toLowerCase();
			if (name == "" || name == " " || name == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid name");
				return true;
			}
			if (!cwc.getWarpsConfig().getWarpNames().contains(name)) {
				sender.sendMessage(pf + ChatColor.RED + "Warp " + ChatColor.GRAY + name + ChatColor.RED + " not found.");
				return true;
			}
		}
		
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
			pplayer = args[1];
		}
		
		
		//Action
		if (bungee) {	
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("WARP");
				out.writeUTF(pplayer);
				out.writeUTF(name);
				out.writeBoolean(silent);
				out.writeBoolean(force);

				Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
		} else {
			ConfigurationSection warp = cwc.getWarpsConfig().getWarp(name);
			World world = cwc.getServer().getWorld(warp.getString("Location.World"));
			int x = warp.getInt("Location.X");
			int y = warp.getInt("Location.Y");
			int z = warp.getInt("Location.Z");
			long yaw = warp.getLong("Location.Yaw");
			long pitch = warp.getLong("Location.Pitch");
			Location location = new Location(world, x, y, z, yaw, pitch);
			
			if (force) {
				player.teleport(location);
			} else {
				player.teleport(LocationUtils.getSafeDestination(location));
			}
			if (!silent) {
				player.sendMessage(pf + "Warping to " + ChatColor.DARK_PURPLE + name);
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have warped " + ChatColor.DARK_PURPLE + player.getDisplayName()
						+ ChatColor.GOLD + " to " + ChatColor.DARK_PURPLE + name);
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.warp", "cwcore.cmd.*", "cwcore.*" };
	}
}