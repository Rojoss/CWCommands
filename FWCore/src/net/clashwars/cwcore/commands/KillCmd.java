package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCmd implements CommandClass {

	private CWCore					cwc;
	private HashMap<String, String>	modifiers		= new HashMap<String, String>();
	private HashMap<String, String>	optionalArgs	= new HashMap<String, String>();
	private String[]				args;

	public KillCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("s", "No messages");
		modifiers.put("f", "Force kill");
		modifiers.put("*", "Look for players on other servers.");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String pf = cwc.getPrefix();
		Player player = null;
		String pplayer = null;
		CWPlayer cwp = null;

		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);

		if (CmdUtils.hasModifier(cmdArgs, "-h", false) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}

		boolean silent = CmdUtils.hasModifier(cmdArgs, "s");
		boolean force = CmdUtils.hasModifier(cmdArgs, "f");
		boolean bungee = CmdUtils.hasModifier(cmdArgs, "*");

		//Args
		if (args.length >= 1) {
			player = cwc.getServer().getPlayer(args[0]);
			pplayer = args[0];

			if (!bungee) {
				if (player == null) {
					sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
					return true;
				}
			}
		}
		cwp = cwc.getPlayerManager().getPlayer(player);

		/* Action */
		if (!force) {
			if (cwp.getGamemode() == 1 || cwp.getGod() == true) {
				sender.sendMessage(pf + ChatColor.RED + "You can't kill this player.");
				return true;
			}
		}
		if (bungee) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("Kill");
				out.writeUTF(sender.getName());
				out.writeUTF(pplayer);
				out.writeBoolean(silent);
				out.writeBoolean(force);

				Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			player.setHealth(0);
			if (!silent) {
				player.sendMessage(pf + "You where killed by " + ChatColor.DARK_PURPLE + sender.getName());
				if (sender.getName() != player.getName()) {
					sender.sendMessage(pf + "You have killed " + ChatColor.DARK_PURPLE + player.getDisplayName());
				}
			}
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.kill", "cwcore.cmd.*", "cwcore.*" };
	}
}