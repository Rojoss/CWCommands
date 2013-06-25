package net.clashwars.cwcore.commands;

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
	
	public VanishCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		CWPlayer cwp = null;
		Boolean on = null;
		boolean vanish = false;
		
		/* Modifiers */
		if (CmdUtils.hasModifier(args,"-h", false)) {
			sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Command help for: " + ChatColor.GOLD + "/"  + lbl + ChatColor.DARK_GRAY + "  =====");
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/vanish [player] [on|off]");
			sender.sendMessage(pf + "Desc: " + ChatColor.GRAY + "Make a player invisible or visible");
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-s" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "No messages");
			args = CmdUtils.modifiedArgs(args,"-h", true);
			return true;
		}
		boolean silent = false;
		if (CmdUtils.hasModifier(args,"-s", true)) {
			silent = true;
			args = CmdUtils.modifiedArgs(args,"-s", true);
		}
		
		/* Console check */
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(pf + ChatColor.RED + "You need to specify a player to use this on the console!!");
				return true;
			}
		} else {
			player = (Player) sender;
		}
		
		/* 1 arg (Player) */
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
		}
		
		/* 2 args (on/off) */
		if (args.length >= 2) {
			if (args[1].toLowerCase().startsWith("on")) {
				on = true;
			} else {
				on = false;
			}
		}
		
		/* null checks */
		if (player == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
		if (on == null) {
			if (cwp.getVanished() == true) {
				cwp.setVanished(false);
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
			} else {
				cwp.setVanished(true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 1));
				vanish = true;
			}
		} else {
		/* Action */
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
