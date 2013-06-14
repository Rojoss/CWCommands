package net.clashwars.cwcore.runnables;

import net.clashwars.cwcore.CWCore;

import org.bukkit.World;

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
    }
}
