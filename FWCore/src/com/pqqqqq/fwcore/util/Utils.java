package com.pqqqqq.fwcore.util;

import java.util.ArrayList;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.pqqqqq.fwcore.Book;

public class Utils {

	public static ItemStack createBook(boolean signed, Book book) {
		ItemStack b = new ItemStack((signed ? Material.WRITTEN_BOOK : Material.BOOK_AND_QUILL), 1);
		net.minecraft.server.ItemStack newStack = new CraftItemStack(b).getHandle();

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
		return new CraftItemStack(newStack);
	}

	public static ItemStack createBook(boolean signed, String title, String author, String... pages) {
		return createBook(signed, new Book(title, author, pages));
	}

	public static Book getBook(ItemStack book) {
		net.minecraft.server.ItemStack nmsStack = CraftItemStack.createNMSItemStack(book);

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