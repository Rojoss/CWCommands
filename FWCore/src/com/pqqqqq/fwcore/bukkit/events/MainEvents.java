package com.pqqqqq.fwcore.bukkit.events;

import java.util.Random;

import net.minecraft.server.v1_5_R1.ContainerChest;
import net.minecraft.server.v1_5_R1.EntityPlayer;
import net.minecraft.server.v1_5_R1.Packet100OpenWindow;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_5_R1.inventory.CraftInventory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.pqqqqq.fwcore.DungeonChest;
import com.pqqqqq.fwcore.FWCore;
import com.pqqqqq.fwcore.Mail;
import com.pqqqqq.fwcore.bukkit.DungeonChestInventory;

public class MainEvents implements Listener {
	private FWCore	fwc;

	public MainEvents(FWCore fwc) {
		this.fwc = fwc;
	}

	/*
	 * @EventHandler(priority = EventPriority.MONITOR) public void entityTele(EntityTeleportEvent event) { if (event.isCancelled()) return;
	 * 
	 * Entity entity = event.getEntity();
	 * 
	 * if (!(entity instanceof Enderman)) return;
	 * 
	 * Enderman enderman = (Enderman) entity;
	 * 
	 * if (fwc.getNoEditVillagers().contains(enderman)) { event.setCancelled(true); event.setTo(event.getFrom()); } }
	 */

	@EventHandler(priority = EventPriority.MONITOR)
	public void pickup(PlayerPickupItemEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		Item item = event.getItem();

		if (!fwc.getMailDrops().containsKey(item))
			return;

		Mail mail = fwc.getMailDrops().get(item);

		if (!mail.getRecipient().equalsIgnoreCase(player.getName()))
			event.setCancelled(true);
	}

	/*
	 * @EventHandler(priority = EventPriority.MONITOR) public void changeBlock(EntityChangeBlockEvent event) { if (event.isCancelled()) return;
	 * 
	 * Entity entity = event.getEntity();
	 * 
	 * if (!(entity instanceof Villager)) return;
	 * 
	 * Enderman enderman = (Enderman) entity;
	 * 
	 * if (fwc.getNoEditVillagers().contains(enderman)) event.setCancelled(true); }
	 */

	@EventHandler(priority = EventPriority.MONITOR)
	public void dmg(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		Entity entity = event.getEntity();

		if (entity instanceof Villager) {
			Villager villager = (Villager) entity;

			if (fwc.getNoEditVillagers().contains(villager))
				event.setCancelled(true);
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
			Entity e = ev.getDamager();

			if (e instanceof Villager) {
				Villager villager = (Villager) e;

				if (fwc.getNoEditVillagers().contains(villager))
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (!fwc.getCreateChests().contains(player.getName()) && !fwc.getDeleteChests().contains(player.getName()))
			return;

		if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (fwc.getCreateChests().contains(player.getName())) {
				fwc.getCreateChests().remove(player.getName());
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);

				if (block.getType() != Material.CHEST) {
					player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "That is not a chest.");
					return;
				}

				DungeonChest dc = new DungeonChest(block);

				if (fwc.getDungeonChests().contains(dc)) {
					player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "That is already a dungeon chest.");
					return;
				}

				fwc.getDungeonChests().add(dc);
				player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Dungeon chest created.");
			} else {
				fwc.getDeleteChests().remove(player.getName());
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);

				if (block.getType() != Material.CHEST) {
					player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "That is not a chest.");
					return;
				}

				for (DungeonChest chest : fwc.getDungeonChests()) {
					if (chest.getChestBlock().equals(block)) {
						fwc.getDungeonChests().remove(chest);
						player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Dungeon chest deleted.");
						return;
					}
				}

				player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "This is not a dungeon chest.");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void bbreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		Block block = event.getBlock();
		boolean willBypass = player.isOp() || player.hasPermission("fwcore.dungeonchest.edit");

		for (int i = 0; i < fwc.getDungeonChests().size(); i++) {
			DungeonChest dc = fwc.getDungeonChests().get(i);

			if (dc.getChestBlock().equals(block)) {
				if (willBypass)
					fwc.getDungeonChests().remove(i);
				else {
					player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "You can't destroy a dungeon chest.");
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

		if (!(inv.getInventory() instanceof DungeonChestInventory))
			return;

		DungeonChestInventory dci = (DungeonChestInventory) inv.getInventory();

		if (!dci.isEdited())
			return;

		dci.getDungeonChest().getAccessed().add(player.getName());
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

			for (DungeonChest chest : fwc.getDungeonChests()) {
				if (chest.getChestBlock().equals(b)) {
					//System.out.println(player.getName() + " Opened dungeon chest");
					event.setCancelled(true);

					if (!player.hasPermission("fwcore.dungeonchest.edit") && !player.isOp() && chest.getAccessed().contains(player.getName())) {
						player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "You can't use this chest again.");
						return;
					}

					DungeonChestInventory dci = new DungeonChestInventory(chest);
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

		if (!(inv.getInventory() instanceof DungeonChestInventory))
			return;

		DungeonChestInventory dci = (DungeonChestInventory) inv.getInventory();
		boolean bypass = player.hasPermission("fwcore.dungeonchest.edit") || player.isOp();

		boolean top = event.getRawSlot() + 1 <= event.getView().getTopInventory().getSize();
		//ItemStack cursor = event.getCursor();
		ItemStack current = event.getCurrentItem();

		/*
		 * System.out.println("Top: " + top);
		 * System.out.println("Raw slot: " + event.getRawSlot());
		 * System.out.println("Slot: " + event.getSlot());
		 * System.out.println("Cursor: " + (cursor == null ? "null" : cursor.toString()));
		 * System.out.println("Current: " + (current == null ? "null" : current.toString()));
		 */

		//System.out.println(player.getName() + " Clicked dungeon chest, top: " + top);

		if (!top && current.getTypeId() != 0) {
			if (bypass)
				dci.setEdited(true);
			else {
				player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "You can't edit a dungeon chest.");
				event.setCancelled(true);
				event.setResult(Result.DENY);
			}
		} else if (top && current.getTypeId() != 0) {
			//System.out.println(player.getName() + " Adding to edited");
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

		quit(event.getPlayer());
	}

	private void quit(Player player) {
		if (player.getOpenInventory() != null)
			player.closeInventory();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void weatherChange(WeatherChangeEvent event) {
		if (event.isCancelled() || !event.toWeatherState())
			return;

		Random random = new Random();
		int rd = 1 + random.nextInt(100);

		if (rd <= fwc.getStormPercent())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void spawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.SPAWNER)
			return;

		final LivingEntity entity = event.getEntity();
		entity.setRemoveWhenFarAway(true);

		fwc.getPlugin().getServer().getScheduler().runTaskLater(fwc.getPlugin(), new Runnable() {

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

	private void openSilently(Player player, net.minecraft.server.v1_5_R1.IInventory inv) {
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
}
