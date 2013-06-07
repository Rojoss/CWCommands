package com.pqqqqq.cwcore.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.pqqqqq.cwcore.CWCore;

public class CWCorePlugin extends JavaPlugin {
	private CWCore cwc;
	
	@Override
	public void onDisable() {
		cwc.onDisable();
	}
	
	@Override
	public void onEnable() {
		cwc = new CWCore(this);
		
		cwc.onEnable();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		return cwc.parseCommand(sender, cmd, lbl, args);
	}
}
