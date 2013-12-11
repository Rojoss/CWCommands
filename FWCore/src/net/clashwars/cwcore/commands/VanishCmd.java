package net.clashwars.cwcore.commands;

import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VanishCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public VanishCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		boolean on = false;
		boolean vanish = false;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(cmdArgs,"-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		
		
		//Console
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
				return true;
			}
		}
		cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
		
		if (args.length >= 2) {
			if (args[1].toLowerCase().startsWith("on")) {
				on = true;
			} else {
				on = false;
			}
		} else {
			if (cwp.getVanished() == true) {
				on = false;
			} else {
				on = true;
			}
		}
		
		
		
		//Action
		if (on) {
			if (cwp.getVanished() == false) {
				cwp.setVanished(true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 1));
				vanish = true;
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Player is already vanished");
				return true;
			}
		} else {
			if (cwp.getVanished() == true) {
				cwp.setVanished(false);
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
			} else {
				sender.sendMessage(pf + ChatColor.RED + "Player is already unvanished");
				return true;
			}
		}
		
		for (Player plr : cwc.getServer().getOnlinePlayers()) {
			if (vanish == true) {
				if (!(plr.hasPermission("cwcore.cmd.vanish.see")))
					plr.hidePlayer(player);
			} else if (vanish == false) {
				plr.showPlayer(player);
			}
		}
		
		if (cwp.getVanished() == true) {
			if (!silent) {
				player.sendMessage(pf + "Vanished!");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have vanished " + ChatColor.DARK_PURPLE + player.getDisplayName());
			}
		} else {
			if (!silent) {
				player.sendMessage(pf + "No longer vanished.");
				if (sender.getName() != player.getName())
					sender.sendMessage(pf + "You have unvanished " + ChatColor.DARK_PURPLE + player.getDisplayName());
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.vanish", "cwcore.cmd.*", "cwcore.*" };
		//Note: "cwcore.cmd.vanish.see" is used to see vanished players.
	}
}
