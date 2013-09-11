package net.clashwars.cwcore.bukkit.events;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.util.Utils;
import net.minecraft.server.v1_6_R2.Packet205ClientCommand;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.PlayerDeathEvent;


public class CoreEvents implements Listener {
	private CWCore	cwc;

	public CoreEvents(CWCore cwc) {
		this.cwc = cwc;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void spawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.SPAWNER)
			return;

		final LivingEntity entity = event.getEntity();
		entity.setRemoveWhenFarAway(true);

		cwc.getPlugin().getServer().getScheduler().runTaskLater(cwc.getPlugin(), new Runnable() {

			@Override
			public void run() {
				if (entity instanceof Tameable) {
					Tameable tame = (Tameable) entity;

					if (tame.isTamed()) {
						entity.setRemoveWhenFarAway(false);
						return;
					}
				}

				if (!entity.isDead() && entity.isValid()) {
					entity.remove();
				}
			}
		}, 2400);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void signChange(SignChangeEvent e){
        Player player = e.getPlayer();
        if (player.hasPermission("cwcore.colorsign") || player.hasPermission("cwcore.*")){
            for (int i = 0; i <= 3; i++){
                e.setLine(i, Utils.integrateColor(e.getLine(i)));
            }
        }
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void quit(PlayerDeathEvent event) {
		
		if (cwc.getAutoRespawn() == false) {
			return;
		}
		
		Entity e = event.getEntity();
        if (e instanceof Player) {
            final Player player = (Player) e;
		
			Bukkit.getScheduler().scheduleSyncDelayedTask(cwc.getPlugin(), new Runnable() {
		        @Override
		        public void run() {
		        	Packet205ClientCommand packet = new Packet205ClientCommand();
		        	packet.a = 1;
					((CraftPlayer) player).getHandle().playerConnection.a(packet);
		        }
		    }, 5);
		}
	}
        
}
