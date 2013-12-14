package net.clashwars.cwcore.components;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.clashwars.cwcore.constants.Effects;
import net.clashwars.cwcore.util.LocationUtils;
import net.clashwars.cwcore.util.Utils;
import net.minecraft.server.v1_7_R1.PacketPlayOutWorldParticles;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomEffect {

	Random	rand	= new Random();
	Field[]	field	= new Field[9];

	public CustomEffect() {
		try {
			field[0] = PacketPlayOutWorldParticles.class.getDeclaredField("a");
			field[1] = PacketPlayOutWorldParticles.class.getDeclaredField("b");
			field[2] = PacketPlayOutWorldParticles.class.getDeclaredField("c");
			field[3] = PacketPlayOutWorldParticles.class.getDeclaredField("d");
			field[4] = PacketPlayOutWorldParticles.class.getDeclaredField("e");
			field[5] = PacketPlayOutWorldParticles.class.getDeclaredField("f");
			field[6] = PacketPlayOutWorldParticles.class.getDeclaredField("g");
			field[7] = PacketPlayOutWorldParticles.class.getDeclaredField("h");
			field[8] = PacketPlayOutWorldParticles.class.getDeclaredField("i");
			for (int i = 0; i <= 8; i++) {
				field[i].setAccessible(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getParticle(String name) {
		for (Effects eff : Effects.values()) {
			for (String alias : eff.getAliases()) {
				if (alias.equalsIgnoreCase(name)) {
					return eff.name();
				}
			}
		}
		if (Utils.getInt(name) >= 0) {
			int id = Utils.getInt(name);
			for (Effects eff : Effects.values()) {
				if (eff.getID() == id) {
					return eff.name();
				}
			}
		}
		for (Effects eff : Effects.values()) {
			for (String alias : eff.getAliases()) {
				if (alias.startsWith(name.length() > 2 ? name.substring(0, 3) : name.substring(0, 1))) {
					return eff.name();
				}
			}
		}
		return null;
	}

	public void playSignal(Location loc) {
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
	}

	public void playFlames(Location loc) {
		loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
	}

	public void playExplosion(Location loc) {
		loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 1F, false, false);
	}

	public void playLightning(Location loc) {
		loc.getWorld().strikeLightningEffect(loc);
	}

	public void playBigSmoke(Location loc) {
		World world = loc.getWorld();
		int lx = loc.getBlockX();
		int ly = loc.getBlockY();
		int lz = loc.getBlockZ();
		Location location;

		for (int x = lx - 1; x <= lx + 1; x++) {
			for (int y = ly; y <= ly + 1; y++) {
				for (int z = lz - 1; z <= lz + 1; z++) {
					for (int i = 0; i <= 8; i += 2) {
						location = new Location(world, x, y, z);
						world.playEffect(location, Effect.SMOKE, i);
					}
				}
			}
		}
	}

	public void playCloud(Location location, String param) {
		int radius = 3;
		try {
			radius = Integer.parseInt(param);
		} catch (NumberFormatException e) {
			radius = 3;
		}
		if (radius > 10) {
			radius = 10;
		}

		World w = location.getWorld();
		int cx = location.getBlockX();
		int cy = location.getBlockY();
		int cz = location.getBlockZ();

		Block b;
		for (int x = cx - radius; x <= cx + radius; x++) {
			for (int z = cz - radius; z <= cz + radius; z++) {
				if (inRange(x, z, cx, cz, radius)) {
					b = w.getBlockAt(x, cy, z);
					if (LocationUtils.isPathable(b)) {
						smoke(w, b, radius);
					} else {
						b = b.getRelative(0, -1, 0);
						if (LocationUtils.isPathable(b)) {
							smoke(w, b, radius);
						} else {
							b = b.getRelative(0, 2, 0);
							if (LocationUtils.isPathable(b)) {
								smoke(w, b, radius);
							}
						}
					}
				}
			}
		}
	}

	public void playSmoke(Location location, String param) {
		int dir = 4;
		if (param != null && !param.isEmpty()) {
			try {
				dir = Integer.parseInt(param);
			} catch (NumberFormatException e) {
			}
		}
		location.getWorld().playEffect(location, Effect.SMOKE, dir);
	}

	public void playSplash(Location location, String param) {
		int pot = 0;
		if (param != null && !param.isEmpty()) {
			try {
				pot = Integer.parseInt(param);
			} catch (NumberFormatException e) {
			}
		}
		location.getWorld().playEffect(location, Effect.POTION_BREAK, pot);
	}

	private void smoke(World w, Block b, int r) {
		Location loc = b.getLocation();
		if (r <= 5) {
			for (int i = 0; i <= 8; i += 2) {
				w.playEffect(loc, Effect.SMOKE, i);
			}
		} else if (r <= 8) {
			w.playEffect(loc, Effect.SMOKE, rand.nextInt(9));
			w.playEffect(loc, Effect.SMOKE, rand.nextInt(9));
		} else {
			w.playEffect(loc, Effect.SMOKE, rand.nextInt(9));
		}
	}

	public void playBlockBreak(Location location, String param) {
		int id = 1;
		try {
			id = Integer.parseInt(param);
		} catch (NumberFormatException e) {
		}
		ItemStack item = new ItemStack(id);
		if (Material.getMaterial(id) != null) {
			if (item.getType().isBlock()) {
				location.getWorld().playEffect(location, Effect.STEP_SOUND, id);
			}
		}
	}

	private boolean inRange(int x1, int z1, int x2, int z2, int r) {
		return sq(x1 - x2) + sq(z1 - z2) < sq(r);
	}

	private int sq(int v) {
		return v * v;
	}

	public void playParticle(Location location, String name, String horSpread, String verSpread, String speed, String count) {
		float hs = Float.parseFloat(horSpread);
		float vs = Float.parseFloat(verSpread);
		float s = Float.parseFloat(speed);
		int c = Integer.parseInt(count);
		String n = getParticle(name);

		playParticleEffect(location, n, hs, vs, s, c);
	}

	public void playParticleEffect(Location location, String name, float spreadHoriz, float spreadVert, float speed, int count, Player... additions) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();
		try {
			field[0].set(packet, name);
			field[1].setFloat(packet, (float) location.getX());
			field[2].setFloat(packet, (float) location.getY());
			field[3].setFloat(packet, (float) location.getZ());
			field[4].setFloat(packet, spreadHoriz);
			field[5].setFloat(packet, spreadVert);
			field[6].setFloat(packet, spreadHoriz);
			field[7].setFloat(packet, speed);
			field[8].setInt(packet, count);

			int rSq = 15 * 15;
			Set<Player> exc = new HashSet<Player>(Arrays.asList(additions));
			
			for (Player player : location.getWorld().getPlayers()) {
				if (player.getLocation().distanceSquared(location) <= rSq || exc.contains(player)) {
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
				} else {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
