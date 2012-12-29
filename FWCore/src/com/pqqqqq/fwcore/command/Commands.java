package com.pqqqqq.fwcore.command;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.server.v1_4_6.EntityFireworks;
import net.minecraft.server.v1_4_6.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftFirework;
import org.bukkit.craftbukkit.v1_4_6.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import com.pqqqqq.fwcore.Book;
import com.pqqqqq.fwcore.FWCore;
import com.pqqqqq.fwcore.Mail;
import com.pqqqqq.fwcore.util.Utils;

public class Commands {
	private FWCore										fwc;
	private final HashMap<String, ArrayList<Method>>	commands	= new HashMap<String, ArrayList<Method>>();

	/* Command fields */
	private final ArrayList<String>						sent		= new ArrayList<String>();

	/*                */

	// ---------------------------------------------- //

	/* Commands */
	/* Mail commands */

	@Command(
			permissions = { "fwcore.mail.send" },
			aliases = { "send", "give" },
			description = "Sends a letter to a player",
			usage = "/mail send <user>",
			example = "/mail send Frank",
			label = "mail")
	public boolean mailSend(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		if (args.length <= 0) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Usage: /mail send <user>.");
			return true;
		}

		ItemStack mail = player.getItemInHand();

		if (mail == null || mail.getType() != Material.WRITTEN_BOOK) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Please put your letter in your hand.");
			return true;
		}

		if (sent.contains(player.getName())) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "The librarians are busy, please wait " + fwc.getMailDelay()
					+ " second(s) in-between mail");
			return true;
		}

		final String s = args[0];
		OfflinePlayer offp = fwc.getPlugin().getServer().getOfflinePlayer(s);

		if (offp == null || !offp.hasPlayedBefore()) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "This user hasn't been on the server yet.");
			return true;
		}

		if (offp.getPlayer() != null && offp.getPlayer().equals(player)) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "You can't send mail to yourself.");
			return true;
		}

		Location loc = player.getLocation();
		Vector dir = loc.getDirection();
		World world = loc.getWorld();

		double xDir = dir.getX() * 1.5;
		double zDir = dir.getZ() * 1.5;

		double xLoc = loc.getX() + xDir;
		double zLoc = loc.getZ() + zDir;
		Location newLoc = new Location(world, xLoc, loc.getY() + 3, zLoc, -loc.getYaw(), loc.getPitch());

		final Villager villager = world.spawn(newLoc, Villager.class);
		villager.setProfession(Villager.Profession.LIBRARIAN);
		fwc.getNoEditVillagers().add(villager);

		/*
		 * EntityPlayer ep = ((CraftPlayer) player).getHandle(); EntityEnderman
		 * ee = ((CraftEnderman) enderman).getHandle();
		 * ee.damageEntity(DamageSource.playerAttack(ep), 1);
		 * enderman.setTarget(player);
		 */

		final Book book = Utils.getBook(mail);

		player.setItemInHand(null);
		player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "You sent a letter to: " + ChatColor.DARK_PURPLE + args[0]);
		player.sendMessage(ChatColor.GOLD + "They will recieve the letter (if online) in 1-5 seconds");

		sent.add(player.getName());
		Bukkit.getScheduler().scheduleSyncDelayedTask(fwc.getPlugin(), new Runnable() {

			@Override
			public void run() {
				sent.remove(player.getName());
			}
		}, (fwc.getMailDelay() * 20) + 30);

		Bukkit.getScheduler().scheduleSyncDelayedTask(fwc.getPlugin(), new Runnable() {

			@Override
			public void run() {
				try {
					villager.remove();
					fwc.getNoEditVillagers().remove(villager);
					fwc.getMail().add(new Mail(player.getName(), s, book));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 30);
		return true;
	}

	/* End of mail commands */
	/* Book commands */

	@Command(
			permissions = { "fwcore.book.change" },
			aliases = { "author" },
			description = "Changes the author of a book",
			usage = "/book author <set|reset> [author]",
			example = "/book author set J.K. Rowling OR /book author reset",
			label = "book")
	public boolean bookChangeAuthor(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		if (args.length <= 0) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Usage: /book author <set|reset> [author].");
			return true;
		}

		ItemStack b = player.getItemInHand();

		if (b == null || b.getType() != Material.WRITTEN_BOOK) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Please put the book in your hand.");
			return true;
		}
		boolean set = args.length >= 2 && !args[0].equalsIgnoreCase("reset");

		Book book = Utils.getBook(b);
		book.setAuthor(set ? implode(args, " ", 1) : null);
		ItemStack newBook = Utils.createBook(true, book);

		player.setItemInHand(newBook);
		player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Author successfully " + (set ? "changed." : "reset."));
		return true;
	}

	@Command(
			permissions = { "fwcore.book.change" },
			aliases = { "title" },
			description = "Changes the title of a book",
			usage = "/book title <set|reset> [title]",
			example = "/book title set Harry Potter OR /book title reset",
			label = "book")
	public boolean bookChangeTitle(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		if (args.length <= 0) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Usage: /book title <set|reset> [title].");
			return true;
		}

		ItemStack b = player.getItemInHand();

		if (b == null || b.getType() != Material.WRITTEN_BOOK) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Please put the book in your hand.");
			return true;
		}

		boolean set = !args[0].equalsIgnoreCase("reset");

		Book book = Utils.getBook(b);
		book.setTitle(set ? implode(args, " ", 0) : null);
		ItemStack newBook = Utils.createBook(true, book);

		player.setItemInHand(newBook);
		player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Title successfully " + (set ? "changed." : "reset."));
		return true;
	}

	@Command(
			permissions = { "fwcore.book.reset" },
			aliases = { "reset" },
			description = "Makes the book editable",
			usage = "/book reset",
			label = "book")
	public boolean bookReset(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		ItemStack b = player.getItemInHand();

		if (b == null || b.getType() != Material.WRITTEN_BOOK) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Please put the book in your hand.");
			return true;
		}

		Book book = Utils.getBook(b);
		ItemStack newBook = Utils.createBook(false, book);

		player.setItemInHand(newBook);
		player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Book successfully reset.");
		return true;
	}

	@Command(
			permissions = { "fwcore.book.save" },
			aliases = { "save" },
			description = "Saves a book in memory",
			usage = "/book save <name>",
			example = "/book save Book1",
			label = "book")
	public boolean bookSave(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		if (args.length <= 0) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Usage: /book save <name>.");
			return true;
		}
		String name = implode(args, " ", 0);

		ItemStack b = player.getItemInHand();

		if (b == null || b.getType() != Material.WRITTEN_BOOK) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Please put the book in your hand.");
			return true;
		}

		if (fwc.getSavedBooks().containsKey(name)) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "There is already a book with that name.");
			return true;
		}

		Book book = Utils.getBook(b);
		fwc.getSavedBooks().put(name, book);
		player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Book saved successfully.");
		return true;
	}

	@Command(
			permissions = { "fwcore.book.give" },
			aliases = { "give" },
			description = "Gives a book in memory to a player",
			usage = "/book give <player> <book>",
			example = "/book give Afred Book1",
			label = "book")
	public boolean bookGive(CommandSender sender, String[] args) {
		if (args.length <= 1) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Usage: /book give <player> <book>.");
			return true;
		}

		Player pl = fwc.getPlugin().getServer().getPlayer(args[0]);

		if (pl == null) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "That player is not online.");
			return true;
		}
		String bb = implode(args, " ", 1);

		if (!fwc.getSavedBooks().containsKey(bb)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "There is no book saved with that name.");
			return true;
		}

		Book book = fwc.getSavedBooks().get(bb);
		ItemStack item = Utils.createBook(true, book);

		pl.getInventory().addItem(item);
		pl.sendMessage(ChatColor.GOLD + "You got the book " + ChatColor.DARK_PURPLE + bb);
		sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "You gave " + ChatColor.DARK_PURPLE + pl.getName() + ChatColor.GOLD
				+ " the book " + ChatColor.DARK_PURPLE + bb);
		return true;
	}

	@Command(
			permissions = { "fwcore.book.remove" },
			aliases = { "remove" },
			description = "Removes a book from memory",
			usage = "/book remove <name>",
			example = "/book remove <name>",
			label = "book")
	public boolean bookRemove(CommandSender sender, String[] args) {
		if (args.length <= 0) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Usage: /book remove <name>.");
			return true;
		}

		if (!fwc.getSavedBooks().containsKey(args[0])) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "There is no book saved with that name.");
			return true;
		}
		fwc.getSavedBooks().remove(args[0]);

		sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + args[0] + ChatColor.GOLD + " successfully removed");
		return true;
	}

	/* End of book commands */
	/* Dungeon chest commands */

	@Command(
			permissions = { "fwcore.dungeonchest.create" },
			aliases = { "create" },
			description = "Creates a \"dungeon\" chest",
			usage = "/chest create",
			label = "chest")
	public boolean createDungeonChest(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		fwc.getCreateChests().add(player.getName());
		fwc.getDeleteChests().remove(player.getName());
		player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Right click a chest to make it a dungeon chest.");
		return true;
	}

	@Command(
			permissions = { "fwcore.dungeonchest.delete" },
			aliases = { "delete" },
			description = "Delete a \"dungeon\" chest",
			usage = "/chest delete",
			label = "chest")
	public boolean deleteDungeonChest(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		fwc.getDeleteChests().add(player.getName());
		fwc.getCreateChests().remove(player.getName());
		player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Right click a dungeon chest to remove it.");
		return true;
	}

	/* End of dungeon chest commands */

	@Command(permissions = { "fwcore.firework" }, aliases = {}, description = "Customize your rockets", usage = "/firework", label = "firework")
	public boolean firework(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		World world = player.getWorld();
		WorldServer ws = ((CraftWorld) world).getHandle();
		ItemStack firework = player.getItemInHand();

		if (firework == null || firework.getType() != Material.FIREWORK) {
			player.sendMessage(ChatColor.RED + "[FWCore] Put your rocket in your hand.");
			return true;
		}

		if (args.length <= 0 || !args[0].equalsIgnoreCase("addcolour") && !args[0].equalsIgnoreCase("addcolor")
				&& !args[0].equalsIgnoreCase("addfade") && !args[0].equalsIgnoreCase("power") && !args[0].equalsIgnoreCase("trail")
				&& !args[0].equalsIgnoreCase("flicker") && !args[0].equalsIgnoreCase("clear") && !args[0].equalsIgnoreCase("type")
				&& !args[0].equalsIgnoreCase("copy") && !args[0].equalsIgnoreCase("addeffect") && !args[0].equalsIgnoreCase("info")) {
			player.sendMessage(ChatColor.BLUE + "[FWCore] Usage: /firework <addcolour|addfade|power|trail|flicker|clear|type|copy|addeffect|info>.");
			return true;
		}

		FireworkMeta meta = (FireworkMeta) firework.getItemMeta();

		EntityFireworks fireworks = new EntityFireworks(ws);
		CraftFirework fw = new CraftFirework(ws.getServer(), fireworks);

		if (args[0].equalsIgnoreCase("copy")) {
			if (args.length <= 1) {
				player.sendMessage(ChatColor.BLUE + "[FWCore] Usage: /firework copy <amount>.");
				player.sendMessage(ChatColor.BLUE + "Example: /firework copy 64 (copy into a 64-stack)");
				return true;
			}

			int amt;
			try {
				amt = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "[FWCore] Amount must be a number.");
				return true;
			}

			try {
				fw.setFireworkMeta(meta);
				Field f = CraftFirework.class.getDeclaredField("item");
				f.setAccessible(true);
				CraftItemStack it = (CraftItemStack) f.get(fw);
				it.setAmount(amt);

				player.getInventory().addItem(it);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (args[0].equalsIgnoreCase("addeffect")) {
			if (args.length <= 3) {
				player.sendMessage(ChatColor.BLUE + "[FWCpre] Usage: /firework addeffect r g b.");
				player.sendMessage(ChatColor.BLUE + "Example: /firework addeffect 255 0 0 (create an effect with base-colour red)");
				return true;
			}

			int r = 0;
			int g = 0;
			int b = 0;

			try {
				r = Integer.parseInt(args[1]);
				g = Integer.parseInt(args[2]);
				b = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "[FWCore] RGB values must be numbers.");
				return true;
			}

			FireworkEffect eft = FireworkEffect.builder().withColor(Color.fromRGB(r, g, b)).build();

			meta.addEffect(eft);
			fw.setFireworkMeta(meta);

			try {
				Field f = CraftFirework.class.getDeclaredField("item");
				f.setAccessible(true);
				CraftItemStack it = (CraftItemStack) f.get(fw);
				it.setAmount(firework.getAmount());

				player.setItemInHand(it);
			} catch (Exception e) {
				e.printStackTrace();
			}

			player.sendMessage(ChatColor.GREEN + "[FWCore] Successfully added effect #" + (meta.getEffectsSize() - 1));
			return true;
		} else if (args[0].equalsIgnoreCase("info")) {
			player.sendMessage(ChatColor.BLUE + "Power (Flight duration): " + meta.getPower());
			player.sendMessage(ChatColor.BLUE + "Effects (" + meta.getEffectsSize() + "):");

			for (int i = 0; i < meta.getEffectsSize(); i++) {
				FireworkEffect effect = meta.getEffects().get(i);
				player.sendMessage(ChatColor.GREEN + "Effect #" + i + ":");
				player.sendMessage(ChatColor.BLUE + "Flicker effect: " + (effect.hasFlicker() ? "yes" : "no"));
				player.sendMessage(ChatColor.BLUE + "Trail effect: " + (effect.hasTrail() ? "yes" : "no"));
				player.sendMessage(ChatColor.BLUE + "Type: " + effect.getType().name().toLowerCase().replace("_", " "));
				player.sendMessage(ChatColor.BLUE + "Colours (" + effect.getColors().size() + "):");

				for (int c = 0; c < effect.getColors().size(); c++) {
					Color color = effect.getColors().get(c);
					player.sendMessage(ChatColor.GREEN + "Colour #" + c + ": R: " + color.getRed() + ", G: " + color.getGreen() + ", B:"
							+ color.getBlue());
				}

				player.sendMessage(ChatColor.BLUE + "Fade Colours (" + effect.getFadeColors().size() + "):");

				for (int c = 0; c < effect.getFadeColors().size(); c++) {
					Color color = effect.getFadeColors().get(c);
					player.sendMessage(ChatColor.GREEN + "Fade Colour #" + c + ": R: " + color.getRed() + ", G: " + color.getGreen() + ", B:"
							+ color.getBlue());
				}
			}

			return true;
		}

		FireworkEffect[] effects = null;

		if (!args[0].equalsIgnoreCase("power")) {
			effects = meta.getEffectsSize() == 0 ? new FireworkEffect[] {} : meta.getEffects().toArray(new FireworkEffect[meta.getEffectsSize()]);
			meta.clearEffects();
		}

		if (args[0].equalsIgnoreCase("addcolour") || args[0].equalsIgnoreCase("addcolor")) {
			if (args.length <= 4) {
				player.sendMessage(ChatColor.BLUE + "[FWCore] Usage: /firework addcolour <effect> r g b.");
				player.sendMessage(ChatColor.BLUE + "Example: /firework addcolour 2 255 0 0 (add red to effect #2)");
				return true;
			}

			int effectn;
			try {
				effectn = Integer.parseInt(args[1]);

				if (effectn < 0 || effectn > (effects.length - 1)) {
					player.sendMessage(ChatColor.RED + "[FWCore] Effect number must be between 0 - " + (effects.length - 1));
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "[FWCore] Effect # must be an integer.");
				return true;
			}

			int r = 0;
			int g = 0;
			int b = 0;

			try {
				r = Integer.parseInt(args[2]);
				g = Integer.parseInt(args[3]);
				b = Integer.parseInt(args[4]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "[FWCore] RGB values must be numbers.");
				return true;
			}

			FireworkEffect effect = cloneEffect(effects[effectn]).withColor(Color.fromRGB(r, g, b)).build();
			effects[effectn] = effect;

			meta.addEffects(effects);
			player.sendMessage(ChatColor.GREEN + "[FWCore] Successfully added colour.");
		} else if (args[0].equalsIgnoreCase("addfade")) {
			if (args.length <= 4) {
				player.sendMessage(ChatColor.BLUE + "[FWCore] Usage: /firework addfade <effect> r g b.");
				player.sendMessage(ChatColor.BLUE + "Example: /firework addfade 2 255 0 0 (add red to effect #2)");
				return true;
			}

			int effectn;
			try {
				effectn = Integer.parseInt(args[1]);

				if (effectn < 0 || effectn > (effects.length - 1)) {
					player.sendMessage(ChatColor.RED + "[FWCore] Effect number must be between 0 - " + (effects.length - 1));
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "[FWCore] Effect # must be an integer.");
				return true;
			}

			int r = 0;
			int g = 0;
			int b = 0;

			try {
				r = Integer.parseInt(args[2]);
				g = Integer.parseInt(args[3]);
				b = Integer.parseInt(args[4]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "[FWCore] RGB values must be numbers.");
				return true;
			}

			FireworkEffect newe = cloneEffect(effects[effectn]).withFade(Color.fromRGB(r, g, b)).build();
			effects[effectn] = newe;

			meta.addEffects(effects);
			player.sendMessage(ChatColor.GREEN + "[FWCore] Successfully added fade.");
		} else if (args[0].equalsIgnoreCase("trail")) {
			if (args.length <= 1) {
				player.sendMessage(ChatColor.BLUE + "[FWCore] Usage: /firework trail <effect>.");
				player.sendMessage(ChatColor.BLUE + "Example: /firework trail 0");
				return true;
			}

			int effectn;
			try {
				effectn = Integer.parseInt(args[1]);

				if (effectn < 0 || effectn > (effects.length - 1)) {
					player.sendMessage(ChatColor.RED + "[FWCore] Effect number must be between 0 - " + (effects.length - 1));
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "[FWCore] Effect # must be an integer.");
				return true;
			}

			FireworkEffect effect = effects[effectn];
			boolean trail = (effect == null ? true : !effect.hasTrail());

			FireworkEffect newe = cloneEffect(effect).trail(trail).build();
			effects[effectn] = newe;

			meta.addEffects(effects);
			player.sendMessage(ChatColor.GREEN + "[FWCore] Successfully " + (trail ? "added a" : "removed the") + " trail");
		} else if (args[0].equalsIgnoreCase("flicker")) {
			if (args.length <= 1) {
				player.sendMessage(ChatColor.BLUE + "[FWCore] Usage: /firework flicker <effect>.");
				player.sendMessage(ChatColor.BLUE + "Example: /firework flicker 0");
				return true;
			}

			int effectn;
			try {
				effectn = Integer.parseInt(args[1]);

				if (effectn < 0 || effectn > (effects.length - 1)) {
					player.sendMessage(ChatColor.RED + "[FWCore] Effect number must be between 0 - " + (effects.length - 1));
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "[FWCore] Effect # must be an integer.");
				return true;
			}

			FireworkEffect effect = effects[effectn];
			boolean flicker = (effect == null ? true : !effect.hasFlicker());

			FireworkEffect newe = cloneEffect(effect).flicker(flicker).build();
			effects[effectn] = newe;
			meta.addEffects(effects);
			player.sendMessage(ChatColor.GREEN + "[FWCore] Successfully " + (flicker ? "added a" : "removed the") + " flicker");
		} else if (args[0].equalsIgnoreCase("power")) {
			if (args.length <= 1) {
				player.sendMessage(ChatColor.BLUE + "[FWCore] Usage: /firework power <amount>.");
				return true;
			}

			try {
				int power = Integer.parseInt(args[1]);

				if (power > 3) {
					player.sendMessage(ChatColor.RED + "[FWCore] Firework power can't be more than 3.");
					return true;
				}

				if (power < 1) {
					player.sendMessage(ChatColor.RED + "[FWCore] Firework power can't be less than 1.");
					return true;
				}

				meta.setPower(power);
				player.sendMessage(ChatColor.GREEN + "[FWCore] Successfully set power (flight duration) to: " + power);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "[FWCore] Power must be a number.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("clear")) {
			meta.clearEffects();
			player.sendMessage(ChatColor.GREEN + "[FWCore] Successfully cleared all effects on the rocket.");
		} else if (args[0].equalsIgnoreCase("type")) {
			if (args.length <= 2) {
				player.sendMessage(ChatColor.BLUE + "[FWCore] Usage: /firework type <effect> <ball|ball_large|burst|creeper|star>.");
				player.sendMessage(ChatColor.BLUE + "Example: /firework type 0 small_ball");
				return true;
			}

			int effectn;
			try {
				effectn = Integer.parseInt(args[1]);

				if (effectn < 0 || effectn > (effects.length - 1)) {
					player.sendMessage(ChatColor.RED + "[FWCore] Effect number must be between 0 - " + (effects.length - 1));
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "[FWCore] Effect # must be an integer.");
				return true;
			}

			Type type = null;

			try {
				type = FireworkEffect.Type.valueOf(args[2].toUpperCase());
			} catch (Throwable e) {
				player.sendMessage(ChatColor.RED + "[FWCore] That is not a valid firework type.");
				return true;
			}

			FireworkEffect newe = cloneEffect(effects[effectn]).with(type).build();
			effects[effectn] = newe;
			meta.addEffects(effects);
			player.sendMessage(ChatColor.GREEN + "[FWCore] Successfully changed the effect type to " + type.name().toLowerCase().replace("_", " "));
		}

		fw.setFireworkMeta(meta);

		try {
			Field f = CraftFirework.class.getDeclaredField("item");
			f.setAccessible(true);
			CraftItemStack it = (CraftItemStack) f.get(fw);
			it.setAmount(firework.getAmount());

			player.setItemInHand(it);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private FireworkEffect.Builder cloneEffect(FireworkEffect effect) {
		FireworkEffect.Builder builder = FireworkEffect.builder();

		if (effect == null)
			return builder;

		builder = builder.withColor(effect.getColors());
		builder = builder.withFade(effect.getFadeColors());
		builder = builder.with(effect.getType());
		builder = builder.trail(effect.hasTrail());
		builder = builder.flicker(effect.hasFlicker());
		return builder;
	}

	/* End of commands */
	public Commands(FWCore fwc) {
		this.fwc = fwc;
	}

	public void populateCommands() {
		commands.clear();

		for (Method method : getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(Command.class) && method.getReturnType() == boolean.class) {
				Command cmd = method.getAnnotation(Command.class);
				String lbl = cmd.label();

				if (commands.containsKey(lbl))
					commands.get(lbl).add(method);
				else
					commands.put(lbl, new ArrayList<Method>(Arrays.asList(new Method[] { method })));
			}
		}

		// Collections.sort(commands, new SortMethods());
	}

	public boolean executeCommand(CommandSender sender, String lbl, String[] args) {
		if (args.length <= 0) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "V" + fwc.getPlugin().getDescription().getVersion()
					+ ChatColor.DARK_PURPLE + " created by " + ChatColor.GOLD + "Pqqqqq");
			sender.sendMessage(ChatColor.DARK_PURPLE + "Use " + ChatColor.GOLD + "/" + lbl + " help");
			return true;
		}

		for (Method method : commands.get(lbl)) {
			try {
				Command commandInfo = method.getAnnotation(Command.class);
				String[] aliases = commandInfo.aliases();
				String[] perms = commandInfo.permissions();

				if (aliases.length <= 0)
					return (Boolean) method.invoke(this, sender, args);

				for (String alias : aliases) {
					if (alias.equalsIgnoreCase(args[0])) {
						check: if (!sender.isOp() && perms != null && perms.length > 0) {
							for (String p : perms) {
								if (sender.hasPermission(p))
									break check;
							}

							sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Insufficient permissions!");
							return true;
						}

						try {
							return (Boolean) method.invoke(this, sender, trimFirst(args));
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Throwable e) {
				continue;
			}
		}

		sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "V" + fwc.getPlugin().getDescription().getVersion()
				+ ChatColor.DARK_PURPLE + " created by " + ChatColor.GOLD + "Pqqqqq");
		sender.sendMessage(ChatColor.DARK_PURPLE + "Use " + ChatColor.GOLD + "/" + lbl + " help");
		return true;
	}

	private String[] trimFirst(String[] a) {
		if (a.length <= 0)
			return a;

		String[] ret = new String[a.length - 1];

		for (int i = 1; i < a.length; i++) {
			ret[i - 1] = a[i];
		}

		return ret;
	}

	private String implode(String[] args, String connect, int start) {
		if (args == null || args.length <= 0)
			return "";

		String ret = "";
		for (int i = start; i < args.length; i++) {
			ret += args[i] + connect;
		}

		return ret.substring(0, ret.length() - connect.length());
	}
}
