package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoundCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public SoundCmd(CWCore cwc) {
		this.cwc = cwc;
		optionalArgs.put("p:<player>", "Play effect at this player");
		optionalArgs.put("loc:<x,y,z>[:world]", "Play the effect on this location");
		modifiers.put("s", "No messages");
		modifiers.put("p", "Personal sound others can't hear.");
		modifiers.put("a", "Play sound at all players.");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		Location loc = null;
		Sound sound = null;
		float volume = 1.0F;
		float pitch = 1.0F;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean personal = CmdUtils.hasModifier(cmdArgs, "p");
		boolean all = CmdUtils.hasModifier(cmdArgs, "a");
		if (CmdUtils.getOptionalArg(cmdArgs, "p:") != null) {
			player = cwc.getServer().getPlayer(((CmdUtils.getOptionalArg(cmdArgs, "p:"))));
		}
		String locStr = CmdUtils.getOptionalArg(cmdArgs, "loc:");
		World world = Utils.getWorld(CmdUtils.getOptionalArg(cmdArgs, "loc:"));

		//Console
		if (!(sender instanceof Player)) {
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Specify a player to play sounds for them.");
				return true;
			}
		} else {
			if (player == null) {
				player = (Player) sender;
			}
		}

		if (world == null) {
			world = player.getWorld();
		}
		if (locStr != null) {
			loc = LocationUtils.getLocation(locStr, world);
		}
		if (loc == null) {
			loc = player.getLocation();
		}

		//Args
		if (args.length >= 1) {
			sound = AliasUtils.findSound(args[0]);
			if (sound == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid sound.");
				return true;
			}
		}

		if (args.length >= 2) {
			try {
				volume = Float.parseFloat(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid volume, Must be a number between 0.0 and 2.0.");
				return true;
			}
		}

		if (args.length >= 3) {
			try {
				pitch = Float.parseFloat(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid pitch, Must be a number between 0.0 and 2.0.");
				return true;
			}
		}

		//Action
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
			world.playSound(loc, sound, volume, pitch);
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
