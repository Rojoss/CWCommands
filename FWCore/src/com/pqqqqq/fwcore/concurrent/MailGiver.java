package com.pqqqqq.fwcore.concurrent;

import java.util.ArrayList;

import net.minecraft.server.EntityItem;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.pqqqqq.fwcore.FWCore;
import com.pqqqqq.fwcore.Mail;
import com.pqqqqq.fwcore.util.Utils;

public class MailGiver implements Runnable {
	private FWCore	fwc;

	public MailGiver(FWCore fwc) {
		this.fwc = fwc;
	}

	@Override
	public void run() {
		ArrayList<Mail> mails = new ArrayList<Mail>();
		mails.addAll(fwc.getMail());

		final ArrayList<String> alreadyRecieved = new ArrayList<String>();
		for (final Mail mail : mails) {
			final Player player = fwc.getPlugin().getServer().getPlayer(mail.getRecipient());

			if (player != null && player.isOnline()) {
				if (alreadyRecieved.contains(player.getName()))
					continue;

				Location loc = player.getLocation();
				Vector dir = loc.getDirection();
				final World world = loc.getWorld();
				final WorldServer ws = ((CraftWorld) world).getHandle();

				double xDir = dir.getX() * 1.5;
				double zDir = dir.getZ() * 1.5;

				double xLoc = loc.getX() + xDir;
				double zLoc = loc.getZ() + zDir;
				Location newLoc = new Location(world, xLoc, loc.getY() + 3, zLoc, -loc.getYaw(), loc.getPitch());

				final Villager villager = world.spawn(newLoc, Villager.class);
				villager.setProfession(Villager.Profession.LIBRARIAN);

				/*
				 * EntityPlayer ep = ((CraftPlayer) player).getHandle();
				 * EntityEnderman ee = ((CraftEnderman) enderman).getHandle();
				 * ee.damageEntity(DamageSource.playerAttack(ep), 1);
				 * enderman.setTarget(player);
				 */

				fwc.getNoEditVillagers().add(villager);
				fwc.getMail().remove(mail);
				player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + mail.getSender() + ChatColor.GOLD + " has sent you mail.");
				alreadyRecieved.add(player.getName());

				Player send = fwc.getPlugin().getServer().getPlayer(mail.getSender());
				if (send != null)
					send.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + player.getName() + ChatColor.GOLD + " recieved your letter.");

				Bukkit.getScheduler().scheduleAsyncDelayedTask(fwc.getPlugin(), new Runnable() {

					@Override
					public void run() {
						try {
							Location loc = villager.getLocation();
							ItemStack i = Utils.createBook(true, mail.getTitle(), mail.getAuthor(), mail.getPages());
							CraftItemStack ci = (CraftItemStack) i;

							EntityItem entity = new EntityItem(ws, loc.getX(), loc.getY(), loc.getZ(), ci.getHandle());
							entity.pickupDelay = 10;
							ws.addEntity(entity);

							CraftItem item = new CraftItem(ws.getServer(), entity);
							fwc.getMailDrops().put(item, mail);

							villager.remove();
							fwc.getNoEditVillagers().remove(villager);

							Thread.sleep(25000);
							if (fwc.getMailDrops().containsKey(item)) {
								item.remove();
								fwc.getMailDrops().remove(item);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 30);
			}
		}
	}
}
