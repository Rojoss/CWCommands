package com.pqqqqq.fwcore.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
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
			usage = "/chest create <seconds>",
			example = "/chest create 60",
			label = "chest")
	public boolean createDungeonChest(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		if (args.length <= 0) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Usage: /chest create <seconds>.");
			return true;
		}

		int time;
		try {
			time = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Time must be an integer.");
			return true;
		}

		fwc.getCreateChests().put(player.getName(), time);
		fwc.getDeleteChests().remove(player.getName());
		fwc.getRefillChests().remove(player.getName());
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
		fwc.getRefillChests().remove(player.getName());
		player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Right click a dungeon chest to remove it.");
		return true;
	}

	@Command(
			permissions = { "fwcore.dungeonchest.edit" },
			aliases = { "refill" },
			description = "Changes fill time for a \"dungeon\" chest",
			usage = "/chest refill <time>",
			example = "/chest refill 30",
			label = "chest")
	public boolean changeRefillDungeonChest(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		if (args.length <= 0) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Usage: /chest refill <time>.");
			return true;
		}

		int time;
		try {
			time = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.DARK_RED + "Time must be an integer.");
			return true;
		}

		fwc.getRefillChests().put(player.getName(), time);
		fwc.getCreateChests().remove(player.getName());
		fwc.getDeleteChests().remove(player.getName());
		player.sendMessage(ChatColor.DARK_PURPLE + "[FWCore] " + ChatColor.GOLD + "Right click a dungeon chest to change the refill time.");
		return true;
	}

	/* End of dungeon chest commands */
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
