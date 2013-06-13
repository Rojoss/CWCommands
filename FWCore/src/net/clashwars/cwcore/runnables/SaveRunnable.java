package net.clashwars.cwcore.runnables;

import org.bukkit.ChatColor;
import org.bukkit.World;

import net.clashwars.cwcore.CWCore;

public class SaveRunnable implements Runnable {
	
	private CWCore cwc;
    
    public SaveRunnable(CWCore cwc) {
        this.cwc = cwc;
    }

    @Override
    public synchronized void run() {
        cwc.getServer().savePlayers();
        for (World world : cwc.getServer().getWorlds()) {
        	world.save();
        }
        cwc.getServer().broadcastMessage(ChatColor.GRAY + "Saved!");
    }
}
