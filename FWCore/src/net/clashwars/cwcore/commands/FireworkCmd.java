package net.clashwars.cwcore.commands;

import java.util.ArrayList;
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

	public FireworkCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		int amt = 1;
		int chance = 0;
		int power = 0;
		boolean trail = false;
		boolean flicker = false;
		Type effectType = null;
		ArrayList<Color> colors = new ArrayList<Color>();
		ArrayList<Color> fcolors = new ArrayList<Color>();
		int itype = 0;
		Location loc = null;

		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args, "-h", false)) {
			CmdUtils.commandHelp(sender, lbl);
			sender.sendMessage(pf + "Optional args: ");
			sender.sendMessage(pf + ChatColor.GRAY + "If any of the optional args are missing it will randomize it! :)");
			sender.sendMessage(ChatColor.DARK_PURPLE + "p:<power>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the power: "
					+ ChatColor.YELLOW + "0, 1, 2, 3, 4, 5");
			sender.sendMessage(ChatColor.DARK_PURPLE + "e:<effect>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the effect type: "
					+ ChatColor.YELLOW + "sball, bball, creep, burst, star");
			sender.sendMessage(ChatColor.DARK_PURPLE + "a:<trail|flicker>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Set the after effect: "
					+ ChatColor.YELLOW + "trail or flicker");
			sender.sendMessage(ChatColor.DARK_PURPLE + "c:<#RRGGBB>,[...],etc" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY
					+ "Add colors can add multiple.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "fc:<#RRGGBB>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY
					+ "Add a fade colors can add multiple.");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-l" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY
					+ "Launch the rocket instead of giving it.");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-e" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Create the firework effect");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-t" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY
					+ "Create/launch firework at target location.");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args, "-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args, "-s", true);
		}
		boolean launch = false;
		if (CmdUtils.hasModifier(args, "-l", true)) {
			launch = true;
			args = CmdUtils.modifiedArgs(args, "-l", true);
		}
		boolean effectOnly = false;
		if (CmdUtils.hasModifier(args, "-e", true)) {
			effectOnly = true;
			args = CmdUtils.modifiedArgs(args, "-e", true);
		}
		boolean targeted = false;
		if (CmdUtils.hasModifier(args, "-t", true)) {
			targeted = true;
			args = CmdUtils.modifiedArgs(args, "-t", true);
		}

		boolean powerSet = false;
		if (CmdUtils.hasModifier(args, "p:", false)) {
			powerSet = true;
			CmdUtils.getArgIndex(args, "p:", false);

			String[] splt = args[CmdUtils.getArgIndex(args, "p:", false)].split(":");
			if (splt.length > 1) {
				try {
					power = Integer.parseInt(splt[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid power amount, Must be a number.");
					return true;
				}
			}
			args = CmdUtils.modifiedArgs(args, "p:", false);
		}

		boolean effectSet = false;
		if (CmdUtils.hasModifier(args, "e:", false)) {
			effectSet = true;
			CmdUtils.getArgIndex(args, "e:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "e:", false)].split(":");
			if (splt.length > 1) {
				if (splt[1].startsWith("sb")) {
					effectType = Type.BALL;
					itype = 0;
				} else if (splt[1].startsWith("bb")) {
					effectType = Type.BALL_LARGE;
					itype = 1;
				} else if (splt[1].startsWith("c")) {
					effectType = Type.CREEPER;
					itype = 3;
				} else if (splt[1].startsWith("bu")) {
					effectType = Type.BURST;
					itype = 4;
				} else if (splt[1].startsWith("s")) {
					effectType = Type.STAR;
					itype = 2;
				}
			}
			args = CmdUtils.modifiedArgs(args, "e:", false);
		}

		boolean afterEffectSet = false;
		if (CmdUtils.hasModifier(args, "a:", false)) {
			afterEffectSet = true;
			CmdUtils.getArgIndex(args, "a:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "a:", false)].split(":");
			if (splt.length > 1) {
				if (splt[1].contains("f")) {
					flicker = true;
				}
				if (splt[1].contains("t")) {
					trail = true;
				}
			}
			args = CmdUtils.modifiedArgs(args, "a:", false);
		}

		boolean colorSet = false;
		int color = -1;
		if (CmdUtils.hasModifier(args, "c:", false)) {
			colorSet = true;
			CmdUtils.getArgIndex(args, "c:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "c:", false)].split(":");
			if (splt.length > 1) {
				String[] splt2 = splt[1].split(",");
				for (int i = 0; i < splt2.length; i++) {
					if (splt2[i].contains("#")) {
						String[] temp = splt2[i].split("#");
						splt2[i] = temp[0];
						if (temp[1].matches("[0-9A-Fa-f]+")) {
							color = Integer.parseInt(temp[1], 16);
						}
					} else {
						if (splt2[i].matches("[0-9A-Fa-f]+")) {
							color = Integer.parseInt(splt2[i], 16);
						}
					}
					Color col = Color.fromRGB(color);
					colors.add(col);
				}
			}
			args = CmdUtils.modifiedArgs(args, "c:", false);
		}

		boolean fadeSet = false;
		if (CmdUtils.hasModifier(args, "fc:", false)) {
			fadeSet = true;
			CmdUtils.getArgIndex(args, "fc:", false);
			String[] splt = args[CmdUtils.getArgIndex(args, "fc:", false)].split(":");
			if (splt.length > 1) {
				String[] splt2 = splt[1].split(",");
				for (int i = 0; i < splt2.length; i++) {
					if (splt2[i].contains("#")) {
						String[] temp = splt2[i].split("#");
						splt2[i] = temp[0];
						if (temp[1].matches("[0-9A-Fa-f]+")) {
							color = Integer.parseInt(temp[1], 16);
						}
					} else {
						if (splt2[i].matches("[0-9A-Fa-f]+")) {
							color = Integer.parseInt(splt2[i], 16);
						}
					}
					Color col = Color.fromRGB(color);
					fcolors.add(col);
				}
			}
			args = CmdUtils.modifiedArgs(args, "fc:", false);
		}

		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			player = (Player) sender;
		}

		/* 1 arg (Amount) */
		if (args.length >= 1) {
			try {
				amt = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid amount, Must be a number.");
				return true;
			}
		}

		/* 2 args (Player) */
		if (args.length >= 2) {
			player = cwc.getServer().getPlayer(args[1]);
		}

		/* Randomize */
		if (!powerSet) {
			power = (int) (Math.random() * 3);
		}
		if (!effectSet) {
			chance = (int) (Math.random() * 4);
			itype = chance;
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
		if (!afterEffectSet) {
			chance = (int) (Math.random() * 4);
			switch (chance) {
				case 0:
					trail = true;
					flicker = false;
					break;
				case 1:
					trail = true;
					flicker = true;
					break;
				case 2:
					trail = false;
					flicker = false;
					break;
				case 3:
					trail = false;
					flicker = true;
					break;
			}
		}
		if (!colorSet) {
			chance = 1 + (int) (Math.random() * 5);
			for (int i = 0; i < chance; i++) {
				colors.add(Utils.getRandomColor());
			}
		}
		if (!fadeSet) {
			chance = 1 + (int) (Math.random() * 5);
			for (int i = 0; i < chance; i++) {
				colors.add(Utils.getRandomColor());
			}
		}

		/* null checks */
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
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
				ItemUtils.createFireworksExplosion(loc, flicker, trail, itype, toIntegerArray(colors), toIntegerArray(fcolors), power);
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
