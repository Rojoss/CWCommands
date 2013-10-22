package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PowertoolCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public PowertoolCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("remove", "Remove powertool from held item");
		modifiers.put("force", "Override current powertool and force set");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		String command = "";
		ItemStack item = null;
		Player player = null;
		CWPlayer cwp = null;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		boolean remove = CmdUtils.hasModifier(cmdArgs, "remove");
		boolean force = CmdUtils.hasModifier(cmdArgs, "force");
		
		if (CmdUtils.hasModifier(cmdArgs,"-h", false) || args.length < 1 && !remove) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		
		//Console
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
			cwp = cwc.getPlayerManager().getPlayer(player.getName());
		}
		
		
		//Args
		if (args.length >= 1) {
			command = args[0];
			for (int i = 1; i < args.length; i++) {
				command += " " + args[i];
			}
		}
		
		
		//Action
		item = player.getItemInHand();
		if (item.getData().getItemType() == Material.AIR) {
			player.sendMessage(pf + ChatColor.RED + "You need to hold an item to set a powertool.");
			return true;
		}
		if (item.getType().isBlock()) {
			player.sendMessage(pf + ChatColor.RED + "Can't set powetools to blocks.");
			return true;
		}
		
		if (Utils.getPowerToolCommandByID(Utils.getPowerToolsList(cwp), item.getTypeId()) != "") {
			if (remove) {
				cwp.setPowertool(Utils.removePowerToolCommandByID(Utils.getPowerToolsList(cwp), item.getTypeId()));
				player.sendMessage(pf + "Powertool removed from " + item.getData().getItemType().name().toLowerCase().replace("_", " "));
				return true;
			}
			
			if (force) {
				cwp.setPowertool(Utils.removePowerToolCommandByID(Utils.getPowerToolsList(cwp), item.getTypeId()));
			} else {
				player.sendMessage(pf + ChatColor.RED + "There is already a powertool set for this item.");
                player.sendMessage(pf + ChatColor.RED + "Add -force to force set or remove with /pt -remove");
                return true;
			}
		} else {
			if (remove) {
				player.sendMessage(pf + ChatColor.RED + "This item doesn't have a powertool set.");
				return true;
			}
		}
		
		if (command.isEmpty()) {
			player.sendMessage(pf + ChatColor.RED + "Invalid command: " + ChatColor.GRAY + command);
			return true;
		}
		cwp.addPowertool(item.getTypeId() + ":" + command.replace(" ", "♒"));
		player.sendMessage(pf + "Powertool set to: " + ChatColor.DARK_PURPLE + "/" + command);
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.powertool", "cwcore.cmd.*", "cwcore.*" };
	}
}