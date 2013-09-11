package net.clashwars.cwcore.util;

import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.server.v1_6_R2.Packet63WorldParticles;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Effects {
	
	Random rand = new Random();
	Field[] field = new Field[9];
	
	public Effects() {
		try {
            field[0] = Packet63WorldParticles.class.getDeclaredField("a");
            field[1] = Packet63WorldParticles.class.getDeclaredField("b");
            field[2] = Packet63WorldParticles.class.getDeclaredField("c");
            field[3] = Packet63WorldParticles.class.getDeclaredField("d");
            field[4] = Packet63WorldParticles.class.getDeclaredField("e");
            field[5] = Packet63WorldParticles.class.getDeclaredField("f");
            field[6] = Packet63WorldParticles.class.getDeclaredField("g");
            field[7] = Packet63WorldParticles.class.getDeclaredField("h");
            field[8] = Packet63WorldParticles.class.getDeclaredField("i");
            for (int i = 0; i <= 8; i++) {
            	field[i].setAccessible(true);
            }
    } catch (Exception e) {
            e.printStackTrace();
    }
	}

	public void playSignal(Location loc) {
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
	}
	
	public void playFlames(Location loc) {
		loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
	}
	
	public void playExplosion(Location loc) {
		loc.getWorld().createExplosion(loc, 0F);
	}
	
	public void playLightning(Location loc) {
		loc.getWorld().strikeLightningEffect(loc);
	}
	
	public void playBigSmoke(Location loc) {
		World world =loc.getWorld();
        int lx = loc.getBlockX();
        int ly = loc.getBlockY();
        int lz = loc.getBlockZ();
        Location location;
        
        for (int x = lx-1; x <= lx+1; x++) {
        	for (int y = ly; y <= ly+1; y++) {
        		for (int z = lz-1; z <= lz+1; z++) {
        			for (int i = 0; i <= 8; i+=2) {
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
                for (int i = 0; i <= 8; i+=2) {
                        w.playEffect(loc, Effect.SMOKE, i);
                }
        } else if (r <= 8) {
                w.playEffect(loc, Effect.SMOKE, rand.nextInt(9));
                w.playEffect(loc, Effect.SMOKE, rand.nextInt(9));
        } else {
                w.playEffect(loc, Effect.SMOKE, rand.nextInt(9));
        }
    }
	
	private boolean inRange(int x1, int z1, int x2, int z2, int r) {
        return sq(x1-x2) + sq(z1-z2) < sq(r);
	}

	private int sq(int v) {
        return v*v;
	}

	public void playBlockBreak(Location location, String param) {
		int id = 1;
        try {
            id = Integer.parseInt(param);
        } catch (NumberFormatException e) {                     
        }
        ItemStack item = new ItemStack(id);
        if (Material.getMaterial(id) != null ) {
	        if (item.getType().isBlock()) {
	        	location.getWorld().playEffect(location, Effect.STEP_SOUND, id);
	        }
        }
	}
	
	public void playParticle(Location location, String name, String horSpread, String verSpread, String speed, String count) {
        float hs = Float.parseFloat(horSpread);
        float vs = Float.parseFloat(verSpread);
        float s = Float.parseFloat(speed);
        int c = Integer.parseInt(count);
        String n = getParticle(name);
        
        playParticleEffect(location, n, hs, vs, s, c, 15);
	}
	
	public void playParticleEffect(Location location, String name, float spreadHoriz, float spreadVert, float speed, int count, int radius) {
        Packet63WorldParticles packet = new Packet63WorldParticles();
        try {
        	field[0].set(packet, name);
        	field[1].setFloat(packet, (float)location.getX());
        	field[2].setFloat(packet, (float)location.getY());
        	field[3].setFloat(packet, (float)location.getZ());
        	field[4].setFloat(packet, spreadHoriz);
        	field[5].setFloat(packet, spreadVert);
        	field[6].setFloat(packet, spreadHoriz);
        	field[7].setFloat(packet, speed);
        	field[8].setInt(packet, count);
                
            int rSq = radius * radius;
            
            for (Player player : location.getWorld().getPlayers()) {
                if (player.getLocation().distanceSquared(location) <= rSq) {
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
                } else {
                }
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
	
	private String getParticle(String name) {
		String n = name.toLowerCase();
		
		switch (n) {
			case "hugeexplosion":
			case "hugeexplode":
				n = "hugeexplosion";
				break;
			case "largeexplode":
			case "largeexplosion":
				n = "largeexplode";
				break;
			case "fireworksspark":
			case "firework":
				n = "fireworksSpark";
				break;
			case "bubble":
				n = "bubble";
				break;
			case "suspended":
			case "suspend":
				n = "suspended";
				break;
			case "depthsuspend":
			case "depthsuspended":
				n = "depthsuspend";
				break;
			case "townaura":
			case "aura":
				n = "townaura";
				break;
			case "crit":
				n = "crit";
				break;
			case "magiccrit":
			case "sharpness":
				n = "magicCrit";
				break;
			case "smoke":
				n = "smoke";
				break;
			case "mobspell":
				n = "mobSpell";
				break;
			case "mobspellambient":
				n = "mobSpellAmbient";
				break;
			case "spell":
				n = "spell";
				break;
			case "instantspell":
				n = "instantSpell";
				break;
			case "witchmagic":
			case "witch":
				n = "witchMagic";
				break;
			case "note":
			case "notes":
			case "music":
				n = "note";
				break;
			case "portal":
				n = "portal";
				break;
			case "enchantmenttable":
			case "enchant":
			case "letters":
				n = "enchantmenttable";
				break;
			case "explode":
			case "explosion":
				n = "explode";
				break;
			case "flame":
			case "flames":
				n = "flame";
				break;
			case "lava":
				n = "lava";
				break;
			case "footstep":
			case "steps":
				n = "footstep";
				break;
			case "splash":
			case "potion":
				n = "splash";
				break;
			case "largesmoke":
			case "bigsmoke":
				n = "largesmoke";
				break;
			case "cloud":
				n = "cloud";
				break;
			case "reddust":
			case "dust":
				n = "reddust";
				break;
			case "snowballpoof":
			case "snowball":
				n = "snowballpoof";
				break;
			case "dripwater":
			case "waterd":
				n = "dripWater";
				break;
			case "driplava":
			case "lavad":
				n = "dripLava";
				break;
			case "snowshovel":
			case "snowbreak":
				n = "snowshovel";
				break;
			case "slime":
				n = "slime";
				break;
			case "heart":
			case "hearts":
				n = "heart";
				break;
			case "angryvillager":
			case "angry":
				n = "angryVillager";
				break;
			case "happyvillager":
			case "happy":
				n = "happyVillager";
				break;
		}
		return n;
	}
}
