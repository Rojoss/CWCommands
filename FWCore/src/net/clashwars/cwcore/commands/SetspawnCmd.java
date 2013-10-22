package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetspawnCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	
	public SetspawnCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;

		if (CmdUtils.hasModifier(cmdArgs,"-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		
		//Console
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
		}
		
		
		//Action
		cwc.getServer().getWorld(player.getWorld().getName()).setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		player.sendMessage(pf + "Spawn set to: " + ChatColor.DARK_PURPLE + player.getLocation().getBlockX() + ChatColor.DARK_GRAY + ", " 
		+ ChatColor.DARK_PURPLE + player.getLocation().getBlockY() + ChatColor.DARK_GRAY + ", " + ChatColor.DARK_PURPLE + player.getLocation().getBlockZ());
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.setspawn", "cwcore.cmd.*", "cwcore.*" };
	}
}
