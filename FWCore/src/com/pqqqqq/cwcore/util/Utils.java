package com.pqqqqq.cwcore.util;

import java.util.ArrayList;

import net.minecraft.server.v1_5_R3.NBTTagCompound;
import net.minecraft.server.v1_5_R3.NBTTagList;
import net.minecraft.server.v1_5_R3.NBTTagString;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.pqqqqq.cwcore.Book;

public class Utils {

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
}
