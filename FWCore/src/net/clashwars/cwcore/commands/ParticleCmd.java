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
import org.bukkit.entity.Player;

public class ParticleCmd implements CommandClass {
	
	private CWCore cwc;
	
	public ParticleCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		Player target = null;
		int amt = 1;
		boolean played = false;
		Location loc = null;
		Location offset = null;
		World world = null;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/particle <particle> [params] [optional args]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Play particle effects");
			sender.sendMessage(pf + "Optional arguments: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "player:<player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Play effect on given player.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "loc:<x,y,z>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Play effect on given location.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "world:<worldname>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the world if you use location.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "amt:<amount>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set amount of particles.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "offset:<x,y,z>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Offset the given location to play effect.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-l" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "List all particles with all args.");
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
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean targetSet = false;
		if (CmdUtils.hasModifier(args,"player:", false)) {
			targetSet = true;
			target = CmdUtils.getPlayerFromArgs(args, "player:", cwc);
			args = CmdUtils.modifiedArgs(args,"player:", false);
		}
		if (CmdUtils.hasModifier(args,"amt:", false)) {
			CmdUtils.getArgIndex(args, "amt:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "amt:", false)].split(":");
        	if (splt.length > 1) {
				try {
				 	amt = Integer.parseInt(splt[1]);
				 } catch (NumberFormatException e) {
				 	sender.sendMessage(pf + ChatColor.RED + "Invalid amount, Must be a number.");
				 	return true;
				 }
        	}
			args = CmdUtils.modifiedArgs(args,"amt:", false);
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (!targetSet) {
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
		if (player == null) {
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
		
		loc = target.getLocation();
		world = target.getWorld();
		offset = new Location(world,0,0,0);
		
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
        		loc = LocationUtils.stringToLocation(splt[1], world);
        		if (loc == null) {
        			sender.sendMessage(pf + ChatColor.RED + "Invalid location, Must be x,y,z");
				 	return true;
        		}
        	}
			args = CmdUtils.modifiedArgs(args,"loc:", false);
		}
		boolean offsetSet = false;
		if (CmdUtils.hasModifier(args,"offset:", false)) {
			CmdUtils.getArgIndex(args, "offset:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "offset:", false)].split(":");
        	if (splt.length > 1) {
        		offset = LocationUtils.stringToLocation(splt[1], world);
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
