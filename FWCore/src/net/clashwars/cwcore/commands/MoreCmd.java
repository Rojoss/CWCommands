package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MoreCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public MoreCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		int amt = 0;
		ItemStack item = null;
		MaterialData md = null;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");

		//Console
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
		}

		if (player.getItemInHand().getData().getItemType() != Material.AIR) {
			md = player.getItemInHand().getData();
		} else {
			sender.sendMessage(pf + ChatColor.RED + "Can't give more air!");
			return true;
		}

		//Args
		if (args.length < 1) {
			amt = (player.getItemInHand().getMaxStackSize() - player.getItemInHand().getAmount());
			if (amt == 0) {
				amt = player.getItemInHand().getMaxStackSize();
			}
		}

		if (args.length >= 1) {
			try {
				amt = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid amount.");
				return true;
			}
		}

		//Action
		item = md.toItemStack(amt);
		player.getInventory().addItem(item);
		String type = item.getType().name().toLowerCase().replace("_", "");
		if (!silent) {
			player.sendMessage(pf + "Given " + ChatColor.DARK_PURPLE + amt + ChatColor.GOLD + " more " + ChatColor.DARK_PURPLE + type);
		}

		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.more", "cwcore.cmd.*", "cwcore.*" };
	}
}