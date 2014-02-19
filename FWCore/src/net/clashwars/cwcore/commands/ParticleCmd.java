package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.constants.Effects;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParticleCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public ParticleCmd(CWCore cwc) {
		this.cwc = cwc;
		optionalArgs.put("p:<player>", "Play effect at this player");
		optionalArgs.put("loc:<x,y,z>[:world]", "Play the effect on this location");
		optionalArgs.put("amt:<amount>", "Set amount of particles");
		optionalArgs.put("offset:<x,y,z>", "Offset where the effect plays");
		modifiers.put("s", "No messages");
		modifiers.put("l", "List all particles with all args");

	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		boolean played = false;
		Location loc = null;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-l", false)) {
			String sep = ChatColor.DARK_GRAY + ", " + ChatColor.GOLD;
			sender.sendMessage(ChatColor.DARK_GRAY + "===== " + ChatColor.DARK_RED + "Particle List" + ChatColor.DARK_GRAY + " =====");
			sender.sendMessage(ChatColor.DARK_RED + "CustomEffect without args: " + ChatColor.GOLD + "signal" + sep + "flames" + sep + "explosion"
					+ sep + "lightning" + sep + "bigsmoke");
			sender.sendMessage(ChatColor.GOLD + "cloud" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "<radius>");
			sender.sendMessage(ChatColor.GOLD + "smoke" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "<direction(0<>8)>");
			sender.sendMessage(ChatColor.GOLD + "splash" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "<potionTypeId>");
			sender.sendMessage(ChatColor.GOLD + "blockbreak" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "<blockID>");
			sender.sendMessage(ChatColor.GOLD + "particle" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY
					+ "<particle> <horSpread> <verSpread> <speed> <count>");
			String particles = "";
			for (Effects particle : Effects.values()) {
				particles += ChatColor.DARK_GRAY + particle.name() + ChatColor.GRAY + ", ";
			}
			sender.sendMessage(ChatColor.DARK_RED + "Particles: " + particles);
			return true;
		}

		if (CmdUtils.hasModifier(cmdArgs, "-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		if (CmdUtils.getOptionalArg(cmdArgs, "p:") != null) {
			player = cwc.getServer().getPlayer(((CmdUtils.getOptionalArg(cmdArgs, "p:"))));
		}
		int amt = Utils.getInt(CmdUtils.getOptionalArg(cmdArgs, "amt:"));
		String locStr = CmdUtils.getOptionalArg(cmdArgs, "loc:");
		String offsetStr = CmdUtils.getOptionalArg(cmdArgs, "offset:");
		World world = Utils.getWorld(CmdUtils.getOptionalArg(cmdArgs, "loc:"));

		//Console
		if (!(sender instanceof Player)) {
			if (player == null && locStr == null) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player or Location+World to use this on the console!");
				return true;
			}
		} else {
			if (player == null) {
				player = (Player) sender;
			}
		}

		if (amt > 100) {
			sender.sendMessage(pf + ChatColor.RED + "Amount can't be more then 100!");
			return true;
		}
		if (amt <= 0) {
			amt = 1;
		}
		if (world == null) {
			world = player.getWorld();
		}

		if (locStr != null && LocationUtils.getLocation(locStr, world) != null) {
			loc = LocationUtils.getLocation(locStr, world);
		} else if (locStr == null) {
			loc = player.getLocation();
		}
		if (offsetStr != null) {
			loc.add(LocationUtils.getLocation(offsetStr, world));
		}

		/* Action */
		if (args.length > 0) {
			String str = args[0].toLowerCase();
			for (int i = 0; i < amt; i++) {
				switch (str) {
					case "signal":
					case "ender":
						cwc.getEffects().playSignal(loc);
						played = true;
						break;
					case "lightning":
						cwc.getEffects().playLightning(loc);
						played = true;
						break;
					case "bigsmoke":
						cwc.getEffects().playBigSmoke(loc);
						played = true;
						break;
					case "cloud":
						if (args.length >= 2) {
							cwc.getEffects().playCloud(loc, args[1]);
						} else {
							sender.sendMessage(pf + ChatColor.RED + "You need to specify a radius for Cloud");
							return true;
						}
						played = true;
						break;
					case "smoke":
						if (args.length >= 2) {
							cwc.getEffects().playSmoke(loc, args[1]);
						} else {
							sender.sendMessage(pf + ChatColor.RED + "You need to specify a direction for smoke (0<>8)");
							return true;
						}
						played = true;
						break;
					case "splash":
					case "potion":
						if (args.length >= 2) {
							cwc.getEffects().playSplash(loc, args[1]);
						} else {
							sender.sendMessage(pf + ChatColor.RED + "You need to specify a splash effect");
							return true;
						}
						played = true;
						break;
					case "blockbreak":
					case "bbreak":
						if (args.length >= 2) {
							cwc.getEffects().playBlockBreak(loc, args[1]);
						} else {
							sender.sendMessage(pf + ChatColor.RED + "You need to specify a <blockID>");
							return true;
						}
						played = true;
						break;
					case "particle":
					case "part":
					case "p":
						if (args.length >= 6) {
							cwc.getEffects().playParticle(loc, args[1], args[2], args[3], args[4], args[5]);
						} else {
							sender.sendMessage(pf + ChatColor.RED + "You need to specify a <particle> <horSpread> <verSpread> <speed> <count>");
							sender.sendMessage(pf + ChatColor.GOLD + "Example: " + ChatColor.GRAY + "/particle p magiccrit 2.0 1.5 1 20");
							return true;
						}
						played = true;
						break;
				}
			}
		}
		if (!played) {
			sender.sendMessage(pf + ChatColor.RED + "Unknown particle effect " + ChatColor.GRAY + args[0]);
			return true;
		}

		if (!silent && played) {
			String locS = "" + ChatColor.GRAY + loc.getBlockX() + ChatColor.DARK_GRAY + "," + ChatColor.GRAY + loc.getBlockY() + ChatColor.DARK_GRAY
					+ "," + ChatColor.GRAY + loc.getBlockZ();
			if (args[0].startsWith("p")) {
				sender.sendMessage(pf + "Particle effect " + args[1] + " played at " + locS);
			} else {
				sender.sendMessage(pf + "Effect " + args[0] + " played at " + locS);
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.particle", "cwcore.cmd.*", "cwcore.*" };
	}
}
