package net.clashwars.cwcore.commands;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnerCmd implements CommandClass {
	
	private CWCore cwc;
	
	public SpawnerCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		EntityType entity = null;
		Block target = null;
		
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/spawner <mob>");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Change a mob spawner type");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			return true;
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
			return true;
		} else {
			player = (Player) sender;
		}
		
		/* 1 arg (Mob) */
		if (args.length >= 1) {
			entity = AliasUtils.findEntity(args[0]);
		}
		
		/* Get target block */
		target = player.getTargetBlock(null, 30);
		
		/* null checks */
		if (entity == null) {
			sender.sendMessage(pf + ChatColor.RED + "Mob " + ChatColor.GRAY + args[0] + ChatColor.RED + " was not recognized!");
		 	return true;
		}
		if (entity.isSpawnable() == false || entity.isAlive() == false) {
			sender.sendMessage(pf + ChatColor.RED + "Entity " + ChatColor.GRAY + args[0] + ChatColor.RED + " can not be used for a spawner!");
		 	return true;
		}
		if (target == null) {
			sender.sendMessage(pf + ChatColor.RED + "You need to look at a spawner to change it.");
		 	return true;
		}
		BlockState state = target.getState();
		if (!(state instanceof CreatureSpawner)) {
			sender.sendMessage(pf + ChatColor.RED + "You need to look at a spawner to change it!");
		 	return true;
		}
		
		/* Action */
		CreatureSpawner spawner = (CreatureSpawner) state;
		spawner.setSpawnedType(entity);
		spawner.update(true);
		
		if (!silent) {
			player.sendMessage(pf + "Spawner changed to " + ChatColor.DARK_PURPLE + args[0]);
		}
		
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.spawner", "cwcore.cmd.*", "cwcore.*" };
	}
}
