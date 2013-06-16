package net.clashwars.cwcore.bukkit.events;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class CmdEvents implements Listener {
	private CWCore	cwc;

	public CmdEvents(CWCore cwc) {
		this.cwc = cwc;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = (Player) event.getPlayer();
		CWPlayer cwp = cwc.getPlayerManager().getPlayer(player);
        for (Player plr : cwc.getServer().getOnlinePlayers()) {
        	CWPlayer cwplr = cwc.getPlayerManager().getPlayer(plr);
            if (cwplr != null && cwplr.getVanished() == true) {
            	if (!(player.hasPermission("cwcore.cmd.vanish.see"))) {
            		player.hidePlayer(plr);
            		if (cwp.getVanished() == true)
            			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 1));
            	}
            }
        }
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
    	  if (cwp.getGod() == true) {
    		  player.setFireTicks(0);
    		  player.setRemainingAir(player.getMaximumAir());
    		  event.setCancelled(true);
    	  }
      }
	}
    
    @EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
    	Entity target = event.getTarget();

        if ((target instanceof Player)) {
      	  Player player = (Player)target;
      	  CWPlayer cwp = cwc.getPlayerManager().getPlayer(player);
      	  if (cwp.getGod() == true) {
      		  event.setCancelled(true);
      	  }
        }
	}
    
    @EventHandler
	public void onFoodChange(FoodLevelChangeEvent event) {
    	Entity entity = event.getEntity();

        if ((entity instanceof Player)) {
      	  Player player = (Player)entity;
      	  CWPlayer cwp = cwc.getPlayerManager().getPlayer(player);
      	  if (cwp.getGod() == true) {
      		  player.setFoodLevel(20);
      		  player.setSaturation(10);
      		  event.setCancelled(true);
      	  }
        }
	}
    
    @EventHandler
	public void onPowertoolUse(final PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
			if (event.getItem() != null && event.getItem().getTypeId() != 0) {
				Player player = event.getPlayer();
				CWPlayer cwp = cwc.getPlayerManager().getPlayer(player.getName());
				String cmd = Utils.getPowerToolCommandByID(Utils.getPowerToolsList(cwp), event.getItem().getTypeId());
				if (cmd != "") {
					player.chat("/" + cmd.replaceAll("_", " "));
					event.setCancelled(true);
				}
			}
		}
	}
	
	  
}
