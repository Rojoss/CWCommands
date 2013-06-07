package com.pqqqqq.cwcore.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.pqqqqq.cwcore.CWCore;

public class CWCorePlugin extends JavaPlugin {
	private CWCore fwc;
	
	@Override
	public void onDisable() {
		fwc.onDisable();
	}
	
	@Override
	public void onEnable() {
		fwc = new CWCore(this);
		
		fwc.onEnable();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		return fwc.parseCommand(sender, cmd, lbl, args);
	}
}
