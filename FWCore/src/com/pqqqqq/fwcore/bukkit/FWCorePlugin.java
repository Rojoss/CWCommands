package com.pqqqqq.fwcore.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.pqqqqq.fwcore.FWCore;

public class FWCorePlugin extends JavaPlugin {
	private FWCore fwc;
	
	@Override
	public void onDisable() {
		fwc.onDisable();
	}
	
	@Override
	public void onEnable() {
		fwc = new FWCore(this);
		
		fwc.onEnable();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		return fwc.parseCommand(sender, cmd, lbl, args);
	}
}
