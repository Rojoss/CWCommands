package net.clashwars.cwcore.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BroadcastCmd implements CommandClass {
	
	private CWCore cwc;
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;;
	
	public BroadcastCmd(CWCore cwc) {
		this.cwc = cwc;
		modifiers.put("p", "No prefix");
		modifiers.put("*", "Broadcast the message on all servers.");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		String msg = "";
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(cmdArgs,"-h", true) || args.length < 1) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		boolean prefix = !CmdUtils.hasModifier(cmdArgs, "p");
		boolean bungee = CmdUtils.hasModifier(cmdArgs, "*");
		
		//Args
		String message = Utils.implode(args, " ", 0);
		if (prefix) {
			msg = "" + ChatColor.DARK_GRAY + ChatColor.BOLD + "[" 
			+ ChatColor.DARK_RED + ChatColor.BOLD + "CW BC" + ChatColor.DARK_GRAY + ChatColor.BOLD + "] " 
			+ ChatColor.GOLD + ChatColor.BOLD + message;
		} else {
			msg = message;
		}
		
		
		//Action
		if (bungee) {
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				
				out.writeUTF("Broadcast");
				out.writeUTF(sender.getName());
				out.writeUTF(msg);
				
				Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			cwc.getServer().broadcastMessage(Utils.integrateColor(msg));
		}
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.broadcast", "cwcore.cmd.*", "cwcore.*" };
	}
}
