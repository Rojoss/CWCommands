package net.clashwars.cwcore.runnables;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.clashwars.cwcore.entity.CWPlayer;

import org.bukkit.entity.Player;

public class SqlUpdateRunnable implements Runnable {
    private Set<CWPlayer> players = new HashSet<CWPlayer>();

    public synchronized Set<CWPlayer> getPlayers() {
        return players;
    }

    @Override
    public synchronized void run() {
        LinkedList<CWPlayer> queue = new LinkedList<CWPlayer>(players);
        CWPlayer player = null;

        while ((player = queue.poll()) != null) {
            Player online = player.getOnlinePlayer();

            if (online == null || !online.isOnline()) {
                players.remove(player);
                continue;
            }

            player.fetchData();
        }
    }
}
