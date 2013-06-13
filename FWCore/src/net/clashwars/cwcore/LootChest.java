package net.clashwars.cwcore;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class LootChest {
	private Block		chest;
	private Chest		c;
	private ItemStack[]	inventory	= null;
	private Set<String>	accessed	= new HashSet<String>();

	public LootChest(Block chest) {
		this.chest = chest;

		findChestInstance();
		resetInventory(c != null ? c.getInventory().getContents() : new ItemStack[0]);
	}

	public void resetInventory(ItemStack[] iss) {
		if (c != null) {
			inventory = new ItemStack[iss.length];

			for (int i = 0; i < inventory.length; i++) {
				ItemStack is = iss[i];
				inventory[i] = is == null ? null : CraftItemStack.asCraftCopy(iss[i].clone());
			}
		}
	}

	public void findChestInstance() {
		this.c = chest.getState() instanceof Chest ? (Chest) chest.getState() : null;
	}

	public Block getChestBlock() {
		return chest;
	}

	public Chest getChest() {
		return c;
	}

	public void setChest(Block chest) {
		this.chest = chest;
	}

	public ItemStack[] getInventory() {
		return inventory;
	}

	public void setInventory(ItemStack[] inventory) {
		this.inventory = inventory;
		resetInventory(inventory);
	}

	public Set<String> getAccessed() {
		return accessed;
	}

	public void setAcccessed(Set<String> accessed) {
		this.accessed = accessed;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LootChest) {
			LootChest dc = (LootChest) obj;
			return dc.getChestBlock().equals(getChestBlock());
		}
		return false;
	}
}