package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
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
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
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
		Player target = null;
		boolean played = false;
		Location loc = null;
		Location offset = null;
		World world = null;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		if (CmdUtils.hasModifier(args,"-l", true)) {
			String sep = ChatColor.DARK_GRAY + ", " + ChatColor.GOLD;
			sender.sendMessage(ChatColor.DARK_GRAY + "===== " + ChatColor.DARK_RED + "Particle List" + ChatColor.DARK_GRAY + " =====");
			sender.sendMessage(ChatColor.DARK_RED + "Effects without args: " + ChatColor.GOLD 
					+ "singnal" + sep + "flames"  + sep + "explosion" + sep + "lightning" + sep + "bigsmoke");
			sender.sendMessage(ChatColor.GOLD + "cloud" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "<radius>");
			sender.sendMessage(ChatColor.GOLD + "smoke" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "<direction(0<>8)>");
			sender.sendMessage(ChatColor.GOLD + "splash" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "<potionTypeId>");
			sender.sendMessage(ChatColor.GOLD + "blockbreak" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "<blockID>");
			sender.sendMessage(ChatColor.GOLD + "particle" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "<particle> <horSpread> <verSpread> <speed> <count>");
			sender.sendMessage(ChatColor.DARK_RED + "Particles: " + ChatColor.GOLD 
					+ "hugeexplode" + sep + "largeexplode" + sep + "firework" + sep + "bubble" + "suspend" + sep + "depthsuspend" + sep + "aura" + sep
					+ "crit" + sep + "magiccrit" + sep + "smoke" + sep + "mobspell" + sep + "mobspellambient" + sep + "spell" + sep + "instantSpell" + sep
					+ "witch" + sep + "note" + sep + "portal" + sep + "enchant" + sep + "explode" + sep + "flames" + sep + "lava" + sep + "steps" + sep
					+ "splash" + sep + "largesmoke" + sep + "cloud" + sep + "dust" + sep + "snowball" + sep + "dripwater" + sep + "driplava" + sep
					+ "snowbreak" + sep + "slime" + sep + "heart" + sep + "angry" + sep + "happy");
			return true;
		}
		
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		if (CmdUtils.getOptionalArg(cmdArgs, "p:") != null) {
			target = cwc.getServer().getPlayer(((CmdUtils.getOptionalArg(cmdArgs, "p:"))));
		}
		String name = CmdUtils.getOptionalArg(cmdArgs, "name:");
		int amt = Utils.getInt(CmdUtils.getOptionalArg(cmdArgs, "amt:"));
		String locStr = CmdUtils.getOptionalArg(cmdArgs, "loc:");
		
		
		//Console
		if (!(sender instanceof Player)) {
			if (!targetSet && !locSet) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player or Location+World to use this on the console!");
				return true;
			}
		} else {
			player = (Player) sender;
			if (!targetSet) {
				target = (Player) sender;
			}
		}
		
		/* null checks */
		if (player == null && !locSet) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (target == null && targetSet == true) {
			if (targetSet) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid target player.");
				return true;
			}
		}
		if (amt > 100) {
    		sender.sendMessage(pf + ChatColor.RED + "Amount can't be more then 100!");
    		return true;
    	}
		
		if (!locSet) {
			loc = target.getLocation();
			world = target.getWorld();
		}
		
		if (CmdUtils.hasModifier(args,"world:", false)) {
			CmdUtils.getArgIndex(args, "world:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "world:", false)].split(":");
        	if (splt.length > 1) {
        		if (cwc.getServer().getWorld(splt[1]) == null) {
				 	sender.sendMessage(pf + ChatColor.RED + "Invalid world.");
				 	return true;
				} else {
					world = cwc.getServer().getWorld(splt[1]);
				}
        	}
			args = CmdUtils.modifiedArgs(args,"world:", false);
		}
		if (CmdUtils.hasModifier(args,"loc:", false)) {
			CmdUtils.getArgIndex(args, "loc:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "loc:", false)].split(":");
        	if (splt.length > 1) {
        		loc = LocationUtils.getLocation(splt[1], world);
        		if (loc == null) {
        			sender.sendMessage(pf + ChatColor.RED + "Invalid location, Must be x,y,z");
				 	return true;
        		}
        	}
			args = CmdUtils.modifiedArgs(args,"loc:", false);
		}
		offset = new Location(world,0,0,0);
		boolean offsetSet = false;
		if (CmdUtils.hasModifier(args,"offset:", false)) {
			CmdUtils.getArgIndex(args, "offset:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "offset:", false)].split(":");
        	if (splt.length > 1) {
        		offset = LocationUtils.getLocation(splt[1], world);
        		if (offset == null) {
        			sender.sendMessage(pf + ChatColor.RED + "Invalid offset, Must be x,y,z");
				 	return true;
        		}
        		offsetSet = true;
        	}
			args = CmdUtils.modifiedArgs(args,"offset:", false);
		}
		
		/* Action */
		if (offsetSet) {
			loc.add(offset);
		}
		if (args.length >= 1) {
			String str = args[0].toLowerCase();
			for (int i = 0; i < amt; i++) {
				switch (str) {
					case "signal":
					case "ender":
						cwc.getEffects().playSignal(loc);
						played = true;
						break;
					case "flames":
					case "flame":
						cwc.getEffects().playFlames(loc);
						played = true;
						break;
					case "explosion":
					case "explode":
						cwc.getEffects().playExplosion(loc);
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
			String locStr = "" + ChatColor.GRAY + loc.getBlockX() + ChatColor.DARK_GRAY + "," + ChatColor.GRAY + loc.getBlockY() + ChatColor.DARK_GRAY + "," + ChatColor.GRAY + loc.getBlockZ();
			if (args[0].startsWith("p")) {
				sender.sendMessage(pf + "Particle effect " + args[1] + " played at " + locStr);
			} else {
				sender.sendMessage(pf + "Effect " + args[0] + " played at " + locStr);
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.particle", "cwcore.cmd.*", "cwcore.*" };
	}
}
