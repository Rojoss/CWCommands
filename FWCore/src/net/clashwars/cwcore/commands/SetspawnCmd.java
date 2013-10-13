package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetspawnCmd implements CommandClass {
	
	private CWCore cwc;
	
	public SetspawnCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h", false)) {
			CmdUtils.commandHelp(sender, lbl);
			return true;
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
		}
		
		/* action */
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
