package net.clashwars.cwcore.util;

import java.util.ArrayList;

import net.clashwars.cwcore.Book;
import net.minecraft.server.v1_5_R3.NBTTagCompound;
import net.minecraft.server.v1_5_R3.NBTTagList;
import net.minecraft.server.v1_5_R3.NBTTagString;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;


public class Utils {

	/* Book Utils */
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

	public static ItemStack createBook(boolean signed, String title, String author, String... pages) {
		return createBook(signed, new Book(title, author, pages));
	}

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
	
	/* String Utils */
	public static String implode(String[] arr, String glue, int start, int end) {
		String ret = "";

		if (arr == null || arr.length <= 0)
			return ret;

		for (int i = start; i <= end && i < arr.length; i++) {
			ret += arr[i] + glue;
		}

		return ret.substring(0, ret.length() - glue.length());
	}

	public static String implode(String[] arr, String glue, int start) {
		return implode(arr, glue, start, arr.length - 1);
	}

	public static String implode(String[] arr, String glue) {
		return implode(arr, glue, 0);
	}

	public static String implode(String[] arr) {
		return implode(arr, " ");
	}

	public static String integrateColor(String str) {
		for (ChatColor c : ChatColor.values()) {
			str = str.replaceAll("&" + c.getChar() + "|&" + Character.toUpperCase(c.getChar()), c.toString());
		}
		return str;
	}
	
	/* CommandClass Utils */
	public static boolean hasModifier(String[] args, String mod) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].toLowerCase().startsWith(mod)) {
				return true;
			}
		}
		return false;
	}
	
	public static String[] modifiedArgs(String[] args, String mod) {
		int i;
        int sloc = -1;
        String temp;
        for (i = 0; i < args.length; i++) {
                if (args[i].toLowerCase().startsWith(mod)) {
                        sloc = i;
                }
        }
        if (sloc != -1) {
                for (i = sloc; i < args.length -1; i++) {
                        temp = args[i];
                    args[i] = args[i + 1];
                    args[i + 1] = temp;
                }
        }
       
        String[] args2 = new String[args.length - 1];
        for (i = 0; i < args2.length; i++) {
                args2[i] = args[i];
        }
        return args2;
	}
}
