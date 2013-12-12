package net.clashwars.cwcore.bukkit.events;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.AliasUtils;
import net.clashwars.cwcore.util.ExpUtils;
import net.clashwars.cwcore.util.InvUtils;
import net.clashwars.cwcore.util.LocationUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class MessageEvents implements PluginMessageListener {
	private CWCore			cwc;
	private String			pf			= "";

	private Player			target		= null;
	private Player			p			= null;
	private World			w			= null;
	private Location		loc			= null;
	private String			teleporter	= null;
	private String			ptarget		= null;
	private String			sender		= null;
	private String			destination	= null;
	private String			world		= null;
	private String			item		= null;
	private boolean			silent		= false;
	private boolean			force		= false;
	private int				amt			= 1;
	private MaterialData	md			= null;
	private ItemStack		i			= null;
	private CWPlayer		cwp			= null;

	public MessageEvents(CWCore cwc) {
		this.cwc = cwc;
		pf = cwc.getPrefix();
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equalsIgnoreCase("CWCore")) {
			return;
		}
		final DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

		cwc.getServer().getScheduler().runTaskLater(cwc.getPlugin(), new Runnable() {

			@Override
			public void run() {
				try {
					String ch = in.readUTF();

					switch (ch.toLowerCase()) {
						case "tp":
							teleporter = in.readUTF();
							destination = in.readUTF();
							silent = in.readBoolean();
							force = in.readBoolean();

							p = cwc.getServer().getPlayer(teleporter);
							target = cwc.getServer().getPlayer(destination);

							if (target == null && p != null) {
								p.sendMessage(pf + ChatColor.RED + "Invalid player");
							}

							if (p != null && target != null) {
								if (force) {
									p.teleport(target);
								} else {
									p.teleport(LocationUtils.getSafeDestination(target.getLocation()));
								}
								if (!silent) {
									p.sendMessage(pf + "You have been teleported to " + target.getDisplayName());
									if (target.getName() != p.getName())
										p.sendMessage(pf + "You have teleported " + ChatColor.DARK_PURPLE + p.getDisplayName() + ChatColor.GOLD
												+ " to " + ChatColor.DARK_PURPLE + target.getDisplayName());
								}
							}
							break;
						case "tphere":
							teleporter = in.readUTF();
							destination = in.readUTF();
							silent = in.readBoolean();
							force = in.readBoolean();

							p = cwc.getServer().getPlayer(teleporter);
							target = cwc.getServer().getPlayer(destination);

							if (p == null && target != null) {
								target.sendMessage(pf + ChatColor.RED + "Invalid player");
								break;
							}

							if (p != null && target != null) {
								if (force) {
									target.teleport(p.getLocation());
								} else {
									target.teleport(LocationUtils.getSafeDestination(p.getLocation()));
								}
								if (!silent) {
									p.sendMessage(pf + "You have teleported " + target.getDisplayName() + ChatColor.GOLD + " to you.");
									target.sendMessage(pf + "You where teleported to " + p.getDisplayName());
								}
							}
							break;
						case "tppos":
							sender = in.readUTF();
							teleporter = in.readUTF();
							world = in.readUTF();
							destination = in.readUTF();
							silent = in.readBoolean();
							force = in.readBoolean();

							p = cwc.getServer().getPlayer(teleporter);

							w = cwc.getServer().getWorld(world);
							if (world == null) {
								p.sendMessage(pf + ChatColor.RED + "Invalid world");
								break;
							}

							if (p == null) {
								sendMessage(sender, pf + ChatColor.RED + "Invalid player");
								break;
							}

							if (p != null) {
								loc = LocationUtils.getLocation(destination, w);
							}

							if (loc != null && p != null && w != null) {
								if (force) {
									p.teleport(loc);
								} else {
									p.teleport(LocationUtils.getSafeDestination(loc));
								}
								if (!silent) {
									p.sendMessage(pf + "You have been teleported to " + ChatColor.DARK_PURPLE + p.getLocation().getX() + ", "
											+ p.getLocation().getY() + ", " + p.getLocation().getZ());
									if (sender.equalsIgnoreCase(p.getName()))
										sendMessage(sender, pf + "You have teleported " + ChatColor.DARK_PURPLE + p.getDisplayName() + ChatColor.GOLD
												+ " to " + ChatColor.DARK_PURPLE + p.getLocation().getX() + ", " + p.getLocation().getY() + ", "
												+ p.getLocation().getZ());
								}
							}
							break;
						case "kill":
							sender = in.readUTF();
							ptarget = in.readUTF();
							silent = in.readBoolean();
							force = in.readBoolean();

							p = cwc.getServer().getPlayer(ptarget);

							if (p == null) {
								sendMessage(sender, pf + ChatColor.RED + "Invalid player");
								break;
							}

							if (p != null) {
								p.setHealth(0);
								if (!silent) {
									p.sendMessage(pf + "You where killed by " + ChatColor.DARK_PURPLE + sender);
									if (sender.equalsIgnoreCase(p.getName())) {
										sendMessage(sender, pf + "You have killed " + ChatColor.DARK_PURPLE + p.getDisplayName());
									}
								}
							}
							break;
						case "clearinv":
							sender = in.readUTF();
							ptarget = in.readUTF();
							item = in.readUTF();
							amt = in.readInt();
							silent = in.readBoolean();
							boolean armor = in.readBoolean();
							boolean inventory = in.readBoolean();
							boolean bar = in.readBoolean();
							boolean hand = in.readBoolean();
							boolean echest = in.readBoolean();
							boolean all = in.readBoolean();

							p = cwc.getServer().getPlayer(ptarget);
							cwc.getServer().broadcastMessage("WORKING");

							if (p == null) {
								sendMessage(sender, pf + ChatColor.RED + "Invalid player");
								break;
							}

							md = AliasUtils.getFullData(item);

							if (p != null) {
								cwc.getServer().broadcastMessage(all + " , " + md + " , " + amt + " , " + item);
								if (all) {
									InvUtils.clearInventorySlots(p, false, 0, -1, md, amt);
									p.getInventory().setArmorContents(null);
								} else {
									if (armor)
										p.getInventory().setArmorContents(null);
									if (inventory)
										InvUtils.clearInventorySlots(p, false, 9, 36, md, amt);
									if (bar)
										InvUtils.clearInventorySlots(p, false, 0, 9, md, amt);
									if (hand)
										InvUtils.clearInventorySlots(p, false, p.getInventory().getHeldItemSlot(),
												p.getInventory().getHeldItemSlot() + 1, md, amt);
									if (echest)
										InvUtils.clearInventorySlots(p, true, 0, -1, md, amt);
								}

								if (!silent) {
									p.sendMessage(pf + "Items in your " + (echest ? "enderchest" : "inventory") + " have been cleared!");
									if (sender.equalsIgnoreCase(p.getName()))
										sendMessage(sender, pf + "You have cleared items in " + ChatColor.DARK_PURPLE + p.getDisplayName() + ChatColor.GOLD
												+ " his " + (echest ? "enderchest" : "inventory") + ".");
								}
							}
							break;
						case "give":
							sender = in.readUTF();
							ptarget = in.readUTF();
							item = in.readUTF();
							amt = in.readInt();
							silent = in.readBoolean();
							boolean drop = in.readBoolean();
							boolean unstack = in.readBoolean();
							boolean equip = in.readBoolean();

							p = cwc.getServer().getPlayer(ptarget);
							md = AliasUtils.getFullData(item);

							if (md == null) {
								sendMessage(sender, pf + ChatColor.RED + "Item " + ChatColor.GRAY + item + ChatColor.RED + " was not recognized!");
								break;
							}
							i = new ItemStack(md.getItemType(), amt, md.getData());

							if (p == null) {
								sendMessage(sender, pf + ChatColor.RED + "Invalid player");
								break;
							}

							boolean equiped = false;
							if (p != null && i != null && md != null && amt != -1) {
								if (equip) {
									if (i.getType().name().endsWith("HELMET")) {
										p.getInventory().setHelmet(i);
										equiped = true;
										drop = false;
										unstack = false;
									} else if (i.getType().name().endsWith("CHESTPLATE")) {
										p.getInventory().setChestplate(i);
										equiped = true;
										drop = false;
										unstack = false;
									} else if (i.getType().name().endsWith("LEGGINGS")) {
										p.getInventory().setLeggings(i);
										equiped = true;
										drop = false;
										unstack = false;
									} else if (i.getType().name().endsWith("BOOTS")) {
										p.getInventory().setBoots(i);
										equiped = true;
										drop = false;
										unstack = false;
									}
								}
								if (!drop && !unstack && !equiped)
									p.getInventory().addItem(i);
								if (drop && !equiped) {
									Location loc = p.getEyeLocation().add(p.getLocation().getDirection());
									loc.getWorld().dropItem(loc, i);
								}
								if (unstack && !equiped) {
									for (int u = 0; u < amt; u++) {
										ItemStack uItem = i;
										uItem.setAmount(1);
										p.getInventory().addItem(uItem);
									}
								}

								String name = i.getItemMeta().getDisplayName();
								if (!silent) {
									if (name == null) {
										sendMessage(sender, pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + item + ChatColor.GOLD + " to "
												+ ChatColor.DARK_PURPLE + p.getDisplayName());
										p.sendMessage(pf + "You received " + ChatColor.DARK_PURPLE + amt + " " + item + ChatColor.GOLD + " from "
												+ ChatColor.DARK_PURPLE + sender);
									} else {
										sendMessage(sender, pf + "Given " + ChatColor.DARK_PURPLE + amt + " " + Utils.integrateColor(name) + ChatColor.GOLD
												+ " to " + ChatColor.DARK_PURPLE + p.getDisplayName());
										p.sendMessage(pf + "You received " + ChatColor.DARK_PURPLE + amt + " " + Utils.integrateColor(name)
												+ ChatColor.GOLD + " from " + ChatColor.DARK_PURPLE + sender);
									}
								}
							}
							break;
						case "whois":
							sender = in.readUTF();
							ptarget = in.readUTF();

							p = cwc.getServer().getPlayer(ptarget);

							if (p == null) {
								sendMessage(sender, pf + ChatColor.RED + "Invalid player");
								break;
							}

							if (p != null) {
								cwp = cwc.getPlayerManager().getPlayer(p.getName());
								ExpUtils expMan = new ExpUtils(p);

								String yes = ChatColor.GREEN + "true";
								String no = ChatColor.DARK_RED + "false";

								sendMessage(sender, ChatColor.DARK_GRAY + "========" + ChatColor.DARK_RED + "CW whois: " + ChatColor.GOLD + p.getName()
										+ ChatColor.DARK_GRAY + "========");
								sendMessage(sender, ChatColor.DARK_PURPLE + "Nickname" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + p.getDisplayName());
								sendMessage(sender, ChatColor.DARK_PURPLE + "Tag" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + cwp.getTag());
								sendMessage(sender, ChatColor.DARK_PURPLE + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD
										+ p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ());
								sendMessage(sender, ChatColor.DARK_PURPLE + "Server" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + cwc.getServer().getServerName()
										+ ChatColor.GRAY + " - " + ChatColor.DARK_GRAY + cwc.getServer().getIp());
								sendMessage(sender, ChatColor.DARK_PURPLE + "Health" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + p.getHealth() + "/"
										+ p.getMaxHealth());
								sendMessage(sender, ChatColor.DARK_PURPLE + "Hunger" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + p.getFoodLevel()
										+ "/20" + " saturation: " + ChatColor.YELLOW + p.getSaturation());
								sendMessage(sender, ChatColor.DARK_PURPLE + "Gamemode" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD
										+ p.getGameMode().toString().toLowerCase());
								sendMessage(sender, ChatColor.DARK_PURPLE + "Flymode" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD
										+ (cwp.getFlying() ? yes : no));
								sendMessage(sender, ChatColor.DARK_PURPLE + "Godmode" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD
										+ (cwp.getGod() ? yes : no));
								sendMessage(sender, ChatColor.DARK_PURPLE + "Vanished" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD
										+ (cwp.getVanished() ? yes : no));
								sendMessage(sender, ChatColor.DARK_PURPLE + "Speed" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "walk: "
										+ ChatColor.YELLOW + p.getWalkSpeed() + ChatColor.GOLD + " fly: " + ChatColor.YELLOW + p.getFlySpeed());
								sendMessage(sender, ChatColor.DARK_PURPLE + "Experience" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD
										+ expMan.getCurrentExp() + ChatColor.GRAY + "Lvl:" + ChatColor.GOLD
										+ expMan.getLevelForExp(expMan.getCurrentExp()));
							}
							break;
						case "broadcast":
							ptarget = in.readUTF();
							String message = in.readUTF();

							if (message != null) {
								cwc.getServer().broadcastMessage(Utils.integrateColor(message));
							}
							break;
						case "switchserver":
							in.readUTF();
							ptarget = in.readUTF();
							
							cwp = cwc.getPlayerManager().getPlayer(ptarget);
							cwp.fetchData();
							break;
						case "warpteleport":
							teleporter = in.readUTF();
							String warpName = in.readUTF();
							world = in.readUTF();
							int x = in.readInt();
							int y = in.readInt();
							int z = in.readInt();
							float yaw = in.readFloat();
							float pitch = in.readFloat();
							silent = in.readBoolean();
							force = in.readBoolean();
							
							target = cwc.getServer().getPlayer(teleporter);
							w = cwc.getServer().getWorld(world);
							
							if (target == null) {
								break;
							}
							
							if (w == null) {
								break;
							}
							
							Location loc = new Location(w, x, y, z, yaw, pitch);
							
							target.teleport(force ? loc : LocationUtils.getSafeDestination(loc));
							
							if (!silent) {
								target.sendMessage(pf + "Warping to " + ChatColor.DARK_PURPLE + warpName);
							}
							break;
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, 5);
	}

	public void sendMessage(String player, String message) throws Exception {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		out.writeUTF("PlayerMessage");
		out.writeUTF(player);
		out.writeUTF(message);
		
		Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwc.getPlugin(), "CWBungee", b.toByteArray());
	}
}
