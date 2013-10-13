package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PowertoolCmd implements CommandClass {
	
	private CWCore cwc;
	
	public PowertoolCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		String command = "";
		ItemStack item = null;
		int id = -1;
		Player player = null;
		CWPlayer cwp = null;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl);
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-remove" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Remove powertool from held item");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-force" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Override current powertool and force it.");
			return true;
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
			cwp = cwc.getPlayerManager().getPlayer(player.getName());
		}
		
		item = player.getItemInHand();
		id = item.getType().getId();
		
		/* reset/force */
		if (CmdUtils.hasModifier(args,"-remove", true)) {
			String pCmd = Utils.getPowerToolCommandByID(Utils.getPowerToolsList(cwp), id);
			if (pCmd != "") {
				cwp.setPowertool(Utils.removePowerToolCommandByID(Utils.getPowerToolsList(cwp), id));
				player.sendMessage(pf + "powertool removed!");
				return true;
			}
			return true;
		}
		if (CmdUtils.hasModifier(args,"-force", true)) {
			String pCmd = Utils.getPowerToolCommandByID(Utils.getPowerToolsList(cwp), id);
			if (pCmd != "") {
				cwp.setPowertool(Utils.removePowerToolCommandByID(Utils.getPowerToolsList(cwp), id));
			}
			args = CmdUtils.modifiedArgs(args,"-force", true);
		}
		
		/* 1 arg (Command) */
		if (args.length >= 1) {
			for (int i = 0; i < args.length; i++) {
				if (i == 0) {
					command += args[i];
				} else {
					command += " " + args[i];
				}
			}
		}
		
		/* null checks */
		if (id == 0) {
			player.sendMessage(pf + ChatColor.RED + "You need to hold an item to set a powertool.");
			return true;
		}
		if (item.getType().isBlock()) {
			player.sendMessage(pf + ChatColor.RED + "Can't set powetools to blocks.");
			return true;
		}
		if (command == "" || command == " ") {
			player.sendMessage(pf + ChatColor.RED + "Invalid command: " + ChatColor.GRAY + command);
			return true;
		}
		if (Utils.getPowerToolCommandByID(Utils.getPowerToolsList(cwp), id) != "") {
			player.sendMessage(pf + ChatColor.RED + "There is already a powertool set for this item. Use -r to reset it.");
			player.sendMessage(pf + ChatColor.RED + "Use -remove to reset/remove your powertool.");
			return true;
		}
		
		/* Action */
		cwp.addPowertool(id + ":" + command.replaceAll(" ", "_"));
		player.sendMessage(pf + "Powertool set to: " + ChatColor.DARK_PURPLE + "/" + command);
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.powertool", "cwcore.cmd.*", "cwcore.*" };
	}
}