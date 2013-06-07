package com.pqqqqq.cwcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import com.pqqqqq.cwcore.bukkit.CWCorePlugin;
import com.pqqqqq.cwcore.bukkit.events.MainEvents;
import com.pqqqqq.cwcore.command.Commands;
import com.pqqqqq.cwcore.config.BookConfig;
import com.pqqqqq.cwcore.config.ChestConfig;
import com.pqqqqq.cwcore.config.Config;
import com.pqqqqq.cwcore.config.PluginConfig;

public class CWCore {
	private CWCorePlugin			cwc;
	private final Logger			log				= Logger.getLogger("Minecraft");

	private Commands				cmds;

	private ArrayList<LootChest>	lootChests	= new ArrayList<LootChest>();
	private ArrayList<String>		deleteChests	= new ArrayList<String>();

	private HashMap<String, Book>	savedBooks		= new HashMap<String, Book>();
	private Set<String>				createChest		= new HashSet<String>();

	// Config stuff
	private Config					cfg;
	private Config					booksCfg;
	private Config					chestCfg;
	private int						chestDelay;

	private void registerEvents() {
		PluginManager pm = getPlugin().getServer().getPluginManager();

		pm.registerEvents(new MainEvents(this), getPlugin());
	}

	public CWCorePlugin getPlugin() {
		return cwc;
	}

	public void log(Object msg) {
		log.info("[CWCore " + getPlugin().getDescription().getVersion() + "]: " + msg.toString());
	}

	public boolean parseCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		String c = cmd.getName();
		if (c.equalsIgnoreCase("book") || c.equalsIgnoreCase("chest") || c.equalsIgnoreCase("firework")
				|| c.equalsIgnoreCase("tploc") || c.equalsIgnoreCase("givexp")) {
			return cmds.executeCommand(sender, lbl, args);
		}
		return false;
	}

	public void onDisable() {
		getPlugin().getServer().getScheduler().cancelTasks(getPlugin());
		booksCfg.save();
		chestCfg.save();

		log("Disabled.");
	}

	public void onEnable() {
		cmds = new Commands(this);
		cmds.populateCommands();

		cfg = new PluginConfig();
		cfg.init();
		cfg.load();

		booksCfg = new BookConfig(this);
		booksCfg.init();
		booksCfg.load();

		chestCfg = new ChestConfig(this);
		chestCfg.init();
		chestCfg.load();

		registerEvents();

		log("Successfully enabled.");
	}

	public CWCore(CWCorePlugin cwc) {
		this.cwc = cwc;
	}

	public ArrayList<LootChest> getLootChests() {
		return lootChests;
	}

	public ArrayList<String> getDeleteChests() {
		return deleteChests;
	}

	public Set<String> getCreateChests() {
		return createChest;
	}

	public HashMap<String, Book> getSavedBooks() {
		return savedBooks;
	}

	public int getChestDelay() {
		return chestDelay;
	}

	public void setChestDelay(int chestDelay) {
		this.chestDelay = chestDelay;
	}
}
