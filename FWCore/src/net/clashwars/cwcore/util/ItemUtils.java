package net.clashwars.cwcore.util;

import java.util.ArrayList;
import java.util.Arrays;

import net.clashwars.cwcore.Book;
import net.minecraft.server.v1_5_R3.EntityFireworks;
import net.minecraft.server.v1_5_R3.NBTTagCompound;
import net.minecraft.server.v1_5_R3.NBTTagList;
import net.minecraft.server.v1_5_R3.NBTTagString;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

public class ItemUtils {

	/**
	 * Create a itemStack From a command.
	 * Will set displayName, Lore, Durability, Enchantments, playerSkulls and colored armor.
	 * @param args (The arguments from a command or a stringlist.)
	 * @param md (The material data like item:data)
	 * @param amt (The amount of the item to give.)
	 * @param player (The player to send error messages to.)
	 * @return ItemStack (The itemStack with all data added to it if specified)
	 */
	public static ItemStack createItemFromCmd(String[] args, MaterialData md, int amt, CommandSender sender) {
		ItemStack item = null;
		String name = null;
		String[] lore = null;
		String phead = null;
		int color = -1;
		Enchantment enchant = null;
		int lvl = -1;
		short dura = -1;
		
		item = new ItemStack(md.getItemType(), amt, md.getData());
		
		/* DisplayName (name:<Name>) */
		int nameArg = CmdUtils.getArgIndex(args,"name:", false);
		if (nameArg != 0) {
			String str = args[nameArg];
			String[] splt = str.split(":");
			
			if (splt.length > 1) {
				name = splt[1];
			}
		}
		
		/* Lore (lore:<Lore>) */
		String s = null;
		int loreArg = CmdUtils.getArgIndex(args,"lore:", false);
		if (loreArg != 0) {
			String str = args[loreArg];
			String[] splt = str.split(":");
			
			if (splt.length > 1) {
				s = splt[1];
				
				if (s.contains("|")) {
					String[] temp = s.split("\\|");
					s = temp[0];
					if (temp.length > 0) {
						lore = Arrays.copyOfRange(temp, 0, temp.length);
						
						for (int i = 0; i < lore.length; i++) {
	                        lore[i] = ChatColor.translateAlternateColorCodes('&', lore[i].replace("_", " "));
						}
					}
				}
			}
		}
		
		/* Durability (dur:<amt>) */
		int durArg = CmdUtils.getArgIndex(args,"dur:", false);
		if (durArg != 0) {
			String str = args[durArg];
			String[] splt = str.split(":");
			
			if (splt.length > 1) {
				try {
				 	dura = Short.parseShort(splt[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Invalid durability amount!");
					return null;
				}
				
				item.setDurability((short) (md.getItemType().getMaxDurability() - dura));
			}
		}
		
			
		/* Colored LeatherArmor (color:<#RRGGBB>) */
		int clrArg = CmdUtils.getArgIndex(args,"color:", false);
		if (clrArg != 0) {
			String str = args[clrArg];
			String[] splt = str.split(":");
			
			if (splt.length > 1) {
				if (splt[1].contains("#")) {
                    String[] temp = splt[1].split("#");
                    splt[1] = temp[0];
                    if (temp[1].matches("[0-9A-Fa-f]+")) {
                            color = Integer.parseInt(temp[1], 16);
                    }
				} else {
					if (splt[1].matches("[0-9A-Fa-f]+")) {
                        color = Integer.parseInt(splt[1], 16);
					}
				}
			}
		}
		
		/* PlayerSkulls (player:<player>) */
		int pArg = CmdUtils.getArgIndex(args,"player:", false);
		if (pArg != 0) {
			String str = args[pArg];
			String[] splt = str.split(":");
			
			if (splt.length > 1) {
				phead = splt[1];
			}
		}
		
		/* Enchantments (e:<enchantment>:<power>) */
		for (int i = 0; i < args.length; i++) {
			if (args[i].toLowerCase().startsWith("e:")) {
				String[] splt = args[i].split(":");
				
				if (splt.length > 1) {
					
					enchant = AliasUtils.findEnchanttype(splt[1]);
					
					try {
					 	lvl = Integer.parseInt(splt[2]);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Invalid level.");
						return null;
					}
						
					if (enchant == null) {
						sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Enchantment not found. " + ChatColor.GRAY + splt[1]);
						return null;
					}
					
					item.addUnsafeEnchantment(enchant, lvl);
				}
			}
		}
		
		/* Add meta if specified */
		ItemMeta meta = item.getItemMeta();
		if (name != null) {
			name = name.replace("_", " ");
			meta.setDisplayName(Utils.integrateColor(name));
		}
		if (lore != null) {
			meta.setLore(Arrays.asList(lore));
		}
		if (color >= 0 && meta instanceof LeatherArmorMeta) {
			try {
				((LeatherArmorMeta)meta).setColor(Color.fromRGB(color));
			} catch (IllegalArgumentException e) {
				sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Invalid color!");
				return null;
			}
		}
		item.setItemMeta(meta);
		
		if (phead != null && item.getType() == Material.SKULL_ITEM) {
			if (item.getDurability() == 3) {
				SkullMeta smeta = (SkullMeta) item.getItemMeta();
				smeta.setOwner(phead);
				item.setItemMeta(smeta);
			}
		}
		
		return item;
	}
	
	/**
	 * Create a book ItemStack with the given values.
	 * @param signed (Does the book needs to be signed or not)
	 * @param title (The title to set for the book)
	 * @param author (The author to set for the book)
	 * @param pages (Pages with text for the book)
	 * @return ItemStack (The itemstack with a book with the given values)
	 */
	public static ItemStack createBook(boolean signed, String title, String author, String... pages) {
		return createBook(signed, new Book(title, author, pages));
	}
	
	/**
	 * This will create the ItemStack with the given values from createBoook
	 * @param signed (Does the book needs to be signed or not)
	 * @param book (The book to create a itemstack from)
	 * @return ItemStack (The itemstack with the book with the given Book)
	 */
	public static ItemStack createBook(boolean signed, Book book) {
		ItemStack b = new ItemStack((signed ? Material.WRITTEN_BOOK : Material.BOOK_AND_QUILL), 1);
		net.minecraft.server.v1_5_R3.ItemStack newStack = CraftItemStack.asNMSCopy(b);

		NBTTagCompound newTag = new NBTTagCompound();

		if (book.getTitle() != null)
			newTag.setString("title", book.getTitle());

		if (book.getAuthor() != null)
			newTag.setString("author", book.getAuthor());

		NBTTagList list = new NBTTagList();

		String[] pages = (book.getPages() == null ? new String[] {} : book.getPages());
		for (int i = 0; i < pages.length; i++) {
			list.add(new NBTTagString("page" + i, pages[i]));
		}

		list.setName("pages");
		newTag.set("pages", list);

		newStack.setTag(newTag);
		return CraftItemStack.asBukkitCopy(newStack);
	}
	
	
	/**
	 * Get book data from a itemStack.
	 * @param book (The book to get the data from)
	 * @return Book (The Book data from the given itemStack)
	 */
	public static Book getBook(ItemStack book) {
		net.minecraft.server.v1_5_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(book);

		NBTTagCompound tagCompound = nmsStack.tag;

		if (tagCompound == null)
			return new Book(null, null);

		String title = tagCompound.getString("title");
		String author = tagCompound.getString("author");
		NBTTagList list = tagCompound.getList("pages");

		ArrayList<String> pages = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			NBTTagString s = (NBTTagString) list.get(i);
			pages.add(s.data);
		}

		return new Book(title, author, pages.toArray(new String[pages.size()]));
	}
	
	public static void createFireworksExplosion(Location location, boolean flicker, boolean trail, int type, int[] colors, int[] fadeColors, int flightDuration) {
        // create item
        net.minecraft.server.v1_5_R3.ItemStack item = new net.minecraft.server.v1_5_R3.ItemStack(401, 1, 0);
        
        // get tag
        NBTTagCompound tag = item.tag;
        if (tag == null) {
                tag = new NBTTagCompound();
        }
        
        // create explosion tag
        NBTTagCompound explTag = new NBTTagCompound("Explosion");
        explTag.setByte("Flicker", flicker ? (byte)1 : (byte)0);
        explTag.setByte("Trail", trail ? (byte)1 : (byte)0);
        explTag.setByte("Type", (byte)type);
        explTag.setIntArray("Colors", colors);
        explTag.setIntArray("FadeColors", fadeColors);
        
        // create fireworks tag
        NBTTagCompound fwTag = new NBTTagCompound("Fireworks");
        fwTag.setByte("Flight", (byte)flightDuration);
        NBTTagList explList = new NBTTagList("Explosions");
        explList.add(explTag);
        fwTag.set("Explosions", explList);
        tag.setCompound("Fireworks", fwTag);
        
        // set tag
        item.tag = tag;
        
        // create fireworks entity
        EntityFireworks fireworks = new EntityFireworks(((CraftWorld)location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), item);
        ((CraftWorld)location.getWorld()).getHandle().addEntity(fireworks);
        
        // cause explosion
        if (flightDuration == 0) {
                ((CraftWorld)location.getWorld()).getHandle().broadcastEntityEffect(fireworks, (byte)17);
                fireworks.die();
        }
    }

}
