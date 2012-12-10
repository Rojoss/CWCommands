package com.pqqqqq.fwcore.bukkit.events;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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

public class MainEvents implements Listener {
	private FWCore				fwc;
	private ArrayList<String>	edited	= new ArrayList<String>();

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
		InventoryHolder holder = event.getView().getTopInventory().getHolder();

		if (!edited.contains(player.getName()))
			return;

		if (!(holder instanceof Chest))
			return;

		edited.remove(player.getName());
		Chest c = (Chest) holder;
		Block b = c.getBlock();

		for (final DungeonChest chest : fwc.getDungeonChests()) {
			if (chest.getChestBlock().equals(b)) {
				/*
				 * if (player.hasPermission("fwcore.dungeonchest.edit") || player.isOp())
				 * chest.setInventory(c.getInventory().getContents());
				 * else
				 */

				//System.out.println(player.getName() + " Adding to dungeon chest accessed");
				chest.getAccessed().add(player.getName());
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void open(InventoryOpenEvent event) {
		if (event.isCancelled())
			return;

		Player player = (Player) event.getPlayer();

		if (player.hasPermission("fwcore.dungeonchest.edit") || player.isOp())
			return;

		InventoryHolder holder = event.getView().getTopInventory().getHolder();

		if (!(holder instanceof Chest))
			return;

		Chest c = (Chest) holder;
		Block b = c.getBlock();

		for (DungeonChest chest : fwc.getDungeonChests()) {
			if (chest.getChestBlock().equals(b)) {
				//System.out.println(player.getName() + " Opened dungeon chest");

				if (chest.getAccessed().contains(player.getName())) {
					player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "You can't use this chest again.");
					event.setCancelled(true);
				}
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void click(InventoryClickEvent event) {
		if (event.isCancelled())
			return;

		Player player = (Player) event.getWhoClicked();
		InventoryHolder holder = event.getView().getTopInventory().getHolder();
		boolean bypass = player.hasPermission("fwcore.dungeonchest.edit") || player.isOp();

		if (!(holder instanceof Chest))
			return;

		Chest c = (Chest) holder;
		Block b = c.getBlock();

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

		for (DungeonChest chest : fwc.getDungeonChests()) {
			if (chest.getChestBlock().equals(b)) {
				//System.out.println(player.getName() + " Clicked dungeon chest, top: " + top);

				if (!top && current.getTypeId() != 0) {
					if (bypass)
						edited.add(player.getName());
					else {
						player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "You can't edit a dungeon chest.");
						event.setCancelled(true);
						event.setResult(Result.DENY);
					}
				} else if (top && current.getTypeId() != 0) {
					//System.out.println(player.getName() + " Adding to edited");
					edited.add(player.getName());
				}
			}
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
}
