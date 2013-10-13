package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WarpsCmd implements CommandClass {
	
	private CWCore cwc;
	
	public WarpsCmd(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] args) {
		String pf = cwc.getPrefix();
		
		/* Modifiers + No args */
		if (CmdUtils.hasModifier(args,"-h", false)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			sender.sendMessage(pf + "Modifiers: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + "-*" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "List warps from all servers.");
			return true;
		}
		boolean bungee = false;
		if (CmdUtils.hasModifier(args,"-*", true)) {
			bungee = true;
			args = CmdUtils.modifiedArgs(args,"-*", true);
		}
		
		/* Action */
		if (bungee) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("WARPS");
				out.writeUTF(sender.getName());

				Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			String msg = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Warps" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
			for (int i = 0; i < cwc.getWarpsConfig().getWarpNames().size(); i++) {
				if (i == 0) {
					msg += cwc.getWarpsConfig().getWarpNames().get(i);
				} else {
					msg += ChatColor.DARK_GRAY + ", " + ChatColor.GOLD + cwc.getWarpsConfig().getWarpNames().get(i);
				}
			}
			sender.sendMessage(msg);
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.warp", "cwcore.cmd.*", "cwcore.*" };
	}
}