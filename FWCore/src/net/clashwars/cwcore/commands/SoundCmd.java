package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoundCmd implements CommandClass {
	
	private CWCore cwc;
	
	public SoundCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		Location loc = null;
		Sound sound = null;
		float volume = 1.0F;
		float pitch = 1.0F;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/sound <sound> [volume(0.0-2.0)] [pitch(0.0-2.0)]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Play a sound.");
			sender.sendMessage(pf + "Optional args: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "player:<player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Play effect at this player.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "loc:<x,y,z>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Play sound effect at specific location");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-p" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Personal sound others can't hear.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-a" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Play sound for all players.");
			return true;
		}
		
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean personal = false;
		if (CmdUtils.hasModifier(args,"-p", true)) {
			personal = true;
			args = CmdUtils.modifiedArgs(args,"-p", true);
		}
		boolean all = false;
		if (CmdUtils.hasModifier(args,"-a", true)) {
			all = true;
			args = CmdUtils.modifiedArgs(args,"-a", true);
		}
		
		boolean targetSet = false;
		if (CmdUtils.hasModifier(args,"player:", false)) {
			targetSet = true;
			player = CmdUtils.getPlayer(args, "player:", cwc);
			args = CmdUtils.modifiedArgs(args,"player:", false);
		}
		String locStr = "";
		if (CmdUtils.hasModifier(args,"loc:", false)) {
			locStr = CmdUtils.getString(args, "loc:");
			args = CmdUtils.modifiedArgs(args,"loc:", false);
		}
		
		
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (targetSet == false) {
				sender.sendMessage(pf + ChatColor.RED + "Specify a player to play sounds for them.");
				return true;
			}
		} else {
			if (player == null) {
				player = (Player) sender;
			}
		}
		
		if (locStr != "") {
			loc = LocationUtils.getLocation(locStr, player.getWorld());
		}
		if (loc == null) {
			loc = player.getLocation();
		}
		
		/* 1 arg (Sound) */
		if (args.length >= 1) {
			sound = AliasUtils.findSound(args[0]);
		}
		
		/* 2 args (volume) */
		if (args.length >= 2) {
			try {
			 	volume = Float.parseFloat(args[1]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid volume, Must be a number between 0.0 and 2.0.");
			 	return true;
			 }
		}
		
		/* 3 args (Pitch) */
		if (args.length >= 3) {
			try {
			 	pitch = Float.parseFloat(args[2]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid pitch, Must be a number between 0.0 and 2.0.");
			 	return true;
			 }
		}
		
		if (sound == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid sound.");
			return true;
		}
		
		/* Action */
		if (all) {
			for (int i = 0; i < loc.getWorld().getPlayers().size(); i++) {
				Player p = loc.getWorld().getPlayers().get(i);
				p.playSound(p.getLocation(), sound, volume, pitch);
			}
			if (!silent) {
				player.sendMessage(pf + "Playing sound " + ChatColor.DARK_PURPLE + sound + " " + ChatColor.GOLD + " for all players.");
			}
			return true;
		}
		if (personal) {
			player.playSound(loc, sound, volume, pitch);
		} else {
			player.getWorld().playSound(loc, sound, volume, pitch);
		}
		
		if (!silent) {
			if (player.getName().equalsIgnoreCase(sender.getName())) {
				player.sendMessage(pf + "Playing sound " + ChatColor.DARK_PURPLE + sound);
			} else {
				sender.sendMessage(pf + "Playing sound " + ChatColor.DARK_PURPLE + sound + " " + ChatColor.GOLD + " for " + player.getDisplayName());
			}
			
		}
		
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.effect", "cwcore.cmd.*", "cwcore.*" };
	}
}
