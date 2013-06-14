package net.clashwars.cwcore.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

public class InvUtils {

	
	/**
	 * Clear items from a player his inventory
	 * Example: clearInventorySlots(player,0,9) will clear the hotbar.
	 * @param player The player to clear the items from
	 * @param firstSlot (The slot index where to start clearing from)
	 * @param lastSlot (The slot index where to start clearing from)
	 * @param md (The material data to remove if null it won't check for any material.
	 * @param amt (The amount of the material to remove if -1 it will remove all of the given material)
	 * @return void
	 */
	public static void clearInventorySlots(Player player, int firstSlot, int lastSlot, MaterialData md, int amt) {
		PlayerInventory inventory = player.getInventory();
		int left = amt;
		
		if (lastSlot == -1)
			lastSlot = inventory.getSize();
		
		if (md == null) {
			for (int i = firstSlot; i < lastSlot; i++) {
				inventory.clear(i);
			}
		} else {
			for (int i = firstSlot; i < lastSlot; i++) {
				if (inventory.getItem(i) != null) {
					if (inventory.getItem(i).getType() == md.getItemType()) {
						if (left != -1 && left != 0) {
							if (inventory.getItem(i).getAmount() <= left) {
								left -= inventory.getItem(i).getAmount();
								inventory.clear(i);
							} else if (inventory.getItem(i).getAmount() > left) {
								inventory.getItem(i).setAmount(inventory.getItem(i).getAmount() - left);
								left = 0;
							}
						} else if (left == 0) {
							return;
						} else {
							inventory.clear(i);
						}
					}
				}
			}
		}
	}
}
