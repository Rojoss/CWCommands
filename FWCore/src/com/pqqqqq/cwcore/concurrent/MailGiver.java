package com.pqqqqq.cwcore.concurrent;

import java.util.ArrayList;

import net.minecraft.server.v1_5_R3.EntityItem;
import net.minecraft.server.v1_5_R3.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.pqqqqq.cwcore.CWCore;
import com.pqqqqq.cwcore.Mail;
import com.pqqqqq.cwcore.util.Utils;

public class MailGiver implements Runnable {
	private CWCore	cwc;

	public MailGiver(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public void run() {
		ArrayList<Mail> mails = new ArrayList<Mail>();
		mails.addAll(cwc.getMail());

		final ArrayList<String> alreadyRecieved = new ArrayList<String>();
		for (final Mail mail : mails) {
			final Player player = cwc.getPlugin().getServer().getPlayer(mail.getRecipient());

			if (player != null && player.isOnline()) {
				if (alreadyRecieved.contains(player.getName()))
					continue;

				final Location loc = player.getLocation();
				final Vector dir = loc.getDirection();
				final World world = loc.getWorld();
				final WorldServer ws = ((CraftWorld) world).getHandle();

				Bukkit.getScheduler().runTask(cwc.getPlugin(), new Runnable() {

					@Override
					public void run() {
						double xDir = dir.getX() * 1.5;
						double zDir = dir.getZ() * 1.5;

						double xLoc = loc.getX() + xDir;
						double zLoc = loc.getZ() + zDir;
						final Location newLoc = new Location(world, xLoc, loc.getY() + 3, zLoc, -loc.getYaw(), loc.getPitch());

						final Pig pig = world.spawn(newLoc, Pig.class);

						cwc.getNoEditVillagers().add(pig);
						cwc.getMail().remove(mail);
						player.sendMessage(ChatColor.DARK_PURPLE + "[CWCore] " + mail.getSender() + ChatColor.GOLD + " has sent you mail.");
						alreadyRecieved.add(player.getName());

						Player send = cwc.getPlugin().getServer().getPlayer(mail.getSender());
						if (send != null)
							send.sendMessage(ChatColor.DARK_PURPLE + "[CWCore] " + player.getName() + ChatColor.GOLD + " recieved your letter.");

						Bukkit.getScheduler().runTaskLater(cwc.getPlugin(), new Runnable() {

							@Override
							public void run() {
								try {
									ItemStack i = Utils.createBook(true, mail.getTitle(), mail.getAuthor(), mail.getPages());
									CraftItemStack ci = CraftItemStack.asCraftCopy(i);

									EntityItem entity = new EntityItem(ws, newLoc.getX(), newLoc.getY(), newLoc.getZ(), CraftItemStack.asNMSCopy(ci));
									entity.pickupDelay = 10;
									ws.addEntity(entity);

									final CraftItem item = new CraftItem(ws.getServer(), entity);
									cwc.getMailDrops().put(item, mail);

									pig.remove();
									cwc.getNoEditVillagers().remove(pig);

									Bukkit.getScheduler().runTaskLater(cwc.getPlugin(), new Runnable() {

										@Override
										public void run() {
											if (cwc.getMailDrops().containsKey(item)) {
												item.remove();
												cwc.getMailDrops().remove(item);
											}
										}
									}, 500);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, 30);
					}
				});

				/*
				 * EntityPlayer ep = ((CraftPlayer) player).getHandle();
				 * EntityEnderman ee = ((CraftEnderman) enderman).getHandle();
				 * ee.damageEntity(DamageSource.playerAttack(ep), 1);
				 * enderman.setTarget(player);
				 */
			}
		}
	}
}
