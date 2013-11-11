package net.clashwars.cwcore.bukkit;

import net.clashwars.cwcore.CWCore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CWCorePlugin extends JavaPlugin {
	private CWCore	cwc;

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
		try {
			return cwc.parseCommand(sender, cmd, lbl, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public CWCore getCWCore() {
		return cwc;
	}
}
