package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.CustomEntity;
import net.clashwars.cwcore.util.LocationUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpawnmobCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public SpawnmobCmd(CWCore cwc) {
		this.cwc = cwc;
		optionalArgs.put("p:<player>", "Spawn the mob at this player");
		optionalArgs.put("loc:<x,y,z>", "Spawn the mob at this location");
		optionalArgs.put("target:<player|x,y,z>", "Make a mob walk towards this player|loc");
		optionalArgs.put("vel:<x,y,z>", "Add velocity to the mob when spawned");
		optionalArgs.put("name:<name>", "Set display name of the mob");
		optionalArgs.put("hp:<amt>", "Set health of the mob");
		optionalArgs.put("size:<amt>", "Set the size of the mob (Slime,Magmacube)");
		optionalArgs.put("color:<color>", "Set the color of the mob (Sheep,Horse,Wolf)");
		optionalArgs.put("job:<librarian,priest,blacksmith,butcher>", "Set job for villagers");
		optionalArgs.put("power:<amt>", "Set the jumping power for horses");
		optionalArgs.put("style:<blackdot,whitedot,white,whitefield>", "Set horseStyle");
		optionalArgs.put("type:<mule,donkey,undead,skeleton>", "Set the horse type");
		optionalArgs.put("hand:<ID>:<enchant>.<lvl>[,<e>.<lvl>]", "Set hand item");
		optionalArgs.put("helm:<ID>:<enchant>.<lvl>[,<e>.<lvl>]", "Set helmet item.");
		optionalArgs.put("chest:<ID>:<enchant>.<lvl>[,<e>.<lvl>]", "Set chestplate item.");
		optionalArgs.put("leg:<ID>:<enchant>.<lvl>[,<e>.<lvl>]", "Set leggings item.");
		optionalArgs.put("boot:<ID>:<enchant>.<lvl>[,<e>.<lvl>]", "Set boots item.");
		optionalArgs.put("armor:[iron|gold|dia]", "Set horse armor.");
		optionalArgs.put("pe:<effect>.<dur>.<lvl>[,<e>.<d>.<l>]", "Set potion effects like pe:speed.10.1,jump.10.2");
		modifiers.put("s", "No messages");
		modifiers.put("d", "Force displayname to be always displayed.");
		modifiers.put("b", "Spawn the mob as a baby (animals,zombie,pigzombie)");
		modifiers.put("t", "Make the mob tamed (horse,wolf,ocelot)");
		modifiers.put("a", "Make the mob angry (wolf,pigzombie)");
		modifiers.put("p", "Set a creeper powered");
		modifiers.put("c", "Add a chest to donkey/mule");
		modifiers.put("r", "Ride the spawned mob");
		modifiers.put("m", "Mount a saddle on the mob (Pig,Horse)");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		String[] mobs = null;
		Location loc = null;
		int amt = 1;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean forceDisplay = CmdUtils.hasModifier(cmdArgs, "d");
		boolean baby = CmdUtils.hasModifier(cmdArgs, "b");
		boolean tamed = CmdUtils.hasModifier(cmdArgs, "t");
		boolean angry = CmdUtils.hasModifier(cmdArgs, "a");
		boolean powered = CmdUtils.hasModifier(cmdArgs, "p");
		boolean ride = CmdUtils.hasModifier(cmdArgs, "r");
		boolean mount = CmdUtils.hasModifier(cmdArgs, "m");
		if (CmdUtils.getOptionalArg(cmdArgs, "p:") != null) {
			player = cwc.getServer().getPlayer(((CmdUtils.getOptionalArg(cmdArgs, "p:"))));
		}
		String name = CmdUtils.getOptionalArg(cmdArgs, "name:");
		int health = Utils.getInt(CmdUtils.getOptionalArg(cmdArgs, "hp:"));
		String locStr = CmdUtils.getOptionalArg(cmdArgs, "loc:");
		String target = CmdUtils.getOptionalArg(cmdArgs, "target:");
		String vel = CmdUtils.getOptionalArg(cmdArgs, "vel:");
		int size = Utils.getInt(CmdUtils.getOptionalArg(cmdArgs, "size:"));
		String color = CmdUtils.getOptionalArg(cmdArgs, "color:");
		String job = CmdUtils.getOptionalArg(cmdArgs, "job:");
		int power = Utils.getInt(CmdUtils.getOptionalArg(cmdArgs, "power:"));
		String style = CmdUtils.getOptionalArg(cmdArgs, "style:");
		String type = CmdUtils.getOptionalArg(cmdArgs, "type:");
		String hand = CmdUtils.getOptionalArg(cmdArgs, "hand:");
		String helm = CmdUtils.getOptionalArg(cmdArgs, "helm:");
		String chest = CmdUtils.getOptionalArg(cmdArgs, "chest:");
		String leg = CmdUtils.getOptionalArg(cmdArgs, "leg:");
		String boot = CmdUtils.getOptionalArg(cmdArgs, "boot:");
		String armor = CmdUtils.getOptionalArg(cmdArgs, "armor:");
		String effects = CmdUtils.getOptionalArg(cmdArgs, "pe:");
		
		//Console
		if (!(sender instanceof Player)) {
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console! (p:<player>");
				return true;
			}
		} else {
			if (player == null) {
				player = (Player) sender;
			}
		}
		

		//Args
		if (args.length >= 1) {
			mobs = args[0].split(",");
		}
		
		if (args.length >= 2) {
			try {
			 	amt = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid amount, Must be a number.");
				return true;
			}
			if (amt > 100) {
				sender.sendMessage(pf + ChatColor.RED + "You can't spawn more then 100 mobs!");
			 	return true;
			}
		}
		
		
		//Action
		if (locStr != null) {
			loc = LocationUtils.getLocation(locStr, player.getWorld());
		}
		if (loc == null) {
			loc = player.getTargetBlock(null, 100).getLocation();
		}
		loc.setY(loc.getY()+1);
		
		
		Vector velocity = null;
		if (vel != null) {
			if (LocationUtils.getVector(vel) != null) {
				velocity = LocationUtils.getVector(vel);
			}
		}
		
		
		Location targetLoc = null;
		if (target != null) {
			if (cwc.getServer().getPlayer(target) != null) {
				targetLoc = cwc.getServer().getPlayer(target).getLocation();
			}
			if (targetLoc == null && LocationUtils.getLocation(target, player.getWorld()) != null) {
				targetLoc = LocationUtils.getLocation(target, player.getWorld());
			}
		}
		
		
		int spawned = 0;
		CustomEntity ce = null;
		EntityType mobType = null;
		Entity spawnedMob = null;
		Entity mob1 = null;
		boolean switchType = false;
		
		while (spawned < amt) {
			for (int i = 0; i < mobs.length; i++) {
				mobType = AliasUtils.findEntity(mobs[i]);
				if (mobType == null) {
					if (mobs[i].startsWith("wit") || mobs[i].startsWith("ske")) {
						mobType = EntityType.SKELETON;
					}
					if (mobs[i].startsWith("zom") || mobs[i].startsWith("vil")) {
						mobType = EntityType.ZOMBIE;
					}
					if (mobType == null) {
						sender.sendMessage(pf + ChatColor.RED + "Invalid mob/entity.");
						return true;
					}
					switchType = true;
				}
				Entity mob = loc.getWorld().spawnEntity(loc,mobType);
				ce = new CustomEntity(mob);
				
				if (switchType != false) ce.switchType();
				if (name != null) ce.setName(name);
				if (health > 0) ce.setHP(health);
				if (size > 0) ce.setSize(size);
				if (color != null) ce.setColor(color);
				if (job != null) ce.setJob(job);
				if (power > 0) ce.setPower(power);
				if (style != null) ce.setStyle(style);
				if (type != null) ce.setType(type);
				if (hand != null) ce.setHand(hand);
				if (helm != null) ce.setHelmet(helm);
				if (chest != null) ce.setChest(chest);
				if (leg != null) ce.setLeg(leg);
				if (boot != null) ce.setBoot(boot);
				if (armor != null) ce.setHorseArmor(armor);
				if (effects != null) ce.setEffects(effects);
				
				if (forceDisplay == true) ce.setDisplay();
				if (baby == true) ce.setBaby();
				if (tamed == true) ce.setTamed(player);
				if (angry == true) ce.setAngry();
				if (powered == true) ce.setPowered();
				if (mount == true) ce.setMounted();
				ce.fixSkeleton();
				
				if (i == 0) {
					spawnedMob = mob;
					mob1 = mob;
				} else {
					spawnedMob.setPassenger(mob);
					spawnedMob = mob;
				}
			}
			spawned++;
		}
		
		
		if (ride == true) spawnedMob.setPassenger(player);
		if (velocity != null) mob1.setVelocity(velocity);
		if (targetLoc != null) {
			if (mob1 instanceof Creature) {
				//TODO: Move the mob to target somehow..
			}
		}
		
		if (!silent) {
			player.sendMessage(pf + "Spawned " + ChatColor.DARK_GRAY + amt + ChatColor.DARK_PURPLE + " " + args[0] + "s");
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.spawnmob", "cwcore.cmd.*", "cwcore.*" };
	}
}
