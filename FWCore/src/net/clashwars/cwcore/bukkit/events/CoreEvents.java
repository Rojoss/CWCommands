package net.clashwars.cwcore.bukkit.events;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.LootChest;
import net.clashwars.cwcore.bukkit.LootChestInventory;
import net.clashwars.cwcore.util.Utils;
import net.minecraft.server.v1_5_R3.ContainerChest;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.Packet100OpenWindow;
import net.minecraft.server.v1_5_R3.Packet205ClientCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftInventory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;


public class CoreEvents implements Listener {
	private CWCore	cwc;
	private String pf = null;

	public CoreEvents(CWCore cwc) {
		this.cwc = cwc;
		pf = cwc.getPrefix();
	}

	@EventHandler
	public void interact(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (!cwc.getCreateChests().contains(player.getName()) && !cwc.getDeleteChests().contains(player.getName()))
			return;

		if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (cwc.getCreateChests().contains(player.getName())) {
				cwc.getCreateChests().remove(player.getName());
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);

				if (block.getType() != Material.CHEST) {
					player.sendMessage(pf + ChatColor.RED + "That is not a chest.");
					return;
				}

				LootChest lc = new LootChest(block);

				if (cwc.getLootChests().contains(lc)) {
					player.sendMessage(pf + ChatColor.RED + "That is already a loot chest.");
					return;
				}

				cwc.getLootChests().add(lc);
				player.sendMessage(pf + "Loot chest created.");
			} else {
				cwc.getDeleteChests().remove(player.getName());
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);

				if (block.getType() != Material.CHEST) {
					player.sendMessage(pf + ChatColor.RED + "That is not a chest.");
					return;
				}

				for (LootChest chest : cwc.getLootChests()) {
					if (chest.getChestBlock().equals(block)) {
						cwc.getLootChests().remove(chest);
						player.sendMessage(pf + "Loot chest deleted.");
						return;
					}
				}

				player.sendMessage(pf + ChatColor.RED + "This is not a loot chest.");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void bbreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		Block block = event.getBlock();
		boolean willBypass = player.isOp() || player.hasPermission("fwcore.lootchest.edit");

		for (int i = 0; i < cwc.getLootChests().size(); i++) {
			LootChest lc = cwc.getLootChests().get(i);

			if (lc.getChestBlock().equals(block)) {
				if (willBypass)
					cwc.getLootChests().remove(i);
				else {
					player.sendMessage(pf + ChatColor.RED + "You can't destroy a loot chest.");
					event.setCancelled(true);
				}
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void close(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();

		CraftInventory inv = (CraftInventory) event.getInventory();

		if (!(inv.getInventory() instanceof LootChestInventory))
			return;

		LootChestInventory lci = (LootChestInventory) inv.getInventory();

		if (!lci.isEdited())
			return;

		lci.getLootChest().getAccessed().add(player.getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void open(InventoryOpenEvent event) {
		if (event.isCancelled())
			return;

		Player player = (Player) event.getPlayer();

		try {
			InventoryHolder holder = event.getView().getTopInventory().getHolder();

			if (!(holder instanceof Chest))
				return;

			Chest c = (Chest) holder;
			Block b = c.getBlock();

			for (LootChest chest : cwc.getLootChests()) {
				if (chest.getChestBlock().equals(b)) {
					event.setCancelled(true);

					if (!player.hasPermission("fwcore.lootchest.edit") && !player.isOp() && chest.getAccessed().contains(player.getName())) {
						player.sendMessage(pf + ChatColor.RED + "You can't use this loot chest again.");
						return;
					}

					LootChestInventory dci = new LootChestInventory(chest);
					openSilently(player, dci);
					break;
				}
			}
		} catch (NullPointerException e) {
			return;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void click(InventoryClickEvent event) {
		if (event.isCancelled())
			return;

		Player player = (Player) event.getWhoClicked();

		CraftInventory inv = (CraftInventory) event.getInventory();

		if (!(inv.getInventory() instanceof LootChestInventory))
			return;

		LootChestInventory dci = (LootChestInventory) inv.getInventory();
		boolean bypass = player.hasPermission("fwcore.lootchest.edit") || player.isOp();

		boolean top = event.getRawSlot() + 1 <= event.getView().getTopInventory().getSize();
		ItemStack current = event.getCurrentItem();

		if (!top && current.getTypeId() != 0) {
			if (bypass)
				dci.setEdited(true);
			else {
				player.sendMessage(pf + ChatColor.RED + "You can't edit a loot chest.");
				event.setCancelled(true);
				event.setResult(Result.DENY);
			}
		} else if (top && current.getTypeId() != 0) {
			dci.setEdited(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void quit(PlayerQuitEvent event) {
		quit(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void quit(PlayerKickEvent event) {
		if (event.isCancelled())
			return;
		
		try {
			quit(event.getPlayer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void quit(Player player) {
		if (player.getOpenInventory() != null)
			try {
				player.closeInventory();
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	private void openSilently(Player player, net.minecraft.server.v1_5_R3.IInventory inv) {
		EntityPlayer p = ((CraftPlayer) player).getHandle();

		if (p.activeContainer != p.defaultContainer) {
			p.closeInventory();
		}

		try {
			int cc = p.nextContainerCounter();
			p.playerConnection.sendPacket(new Packet100OpenWindow(cc, 0, inv.getName(), inv.getSize(), true));
			p.activeContainer = new ContainerChest(p.inventory, inv);
			p.activeContainer.windowId = cc;
			p.activeContainer.addSlotListener(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
