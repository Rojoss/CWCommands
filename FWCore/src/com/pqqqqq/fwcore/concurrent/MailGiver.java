package com.pqqqqq.fwcore.concurrent;

import java.util.ArrayList;

import net.minecraft.server.v1_4_6.EntityItem;
import net.minecraft.server.v1_4_6.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftItem;
import org.bukkit.craftbukkit.v1_4_6.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
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

				final Witch witch = world.spawn(newLoc, Witch.class);

				/*
				 * EntityPlayer ep = ((CraftPlayer) player).getHandle();
				 * EntityEnderman ee = ((CraftEnderman) enderman).getHandle();
				 * ee.damageEntity(DamageSource.playerAttack(ep), 1);
				 * enderman.setTarget(player);
				 */

				fwc.getNoEditVillagers().add(witch);
				fwc.getMail().remove(mail);
				player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + mail.getSender() + ChatColor.GOLD + " has sent you mail.");
				alreadyRecieved.add(player.getName());

				Player send = fwc.getPlugin().getServer().getPlayer(mail.getSender());
				if (send != null)
					send.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + player.getName() + ChatColor.GOLD + " recieved your letter.");

				Bukkit.getScheduler().runTaskLaterAsynchronously(fwc.getPlugin(), new Runnable() {

					@Override
					public void run() {
						try {
							Location loc = witch.getLocation();
							ItemStack i = Utils.createBook(true, mail.getTitle(), mail.getAuthor(), mail.getPages());
							CraftItemStack ci = (CraftItemStack) i;

							EntityItem entity = new EntityItem(ws, loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(ci));
							entity.pickupDelay = 10;
							ws.addEntity(entity);

							CraftItem item = new CraftItem(ws.getServer(), entity);
							fwc.getMailDrops().put(item, mail);

							witch.remove();
							fwc.getNoEditVillagers().remove(witch);

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
