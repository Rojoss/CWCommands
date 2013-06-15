package net.clashwars.cwcore.bukkit.events;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class MainEvents implements Listener {
	private CWCore	cwc;
	private String pf = null;

	public MainEvents(CWCore cwc) {
		this.cwc = cwc;
		pf = cwc.getPrefix();
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CWPlayer cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
        cwc.getSqlUpdateTask().getPlayers().add(cwp);
        
        if (cwp.getNick() != "" && cwp.getNick() != null && cwp.getNick() != player.getDisplayName()) {
        	player.setDisplayName(Utils.integrateColor(cwp.getNick()));
        }
        
        if (cwp.getMaxHealth() != 0 && cwp.getMaxHealth() != player.getMaxHealth()) {
        	player.setMaxHealth(cwp.getMaxHealth());
        }
        
        if (cwp.getGamemode() == 0 && player.getGameMode().getValue() != 0) {
        	player.setGameMode(GameMode.SURVIVAL);
        } else if (cwp.getGamemode() == 1 && player.getGameMode().getValue() != 1) {
        	player.setGameMode(GameMode.CREATIVE);
        } else if (cwp.getGamemode() == 2 && player.getGameMode().getValue() != 2) {
        	player.setGameMode(GameMode.ADVENTURE);
        }
        
        if (cwp.getFlying() == true) {
        	player.setAllowFlight(true);
        	player.setFlying(true);
        } else {
        	player.setAllowFlight(false);
        	player.setFlying(false);
        }
    }
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void quit(PlayerQuitEvent event) {
        quit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void kick(PlayerKickEvent event) {
        quit(event.getPlayer());
    }
    
    private void quit(final Player p) {
    	//Player player = p;
    	
    }
}
