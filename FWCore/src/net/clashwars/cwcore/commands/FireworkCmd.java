package net.clashwars.cwcore.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.ItemUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkCmd implements CommandClass {

	private CWCore	cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;

	public FireworkCmd(CWCore cwc) {
		this.cwc = cwc;
		optionalArgs.put("p:<power>", "Set the power amount");
		optionalArgs.put("e:<effect>", "Set the effect type: sball, bball, creep, burst, star");
		optionalArgs.put("c:<#RRGGBB>[,#..]", "Set colors");
		optionalArgs.put("fc:<#RRGGBB>", "Set fade colors");
		modifiers.put("s", "No messages");
		modifiers.put("fl", "Add flicker/twinkle effect");
		modifiers.put("fa", "Add fade/trail effect");
		modifiers.put("l", "Launch the firework");
		modifiers.put("e", "Play firework effect without rocket");
		modifiers.put("t", "Use target location for effect/launch");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		int amt = 1;
		int chance = 0;
		Location loc = null;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(args, "-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			sender.sendMessage(pf + ChatColor.GOLD + "It will randomize everything you don't specify! :D");
			return true;
		}
		
		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean trail = CmdUtils.hasModifier(cmdArgs, "fa");
		boolean flicker = CmdUtils.hasModifier(cmdArgs, "fl");
		boolean launch = CmdUtils.hasModifier(cmdArgs, "l");
		boolean effectOnly = CmdUtils.hasModifier(cmdArgs, "e");
		boolean targeted = CmdUtils.hasModifier(cmdArgs, "t");
		int power = Utils.getInt(CmdUtils.getOptionalArg(cmdArgs, "p:"));
		Type effectType = Utils.getFireworkEffect(CmdUtils.getOptionalArg(cmdArgs, "e:"));
		ArrayList<Color> colors = Utils.getColors(CmdUtils.getOptionalArg(cmdArgs, "c:"));
		ArrayList<Color> fcolors = Utils.getColors(CmdUtils.getOptionalArg(cmdArgs, "fc:"));

		
		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			player = (Player) sender;
		}

		
		//Args
		if (args.length >= 1) {
			try {
				amt = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid amount, Must be a number.");
				return true;
			}
		}

		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}

		
		//Randomize
		if (power < 0) {
			power = (int) (Math.random() * 4);
		}
		if (effectType == null) {
			chance = (int) (Math.random() * 4);
			switch (chance) {
				case 0:
					effectType = Type.BALL;
					break;
				case 1:
					effectType = Type.BALL_LARGE;
					break;
				case 2:
					effectType = Type.CREEPER;
					break;
				case 3:
					effectType = Type.BURST;
					break;
				case 4:
					effectType = Type.STAR;
					break;
			}
		}
		if (colors.size() == 0) {
			colors.add(Utils.getRandomColor());
		}
		for (int i = 0; i < colors.size(); i++) {
			if (colors.get(i) == null) {
				colors.set(i, Utils.getRandomColor());
			}
		}
		if (fcolors.size() == 0) {
			fcolors.add(Utils.getRandomColor());
		}
		for (int i = 0; i < fcolors.size(); i++) {
			if (fcolors.get(i) == null) {
				fcolors.set(i, Utils.getRandomColor());
			}
		}

		/* Action */
		if (targeted) {
			loc = (Location) player.getTargetBlock(null, 100).getLocation();
		} else {
			loc = player.getLocation();
		}

		FireworkEffect.Builder b = FireworkEffect.builder();
		b.with(effectType);
		b.withColor(colors);
		b.withFade(fcolors);
		b.trail(trail);
		b.flicker(flicker);

		if (launch) {
			for (int i = 0; i < amt; i++) {
				Firework fw = (Firework) player.getWorld().spawn(loc, Firework.class);
				FireworkMeta fm = (FireworkMeta) fw.getFireworkMeta();
				fm.setPower(power);
				fm.addEffect(b.build());
				fw.setFireworkMeta(fm);
			}
		}
		if (effectOnly && !launch) {
			power = 0;
			for (int i = 0; i < amt; i++) {
				ItemUtils.createFireworksExplosion(loc, flicker, trail, effectType, toIntegerArray(colors), toIntegerArray(fcolors), power);
			}
		}
		if (!effectOnly && !launch) {
			ItemStack fwork = new ItemStack(Material.FIREWORK, amt);
			FireworkMeta meta = (FireworkMeta) fwork.getItemMeta();
			meta.setPower(power);
			meta.addEffect(b.build());
			fwork.setItemMeta(meta);

			player.getInventory().addItem(fwork);
		}

		if (!silent) {
			String msg = pf + "You have";
			if (launch)
				msg += " launched ";
			if (effectOnly && !launch)
				msg += " created ";
			if (!launch && !effectOnly)
				msg += " given ";
			msg += "" + ChatColor.DARK_PURPLE + amt + ChatColor.GOLD;
			msg += amt > 1 ? " fireworks" : " firework" + " at ";
			msg += ChatColor.DARK_PURPLE + player.getName() + ChatColor.GRAY + " - ";
			msg += ChatColor.GOLD + " Effect: " + ChatColor.GRAY + effectType.toString().toLowerCase();
			msg += ChatColor.GOLD + " Trail: " + ChatColor.GRAY + trail;
			msg += ChatColor.GOLD + " Flicker: " + ChatColor.GRAY + flicker;

			player.sendMessage(msg);
		}

		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.firework", "cwcore.cmd.*", "cwcore.*" };
	}

	private int[] toIntegerArray(List<Color> cols) {
		int[] ret = new int[cols.size()];
		
		for (int i=0;i<cols.size();i++) {
			ret[i] = cols.get(i).asRGB();
		}
		
		return ret;
	}
}
