package net.clashwars.cwcore.bukkit.events;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.entity.CWPlayer;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MainEvents implements Listener {
	private CWCore	cwc;

	public MainEvents(CWCore cwc) {
		this.cwc = cwc;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void join(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (cwc.getPlayerManager().getPlayer(player) == null) {
			player.sendMessage(Utils.integrateColor("&8&l=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="));
			player.sendMessage(Utils.integrateColor("&8&l=-"));
			player.sendMessage(Utils.integrateColor("&8&l=- &6Welcome to &4&lClashWars ") + ChatColor.GOLD + player.getName() + "!");
			player.sendMessage(Utils.integrateColor("&8&l=-"));
			player.sendMessage(Utils.integrateColor("&8&l=- &6You can learn how the server works on our website."));
			player.sendMessage(Utils.integrateColor("&8&l=- &6It isn't hard though so you can also just play!"));
			player.sendMessage(Utils.integrateColor("&8&l=-"));
			player.sendMessage(Utils.integrateColor("&8&l=- &6The website: &9&lhttp://clashwars.com"));
			player.sendMessage(Utils.integrateColor("&8&l=-"));
			player.sendMessage(Utils.integrateColor("&8&l=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="));
		} else {
			player.sendMessage(Utils.integrateColor("&8&l=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="));
			player.sendMessage(Utils.integrateColor("&8&l=-"));
			player.sendMessage(Utils.integrateColor("&8&l=- &6Welcome back ") + ChatColor.GOLD + player.getName() + "!");
			player.sendMessage(Utils.integrateColor("&8&l=-"));
			player.sendMessage(Utils.integrateColor("&8&l=- &6Make sure to check out our website."));
			player.sendMessage(Utils.integrateColor("&8&l=- &6The website: &9&lhttp://clashwars.com"));
			player.sendMessage(Utils.integrateColor("&8&l=-"));
			player.sendMessage(Utils.integrateColor("&8&l=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="));
		}

		CWPlayer cwp = cwc.getPlayerManager().getOrCreatePlayer(player);
		cwc.getSqlUpdateTask().getPlayers().add(cwp);

		if (cwp.getNick() != "" && cwp.getNick() != null && cwp.getNick() != player.getDisplayName()) {
			player.setDisplayName(Utils.integrateColor(cwp.getNick()));
		}

		if (player.hasPermission("cwcore.sync")) {
			if (cwp.getMaxHealth() != 0 && cwp.getMaxHealth() != player.getMaxHealth()) {
				player.setMaxHealth(cwp.getMaxHealth());
			}

			if (cwp.getGamemode() == 0 && !player.getGameMode().equals(GameMode.SURVIVAL)) {
				player.setGameMode(GameMode.SURVIVAL);
			} else if (cwp.getGamemode() == 1 && !player.getGameMode().equals(GameMode.CREATIVE)) {
				player.setGameMode(GameMode.CREATIVE);
			} else if (cwp.getGamemode() == 2 && !player.getGameMode().equals(GameMode.ADVENTURE)) {
				player.setGameMode(GameMode.ADVENTURE);
			}
		}
	}

}
