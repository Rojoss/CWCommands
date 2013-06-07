package com.pqqqqq.cwcore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Cuboid {
	private Block	b1;
	private Block	b2;

	public Cuboid(Block b1, Block b2) {
		this.b1 = b1;
		this.b2 = b2;
	}

	public Cuboid(Player player, int radius) {
		Location loc = player.getLocation();
		World world = loc.getWorld();

		double b1XLoc = loc.getX() - radius;
		double b1YLoc = loc.getY() - radius;
		double b1ZLoc = loc.getZ() - radius;

		double b2XLoc = loc.getX() + radius;
		double b2YLoc = loc.getY() + radius;
		double b2ZLoc = loc.getZ() + radius;

		this.b1 = new Location(world, b1XLoc, b1YLoc, b1ZLoc).getBlock();
		this.b2 = new Location(world, b2XLoc, b2YLoc, b2ZLoc).getBlock();
	}

	public Block getBlock1() {
		return b1;
	}

	public Block getBlock2() {
		return b2;
	}

	public List<Block> getBlocks() {
		ArrayList<Block> blocks = new ArrayList<Block>();
		World world = b1.getWorld();

		for (int x = b1.getX(); x <= b2.getX(); x++) {
			for (int y = b1.getY(); y <= b2.getY(); y++) {
				for (int z = b1.getZ(); z <= b2.getZ(); z++) {
					blocks.add(new Location(world, x, y, z).getBlock());
				}
			}
		}
		return blocks;
	}

	public List<Block> getBlocks(Material material) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		World world = b1.getWorld();

		for (int x = b1.getX(); x <= b2.getX(); x++) {
			for (int y = b1.getY(); y <= b2.getY(); y++) {
				for (int z = b1.getZ(); z <= b2.getZ(); z++) {
					Block b = new Location(world, x, y, z).getBlock();

					if (b.getType() == material)
						blocks.add(b);
				}
			}
		}
		return blocks;
	}
}
