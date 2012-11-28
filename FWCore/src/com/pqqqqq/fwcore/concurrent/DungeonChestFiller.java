package com.pqqqqq.fwcore.concurrent;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.pqqqqq.fwcore.DungeonChest;
import com.pqqqqq.fwcore.FWCore;

public class DungeonChestFiller implements Runnable {
	private FWCore	fwc;

	public DungeonChestFiller(FWCore fwc) {
		this.fwc = fwc;
	}

	public void run() {
		ArrayList<DungeonChest> chests = new ArrayList<DungeonChest>();
		chests.addAll(fwc.getDungeonChests());

		for (DungeonChest chest : chests) {
			long timeMillis = chest.getRefillTime() * 1000;
			long diff = (timeMillis + chest.getLastRefill()) - System.currentTimeMillis();

			if (diff <= 0) {
				chest.findChestInstance();

				Chest c = chest.getChest();
				World world = c.getWorld();

				ArrayList<Player> opened = new ArrayList<Player>();

				for (HumanEntity he : c.getInventory().getViewers()) {
					Player player = (Player) he;

					if (player.getOpenInventory().getTopInventory().getHolder().equals(c))
						opened.add(player);
				}

				if (c != null && opened.isEmpty()) {
					c.getInventory().setContents(chest.getInventory());
					chest.resetInventory(c.getInventory().getContents());
					world.playEffect(c.getLocation().add(new Vector(0, 1, 0)), Effect.SMOKE, BlockFace.UP);
					world.playEffect(c.getLocation(), Effect.MOBSPAWNER_FLAMES, null);

					chest.setLastRefill(System.currentTimeMillis());
				}
			}
		}
	}
}
