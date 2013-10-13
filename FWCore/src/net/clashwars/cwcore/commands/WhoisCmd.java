package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.ExperienceManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhoisCmd implements CommandClass {
	
	private CWCore cwc;
	
	public WhoisCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		Player player = null;
		String pplayer = null;
		CWPlayer cwp = null;
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl);
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-*" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Check players on other servers.");
			return true;
		}
		boolean bungee = false;
		if (CmdUtils.hasModifier(args,"-*", true)) {
			bungee = true;
			args = CmdUtils.modifiedArgs(args,"-*", true);
		}
		
		/* 1 arg (Player) */
		if (args.length >= 1) {
			if (!bungee) {
				if (cwc.getServer().getPlayer(args[0]) == cwc.getServer().getPlayer(sender.getName())) {
					player = cwc.getServer().getPlayer(args[0]);
					cwp = cwc.getPlayerManager().getPlayer(player.getName());
				} else {
					if (sender.hasPermission("cwcore.cmd.whois.others")) {
						player = cwc.getServer().getPlayer(args[0]);
					} else {
						sender.sendMessage(pf + ChatColor.RED + "insufficient permissions!" 
								+ ChatColor.GRAY + " - " + ChatColor.DARK_GRAY + "'" + ChatColor.DARK_RED + "cwcore.cmd.whois.others" + ChatColor.DARK_GRAY + "'");
						return true;
					}
				}
			} else {
				if (sender.hasPermission("cwcore.cmd.whois.others")) {
					pplayer = args[0];
				} else {
					sender.sendMessage(pf + ChatColor.RED + "insufficient permissions!" 
							+ ChatColor.GRAY + " - " + ChatColor.DARK_GRAY + "'" + ChatColor.DARK_RED + "cwcore.cmd.whois.others" + ChatColor.DARK_GRAY + "'");
					return true;
				}
			}
		}
		
		/* null checks */
		if (player == null && !bungee) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}
		
		/* Action */
		if (bungee) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("Whois");
				out.writeUTF(sender.getName());
				out.writeUTF(pplayer);

				Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			cwp = cwc.getPlayerManager().getPlayer(player.getName());
			ExperienceManager expMan = new ExperienceManager(player);
			
			String yes = ChatColor.GREEN + "true";
			String no = ChatColor.DARK_RED + "false";
			
			sender.sendMessage(ChatColor.DARK_GRAY + "========" + ChatColor.DARK_RED + "CW whois: " + ChatColor.GOLD + player.getName() + ChatColor.DARK_GRAY + "========");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Nickname" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getDisplayName());
			sender.sendMessage(ChatColor.DARK_PURPLE + "Tag" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + cwp.getTag());
			sender.sendMessage(ChatColor.DARK_PURPLE + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD 
					+ player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ());
			sender.sendMessage(ChatColor.DARK_PURPLE + "Health" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getHealth() + "/" + player.getMaxHealth());
			sender.sendMessage(ChatColor.DARK_PURPLE + "Hunger" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getFoodLevel() + "/20" + " saturation: " + ChatColor.YELLOW + player.getSaturation());
			sender.sendMessage(ChatColor.DARK_PURPLE + "Gamemode" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getGameMode().toString().toLowerCase());
			sender.sendMessage(ChatColor.DARK_PURPLE + "Flymode" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + (cwp.getFlying() ? yes:no));
			sender.sendMessage(ChatColor.DARK_PURPLE + "Godmode" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + (cwp.getGod() ? yes:no));
			sender.sendMessage(ChatColor.DARK_PURPLE + "Vanished" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + (cwp.getVanished() ? yes:no));
			sender.sendMessage(ChatColor.DARK_PURPLE + "Frozen" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + (cwc.getFrozenPlayers().contains(player) ? yes:no));
			sender.sendMessage(ChatColor.DARK_PURPLE + "Speed" + ChatColor.DARK_GRAY + ": " 
					+ ChatColor.GOLD + "walk: " + ChatColor.YELLOW + player.getWalkSpeed() + ChatColor.GOLD + " fly: " + ChatColor.YELLOW + player.getFlySpeed());
			sender.sendMessage(ChatColor.DARK_PURPLE + "Experience" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + expMan.getCurrentExp() + ChatColor.GRAY 
					+ "Lvl:" + ChatColor.GOLD + expMan.getLevelForExp(expMan.getCurrentExp()));
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.whois", "cwcore.cmd.*", "cwcore.*" };
		//Extra: cwcore.cmd.whois.others
	}
}