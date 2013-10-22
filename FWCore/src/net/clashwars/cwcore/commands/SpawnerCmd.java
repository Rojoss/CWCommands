package net.clashwars.cwcore.commands;

import java.util.HashMap;

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
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public SpawnerCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		EntityType entity = null;
		Block target = null;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		if (CmdUtils.hasModifier(cmdArgs,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			return true;
		}
		
		
		//Console
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
