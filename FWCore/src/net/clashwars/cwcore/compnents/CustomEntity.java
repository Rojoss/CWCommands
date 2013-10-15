package net.clashwars.cwcore.compnents;

import java.util.List;

import net.clashwars.cwcore.util.ItemUtils;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public class CustomEntity {
	
	Entity e = null;
	Creature ec = null;

	public CustomEntity(Entity entity) {
		e = entity;
		if (e instanceof Creature) {
			ec = (Creature) e;
		}
	}
	
	public void setName(String arg) {
		ec.setCustomName(Utils.integrateColor(arg));
	}
	
	public void setHP(int arg) {
		ec.setMaxHealth(Float.parseFloat(String.valueOf(arg)));
		ec.setHealth(arg);
	}
	
	public void setSize(int arg) {
		if (e instanceof Slime) {
			((Slime)e).setSize(arg);
		}
	}
	
	public void setColor(String arg) {
		if (e instanceof Colorable) {
			 try {
                 ((Sheep)e).setColor(DyeColor.valueOf(arg.toUpperCase()));
			 } catch (Exception e) {
			 }
		}
		if (e instanceof Wolf) {
			 try {
                ((Wolf)e).setCollarColor(DyeColor.valueOf(arg.toUpperCase()));
			 } catch (Exception e) {
			 }
		}
		if (e instanceof Horse) {
			switch (arg.toLowerCase()) {
				case "black":
					((Horse)e).setColor(Color.BLACK);
					break;
				case "brown":
					((Horse)e).setColor(Color.BROWN);
					break;
				case "chestnut":
				case "nut":
					((Horse)e).setColor(Color.CHESTNUT);
					break;
				case "cream":
					((Horse)e).setColor(Color.CREAMY);
					break;
				case "darkbrown":
				case "dbrown":
					((Horse)e).setColor(Color.DARK_BROWN);
					break;
				case "gray":
					((Horse)e).setColor(Color.GRAY);
					break;
				case "white":
					((Horse)e).setColor(Color.WHITE);
					break;
			}
		}
	}
	
	public void setJob(String arg) {
		if (e instanceof Villager) {
			switch (arg.toLowerCase()) {
				case "librarian":
				case "lib":
					((Villager)e).setProfession(Profession.LIBRARIAN);
					break;
				case "priest":
					((Villager)e).setProfession(Profession.PRIEST);
					break;
				case "blacksmith":
				case "smith":
					((Villager)e).setProfession(Profession.BLACKSMITH);
					break;
				case "butcher":
				case "but":
					((Villager)e).setProfession(Profession.BUTCHER);
					break;
				case "farmer":
					((Villager)e).setProfession(Profession.FARMER);
					break;
			}
		}
	}
	
	public void setPower(double arg) {
		if (e instanceof Horse) {
			((Horse)e).setJumpStrength(arg);
		}
	}
	
	public void setStyle(String arg) {
		if (e instanceof Horse) {
			switch (arg.toLowerCase()) {
				case "blackdots":
				case "blackdot":
					((Horse)e).setStyle(Style.BLACK_DOTS);
					break;
				case "whitedots":
				case "whitedot":
					((Horse)e).setStyle(Style.WHITE_DOTS);
					break;
				case "white":
					((Horse)e).setStyle(Style.WHITE);
					break;
				case "whitefield":
					((Horse)e).setStyle(Style.WHITEFIELD);
					break;
				case "none":
					((Horse)e).setStyle(Style.NONE);
					break;
			}
		}
	}
	
	//BROKEN
	public void setType(String arg) {
		if (e instanceof Horse) {
			switch (arg.toLowerCase()) {
				case "mule":
					((Horse)e).setVariant(Variant.MULE);
					break;
				case "donkey":
					((Horse)e).setVariant(Variant.DONKEY);
					break;
				case "undead":
				case "zombie":
					((Horse)e).setVariant(Variant.UNDEAD_HORSE);
					break;
				case "skeleton":
				case "ghost":
					((Horse)e).setVariant(Variant.SKELETON_HORSE);
					break;
			}
		}
	}
	
	public void setHand(String arg) {
		if (ItemUtils.getItem(arg) != null) {
			ec.getEquipment().setItemInHand(ItemUtils.getItem(arg).getItem());
			if (e instanceof Enderman) {
				((Enderman)e).setCarriedMaterial(ItemUtils.getItem(arg).getItem().getData());
			}
		}
	}
	
	public void setHelmet(String arg) {
		if (ItemUtils.getItem(arg) != null) {
			ec.getEquipment().setHelmet(ItemUtils.getItem(arg).getItem());
		}
	}
	
	public void setChest(String arg) {
		if (ItemUtils.getItem(arg) != null) {
			ec.getEquipment().setChestplate(ItemUtils.getItem(arg).getItem());
		}
	}
	
	public void setLeg(String arg) {
		if (ItemUtils.getItem(arg) != null) {
			ec.getEquipment().setLeggings(ItemUtils.getItem(arg).getItem());
		}
	}
	
	public void setBoot(String arg) {
		if (ItemUtils.getItem(arg) != null) {
			ec.getEquipment().setBoots(ItemUtils.getItem(arg).getItem());
		}
	}
	
	public void setHorseArmor(String arg) {
		if (e instanceof Horse) {
			if (arg.toLowerCase().startsWith("ir")) {
				((Horse)e).getInventory().setArmor(new ItemStack(Material.IRON_BARDING,1));
			}
			if (arg.toLowerCase().startsWith("go")) {
				((Horse)e).getInventory().setArmor(new ItemStack(Material.GOLD_BARDING,1));
			}
			if (arg.toLowerCase().startsWith("di")) {
				((Horse)e).getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING,1));
			}
		}
	}
	
	public void setEffects(String arg) {
		List<PotionEffect> effects = ItemUtils.getPotionEffects(arg);
		
		if (effects != null && !effects.contains(null)) {
			for (PotionEffect effect : effects) {
				ec.addPotionEffect(effect, true);
			}
		}
	}
	
	public void setDisplay() {
		ec.setCustomNameVisible(true);
	}
	
	public void setBaby() {
		if (e instanceof Ageable) {
			((Ageable)e).setBaby();
		}
		if (e instanceof Zombie) {
			((Zombie)e).setBaby(true);
		}
		if (e instanceof PigZombie) {
			((PigZombie)e).setBaby(true);
		}
	}
	
	public void setTamed(Player p) {
		if (e instanceof Tameable) {
			((Tameable)e).setTamed(true);
			if (p != null) {
				((Tameable)e).setOwner(p);
			}
		}
	}
	
	public void setAngry() {
		if (e instanceof Wolf) {
			((Wolf)e).setAngry(true);
		}
		if (e instanceof PigZombie) {
			((PigZombie)e).setAngry(true);
		}
	}
	
	public void setPowered() {
		if (e instanceof Creeper) {
			((Creeper)e).setPowered(true);
		}
	}
	
	public void setMounted() {
		if (e instanceof Pig) {
			((Pig)e).setSaddle(true);
		}
		if (e instanceof Horse) {
			((Horse)e).getInventory().setSaddle(new ItemStack(Material.SADDLE,1));
		}
	}
	
	public void setChest() {
		if (e instanceof Horse) {
			if (((Horse)e).getVariant() == Variant.DONKEY || ((Horse)e).getVariant() == Variant.MULE) {
				((Horse)e).setCarryingChest(true);
			}
		}
	}

	public void switchType() {
		if (e instanceof Zombie) {
			((Zombie)e).setVillager(true);
		}
		if (e instanceof Skeleton) {
			((Skeleton)e).setSkeletonType(SkeletonType.WITHER);
		}
	}
	
	public void fixSkeleton() {
		if (e instanceof Skeleton) {
			if (((Skeleton)e).getSkeletonType() == SkeletonType.WITHER){
				((Skeleton)e).getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD));
			} else {
				((Skeleton)e).getEquipment().setItemInHand(new ItemStack(Material.BOW));
			}
		}
	}
	
}
