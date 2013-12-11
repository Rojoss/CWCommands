package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.LocationUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public TopCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
		}
		
		/* action */
		LocationUtils.tpToTop(cwc, player);
		if (!silent)
			player.sendMessage(pf + "Teleporting to top...");
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.top", "cwcore.cmd.*", "cwcore.*" };
	}
}
