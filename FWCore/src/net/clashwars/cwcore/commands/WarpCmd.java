package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class WarpCmd implements CommandClass {
	
	private CWCore cwc;
	
	public WarpCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		String name = "";
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/warp <name> [player]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Teleport to a warp.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-f" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Force tp doesn't check for safe locations");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-l" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "List all warps.");
			return true;
		}
		if (CmdUtils.hasModifier(args,"-l", true)) {
			String msg = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Warps" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
			for (int i = 0; i < cwc.getWarpsConfig().getWarpNames().size(); i++) {
				if (i == 0) {
					msg += cwc.getWarpsConfig().getWarpNames().get(i);
				} else {
					msg += ChatColor.DARK_GRAY + ", " + ChatColor.GOLD + cwc.getWarpsConfig().getWarpNames().get(i);
				}
			}
			sender.sendMessage(msg);
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
		
		/* Console check */
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
		}
		
		/* 1 arg (Name) */
		if (args.length >= 1) {
			name = args[0];
		}
		
		/* 2 args (Player) */
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
		}
		
		/* null checks */
		if (name == "" || name == " " || name == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid name");
			return true;
		}
		if (!cwc.getWarpsConfig().getWarpNames().contains(name)) {
			sender.sendMessage(pf + ChatColor.RED + "Warp " + ChatColor.GRAY + name + ChatColor.RED + " not found.");
			return true;
		}
		
		/* Action */
		ConfigurationSection warp = cwc.getWarpsConfig().getWarp(name);
		World world = cwc.getServer().getWorld(warp.getString("Location.World"));
		int x = warp.getInt("Location.X");
		int y = warp.getInt("Location.Y");
		int z = warp.getInt("Location.Z");
		long yaw = warp.getLong("Location.Yaw");
		long pitch = warp.getLong("Location.Pitch");
		Location location = new Location(world, x, y, z);
		location.setYaw(yaw);
		location.setPitch(pitch);
		
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
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.warp", "cwcore.cmd.*", "cwcore.*" };
	}
}