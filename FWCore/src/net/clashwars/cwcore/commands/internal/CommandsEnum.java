package net.clashwars.cwcore.commands.internal;

import net.clashwars.cwcore.commands.*;

public enum CommandsEnum {
	BROADCAST(BroadcastCmd.class,"broadcast","bc","bcmsg"),
	GAMEMODE(GamemodeCmd.class,"gamemode","gm"),
	HEAL(HealCmd.class,"heal","hp","health"),
	INVSEE(InvseeCmd.class,"invsee","invedit"),
	ITEM(ItemCmd.class,"item","i"),
	GIVE(GiveCmd.class,"give"),
	NICK(NickCmd.class,"nick","nickname"),
	TAG(TagCmd.class,"tag","nametag"),
	REALNAME(RealnameCmd.class,"realname","rname"),
	REMOVEPOTS(RemovepotsCmd.class,"removepots","removepotions","rpots"),
	TIME(TimeCmd.class,"time","day","night"),
	CLEARINV(ClearinvCmd.class,"clearinv","ci","clearinventory"),
	MORE(MoreCmd.class,"more","stack"),
	GUI(GuiCmd.class,"gui"),
	TOP(TopCmd.class,"top"),
	SUICIDE(SuicideCmd.class,"suicide","killme"),
	KILL(KillCmd.class,"kill","slay"),
	GOD(GodCmd.class,"god","godmode"),
	FLY(FlyCmd.class,"fly","flymode"),
	VANISH(VanishCmd.class,"vanish","v","invis"),
	SPEED(SpeedCmd.class,"speed"),
	SPAWNER(SpawnerCmd.class,"spawner","mobspawner","monsterspawner","changespawner"),
	SPAWNMOB(SpawnmobCmd.class,"spawnmob", "spawnm", "mobspawn", "sm", "mspawn"),
	POWERTOOL(PowertoolCmd.class,"powertool", "pt", "macro", "bind"),
	TELEPORT(TeleportCmd.class,"teleport", "tp"),
	TELEPORTHERE(TeleporthereCmd.class,"teleporthere", "tphere"),
	TELEPORTPOS(TeleportposCmd.class,"teleportpos", "tppos", "tploc"),
	SUDO(SudoCmd.class,"sudo"),
	WHOIS(WhoisCmd.class,"whois"),
	SETWARP(WarpsetCmd.class,"setwarp"),
	DELWARP(WarpdelCmd.class,"delwarp"),
	WARP(WarpCmd.class,"warp"),
	WARPS(WarpsCmd.class,"warps"),
	EXP(ExpCmd.class,"exp"),
	FIREWORK(FireworkCmd.class,"firework" , "fw"),
	SETSPAWN(SetspawnCmd.class,"setspawn"),
	PARTICLE(ParticleCmd.class,"particle" , "particles"),
	SPAWN(SpawnCmd.class,"spawn"),
	ITEMINFO(IteminfoCmd.class,"iteminfo" , "ii", "itemi"),
	EFFECT(EffectCmd.class,"effect" , "potioneffect", "potion"),
	SOUND(SoundCmd.class,"sound" , "playsound"),
	CMDS(CmdsCmd.class,"cmds" , "commands", "command", "cmd")
	;

	private String[]						aliases;
	private Class<? extends CommandClass>	clazz;

	private CommandsEnum(Class<? extends CommandClass> clazz, String... aliases) {
		this.clazz = clazz;
		this.aliases = aliases;
	}

	public Class<? extends CommandClass> getCommandClass() {
		return clazz;
	}

	public String[] getAliases() {
		return aliases;
	}

	public static Class<? extends CommandClass> fromString(String str) {
		for (CommandsEnum ce : CommandsEnum.values()) {
			for (String alias : ce.getAliases()) {
				if (alias.equalsIgnoreCase(str)) {
					return ce.getCommandClass();
				}
			}
		}

		return null;
	}
}
