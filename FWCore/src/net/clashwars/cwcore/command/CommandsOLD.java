package net.clashwars.cwcore.command;


public class CommandsOLD {
	//private CWCore										cwc;
	//private String pf = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
	//private final HashMap<String, ArrayList<Method>>	commands	= new HashMap<String, ArrayList<Method>>();
	
	
	//TODO: Convert to other command system
	
/* 
 * 
 * CommandClass: exp
 * 
	@Command(
            permissions = { "cwcore.exp" },
            aliases = {},
            description = "Give/Take/Show experience",
            usage = "/exp <player> [xp[L]]",
            example = "/exp joe 10L",
            label = "exp")
	public boolean exp(CommandSender sender, String[] args) {
	    if (args.length < 1) {
	            sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/exp <player> [xp[L]]");
	            return true;
	    }
	
	    Player player = cwc.getPlugin().getServer().getPlayer(args[0]);
	
	    if (player == null) {
	    	sender.sendMessage(pf + "Invalid player.");
	    	return true;
	    }
	    
	    if (args.length == 1) {
	    	sender.sendMessage(pf + ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.GOLD + " his experience is: " 
	    			+ ChatColor.DARK_PURPLE + player.getTotalExperience()
            		+ ChatColor.GOLD + " Level: " + ChatColor.DARK_PURPLE + player.getLevel());
	    	return true;
	    }
	
	    boolean levels = false;
	    if (args[1].toLowerCase().endsWith("l")) {
	            levels = true;
	            args[1] = args[1].substring(0, args[1].length() - 1);
	    }
	
	    int amt;
	    try {
	            amt = Integer.parseInt(args[1]);
	    } catch (NumberFormatException e) {
	            sender.sendMessage(pf + ChatColor.RED + "Invalid amount.");
	            return true;
	    }
	
	    if (levels) {
	            int newl = player.getLevel() + amt;
	            if (newl <= 0) {
	                    player.setLevel(0);
	            } else {
	                    player.giveExpLevels(amt);
	            }
	    } else {
	            int newxp = player.getTotalExperience() + amt;
	            
	            if (newxp <= 0) {
	                    player.setTotalExperience(0);
	            } else {
	                    player.giveExp(amt);
	            }
	    }
	    return true;
	}
	
/* 
 * 
 * CommandClass: chest
 * 
		@Command(
				permissions = { "cwcore.lootchest.create" },
				aliases = { "create" },
				description = "Creates a \"loot\" chest",
				usage = "/chest create",
				label = "chest")
		public boolean createDungeonChest(CommandSender sender, String[] args) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(pf + ChatColor.RED + "Player-only command.");
				return true;
			}

			final Player player = (Player) sender;

			cwc.getCreateChests().add(player.getName());
			cwc.getDeleteChests().remove(player.getName());
			player.sendMessage(pf + "Right click a chest to make it a loot chest.");
			return true;
		}

		@Command(
				permissions = { "cwcore.lootchest.delete" },
				aliases = { "delete" },
				description = "Delete a \"loot\" chest",
				usage = "/chest delete",
				label = "chest")
		public boolean deleteDungeonChest(CommandSender sender, String[] args) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(pf + ChatColor.RED + "Player-only command.");
				return true;
			}

			final Player player = (Player) sender;

			cwc.getDeleteChests().add(player.getName());
			cwc.getCreateChests().remove(player.getName());
			player.sendMessage(pf + "Right click a loot chest to remove it.");
			return true;
		}

		@Command(
				permissions = {},
				aliases = { "help" },
				description = "Chest help",
				usage = "/chest help <page>",
				example = "/chest help 1",
				label = "chest")
		public boolean chestHelp(CommandSender sender, String[] args) {
			return help(sender, args, "chest");
		}
	
/* 
 * 
 * CommandClass: book
 * 
	@Command(
			permissions = { "cwcore.book.change" },
			aliases = { "author" },
			description = "Changes the author of a book",
			usage = "/book author <set|reset> [author]",
			example = "/book author set J.K. Rowling OR /book author reset",
			label = "book")
	public boolean bookChangeAuthor(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		if (args.length <= 0) {
			player.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/book author <set|reset> [author].");
			return true;
		}

		ItemStack b = player.getItemInHand();

		if (b == null || b.getType() != Material.WRITTEN_BOOK) {
			player.sendMessage(pf + ChatColor.RED + "Please put the book in your hand.");
			return true;
		}
		boolean set = args.length >= 2 && !args[0].equalsIgnoreCase("reset");

		Book book = Utils.getBook(b);
		book.setAuthor(set ? implode(args, " ", 1) : null);
		ItemStack newBook = Utils.createBook(true, book);

		player.setItemInHand(newBook);
		player.sendMessage(pf + "Author successfully " + (set ? "changed." : "reset."));
		return true;
	}

	@Command(
			permissions = { "cwcore.book.change" },
			aliases = { "title" },
			description = "Changes the title of a book",
			usage = "/book title <set|reset> [title]",
			example = "/book title set Harry Potter OR /book title reset",
			label = "book")
	public boolean bookChangeTitle(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		if (args.length <= 0) {
			player.sendMessage(pf + "Usage:" + ChatColor.DARK_PURPLE + "/book title <set|reset> [title]");
			return true;
		}

		ItemStack b = player.getItemInHand();

		if (b == null || b.getType() != Material.WRITTEN_BOOK) {
			player.sendMessage(pf + ChatColor.RED + "Please put the book in your hand.");
			return true;
		}

		boolean set = !args[0].equalsIgnoreCase("reset");

		Book book = Utils.getBook(b);
		book.setTitle(set ? implode(args, " ", 0) : null);
		ItemStack newBook = Utils.createBook(true, book);

		player.setItemInHand(newBook);
		player.sendMessage(pf + "Title successfully " + (set ? "changed." : "reset."));
		return true;
	}

	@Command(
			permissions = { "cwcore.book.reset" },
			aliases = { "reset" },
			description = "Makes the book editable",
			usage = "/book reset",
			label = "book")
	public boolean bookReset(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		ItemStack b = player.getItemInHand();

		if (b == null || b.getType() != Material.WRITTEN_BOOK) {
			player.sendMessage(pf + ChatColor.RED + "Please put the book in your hand.");
			return true;
		}

		Book book = Utils.getBook(b);
		ItemStack newBook = Utils.createBook(false, book);

		player.setItemInHand(newBook);
		player.sendMessage(pf + "Book successfully reset.");
		return true;
	}

	@Command(
			permissions = { "cwcore.book.save" },
			aliases = { "save" },
			description = "Saves a book in memory",
			usage = "/book save <name>",
			example = "/book save Book1",
			label = "book")
	public boolean bookSave(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(pf + ChatColor.RED + "Player-only command.");
			return true;
		}

		final Player player = (Player) sender;

		if (args.length <= 0) {
			player.sendMessage(pf + "Usage:" + ChatColor.DARK_PURPLE + "/book save <name>");
			return true;
		}
		String name = implode(args, " ", 0);

		ItemStack b = player.getItemInHand();

		if (b == null || b.getType() != Material.WRITTEN_BOOK) {
			player.sendMessage(pf + "Please put the book in your hand.");
			return true;
		}

		if (cwc.getSavedBooks().containsKey(name)) {
			player.sendMessage(pf + ChatColor.RED + "There is already a book with that name.");
			return true;
		}

		Book book = Utils.getBook(b);
		cwc.getSavedBooks().put(name, book);
		player.sendMessage(pf + "Book saved successfully.");
		return true;
	}

	@Command(
			permissions = { "cwcore.book.give" },
			aliases = { "give" },
			description = "Gives a book in memory to a player",
			usage = "/book give <player> <book>",
			example = "/book give Afred Book1",
			label = "book")
	public boolean bookGive(CommandSender sender, String[] args) {
		if (args.length <= 1) {
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/book give <player> <book>");
			return true;
		}

		Player pl = cwc.getPlugin().getServer().getPlayer(args[0]);

		if (pl == null) {
			sender.sendMessage(pf + ChatColor.RED + "That player is not online.");
			return true;
		}
		String bb = implode(args, " ", 1);

		if (!cwc.getSavedBooks().containsKey(bb)) {
			sender.sendMessage(pf + ChatColor.RED + "There is no book saved with that name.");
			return true;
		}

		Book book = cwc.getSavedBooks().get(bb);
		ItemStack item = Utils.createBook(true, book);

		pl.getInventory().addItem(item);
		sender.sendMessage(pf + "You gave " + ChatColor.DARK_PURPLE + pl.getName() + ChatColor.GOLD
				+ " the book " + ChatColor.DARK_PURPLE + bb);
		return true;
	}

	@Command(
			permissions = { "cwcore.book.remove" },
			aliases = { "remove" },
			description = "Removes a book from memory",
			usage = "/book remove <name>",
			example = "/book remove <name>",
			label = "book")
	public boolean bookRemove(CommandSender sender, String[] args) {
		if (args.length <= 0) {
			sender.sendMessage(pf + "Usage: /book remove <name>.");
			return true;
		}

		if (!cwc.getSavedBooks().containsKey(args[0])) {
			sender.sendMessage(pf + ChatColor.RED + "There is no book saved with that name.");
			return true;
		}
		cwc.getSavedBooks().remove(args[0]);

		sender.sendMessage(pf + ChatColor.DARK_PURPLE + args[0] + ChatColor.GOLD + " successfully removed");
		return true;
	}

	@Command(permissions = {}, aliases = { "help" }, description = "Book help", usage = "/book help <page>", example = "/book help 1", label = "book")
	public boolean bookHelp(CommandSender sender, String[] args) {
		return help(sender, args, "book");
	}

/* 
 * 
 * CommandClass: firework
 * 

	@Command(permissions = { "cwcore.firework" }, aliases = {}, description = "Customize your rockets", usage = "/firework", label = "firework")
	public boolean firework(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		World world = player.getWorld();
		WorldServer ws = ((CraftWorld) world).getHandle();
		ItemStack firework = player.getItemInHand();

		if (firework == null || firework.getType() != Material.FIREWORK) {
			player.sendMessage(pf + ChatColor.RED +  "Put your rocket in your hand.");
			return true;
		}

		if (args.length <= 0 || !args[0].equalsIgnoreCase("addcolour") && !args[0].equalsIgnoreCase("addcolor")
				&& !args[0].equalsIgnoreCase("addfade") && !args[0].equalsIgnoreCase("power") && !args[0].equalsIgnoreCase("trail")
				&& !args[0].equalsIgnoreCase("flicker") && !args[0].equalsIgnoreCase("clear") && !args[0].equalsIgnoreCase("type")
				&& !args[0].equalsIgnoreCase("copy") && !args[0].equalsIgnoreCase("addeffect") && !args[0].equalsIgnoreCase("info")) {
			player.sendMessage(pf + ChatColor.GOLD + "Usage: " + ChatColor.GOLD 
					+ "/firework <addcolour|addfade|power|trail|flicker|clear|type|copy|addeffect|info>.");
			return true;
		}

		FireworkMeta meta = (FireworkMeta) firework.getItemMeta();

		EntityFireworks fireworks = new EntityFireworks(ws);
		CraftFirework fw = new CraftFirework(ws.getServer(), fireworks);

		if (args[0].equalsIgnoreCase("copy")) {
			if (args.length <= 1) {
				player.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/firework copy <amount>");
				player.sendMessage(ChatColor.DARK_GRAY + "Example: /firework copy 64 (copy into a 64-stack)");
				return true;
			}

			int amt;
			try {
				amt = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				player.sendMessage(pf + ChatColor.RED + "Amount must be a number.");
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
				player.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/firework addeffect R G B");
				player.sendMessage(ChatColor.DARK_GRAY + "Example: /firework addeffect 255 0 0 (create an effect with base-color red)");
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
				player.sendMessage(pf + ChatColor.RED + "RGB values must be numbers.");
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

			player.sendMessage(pf + ChatColor.GOLD + "Successfully added effect " + ChatColor.DARK_PURPLE + "#" + (meta.getEffectsSize() - 1));
			return true;
		} else if (args[0].equalsIgnoreCase("info")) {
			player.sendMessage(ChatColor.DARK_PURPLE + "Power (Flight duration): " + meta.getPower());
			player.sendMessage(ChatColor.DARK_PURPLE + "Effects (" + meta.getEffectsSize() + "):");

			for (int i = 0; i < meta.getEffectsSize(); i++) {
				FireworkEffect effect = meta.getEffects().get(i);
				player.sendMessage(ChatColor.GOLD + "Effect " + ChatColor.DARK_PURPLE + "#" + i + ChatColor.GOLD + ":");
				player.sendMessage(ChatColor.GOLD + "Flicker effect: " + (effect.hasFlicker() ? ChatColor.GREEN + "yes" : ChatColor.RED + "no"));
				player.sendMessage(ChatColor.GOLD + "Trail effect: " + (effect.hasTrail() ? ChatColor.GREEN + "yes" : ChatColor.RED + "no"));
				player.sendMessage(ChatColor.GOLD + "Type: " + ChatColor.DARK_PURPLE + effect.getType().name().toLowerCase().replace("_", " "));
				player.sendMessage(ChatColor.GOLD + "Colors (" + ChatColor.DARK_PURPLE + effect.getColors().size() + ChatColor.GOLD + "):");

				for (int c = 0; c < effect.getColors().size(); c++) {
					Color color = effect.getColors().get(c);
					player.sendMessage(ChatColor.GOLD + "Color " + ChatColor.DARK_PURPLE + "#" + c + ChatColor.GOLD + ": "
					+ ChatColor.DARK_RED + "R: " + ChatColor.RED + color.getRed() 
					+ ChatColor.DARK_GREEN +", G: " + ChatColor.GREEN + color.getGreen() 
					+ ChatColor.DARK_BLUE + ", B:" + ChatColor.BLUE + color.getBlue());
				}

				player.sendMessage(ChatColor.GOLD + "Fade Colours (" + ChatColor.DARK_PURPLE + effect.getFadeColors().size() + ChatColor.GOLD + "):");

				for (int c = 0; c < effect.getFadeColors().size(); c++) {
					Color color = effect.getFadeColors().get(c);
					player.sendMessage(ChatColor.GOLD + "Fade Color " + ChatColor.DARK_PURPLE + "#" + c + ChatColor.GOLD + ": "
							+ ChatColor.DARK_RED + "R: " + ChatColor.RED + color.getRed() 
							+ ChatColor.DARK_GREEN +", G: " + ChatColor.GREEN + color.getGreen() 
							+ ChatColor.DARK_BLUE + ", B:" + ChatColor.BLUE + color.getBlue());
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
				player.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/firework addcolour <effect> R G B");
				player.sendMessage(ChatColor.DARK_GRAY + "Example: /firework addcolour 2 255 0 0 (add red to effect #2)");
				return true;
			}

			int effectn;
			try {
				effectn = Integer.parseInt(args[1]);

				if (effectn < 0 || effectn > (effects.length - 1)) {
					player.sendMessage(pf + ChatColor.RED + "Effect number must be between 0 - " + (effects.length - 1));
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(pf + ChatColor.RED + "Effect # must be an integer.");
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
				player.sendMessage(pf + ChatColor.RED + "RGB values must be numbers.");
				return true;
			}

			FireworkEffect effect = cloneEffect(effects[effectn]).withColor(Color.fromRGB(r, g, b)).build();
			effects[effectn] = effect;

			meta.addEffects(effects);
			player.sendMessage(pf + "Successfully added colour.");
		} else if (args[0].equalsIgnoreCase("addfade")) {
			if (args.length <= 4) {
				player.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/firework addfade <effect> R G B");
				player.sendMessage(ChatColor.DARK_GRAY + "Example: /firework addfade 2 255 0 0 (add red to effect #2)");
				return true;
			}

			int effectn;
			try {
				effectn = Integer.parseInt(args[1]);

				if (effectn < 0 || effectn > (effects.length - 1)) {
					player.sendMessage(pf + ChatColor.RED + "Effect number must be between 0 - " + (effects.length - 1));
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(pf + ChatColor.RED + "Effect # must be an integer.");
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
				player.sendMessage(pf + ChatColor.RED + "RGB values must be numbers.");
				return true;
			}

			FireworkEffect newe = cloneEffect(effects[effectn]).withFade(Color.fromRGB(r, g, b)).build();
			effects[effectn] = newe;

			meta.addEffects(effects);
			player.sendMessage(pf + "[CWCore] Successfully added fade.");
		} else if (args[0].equalsIgnoreCase("trail")) {
			if (args.length <= 1) {
				player.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/firework trail <effect>");
				player.sendMessage(ChatColor.DARK_GRAY + "Example: /firework trail 0");
				return true;
			}

			int effectn;
			try {
				effectn = Integer.parseInt(args[1]);

				if (effectn < 0 || effectn > (effects.length - 1)) {
					player.sendMessage(pf + ChatColor.RED + "Effect number must be between 0 - " + (effects.length - 1));
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(pf + ChatColor.RED + "Effect # must be an integer.");
				return true;
			}

			FireworkEffect effect = effects[effectn];
			boolean trail = (effect == null ? true : !effect.hasTrail());

			FireworkEffect newe = cloneEffect(effect).trail(trail).build();
			effects[effectn] = newe;

			meta.addEffects(effects);
			player.sendMessage(pf + "Successfully " + (trail ? "added a" : "removed the") + " trail");
		} else if (args[0].equalsIgnoreCase("flicker")) {
			if (args.length <= 1) {
				player.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/firework flicker <effect>");
				player.sendMessage(ChatColor.DARK_GRAY + "Example: /firework flicker 0");
				return true;
			}

			int effectn;
			try {
				effectn = Integer.parseInt(args[1]);

				if (effectn < 0 || effectn > (effects.length - 1)) {
					player.sendMessage(pf + ChatColor.RED + "Effect number must be between 0 - " + (effects.length - 1));
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(pf + ChatColor.RED + "Effect # must be an integer.");
				return true;
			}

			FireworkEffect effect = effects[effectn];
			boolean flicker = (effect == null ? true : !effect.hasFlicker());

			FireworkEffect newe = cloneEffect(effect).flicker(flicker).build();
			effects[effectn] = newe;
			meta.addEffects(effects);
			player.sendMessage(pf + "Successfully " + (flicker ? "added a" : "removed the") + " flicker");
		} else if (args[0].equalsIgnoreCase("power")) {
			if (args.length <= 1) {
				player.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/firework power <amount 1-3>.");
				return true;
			}

			try {
				int power = Integer.parseInt(args[1]);

				if (power > 3) {
					player.sendMessage(pf + ChatColor.RED + "Firework power can't be more than 3.");
					return true;
				}

				if (power < 1) {
					player.sendMessage(pf + ChatColor.RED + "Firework power can't be less than 1.");
					return true;
				}

				meta.setPower(power);
				player.sendMessage(pf + "Successfully set power (flight duration) to: " + ChatColor.DARK_PURPLE + power);
			} catch (NumberFormatException e) {
				player.sendMessage(pf + ChatColor.RED + "Power must be a number.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("clear")) {
			meta.clearEffects();
			player.sendMessage(pf + "Successfully cleared all effects on the rocket.");
		} else if (args[0].equalsIgnoreCase("type")) {
			if (args.length <= 2) {
				player.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/firework type <effectID> <ball|ball_large|burst|creeper|star>");
				player.sendMessage(ChatColor.DARK_GRAY + "Example: /firework type 0 small_ball");
				return true;
			}

			int effectn;
			try {
				effectn = Integer.parseInt(args[1]);

				if (effectn < 0 || effectn > (effects.length - 1)) {
					player.sendMessage(pf + ChatColor.RED + "Effect number must be between 0 - " + (effects.length - 1));
					return true;
				}
			} catch (NumberFormatException e) {
				player.sendMessage(pf + ChatColor.RED + "Effect # must be an integer.");
				return true;
			}

			Type type = null;

			try {
				type = FireworkEffect.Type.valueOf(args[2].toUpperCase());
			} catch (Throwable e) {
				player.sendMessage(pf + ChatColor.RED + "That is not a valid firework type.");
				return true;
			}

			FireworkEffect newe = cloneEffect(effects[effectn]).with(type).build();
			effects[effectn] = newe;
			meta.addEffects(effects);
			player.sendMessage(ChatColor.GREEN + "Successfully changed the effect type to " + type.name().toLowerCase().replace("_", " "));
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

	@Command(
			permissions = { "cwcore.tploc" },
			aliases = {},
			description = "Teleport a player",
			usage = "/tploc <player> <x,y,z> <world>",
			label = "tploc")
	public boolean tploc(CommandSender sender, String[] args) {
		if (args.length <= 2) {
			sender.sendMessage(pf + "Usage: " + ChatColor.DARK_PURPLE + "/tploc <player> <x,y,z> <world>");
			return true;
		}

		Player player = cwc.getPlugin().getServer().getPlayer(args[0]);

		if (player == null || !player.isOnline()) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid player.");
			return true;
		}

		String[] coords = args[1].split(",");

		if (coords.length <= 2) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid coordinate pattern. <x,y,z>");
			return true;
		}

		int x;
		int y;
		int z;

		try {
			x = Integer.parseInt(coords[0].trim());
			y = Integer.parseInt(coords[1].trim());
			z = Integer.parseInt(coords[2].trim());
		} catch (NumberFormatException e) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid coordinates. <x,y,z>");
			return true;
		}

		World world = cwc.getPlugin().getServer().getWorld(args[2]);

		if (world == null) {
			sender.sendMessage(pf + ChatColor.RED + "Invalid world.");
			return true;
		}

		for (int yt = y; yt <= 256; yt++) {
			Location tloc = new Location(world, x, yt, z);
			Location tlocu = null;

			if (yt < 256) {
				tlocu = new Location(world, x, yt + 1, z);
			}

			if (tloc.getBlock().getTypeId() == 0 && (tlocu == null || tlocu.getBlock().getTypeId() == 0)) {
				player.teleport(tloc);
				return true;
			}
		}

		return true;
	}
/* End of commands
	
	
	public CommandsOLD(CWCore cwc) {
		this.cwc = cwc;
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
	}

	public boolean executeCommand(CommandSender sender, String lbl, String[] args) {

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

							sender.sendMessage(pf + ChatColor.RED + "Insufficient permissions!");
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

		sender.sendMessage(pf + "V" + cwc.getPlugin().getDescription().getVersion() + " - Core features for clashwars!");
		sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.DARK_PURPLE + "/" + lbl + " help" + ChatColor.GOLD + " to see all commands!");
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

	private boolean help(CommandSender sender, String[] args, String lbl) {
		ArrayList<Method> cmds = commands.get(lbl);

		int page = 1;
		int pages = (int) Math.ceil(cmds.size() / 7D);

		try {
			page = Integer.parseInt(args[0]);
		} catch (Throwable e) {
		}

		if (page > pages) {
			sender.sendMessage(pf + ChatColor.RED + "There are/is only " + pages + " page(s).");
			return true;
		}

		int start = (page - 1) * 7;
		int end = (page * 7) - 1;

		sender.sendMessage(ChatColor.DARK_PURPLE + "===== " 
			+ ChatColor.GOLD + "CW Core Help Page " + ChatColor.GRAY + page + ChatColor.DARK_PURPLE + "/" + ChatColor.DARK_GRAY + pages 
			+ ChatColor.DARK_PURPLE + " =====");
		for (int i = start; i <= end && i < commands.size(); i++) {
			Method method = cmds.get(i);
			Command command = method.getAnnotation(Command.class);
			String description = command.description();
			String alias = command.aliases()[0];

			sender.sendMessage(ChatColor.DARK_PURPLE + "/" + lbl + " " + alias + " --> " + ChatColor.GOLD + description);
		}
		return true;
	}
}
*/
}