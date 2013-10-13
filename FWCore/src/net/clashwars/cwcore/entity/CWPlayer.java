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

    private String               nick           	= "";
    private String               tag        	    = "";
    private int                  gm			        = 0;
    private boolean              god              	= false;
    private boolean              vanished           = false;
    private boolean              flying          	= false;
    private boolean              frozen          	= false;
    private float                walkSpeed          = 0;
    private float                flySpeed           = 0;
    private int                  maxHealth         	= 0;
    private String               powertools        	= "";

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

    public boolean getGod() {
        return god;
    }

    public void setGod(boolean god) {
        this.god = god;
        cwc.getSQLConnection().update("users", "god", god, "id", id);
    }
    
    public boolean getVanished() {
        return vanished;
    }

    public void setVanished(boolean vanished) {
        this.vanished = vanished;
        cwc.getSQLConnection().update("users", "vanished", vanished, "id", id);
    }
    
    public boolean getFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
        cwc.getSQLConnection().update("users", "flying", flying, "id", id);
    }
    
    public boolean getFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
        cwc.getSQLConnection().update("users", "frozen", frozen, "id", id);
    }
    
    public float getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
        cwc.getSQLConnection().update("users", "walkSpeed", walkSpeed, "id", id);
    }
    
    public float getFlySpeed() {
        return flySpeed;
    }

    public void setFlySpeed(float flySpeed) {
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
    
    public String getPowertool() {
        return powertools;
    }

    public void setPowertool(String powertool) {
        this.powertools = powertool;
        cwc.getSQLConnection().update("users", "powertools", powertools, "id", id);
    }
    
    public void addPowertool(String powertool) {
    	if (this.powertools == "") {
    		this.powertools += powertool;
    	} else {
    		this.powertools += "»" + powertool;
    	}
        cwc.getSQLConnection().update("users", "powertools", powertools, "id", id);
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
        nick = (String) assoc.get("nick");
        tag = (String) assoc.get("tag");
        gm = (Integer) assoc.get("gm");
        god = (boolean) assoc.get("god");
        vanished = (boolean) assoc.get("vanished");
        flying = (boolean) assoc.get("flying");
        walkSpeed = (float) assoc.get("walkSpeed");
        flySpeed = (float) assoc.get("flySpeed");
        maxHealth = (Integer) assoc.get("maxHealth");
        powertools = (String) assoc.get("powertools");

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
