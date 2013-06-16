package net.clashwars.cwcore.commands;

import java.util.List;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.EntityUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnmobCmd implements CommandClass {
	
	private CWCore cwc;
	
	public SpawnmobCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		Player target = null;
		String name = null;
		int health = -1;
		EntityType mob = null;
		List<String> mobs = null;
		List<String> mobsData = null;
		Location loc = null;
		int amt = 1;
		
		/* Modifiers + No args */
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s")) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s");
		}
		boolean forceTarget = false;
		if (CmdUtils.hasModifier(args,"-t")) {
			forceTarget = true;
			args = CmdUtils.modifiedArgs(args,"-t");
		}
		boolean forceSpawn = false;
		if (CmdUtils.hasModifier(args,"-f")) {
			forceSpawn = true;
			args = CmdUtils.modifiedArgs(args,"-f");
		}
		boolean displayName = false;
		if (CmdUtils.hasModifier(args,"-d")) {
			displayName = true;
			args = CmdUtils.modifiedArgs(args,"-d");
		}
		boolean targetSet = false;
		if (CmdUtils.hasModifier(args,"player:")) {
			targetSet = true;
			target = CmdUtils.getPlayerFromArgs(args, "player:", cwc);
			args = CmdUtils.modifiedArgs(args,"player:");
		}
		boolean nameSet = false;
		if (CmdUtils.hasModifier(args,"name:")) {
			nameSet = true;
			String[] splt = args[CmdUtils.getArgIndex(args, "name:")].split(":");
			name = splt[1];
			args = CmdUtils.modifiedArgs(args,"name:");
		}
		boolean healthSet = false;
		if (CmdUtils.hasModifier(args,"hp:")) {
			healthSet = true;
			CmdUtils.getArgIndex(args, "hp:");
			
			String[] splt = args[CmdUtils.getArgIndex(args, "hp:")].split(":");
        	if (splt.length > 1) {
				try {
				 	health = Integer.parseInt(splt[1]);
				 } catch (NumberFormatException e) {
				 	sender.sendMessage(pf + ChatColor.RED + "Invalid hp amount, Must be a number.");
				 	return true;
				 }
        	}
			args = CmdUtils.modifiedArgs(args,"hp:");
		}
		if (CmdUtils.hasModifier(args,"-h") || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/spawnmob [mob[:data],mob[:data],...] [amount] [Optional args]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Spawn mobs");
			sender.sendMessage(pf + "Optional args: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "player:<player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Spawn mobs at player location");
			sender.sendMessage(ChatColor.DARK_PURPLE + "name:<name>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set display name of the mob");
			sender.sendMessage(ChatColor.DARK_PURPLE + "hp:<amt>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set health of the mob");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-t" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Force mob to attack target, requires player:<player>");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-d" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Force a name displayed, requires name:<name>");
			sender.sendMessage(pf + "Mob data: ");
			sender.sendMessage(ChatColor.YELLOW + "(ageable):" + ChatColor.GRAY + "baby , "
					+ ChatColor.YELLOW + "creeper:" + ChatColor.GRAY + "powered");
			sender.sendMessage(ChatColor.YELLOW + "wolf:" + ChatColor.GRAY + "angry|tamed , "
					+ ChatColor.YELLOW + "slimes:" + ChatColor.GRAY + "size(1-120)");
			sender.sendMessage(ChatColor.YELLOW + "skeleton:" + ChatColor.GRAY + "wither , "
					+ ChatColor.YELLOW + "zombie:" + ChatColor.GRAY + "villager"); 
			sender.sendMessage(ChatColor.YELLOW + "sheep:" + ChatColor.GRAY + "<color> , "
					+ ChatColor.YELLOW + "villager:" + ChatColor.GRAY + "librarian|priest|blacksmith|butcher");
			sender.sendMessage(ChatColor.YELLOW + "ocelot:" + ChatColor.GRAY + "tamed|siamese|red|black , "
					+ ChatColor.YELLOW + "xporb:" + ChatColor.GRAY + "<amount>");
			return true;
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 2 && targetSet == false) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console! (player:<player>");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		/* 1 arg (mob:data,mob:data) */
		if (args.length >= 1) {
			mobs = EntityUtils.splitMobs(args[0]);
			mobsData = EntityUtils.splitData(args[0]);
		}
		
		/* 2 args (Amount) */
		if (args.length >= 2) {
			try {
			 	amt = Integer.parseInt(args[1]);
			 } catch (NumberFormatException e) {
			 	sender.sendMessage(pf + ChatColor.RED + "Invalid amount, Must be a number.");
			 	return true;
			 }
		}
		
		/* Get location */
		if (targetSet) {
			loc = target.getLocation();
		} else {
			loc = player.getTargetBlock(null, 100).getLocation();
			loc.setY(loc.getY()+1);
		}
		
		/* null checks */
		if (amt > 100 && forceSpawn == false) {
			sender.sendMessage(pf + ChatColor.RED + "You can't spawn more then 100 mobs!");
		 	return true;
		}
		
		/* Action */
		int spawned = 0;
		while (spawned < amt) {
		Entity spawnedMob = null;
		Entity spawnedMount = null;
		for (int i = 0; i < mobs.size(); i++) {
			if (i == 0) {
				mob = AliasUtils.findEntityType(mobs.get(i));
				if (mob == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid mob.");
					return true;
				}
					spawnedMob = cwc.getServer().getWorld(player.getWorld().getName()).spawnEntity(loc, mob);
					if (mobsData.get(i) != null) {
						EntityUtils.changeMobData(mob, spawnedMob, mobsData.get(i), player);
					}
					if (healthSet == true && spawnedMob instanceof Creature) {
						((Creature)spawnedMob).setMaxHealth(health);
						((Creature)spawnedMob).setHealth(health);
					}
					if (nameSet == true && spawnedMob instanceof Creature) {
						((Creature)spawnedMob).setCustomName(Utils.integrateColor(name));
						if (displayName) {
							((Creature)spawnedMob).setCustomNameVisible(true);
						}
					}
					if (targetSet == true && forceTarget == true &&spawnedMob instanceof Creature) {
						((Creature)spawnedMob).setTarget(target);
					}
					spawned++;
			} else {
				mob = AliasUtils.findEntityType(mobs.get(i));
				if (mob == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid mob.");
					return true;
				}
					spawnedMount = cwc.getServer().getWorld(player.getWorld().getName()).spawnEntity(loc, mob);
					if (mobsData.get(i) != null) {
						EntityUtils.changeMobData(mob, spawnedMount, mobsData.get(i), player);
					}
					if (healthSet == true && spawnedMount instanceof Creature) {
						((Creature)spawnedMount).setMaxHealth(health);
						((Creature)spawnedMount).setHealth(health);
					}
					if (nameSet == true && spawnedMount instanceof Creature) {
						((Creature)spawnedMount).setCustomName(Utils.integrateColor(name));
						if (displayName) {
							((Creature)spawnedMob).setCustomNameVisible(true);
						}
					}
					if (targetSet == true && forceTarget == true &&spawnedMob instanceof Creature) {
						((Creature)spawnedMount).setTarget(target);
					}
					spawnedMob.setPassenger(spawnedMount);
					spawnedMob = spawnedMount;
				}
			}
		}
		
		if (!silent) {
			String msg = pf + "Spawned " + ChatColor.DARK_PURPLE + amt + " " + args[0];
			if (healthSet) {
				msg += (ChatColor.GOLD + " health: " + ChatColor.DARK_PURPLE + health);
			}
			if (nameSet) {
				msg += (ChatColor.GOLD + " name: " + ChatColor.DARK_PURPLE + name);
			}
			if (targetSet) {
				msg += (ChatColor.GOLD + " at " + ChatColor.DARK_PURPLE + target.getDisplayName());
			}
			player.sendMessage(msg);
		}
		
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.spawnmob", "cwcore.cmd.*", "cwcore.*" };
	}
}
