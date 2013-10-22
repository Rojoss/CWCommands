package net.clashwars.cwcore.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.clashwars.cwcore.CWCore;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LocationUtils {
	
	public static final Set<Integer> HOLLOW_MATERIALS = new HashSet<Integer>();
	private static final HashSet<Byte> TRANSPARENT_MATERIALS = new HashSet<Byte>();
	public final static int RADIUS = 3;
	public final static Vector3D[] VOLUME;
	
	
	public static void tpToTop(CWCore cwc, Player player) {
		double topY = cwc.getServer().getWorld(player.getWorld().getName()).getHighestBlockYAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
		Location loc = new Location(player.getWorld(), player.getLocation().getX(), topY, player.getLocation().getZ());
		loc.setYaw(player.getLocation().getYaw());
		loc.setPitch(player.getLocation().getPitch());
		player.teleport(loc);
	}
	
	
	public static Location getLocation(String str, World world) {
		String[] splt = str.split(":");
		String[] locS = str.split(",");
		if (splt.length > 0) {
			locS = splt[0].split(",");
		}
		int x = Integer.parseInt(locS[0].trim());
		int y = Integer.parseInt(locS[1].trim());
		int z = Integer.parseInt(locS[2].trim());

		return new Location(world, x, y, z);
	}
	
	public static Vector getVector(String str) {
		String[] locS = str.split(",");

		double x = Double.parseDouble(locS[0]);
		double y = Double.parseDouble(locS[1]);
		double z = Double.parseDouble(locS[2]);

		return new Vector(x, y, z);
	}
	
	/* From Essentials */
	public static class Vector3D {
		public Vector3D(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public int x;
		public int y;
		public int z;
	}
	
	static {
		List<Vector3D> pos = new ArrayList<Vector3D>();
		for (int x = -RADIUS; x <= RADIUS; x++) {
			for (int y = -RADIUS; y <= RADIUS; y++) {
				for (int z = -RADIUS; z <= RADIUS; z++) {
					pos.add(new Vector3D(x, y, z));
				}
			}
		}
		Collections.sort(
				pos, new Comparator<Vector3D>() {
			@Override
			public int compare(Vector3D a, Vector3D b) {
				return (a.x * a.x + a.y * a.y + a.z * a.z) - (b.x * b.x + b.y * b.y + b.z * b.z);
			}
		});
		VOLUME = pos.toArray(new Vector3D[0]);
	}
	
	static boolean isBlockAboveAir(final World world, final int x, final int y, final int z) {
		return HOLLOW_MATERIALS.contains(world.getBlockAt(x, y - 1, z).getType().getId());
	}

	public static boolean isBlockUnsafe(final World world, final int x, final int y, final int z) {
		if (isBlockDamaging(world, x, y, z)) {
			return true;
		}
		return isBlockAboveAir(world, x, y, z);
	}

	public static boolean isBlockDamaging(final World world, final int x, final int y, final int z) {
		final Block below = world.getBlockAt(x, y - 1, z);
		if (below.getType() == Material.LAVA || below.getType() == Material.STATIONARY_LAVA) {
			return true;
		}
		if (below.getType() == Material.FIRE) {
			return true;
		}
		if (below.getType() == Material.BED_BLOCK) {
			return true;
		}
		if ((!HOLLOW_MATERIALS.contains(world.getBlockAt(x, y, z).getType().getId())) || (!HOLLOW_MATERIALS.contains(world.getBlockAt(x, y + 1, z).getType().getId()))) {
			return true;
		}
		return false;
	}

	public static Location getSafeDestination(final Location loc) {
		if (loc == null || loc.getWorld() == null) {
			return null;
		}
		final World world = loc.getWorld();
		int x = loc.getBlockX();
		int y = (int)Math.round(loc.getY());
		int z = loc.getBlockZ();
		final int origX = x;
		final int origY = y;
		final int origZ = z;
		while (isBlockAboveAir(world, x, y, z)) {
			y -= 1;
			if (y < 0) {
				y = origY;
				break;
			}
		}
		if (isBlockUnsafe(world, x, y, z)) {
			x = Math.round(loc.getX()) == origX ? x - 1 : x + 1;
			z = Math.round(loc.getZ()) == origZ ? z - 1 : z + 1;
		}
		int i = 0;
		while (isBlockUnsafe(world, x, y, z)) {
			i++;
			if (i >= VOLUME.length) {
				x = origX;
				y = origY + RADIUS;
				z = origZ;
				break;
			}
			x = origX + VOLUME[i].x;
			y = origY + VOLUME[i].y;
			z = origZ + VOLUME[i].z;
		}
		while (isBlockUnsafe(world, x, y, z)) {
			y += 1;
			if (y >= world.getMaxHeight()) {
				x += 1;
				break;
			}
		}
		while (isBlockUnsafe(world, x, y, z)) {
			y -= 1;
			if (y <= 1) {
				x += 1;
				y = world.getHighestBlockYAt(x, z);
				if (x - 48 > loc.getBlockX())
				{
					return loc;
				}
			}
		}
		return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
	}
	
	static {
		HOLLOW_MATERIALS.add(Material.AIR.getId());
		HOLLOW_MATERIALS.add(Material.SAPLING.getId());
		HOLLOW_MATERIALS.add(Material.POWERED_RAIL.getId());
		HOLLOW_MATERIALS.add(Material.DETECTOR_RAIL.getId());
		HOLLOW_MATERIALS.add(Material.LONG_GRASS.getId());
		HOLLOW_MATERIALS.add(Material.DEAD_BUSH.getId());
		HOLLOW_MATERIALS.add(Material.YELLOW_FLOWER.getId());
		HOLLOW_MATERIALS.add(Material.RED_ROSE.getId());
		HOLLOW_MATERIALS.add(Material.BROWN_MUSHROOM.getId());
		HOLLOW_MATERIALS.add(Material.RED_MUSHROOM.getId());
		HOLLOW_MATERIALS.add(Material.TORCH.getId());
		HOLLOW_MATERIALS.add(Material.REDSTONE_WIRE.getId());
		HOLLOW_MATERIALS.add(Material.SEEDS.getId());
		HOLLOW_MATERIALS.add(Material.SIGN_POST.getId());
		HOLLOW_MATERIALS.add(Material.WOODEN_DOOR.getId());
		HOLLOW_MATERIALS.add(Material.LADDER.getId());
		HOLLOW_MATERIALS.add(Material.RAILS.getId());
		HOLLOW_MATERIALS.add(Material.WALL_SIGN.getId());
		HOLLOW_MATERIALS.add(Material.LEVER.getId());
		HOLLOW_MATERIALS.add(Material.STONE_PLATE.getId());
		HOLLOW_MATERIALS.add(Material.IRON_DOOR_BLOCK.getId());
		HOLLOW_MATERIALS.add(Material.WOOD_PLATE.getId());
		HOLLOW_MATERIALS.add(Material.REDSTONE_TORCH_OFF.getId());
		HOLLOW_MATERIALS.add(Material.REDSTONE_TORCH_ON.getId());
		HOLLOW_MATERIALS.add(Material.STONE_BUTTON.getId());
		HOLLOW_MATERIALS.add(Material.SNOW.getId());
		HOLLOW_MATERIALS.add(Material.SUGAR_CANE_BLOCK.getId());
		HOLLOW_MATERIALS.add(Material.DIODE_BLOCK_OFF.getId());
		HOLLOW_MATERIALS.add(Material.DIODE_BLOCK_ON.getId());
		HOLLOW_MATERIALS.add(Material.PUMPKIN_STEM.getId());
		HOLLOW_MATERIALS.add(Material.MELON_STEM.getId());
		HOLLOW_MATERIALS.add(Material.VINE.getId());
		HOLLOW_MATERIALS.add(Material.FENCE_GATE.getId());
		HOLLOW_MATERIALS.add(Material.WATER_LILY.getId());
		HOLLOW_MATERIALS.add(Material.NETHER_WARTS.getId());

		for (Integer integer : HOLLOW_MATERIALS) {
			TRANSPARENT_MATERIALS.add(integer.byteValue());
		}
		TRANSPARENT_MATERIALS.add((byte)Material.WATER.getId());
		TRANSPARENT_MATERIALS.add((byte)Material.STATIONARY_WATER.getId());
	}
	/* End from Essentials */

	public static boolean isPathable(Block block) {
        return isPathable(block.getType());
	}
	
	public static boolean isPathable(Material material) {
        return
                        material == Material.AIR ||
                        material == Material.SAPLING ||
                        material == Material.WATER ||
                        material == Material.STATIONARY_WATER ||
                        material == Material.POWERED_RAIL ||
                        material == Material.DETECTOR_RAIL ||
                        material == Material.LONG_GRASS ||
                        material == Material.DEAD_BUSH ||
                        material == Material.YELLOW_FLOWER ||
                        material == Material.RED_ROSE ||
                        material == Material.BROWN_MUSHROOM ||
                        material == Material.RED_MUSHROOM ||
                        material == Material.TORCH ||
                        material == Material.FIRE ||
                        material == Material.REDSTONE_WIRE ||
                        material == Material.CROPS ||
                        material == Material.SIGN_POST ||
                        material == Material.LADDER ||
                        material == Material.RAILS ||
                        material == Material.WALL_SIGN ||
                        material == Material.LEVER ||
                        material == Material.STONE_PLATE ||
                        material == Material.WOOD_PLATE ||
                        material == Material.REDSTONE_TORCH_OFF ||
                        material == Material.REDSTONE_TORCH_ON ||
                        material == Material.STONE_BUTTON ||
                        material == Material.SNOW ||
                        material == Material.SUGAR_CANE_BLOCK ||
                        material == Material.VINE ||
                        material == Material.WATER_LILY ||
                        material == Material.NETHER_STALK;
	}
}
