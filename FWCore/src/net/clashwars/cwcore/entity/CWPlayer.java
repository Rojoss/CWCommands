package net.clashwars.cwcore.entity;

import java.util.ArrayList;
import java.util.Map;
import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.sql.SqlConnection;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CWPlayer {
    private CWCore               cwc;
    private int                  id;
    private String               name;

    private int					 credits			= 0;
    private String               nick           	= "";
    private String               tag        	    = "";
    private int                  gm			        = 0;
    private int                  god              	= 0;
    private int                  vanished           = 0;
    private int                  flying          	= 0;
    private int                  walkSpeed          = 0;
    private int                  flySpeed           = 0;
    private int                  maxHealth         	= 0;

    public CWPlayer(CWCore cwc, int id, String name) {
        this.cwc = cwc;
        this.id = id;
        this.name = name;
    }

    public CWCore getPlugin() {
        return cwc;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
        cwc.getSQLConnection().update("users", "credits", credits, "id", id);
    }

    public void addCredits(int credits) {
        this.credits += credits;
        cwc.getSQLConnection().update("users", "credits", credits, "id", id);
    }
    
    public void takeCredits(int credits) {
        this.credits -= credits;
        cwc.getSQLConnection().update("users", "credits", credits, "id", id);
    }
    
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        cwc.getSQLConnection().update("users", "nick", nick, "id", id);
    }
    
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        cwc.getSQLConnection().update("users", "tag", tag, "id", id);
    }

    public int getGamemode() {
        return gm;
    }

    public void setGamemode(int gm) {
        this.gm = gm;
        cwc.getSQLConnection().update("users", "gm", gm, "id", id);
    }

    public int getGod() {
        return god;
    }

    public void setGod(int god) {
        this.god = god;
        cwc.getSQLConnection().update("users", "god", god, "id", id);
    }
    
    public int getVanished() {
        return vanished;
    }

    public void setVanished(int vanished) {
        this.vanished = vanished;
        cwc.getSQLConnection().update("users", "vanished", vanished, "id", id);
    }
    
    public int getFlying() {
        return flying;
    }

    public void setFlying(int flying) {
        this.flying = flying;
        cwc.getSQLConnection().update("users", "flying", flying, "id", id);
    }
    
    public int getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(int walkSpeed) {
        this.walkSpeed = walkSpeed;
        cwc.getSQLConnection().update("users", "walkSpeed", walkSpeed, "id", id);
    }
    
    public int getFlySpeed() {
        return flySpeed;
    }

    public void setFlySpeed(int flySpeed) {
        this.flySpeed = flySpeed;
        cwc.getSQLConnection().update("users", "flySpeed", flySpeed, "id", id);
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        cwc.getSQLConnection().update("users", "maxHealth", maxHealth, "id", id);
    }
    
    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(getName());
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(getName());
    }

    public void fetchData() {
        SqlConnection sql = cwc.getSQLConnection();

        ArrayList<Map<String, Object>> read = sql.read("users", "WHERE id = ?", getId());
        if (!read.isEmpty()) {
            Map<String, Object> assoc = read.get(0);
            interpretData(assoc);
        }
    }

    public void interpretData(Map<String, Object> assoc) {
        credits = (Integer) assoc.get("credits");
        nick = (String) assoc.get("nick");
        tag = (String) assoc.get("tag");
        gm = (Integer) assoc.get("gm");
        god = (Integer) assoc.get("god");
        vanished = (Integer) assoc.get("vanished");
        flying = (Integer) assoc.get("flying");
        walkSpeed = (Integer) assoc.get("walkSpeed");
        flySpeed = (Integer) assoc.get("flySpeed");
        maxHealth = (Integer) assoc.get("maxHealth");

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CWPlayer) {
            CWPlayer other = (CWPlayer) obj;

            return other.getId() == id;
        }
        return false;
    }
}
