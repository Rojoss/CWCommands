package com.pqqqqq.fwcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.PluginManager;

import com.pqqqqq.fwcore.bukkit.FWCorePlugin;
import com.pqqqqq.fwcore.bukkit.events.MainEvents;
import com.pqqqqq.fwcore.command.Commands;
import com.pqqqqq.fwcore.concurrent.MailGiver;
import com.pqqqqq.fwcore.config.BookConfig;
import com.pqqqqq.fwcore.config.ChestConfig;
import com.pqqqqq.fwcore.config.Config;
import com.pqqqqq.fwcore.config.MailConfig;
import com.pqqqqq.fwcore.config.PluginConfig;

public class FWCore {
	private FWCorePlugin			fwc;
	private final Logger			log				= Logger.getLogger("Minecraft");

	private Commands				cmds;

	private ArrayList<Villager>		notele			= new ArrayList<Villager>();
	private ArrayList<Mail>			mail			= new ArrayList<Mail>();
	private ArrayList<DungeonChest>	dungeonChests	= new ArrayList<DungeonChest>();
	private ArrayList<String>		deleteChests	= new ArrayList<String>();

	private HashMap<Item, Mail>		mailDrops		= new HashMap<Item, Mail>();
	private HashMap<String, Book>	savedBooks		= new HashMap<String, Book>();
	private Set<String>				createChest		= new HashSet<String>();

	// Config stuff
	private Config					cfg;
	private Config					mailCfg;
	private Config					booksCfg;
	private Config					chestCfg;
	private int						mailDelay;
	private int						chestDelay;
	private int						stormPercent;

	private void registerEvents() {
		PluginManager pm = getPlugin().getServer().getPluginManager();

		pm.registerEvents(new MainEvents(this), getPlugin());
	}

	public FWCorePlugin getPlugin() {
		return fwc;
	}

	public void log(Object msg) {
		log.info("[FWCore " + getPlugin().getDescription().getVersion() + "]: " + msg.toString());
	}

	public boolean parseCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		String c = cmd.getName();
		if (c.equalsIgnoreCase("mail") || c.equalsIgnoreCase("book") || c.equalsIgnoreCase("chest")) {
			return cmds.executeCommand(sender, lbl, args);
		}
		return false;
	}

	public void onDisable() {
		getPlugin().getServer().getScheduler().cancelTasks(getPlugin());
		mailCfg.save();
		booksCfg.save();
		chestCfg.save();

		for (DungeonChest dc : dungeonChests) {
			Chest chest = dc.getChest();

			if (chest == null)
				continue;

			chest.getInventory().setContents(dc.getInventory());
			dc.resetInventory(chest.getInventory().getContents());
		}

		log("Disabled.");
	}

	public void onEnable() {
		cmds = new Commands(this);
		cmds.populateCommands();

		cfg = new PluginConfig(this);
		cfg.init();
		cfg.load();

		mailCfg = new MailConfig(this);
		mailCfg.init();
		mailCfg.load();

		booksCfg = new BookConfig(this);
		booksCfg.init();
		booksCfg.load();

		chestCfg = new ChestConfig(this);
		chestCfg.init();
		chestCfg.load();

		registerEvents();

		getPlugin().getServer().getScheduler().scheduleAsyncRepeatingTask(getPlugin(), new MailGiver(this), 100, 100);
		// getPlugin().getServer().getScheduler().scheduleAsyncRepeatingTask(getPlugin(),
		// new ChestRemoval(this), 10, 10);
		log("Successfully enabled.");
	}

	public FWCore(FWCorePlugin fwc) {
		this.fwc = fwc;
	}

	public ArrayList<Villager> getNoEditVillagers() {
		return notele;
	}

	public ArrayList<Mail> getMail() {
		return mail;
	}

	public ArrayList<DungeonChest> getDungeonChests() {
		return dungeonChests;
	}

	public ArrayList<String> getDeleteChests() {
		return deleteChests;
	}

	public Set<String> getCreateChests() {
		return createChest;
	}

	public HashMap<Item, Mail> getMailDrops() {
		return mailDrops;
	}

	public HashMap<String, Book> getSavedBooks() {
		return savedBooks;
	}

	public int getMailDelay() {
		return mailDelay;
	}

	public void setMailDelay(int mailDelay) {
		this.mailDelay = mailDelay;
	}

	public int getChestDelay() {
		return chestDelay;
	}

	public void setChestDelay(int chestDelay) {
		this.chestDelay = chestDelay;
	}

	public int getStormPercent() {
		return stormPercent;
	}

	public void setStormPercent(int stormPercent) {
		this.stormPercent = stormPercent;
	}
}
