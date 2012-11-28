package com.pqqqqq.fwcore.concurrent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.pqqqqq.fwcore.Cuboid;
import com.pqqqqq.fwcore.FWCore;

public class ChestRemoval implements Runnable {
	private FWCore								fwc;
	private HashMap<Player, ArrayList<Block>>	changed	= new HashMap<Player, ArrayList<Block>>();

	public ChestRemoval(FWCore fwc) {
		this.fwc = fwc;
	}

	@Override
	public void run() {
		ArrayList<Player> players = new ArrayList<Player>();
		players.addAll(Arrays.asList(fwc.getPlugin().getServer().getOnlinePlayers()));

		try {
			for (Player player : players) {
				Cuboid cuboid = new Cuboid(player, 5);
				ArrayList<Block> c = changed.containsKey(player) ? changed.get(player) : new ArrayList<Block>();
				World world = player.getWorld();

				for (Block chest : cuboid.getBlocks(Material.CHEST)) {
					if (c.contains(chest))
						continue;

					Chunk chunk = chest.getLocation().getChunk();
					world.refreshChunk(chunk.getX(), chunk.getZ());

					Thread.sleep(20);
					
					player.sendBlockChange(chest.getLocation(), Material.STONE, (byte) 0);
					c.add(chest);
				}

				changed.put(player, c);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
