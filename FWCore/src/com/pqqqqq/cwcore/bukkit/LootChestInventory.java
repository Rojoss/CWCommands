package com.pqqqqq.cwcore.bukkit;

import net.minecraft.server.v1_5_R3.TileEntityChest;

import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.pqqqqq.cwcore.LootChest;

public class LootChestInventory extends TileEntityChest {
	private LootChest	lc;
	private boolean			edited	= false;

	public LootChestInventory(LootChest lc) {
		super();
		this.lc = lc;
		restore();
	}

	public LootChest getLootChest() {
		return lc;
	}

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
	}

	public void restore() {
		ItemStack[] inv = lc.getInventory();

		for (int i = 0; i < getSize(); i++) {
			ItemStack it = inv[i];

			setItem(i, (it == null ? null : CraftItemStack.asNMSCopy(it.clone())));
		}
	}
}
