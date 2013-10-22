package net.clashwars.cwcore.bukkit.events;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.components.ItemMenu;
import net.clashwars.cwcore.util.Utils;
import net.minecraft.server.v1_6_R2.Packet205ClientCommand;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class CoreEvents implements Listener {
	private CWCore	cwc;
	private ItemMenu spawnerMenu;
	private CreatureSpawner spawner = null;

	public CoreEvents(CWCore cwc) {
		this.cwc = cwc;
		
		createMenus();
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
	
	@EventHandler
	public void spawnerModify(final PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if (event.getClickedBlock().getType() != Material.MOB_SPAWNER) {
			return;
		}
		Player player = event.getPlayer();
		if (!player.isSneaking()) {
			return;
		}
		if (!player.hasPermission("cwcore.spawner") && !player.hasPermission("cwcore.*")) {
			return;
		}
		BlockState state = event.getClickedBlock().getState();
		if (!(state instanceof CreatureSpawner)) {
			return;
		}
		spawner = (CreatureSpawner) state;
		
		spawnerMenu.open(player);
	}
	
	private void createMenus() {
		spawnerMenu = new ItemMenu("Choose what to modify", 9, new ItemMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(ItemMenu.OptionClickEvent event) {
            	Player p = event.getPlayer();
            	switch (Utils.stripColorCodes(event.getName()).toLowerCase()) {
                	case "entity-type":
                		openEntityTypeMenu(p);
                		break;
                	case "spawn-amount":
                		openSpawnAmountMenu(p);
                		break;
                	case "spawn-range":
                		openSpawnRangeMenu(p);
                		break;
                	case "min-spawn-delay":
                		openMinSpawnDelayMenu(p);
                		break;
                	case "max-spawn-delay":
                		openMaxSpawnDelayRangeMenu(p);
                		break;
                	case "entity-limit":
                		openEntityLimitMenu(p);
                		break;
                	case "player-range":
                		openPlayerRangeMenu(p);
                		break;
                }
            	event.setWillClose(false);
            }

        }, cwc)
        .setOption(1, new ItemStack(Material.MONSTER_EGG, 1), "&6&lEntity-Type", "&7Set the entity type of this spawner.")
        .setOption(2, new ItemStack(Material.SIGN, 1), "&6&lSpawn-Amount", "&7Set the amount of entities.", "&7This amount of entities will spawn each time it spawns.")
        .setOption(3, new ItemStack(Material.TRIPWIRE_HOOK, 1), "&6&lSpawn-Range", "&7Set the spawn range.", "&7Entities will spawn inside this range.")
        .setOption(4, new ItemStack(Material.WATCH, 1), "&6&lMin-Spawn-Delay", "&7Set the min spawn delay.", "&7The spawn delay used a time between min and max delay.")
        .setOption(5, new ItemStack(Material.WATCH, 1), "&6&lMax-Spawn-Delay", "&7Set the max spawn delay.", "&7The spawn delay used a time between min and max delay.")
        .setOption(6, new ItemStack(Material.EMERALD, 1), "&6&lEntity-Limit", "&7Set the entity limit.", "&7If it's reached it won't spawn more.")
        .setOption(7, new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "&6&lPlayer-Range", "&7Range from where the spawner is triggered.");
	}
	
	private void openEntityTypeMenu(final Player p) {
		ItemMenu entityMenu = new ItemMenu("Choose a entity", 54, new ItemMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(ItemMenu.OptionClickEvent event) {
            	spawner.setCreatureTypeByName(Utils.stripColorCodes(event.getName()).toLowerCase());
                spawner.update();
                p.sendMessage("Clicked: " + Utils.integrateColor(event.getName()));
                event.setWillClose(true);
                event.setWillDestroy(true);
            }

        }, cwc)
        .setOption(0, new ItemStack(Material.MONSTER_EGG, 1, (short) 90), "&6&lPig")
        .setOption(1, new ItemStack(Material.MONSTER_EGG, 1, (short) 93), "&6&lChicken")
        .setOption(2, new ItemStack(Material.MONSTER_EGG, 1, (short) 92), "&6&lCow")
        .setOption(3, new ItemStack(Material.MONSTER_EGG, 1, (short) 96), "&6&lMooshroom")
        .setOption(4, new ItemStack(Material.MONSTER_EGG, 1, (short) 91), "&6&lSheep")
        .setOption(5, new ItemStack(Material.MONSTER_EGG, 1, (short) 95), "&6&lWolf")
        .setOption(6, new ItemStack(Material.MONSTER_EGG, 1, (short) 98), "&6&lOcelot")
        .setOption(7, new ItemStack(Material.MONSTER_EGG, 1, (short) 100), "&6&lHorse")
        .setOption(8, new ItemStack(Material.SNOW_BLOCK, 1), "&6&lSnowGolem")
        
        .setOption(9, new ItemStack(Material.MONSTER_EGG, 1, (short) 120), "&6&lVillager")
        .setOption(10, new ItemStack(Material.MONSTER_EGG, 1, (short) 120), "&6&lSquid")
        .setOption(11, new ItemStack(Material.MONSTER_EGG, 1, (short) 65), "&6&lBat")
        .setOption(12, new ItemStack(Material.MONSTER_EGG, 1, (short) 57), "&6&lPigman")
        .setOption(13, new ItemStack(Material.MONSTER_EGG, 1, (short) 58), "&6&lEnderman")
        .setOption(14, new ItemStack(Material.MONSTER_EGG, 1, (short) 54), "&6&lZombie")
        .setOption(15, new ItemStack(Material.MONSTER_EGG, 1, (short) 54), "&6&lZombie Villager")
        .setOption(16, new ItemStack(Material.MONSTER_EGG, 1, (short) 54), "&6&lGiant")
        .setOption(17, new ItemStack(Material.IRON_BLOCK, 1), "&6&lIronGolem")
        
        .setOption(18, new ItemStack(Material.MONSTER_EGG, 1, (short) 51), "&6&lSkeleton")
        .setOption(19, new ItemStack(Material.MONSTER_EGG, 1, (short) 51), "&6&lWither Skeleton")
        .setOption(20, new ItemStack(Material.MONSTER_EGG, 1, (short) 52), "&6&lSpider")
        .setOption(21, new ItemStack(Material.MONSTER_EGG, 1, (short) 59), "&6&lCave Spider")
        .setOption(22, new ItemStack(Material.MONSTER_EGG, 1, (short) 50), "&6&lCreeper")
        .setOption(23, new ItemStack(Material.MONSTER_EGG, 1, (short) 66), "&6&lWitch")
        .setOption(24, new ItemStack(Material.MONSTER_EGG, 1, (short) 60), "&6&lSilverfish")
        .setOption(25, new ItemStack(Material.MONSTER_EGG, 1, (short) 55), "&6&lSlime")
        .setOption(26, new ItemStack(Material.MONSTER_EGG, 1, (short) 62), "&6&lMagma Cube")
        
        .setOption(27, new ItemStack(Material.MONSTER_EGG, 1, (short) 61), "&6&lBlaze")
        .setOption(28, new ItemStack(Material.MONSTER_EGG, 1, (short) 56), "&6&lGhast")
        .setOption(29, new ItemStack(Material.DRAGON_EGG, 1), "&6&lDragon")
        .setOption(30, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), "&6&lWither")
        
        .setOption(45, new ItemStack(Material.MONSTER_EGG, 1), "&6&lBoat")
        .setOption(46, new ItemStack(Material.MONSTER_EGG, 1), "&6&lMinecart")
        .setOption(47, new ItemStack(Material.MONSTER_EGG, 1), "&6&lXP")
        .setOption(48, new ItemStack(Material.MONSTER_EGG, 1), "&6&lTNT")
        .setOption(49, new ItemStack(Material.MONSTER_EGG, 1), "&6&lSmall Fireball")
        .setOption(50, new ItemStack(Material.MONSTER_EGG, 1), "&6&lBig Fireball")
        .setOption(51, new ItemStack(Material.MONSTER_EGG, 1), "&6&lFalling block");
		entityMenu.open(p);
	}
	
	private void openSpawnAmountMenu(final Player p) {
		
	}
	
	private void openSpawnRangeMenu(final Player p) {
		
	}
	
	private void openMinSpawnDelayMenu(final Player p) {
		
	}
	
	private void openMaxSpawnDelayRangeMenu(final Player p) {
		
	}
	
	private void openEntityLimitMenu(final Player p) {
		
	}
	
	private void openPlayerRangeMenu(final Player p) {
		
	}  
}
