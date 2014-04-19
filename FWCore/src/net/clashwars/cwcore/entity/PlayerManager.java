package net.clashwars.cwcore.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.sql.SqlConnection;
import net.clashwars.cwcore.util.Utils;

import org.bukkit.entity.Player;

public class PlayerManager implements Manager {
	private CWCore					cwc;
	private Map<Integer, CWPlayer>	players	= new HashMap<Integer, CWPlayer>();

	public PlayerManager(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public void populate() {
		players.clear();
		SqlConnection sql = cwc.getSQLConnection();

		ArrayList<Map<String, Object>> users = sql.read("users");

		for (Map<String, Object> map : users) {

			int id = (Integer) map.get("id");
			UUID uid = UUID.fromString((String)map.get("player"));

			CWPlayer player = new CWPlayer(cwc, id, uid);
			player.interpretData(map);

			players.put(id, player);
		}
	}

	public CWPlayer getPlayer(UUID uuid) {
		for (Map.Entry<Integer, CWPlayer> entry : players.entrySet()) {

			CWPlayer cwp = entry.getValue();

			if (cwp.getUUID().equals(uuid)) {
				return cwp;
			}
		}
		return null;
	}

	public CWPlayer getPlayerFromNick(String nick) {
		for (Map.Entry<Integer, CWPlayer> entry : players.entrySet()) {

			CWPlayer cwp = entry.getValue();

			String str = Utils.stripColorCodes(cwp.getNick());

			if (str.equalsIgnoreCase(nick) || cwp.getNick().equalsIgnoreCase(nick)) {
				return cwp;
			}
		}
		return null;
	}

	public CWPlayer getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}

	public CWPlayer getOrCreatePlayer(UUID uid) {
		CWPlayer cwp = getPlayer(uid);

		if (cwp != null) {
			return cwp;
		}

		int id = players.size();
		cwp = new CWPlayer(cwc, id, uid);
		players.put(id, cwp);
		cwc.getSQLConnection().set("users", id, uid.toString(), "", "", 0, 0, 0, 0, 0, 0, 0, "");

		return cwp;
	}

	public CWPlayer getOrCreatePlayer(Player player) {
		return getOrCreatePlayer(player.getUniqueId());
	}

	public Map<Integer, CWPlayer> getPlayers() {
		return players;
	}
}
