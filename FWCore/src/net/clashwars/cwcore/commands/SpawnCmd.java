package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCmd implements CommandClass {
	
	private CWCore cwc;
	
	public SpawnCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h", false)) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/spawn");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Teleport to the spawnpoint");
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
		player.teleport(cwc.getServer().getWorld(player.getWorld().getName()).getSpawnLocation());
		player.sendMessage(pf + "Teleporting to spawn...");
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.spawn", "cwcore.cmd.*", "cwcore.*" };
	}
}
