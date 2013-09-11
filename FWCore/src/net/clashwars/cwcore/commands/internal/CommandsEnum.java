package net.clashwars.cwcore.commands.internal;

import net.clashwars.cwcore.commands.BroadcastCmd;
import net.clashwars.cwcore.commands.ClearinvCmd;
import net.clashwars.cwcore.commands.ExpCmd;
import net.clashwars.cwcore.commands.FireworkCmd;
import net.clashwars.cwcore.commands.FlyCmd;
import net.clashwars.cwcore.commands.FreezeCmd;
import net.clashwars.cwcore.commands.GamemodeCmd;
import net.clashwars.cwcore.commands.GiveCmd;
import net.clashwars.cwcore.commands.GodCmd;
import net.clashwars.cwcore.commands.GuiCmd;
import net.clashwars.cwcore.commands.HatCmd;
import net.clashwars.cwcore.commands.HealCmd;
import net.clashwars.cwcore.commands.InvseeCmd;
import net.clashwars.cwcore.commands.ItemCmd;
import net.clashwars.cwcore.commands.IteminfoCmd;
import net.clashwars.cwcore.commands.KillCmd;
import net.clashwars.cwcore.commands.MoreCmd;
import net.clashwars.cwcore.commands.NickCmd;
import net.clashwars.cwcore.commands.ParticleCmd;
import net.clashwars.cwcore.commands.PowertoolCmd;
import net.clashwars.cwcore.commands.RealnameCmd;
import net.clashwars.cwcore.commands.RemovepotsCmd;
import net.clashwars.cwcore.commands.SetspawnCmd;
import net.clashwars.cwcore.commands.SpawnCmd;
import net.clashwars.cwcore.commands.SpawnerCmd;
import net.clashwars.cwcore.commands.SpawnmobCmd;
import net.clashwars.cwcore.commands.SpeedCmd;
import net.clashwars.cwcore.commands.SudoCmd;
import net.clashwars.cwcore.commands.SuicideCmd;
import net.clashwars.cwcore.commands.TeleportCmd;
import net.clashwars.cwcore.commands.TeleporthereCmd;
import net.clashwars.cwcore.commands.TeleportposCmd;
import net.clashwars.cwcore.commands.TimeCmd;
import net.clashwars.cwcore.commands.TopCmd;
import net.clashwars.cwcore.commands.VanishCmd;
import net.clashwars.cwcore.commands.WarpCmd;
import net.clashwars.cwcore.commands.WarpdelCmd;
import net.clashwars.cwcore.commands.WarpsCmd;
import net.clashwars.cwcore.commands.WarpsetCmd;
import net.clashwars.cwcore.commands.WhoisCmd;

public enum CommandsEnum {
	BROADCAST(BroadcastCmd.class,"broadcast","bc","bcmsg"),
	FREEZE(FreezeCmd.class,"freeze","lock"),
	GAMEMODE(GamemodeCmd.class,"gamemode","gm"),
	HEAL(HealCmd.class,"heal","hp","health"),
	INVSEE(InvseeCmd.class,"invsee","invedit"),
	ITEM(ItemCmd.class,"item","i"),
	GIVE(GiveCmd.class,"give"),
	NICK(NickCmd.class,"nick","nickname"),
	REALNAME(RealnameCmd.class,"realname","rname"),
	REMOVEPOTS(RemovepotsCmd.class,"removepots","removepotions","rpots"),
	TIME(TimeCmd.class,"time","day","night"),
	CLEARINV(ClearinvCmd.class,"clearinv","ci","clearinventory"),
	HAT(HatCmd.class,"hat","head"),
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
