package net.clashwars.cwcore.bukkit.events;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class MainEvents implements Listener {
	private CWCore	cwc;
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;

	public MainEvents(CWCore cwc) {
		this.cwc = cwc;
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CWPlayer cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
        cwc.getSqlUpdateTask().getPlayers().add(cwp);
        
        player.sendMessage(pf + "Name: " + cwp.getName() + "  Gamemode: " + cwp.getGamemode() + "  Nick: " + cwp.getNick()
        		+ "  Health: " + cwp.getMaxHealth());
        
        player.setDisplayName(Utils.integrateColor(cwp.getNick()));
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
    	//final CWPlayer cwp = cwc.getPlayerManager().getOrCreatePlayer(p);
    }
}
