package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.CustomEntity;
import net.clashwars.cwcore.util.LocationUtils;

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
	
	public SpawnmobCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		String[] mobs = null;
		Location loc = null;
		int amt = 1;
		
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			sender.sendMessage(pf + "Optional args: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "p:<player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Spawn the mob at this player");
			sender.sendMessage(ChatColor.DARK_PURPLE + "name:<name>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set display name of the mob");
			sender.sendMessage(ChatColor.DARK_PURPLE + "hp:<amt>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set health of the mob");
			sender.sendMessage(ChatColor.DARK_PURPLE + "loc:<x,y,z>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Spawn the mob at this location");
			sender.sendMessage(ChatColor.DARK_PURPLE + "target:<player|x,y,z>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Make a mob walk towards this player|loc");
			sender.sendMessage(ChatColor.DARK_PURPLE + "vel:<x,y,z>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Add velocity to the mob when spawned");
			sender.sendMessage(ChatColor.DARK_PURPLE + "size:<amt>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the size of the mob (Slime,Magmacube)");
			sender.sendMessage(ChatColor.DARK_PURPLE + "color:<color>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the color of the mob (Sheep,Horse,Wolf)");
			sender.sendMessage(ChatColor.DARK_PURPLE + "job:<librarian,priest,blacksmith,butcher>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set job for villagers");
			sender.sendMessage(ChatColor.DARK_PURPLE + "power:<amt>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the jumping power for horses");
			sender.sendMessage(ChatColor.DARK_PURPLE + "style:<blackdot,whitedot,white,whitefield>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set horseStyle");
			sender.sendMessage(ChatColor.DARK_PURPLE + "type:<mule,donkey,undead,skeleton>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the horse type.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "hand:<ID>:[data]:[enchant:lvl,...]" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set item in hand.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "helm:<ID>:[data]:[enchant:lvl,...]" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set helmet item.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "chest:<ID>:[data]:[enchant:lvl,...]" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set chest item.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "leg:<ID>:[data]:[enchant:lvl,...]" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set leggings item.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "boot:<ID>:[data]:[enchant:lvl,...]" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set boots item.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "armor:[iron|gold|dia]" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set horse armor.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-d" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Force displayname to be always displayed.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-b" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Spawn the mob as a baby (animals,zombie,pigzombie)");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-t" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Make the mob tamed (horse,wolf,ocelot)");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-a" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Make the mob angry (wolf,pigzombie)");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-p" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set a creeper powered");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-c" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Add a chest to donkey/mule");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-r" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Ride the spawned mob");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-m" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Mount a saddle on the mob (Pig,Horse)");
			return true;
		}
		
		// MODIFIERS
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		boolean forceDisplay = false;
		if (CmdUtils.hasModifier(args,"-d", true)) {
			forceDisplay = true;
			args = CmdUtils.modifiedArgs(args,"-d", true);
		}
		boolean baby = false;
		if (CmdUtils.hasModifier(args,"-b", true)) {
			baby = true;
			args = CmdUtils.modifiedArgs(args,"-b", true);
		}
		boolean tamed = false;
		if (CmdUtils.hasModifier(args,"-t", true)) {
			tamed = true;
			args = CmdUtils.modifiedArgs(args,"-t", true);
		}
		boolean angry = false;
		if (CmdUtils.hasModifier(args,"-a", true)) {
			angry = true;
			args = CmdUtils.modifiedArgs(args,"-a", true);
		}
		boolean powered = false;
		if (CmdUtils.hasModifier(args,"-p", true)) {
			powered = true;
			args = CmdUtils.modifiedArgs(args,"-p", true);
		}
		boolean ride = false;
		if (CmdUtils.hasModifier(args,"-r", true)) {
			ride = true;
			args = CmdUtils.modifiedArgs(args,"-r", true);
		}
		boolean mount = false;
		if (CmdUtils.hasModifier(args,"-m", true)) {
			mount = true;
			args = CmdUtils.modifiedArgs(args,"-m", true);
		}
		
		// OPTIONAL ARGS
		Player player = null;
		if (CmdUtils.hasModifier(args,"p:", false)) {
			player = CmdUtils.getPlayer(args, "p:", cwc);
			args = CmdUtils.modifiedArgs(args,"p:", false);
		}
		String name = "";
		if (CmdUtils.hasModifier(args,"name:", false)) {
			name = CmdUtils.getString(args, "name:");
			args = CmdUtils.modifiedArgs(args,"name:", false);
		}
		int health = 0;
		if (CmdUtils.hasModifier(args,"hp:", false)) {
			health = CmdUtils.getInt(args, "hp:");
			args = CmdUtils.modifiedArgs(args,"hp:", false);
		}
		String locStr = "";
		if (CmdUtils.hasModifier(args,"loc:", false)) {
			locStr = CmdUtils.getString(args, "loc:");
			args = CmdUtils.modifiedArgs(args,"loc:", false);
		}
		String target = "";
		if (CmdUtils.hasModifier(args,"target:", false)) {
			locStr = CmdUtils.getString(args, "target:");
			args = CmdUtils.modifiedArgs(args,"target:", false);
		}
		String vel = "";
		if (CmdUtils.hasModifier(args,"vel:", false)) {
			vel = CmdUtils.getString(args, "vel:");
			args = CmdUtils.modifiedArgs(args,"vel:", false);
		}
		int size = 0;
		if (CmdUtils.hasModifier(args,"size:", false)) {
			size = CmdUtils.getInt(args, "size:");
			args = CmdUtils.modifiedArgs(args,"size:", false);
		}
		String color = "";
		if (CmdUtils.hasModifier(args,"color:", false)) {
			color = CmdUtils.getString(args, "color:");
			args = CmdUtils.modifiedArgs(args,"color:", false);
		}
		String job = "";
		if (CmdUtils.hasModifier(args,"job:", false)) {
			job = CmdUtils.getString(args, "job:");
			args = CmdUtils.modifiedArgs(args,"job:", false);
		}
		int power = 0;
		if (CmdUtils.hasModifier(args,"power:", false)) {
			power = CmdUtils.getInt(args, "power:");
			args = CmdUtils.modifiedArgs(args,"power:", false);
		}
		String style = "";
		if (CmdUtils.hasModifier(args,"style:", false)) {
			style = CmdUtils.getString(args, "style:");
			args = CmdUtils.modifiedArgs(args,"style:", false);
		}
		String type = "";
		if (CmdUtils.hasModifier(args,"type:", false)) {
			job = CmdUtils.getString(args, "type:");
			args = CmdUtils.modifiedArgs(args,"type:", false);
		}
		String hand = "";
		if (CmdUtils.hasModifier(args,"hand:", false)) {
			hand = CmdUtils.getString(args, "hand:");
			args = CmdUtils.modifiedArgs(args,"hand:", false);
		}
		String helm = "";
		if (CmdUtils.hasModifier(args,"helm:", false)) {
			helm = CmdUtils.getString(args, "helm:");
			args = CmdUtils.modifiedArgs(args,"helm:", false);
		}
		String chest = "";
		if (CmdUtils.hasModifier(args,"chest:", false)) {
			chest = CmdUtils.getString(args, "chest:");
			args = CmdUtils.modifiedArgs(args,"chest:", false);
		}
		String leg = "";
		if (CmdUtils.hasModifier(args,"leg:", false)) {
			leg = CmdUtils.getString(args, "leg:");
			args = CmdUtils.modifiedArgs(args,"leg:", false);
		}
		String boot = "";
		if (CmdUtils.hasModifier(args,"boot:", false)) {
			boot = CmdUtils.getString(args, "boot:");
			args = CmdUtils.modifiedArgs(args,"boot:", false);
		}
		String armor = "";
		if (CmdUtils.hasModifier(args,"armor:", false)) {
			armor = CmdUtils.getString(args, "armor:");
			args = CmdUtils.modifiedArgs(args,"armor:", false);
		}
		
		/* Console check */
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
		
		/* 1 arg (Mobs) */
		if (args.length >= 1) {
			mobs = args[0].split(",");
		}
		
		/* 2 args (Amount) */
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
		
		/* Get location */
		if (locStr != "") {
			loc = LocationUtils.getLocation(locStr, player.getWorld());
		}
		if (loc == null) {
			loc = player.getTargetBlock(null, 100).getLocation();
		}
		loc.setY(loc.getY()+1);
		
		/* Get Velocity */
		Vector velocity = null;
		if (vel != "") {
			if (LocationUtils.getVector(vel) != null) {
				velocity = LocationUtils.getVector(vel);
			}
		}
		
		/* Get Target */
		Location targetLoc = null;
		if (target != "") {
			if (cwc.getServer().getPlayer(target) != null) {
				targetLoc = cwc.getServer().getPlayer(target).getLocation();
			}
			if (targetLoc == null && LocationUtils.getLocation(target, player.getWorld()) != null) {
				targetLoc = LocationUtils.getLocation(target, player.getWorld());
			}
		}
		
		
		/* Action */
		int spawned = 0;
		CustomEntity ce = null;
		EntityType mobType = null;
		Entity spawnedMob = null;
		Entity mob1 = null;
		
		while (spawned < amt) {
			for (int i = 0; i < mobs.length; i++) {
				mobType = AliasUtils.findEntity(mobs[i]);
				if (mobType == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid mob/entity.");
					return true;
				}
				Entity mob = loc.getWorld().spawnEntity(loc,mobType);
				ce = new CustomEntity(mob);
				
				if (name != "") ce.setName(name);
				if (health > 0) ce.setHP(health);
				if (size > 0) ce.setSize(size);
				if (color != "") ce.setColor(color);
				if (job != "") ce.setJob(job);
				if (power > 0) ce.setPower(power);
				if (style != "") ce.setStyle(style);
				if (type != "") ce.setType(type);
				if (hand != "") ce.setHand(hand);
				if (helm != "") ce.setHelmet(helm);
				if (chest != "") ce.setChest(chest);
				if (leg != "") ce.setLeg(leg);
				if (boot != "") ce.setBoot(boot);
				if (armor != "") ce.setHorseArmor(armor);
				
				if (forceDisplay == true) ce.setDisplay();
				if (baby == true) ce.setBaby();
				if (tamed == true) ce.setTamed(player);
				if (angry == true) ce.setAngry();
				if (powered == true) ce.setPowered();
				if (mount == true) ce.setMounted();
				
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
