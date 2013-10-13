package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SudoCmd implements CommandClass {
	
	private CWCore cwc;
	
	public SudoCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		String command = "";
		Player player = null;
		ConsoleCommandSender console = null;
		boolean fromConsole = false;
		String perm = "";
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl);
			sender.sendMessage(pf + "Optional args: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "perm:<permission>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Temporary grant a permission");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-silent" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-op" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Temporary op player");
			sender.sendMessage(ChatColor.DARK_PURPLE + "CONSOLE" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Run command from console (Use this instead of Playername)");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-silent", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-silent", true);
		}
		boolean giveOP = false;
		if (CmdUtils.hasModifier(args,"-op", true)) {
			giveOP = true;
			args = CmdUtils.modifiedArgs(args,"-op", true);
		}
		boolean permSet = false;
		if (CmdUtils.hasModifier(args,"perm:", false)) {
			permSet = true;
			String[] splt = args[CmdUtils.getArgIndex(args, "perm:", false)].split(":");
			perm = splt[1];
			args = CmdUtils.modifiedArgs(args,"perm:", false);
		}
		
		/* 1 arg (Player) */
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("CONSOLE")) {
				console = cwc.getServer().getConsoleSender();
				fromConsole = true;
			} else {
				player = cwc.getServer().getPlayer(args[0]);
			}
		}
		
		/* 2 args (Command) */
		if (args.length >= 1) {
			for (int i = 1; i < args.length; i++) {
				if (i == 1) {
					command += args[i];
				} else {
					command += " " + args[i];
				}
			}
		}
		
		/* null checks */
		if (command == "" || command == " ") {
			sender.sendMessage(pf + ChatColor.RED + "Invalid command: " + ChatColor.GRAY + command);
			return true;
		}
		if (fromConsole && console == null) {
			sender.sendMessage(pf + ChatColor.RED + "Error while trying to send from console.");
			return true;
		}
		if (!fromConsole && player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		if (permSet && perm == "" || perm == " ") {
			sender.sendMessage(pf + ChatColor.RED + "Invalid permission: " + ChatColor.GRAY + perm);
			return true;
		}
		
		/* Action */
		if (fromConsole) {
			cwc.getServer().dispatchCommand(console, command);
		} else {
			boolean curOp = player.isOp();
			if (giveOP)
				player.setOp(true);
			if (permSet)
				cwc.getPermissions().playerAdd(player.getWorld(), player.getName(), perm);
			
			player.chat("/" + command);
			sender.sendMessage(pf + "giving: " + ChatColor.DARK_PURPLE + perm + " , " 
			+ (cwc.getPermissions().has(player.getWorld(), player.getName() ,perm) ? ChatColor.GREEN+"success":ChatColor.RED+"fail"));
			
			if (!silent)
				player.sendMessage(pf + "You where forced to run the command: " + ChatColor.DARK_PURPLE + "/" + command);
			if (giveOP)
				player.setOp(curOp);
			if (permSet)
				cwc.getPermissions().playerRemove(player.getWorld(), player.getName(), perm);
		}
		if (!silent) {
			sender.sendMessage(pf + "You forced " + ChatColor.DARK_PURPLE + (fromConsole ? "the console":player.getDisplayName())
				+ ChatColor.GOLD + " to run: " + ChatColor.DARK_PURPLE + "/" + command);
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.sudo", "cwcore.cmd.*", "cwcore.*" };
	}
}