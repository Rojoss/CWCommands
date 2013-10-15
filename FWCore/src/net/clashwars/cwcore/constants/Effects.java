package net.clashwars.cwcore.constants;


public enum Effects {
	hugeexplosion(0, "hugeexplosion","hugeexplode"),
	largeexplode(1, "largeexplode","largeexplosion"),
	fireworksSpark(2, "fireworksSpark","firework"),
	bubble(3, "bubble","bub"),
	suspended(4, "suspended","suspend"),
	depthsuspend(5, "depthsuspend","depthsuspended"),
	townaura(6, "townaura","aura"),
	crit(7, "crit","critical"),
	magicCrit(8, "magicCrit","sharpness","mcrit"),
	smoke(9, "smoke"),
	mobSpell(10, "mobSpell","mob","mobSpell"),
	mobSpellAmbient(11, "mobSpellAmbient","mobAmbient","mobPotionAmbient"),
	spell(12, "spell","potion"),
	instantSpell(13, "instantSpell","instantPotion"),
	witchMagic(14, "witchMagic","witch"),
	note(15, "note","notes","music","pling"),
	portal(16, "portal","netherPortal"),
	enchantmenttable(17, "enchantmenttable","enchant","etable","enchanttable","letters","books","letter"),
	explode(18, "explode","explosion"),
	flame(19, "flame","flames"),
	lava(20, "lava"),
	footstep(21, "footstep","steps","step"),
	splash(22, "splash"),
	largesmoke(23, "largesmoke","bigsmoke"),
	cloud(24, "cloud","smokeCloud"),
	reddust(25, "reddust","dust"),
	snowballpoof(26, "snowballpoof","snowball"),
	dripWater(27, "dripWater","waterdrip"),
	dripLava(28, "dripLava","lavadrip"),
	snowshovel(29, "snowshovel","snowbreak","breaksnow"),
	slime(30, "slime"),
	heart(31, "heart","hearts"),
	angryVillager(32, "angryVillager","angry"),
	happyVillager(33, "happyVillager","happy")
	;
	
	
	
	private String[] aliases;
	private int id;

	private Effects(int id, String... aliases) {
		this.aliases = aliases;
	}
	
	public int getID() {
		return id;
	}
	
	public String[] getAliases() {
		return aliases;
	}
}
