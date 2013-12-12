package net.clashwars.cwcore.commands;

import java.util.HashMap;

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
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public SudoCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("silent", "No messages");
		modifiers.put("op", "Temporary OP player.");
		optionalArgs.put("perm:<permission>", "Temporary grant a permission");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		String command = "";
		Player player = null;
		ConsoleCommandSender console = null;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(cmdArgs,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			sender.sendMessage(ChatColor.DARK_PURPLE + "CONSOLE" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Run command from console (Use this instead of Playername)");
			return true;
		}
		
		boolean silent = CmdUtils.hasModifier(cmdArgs, "silent");
		boolean giveOP = CmdUtils.hasModifier(cmdArgs, "op");
		String perm = CmdUtils.getOptionalArg(cmdArgs, "perm:");
		
		
		//Args
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("CONSOLE")) {
				console = cwc.getServer().getConsoleSender();
				if (console == null) {
					sender.sendMessage(pf + ChatColor.RED + "Error while trying to send from console.");
					return true;
				}
			} else {
				player = cwc.getServer().getPlayer(args[0]);
				if (player == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
					return true;
				}
			}
		}
		
		if (args.length >= 1) {
			for (int i = 1; i < args.length; i++) {
				if (i == 1) {
					command += args[i];
				} else {
					command += " " + args[i];
				}
			}
			if (command == "" || command == " ") {
				sender.sendMessage(pf + ChatColor.RED + "Invalid command: " + ChatColor.GRAY + command);
				return true;
			}
		}
		
		
		//Action
		if (CmdUtils.hasOptionalArg(cmdArgs, "perm:")) {
			if (perm == null || perm == "" || perm == " ") {
				sender.sendMessage(pf + ChatColor.RED + "Invalid permission: " + ChatColor.GRAY + perm);
				return true;
			}
		}
		if (console != null) {
			cwc.getServer().dispatchCommand(console, command);
		} else {
			boolean curOp = player.isOp();
			if (giveOP)
				player.setOp(true);
			if (perm != null)
				cwc.getPermissions().playerAdd(player.getWorld(), player.getName(), perm);
			
			player.chat("/" + command);
			
			if (!silent)
				player.sendMessage(pf + "You where forced to run the command: " + ChatColor.DARK_PURPLE + "/" + command);
			if (giveOP)
				player.setOp(curOp);
			if (perm != null)
				cwc.getPermissions().playerRemove(player.getWorld(), player.getName(), perm);
		}
		if (!silent) {
			sender.sendMessage(pf + "You forced " + ChatColor.DARK_PURPLE + (console != null ? "the console":player.getDisplayName())
				+ ChatColor.GOLD + " to run: " + ChatColor.DARK_PURPLE + "/" + command);
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.sudo", "cwcore.cmd.*", "cwcore.*" };
	}
}