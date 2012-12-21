package com.pqqqqq.fwcore.bukkit;

import net.minecraft.server.v1_4_6.TileEntityChest;

import org.bukkit.craftbukkit.v1_4_6.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.pqqqqq.fwcore.DungeonChest;

public class DungeonChestInventory extends TileEntityChest {
	private DungeonChest	dc;
	private boolean			edited	= false;

	public DungeonChestInventory(DungeonChest dc) {
		super();
		this.dc = dc;
		restore();
	}

	public DungeonChest getDungeonChest() {
		return dc;
	}

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
	}

	public void restore() {
		ItemStack[] inv = dc.getInventory();

		for (int i = 0; i < getSize(); i++) {
			ItemStack it = inv[i];

			setItem(i, (it == null ? null : CraftItemStack.asNMSCopy(it.clone())));
		}
	}
}
