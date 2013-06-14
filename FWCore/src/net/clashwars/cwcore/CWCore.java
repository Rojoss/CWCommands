package net.clashwars.cwcore;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import net.clashwars.cwcore.bukkit.CWCorePlugin;
import net.clashwars.cwcore.bukkit.events.CoreEvents;
import net.clashwars.cwcore.bukkit.events.MainEvents;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.commands.internal.CommandsEnum;
import net.clashwars.cwcore.config.AliasesConfig;
import net.clashwars.cwcore.config.BookConfig;
import net.clashwars.cwcore.config.ChestConfig;
import net.clashwars.cwcore.config.Config;
import net.clashwars.cwcore.config.PluginConfig;
import net.clashwars.cwcore.entity.PlayerManager;
import net.clashwars.cwcore.runnables.SaveRunnable;
import net.clashwars.cwcore.runnables.SqlUpdateRunnable;
import net.clashwars.cwcore.sql.SqlConnection;
import net.clashwars.cwcore.sql.SqlInfo;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public class CWCore {
	private CWCorePlugin			cwc;
	private final Logger			log				= Logger.getLogger("Minecraft");
	
	private PlayerManager			pm;
	private SqlConnection			sql;
	private SqlInfo					sqlInfo;
	private Config					cfg;
	private Config					booksCfg;
	private Config					chestCfg;
	private Config					aliasesCfg;

	private ArrayList<LootChest>	lootChests		= new ArrayList<LootChest>();
	private ArrayList<String>		deleteChests	= new ArrayList<String>();
	private ArrayList<String>		freeze			= new ArrayList<String>();
	private int						chestDelay;
	private String					pf 				= ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;

	private HashMap<String, Book>	savedBooks		= new HashMap<String, Book>();
	private Set<String>				createChest		= new HashSet<String>();
	private SqlUpdateRunnable		sqlr;

	//private Set<CommandClass>		commands		= new HashSet<CommandClass>();

	public CWCore(CWCorePlugin cwc) {
		this.cwc = cwc;
	}

	public void log(Object msg) {
		log.info("[CWCore " + getPlugin().getDescription().getVersion() + "]: " + msg.toString());
	}

	public void onDisable() {
		getServer().getScheduler().cancelTasks(getPlugin());
		booksCfg.save();
		chestCfg.save();
		cfg.load();

		log("Disabled.");
	}

	public void onEnable() {

		cfg = new PluginConfig(this);
		cfg.init();
		cfg.load();

		booksCfg = new BookConfig(this);
		booksCfg.init();
		booksCfg.load();

		chestCfg = new ChestConfig(this);
		chestCfg.init();
		chestCfg.load();
		
		aliasesCfg = new AliasesConfig();
		aliasesCfg.init();
		aliasesCfg.load();

		sql = new SqlConnection();
		attemptSQLConnection();

		pm = new PlayerManager(this);
		pm.populate();

		registerEvents();
		registerTasks();

		log("Successfully enabled.");
	}

	public boolean parseCommand(CommandSender sender, Command cmd, String lbl, String[] args) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		Class<? extends CommandClass> clazz = CommandsEnum.fromString(cmd.getName());
		if (clazz != null) {
			CommandClass cc = clazz.getConstructor(CWCore.class).newInstance(this);
			String[] perms = cc.permissions();
			
			mb: if (perms != null && perms.length > 0 && !sender.isOp()) {
				String permDisplay = perms[0];
				
				for (String perm : perms) {
					if (sender.hasPermission(perm)) {
						break mb;
					}
				}
				
				sender.sendMessage(pf + ChatColor.RED + "insufficient permissions!" 
				+ ChatColor.GRAY + " - " + ChatColor.DARK_GRAY + "'" + ChatColor.DARK_RED + permDisplay + ChatColor.DARK_GRAY + "'");
				return true;
			}
			
			return cc.execute(sender, cmd, lbl, args);
		}
		return false;
	}

	private void registerEvents() {
		PluginManager pm = getPlugin().getServer().getPluginManager();
		pm.registerEvents(new MainEvents(this), getPlugin());
		pm.registerEvents(new CoreEvents(this), getPlugin());
	}

	private void registerTasks() {
		BukkitScheduler sch = getServer().getScheduler();

		sch.runTaskTimerAsynchronously(getPlugin(), (sqlr = new SqlUpdateRunnable()), 0, 600);
		sch.runTaskTimer(getPlugin(), new SaveRunnable(this), 0, 1200);
	}

	/* GETTERS & SETTERS*/

	public CWCorePlugin getPlugin() {
		return cwc;
	}

	public Server getServer() {
		return getPlugin().getServer();
	}

	/* SQL */
	public SqlInfo getSqlInfo() {
		return sqlInfo;
	}

	public void setSqlInfo(SqlInfo sqlInfo) {
		this.sqlInfo = sqlInfo;
	}

	public void attemptSQLConnection() {
		sql.connect(sqlInfo);
		sql.createTable(sqlInfo.getDb(), "users", Constants.USERS);
	}

	public SqlConnection getSQLConnection() {
		return sql;
	}

	public SqlUpdateRunnable getSqlUpdateTask() {
		return sqlr;
	}

	/* Config */

	public Config getConfig() {
		return cfg;
	}

	public Config getBookConfig() {
		return booksCfg;
	}

	public Config getChestsConfig() {
		return chestCfg;
	}
	
	public Config getAliasesConfig() {
		return aliasesCfg;
	}

	/* Chests */
	public ArrayList<LootChest> getLootChests() {
		return lootChests;
	}

	public ArrayList<String> getDeleteChests() {
		return deleteChests;
	}

	public Set<String> getCreateChests() {
		return createChest;
	}

	/* Books */
	public HashMap<String, Book> getSavedBooks() {
		return savedBooks;
	}

	public int getChestDelay() {
		return chestDelay;
	}

	public void setChestDelay(int chestDelay) {
		this.chestDelay = chestDelay;
	}

	/* PlayerManager */
	public PlayerManager getPlayerManager() {
		return pm;
	}

	public ArrayList<String> getFrozenPlayers() {
		return freeze;
	}
	
	/* Other */
	
	public String getPrefix() {
		return pf;
	}
}
