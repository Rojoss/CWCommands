package net.clashwars.cwcore.commands.internal;

import net.clashwars.cwcore.commands.BroadcastCmd;
import net.clashwars.cwcore.commands.FreezeCmd;
import net.clashwars.cwcore.commands.GamemodeCmd;
import net.clashwars.cwcore.commands.GiveCmd;
import net.clashwars.cwcore.commands.HealCmd;
import net.clashwars.cwcore.commands.InvseeCmd;
import net.clashwars.cwcore.commands.ItemCmd;
import net.clashwars.cwcore.commands.NickCmd;
import net.clashwars.cwcore.commands.RealnameCmd;
import net.clashwars.cwcore.commands.RemovepotsCmd;
import net.clashwars.cwcore.commands.TimeCmd;

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
