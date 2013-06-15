package net.clashwars.cwcore.bukkit.events;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.entity.CWPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;


public class CmdEvents implements Listener {
	private CWCore	cwc;
	private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;

	public CmdEvents(CWCore cwc) {
		this.cwc = cwc;
	}
    
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if(cwc.getViewList().containsKey(player.getName())) {
            Player target = cwc.getViewList().get(player.getName());
            ItemStack[] items = event.getInventory().getContents();
            target.getEnderChest().clear();
            target.getEnderChest().setContents(items);
            
            if(!((player == target) || target.isOnline()))
            	target.saveData();
            
            cwc.getViewList().remove(player.getName());
            player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 5.0f, 1.0f);
        }
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
      Entity entity = event.getEntity();

      if ((entity instanceof Player)) {
    	  Player player = (Player)entity;
    	  CWPlayer cwp = cwc.getPlayerManager().getPlayer(player);
    	  if (cwp.getGod() == 1) {
    		  player.setFireTicks(0);
    		  player.setRemainingAir(player.getMaximumAir());
    		  event.setCancelled(true);
    	  }
      }
	}
	
	  
}
