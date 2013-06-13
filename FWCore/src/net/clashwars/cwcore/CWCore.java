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
import net.clashwars.cwcore.command.Commands;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.config.BookConfig;
import net.clashwars.cwcore.config.ChestConfig;
import net.clashwars.cwcore.config.Config;
import net.clashwars.cwcore.config.PluginConfig;
import net.clashwars.cwcore.entity.PlayerManager;
import net.clashwars.cwcore.runnables.SaveRunnable;
import net.clashwars.cwcore.runnables.SqlUpdateRunnable;
import net.clashwars.cwcore.sql.SqlConnection;
import net.clashwars.cwcore.sql.SqlInfo;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitScheduler;
import org.reflections.Reflections;

public class CWCore {
	private CWCorePlugin			cwc;
	private final Logger			log				= Logger.getLogger("Minecraft");

	private Commands				cmds;
	private PlayerManager			pm;
	private Permission				perm;
	private SqlConnection			sql;
	private SqlInfo					sqlInfo;
	private Config					cfg;
	private Config					booksCfg;
	private Config					chestCfg;

	private ArrayList<LootChest>	lootChests		= new ArrayList<LootChest>();
	private ArrayList<String>		deleteChests	= new ArrayList<String>();
	private ArrayList<String>		freeze			= new ArrayList<String>();
	private int						chestDelay;

	private HashMap<String, Book>	savedBooks		= new HashMap<String, Book>();
	private Set<String>				createChest		= new HashSet<String>();
	private SqlUpdateRunnable		sqlr;

	private Set<CommandClass>		commands		= new HashSet<CommandClass>();

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
		cmds = new Commands(this);
		cmds.populateCommands();
		registerCommands();

		cfg = new PluginConfig(this);
		cfg.init();
		cfg.load();

		booksCfg = new BookConfig(this);
		booksCfg.init();
		booksCfg.load();

		chestCfg = new ChestConfig(this);
		chestCfg.init();
		chestCfg.load();

		sql = new SqlConnection();
		attemptSQLConnection();

		pm = new PlayerManager(this);
		pm.populate();

		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perm = rsp.getProvider();

		registerEvents();
		registerTasks();

		log("Successfully enabled.");
	}

	public boolean parseCommand(CommandSender sender, Command cmd, String lbl, String[] args) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		/*String c = cmd.getName();
		if (c.equalsIgnoreCase("book") || c.equalsIgnoreCase("chest") || c.equalsIgnoreCase("firework")
				|| c.equalsIgnoreCase("tploc") || c.equalsIgnoreCase("exp")) {
			return cmds.executeCommand(sender, lbl, args);
		}*/

		for (CommandClass cc : commands) {
			for (String alias : cc.aliases()) {
				if (alias.equalsIgnoreCase(cmd.getName())) {
					return cc.execute(sender, cmd, lbl, args);
				}
			}
		}

		return false;
	}

	public void registerCommands() {
		/*getServer().getPluginCommand("heal").setExecutor(new HealCmd(this));
		getServer().getPluginCommand("gm").setExecutor(new GamemodeCmd(this));
		getServer().getPluginCommand("bc").setExecutor(new BroadcastCmd(this));
		getServer().getPluginCommand("freeze").setExecutor(new FreezeCmd(this));
		getServer().getPluginCommand("nick").setExecutor(new NickCmd(this));
		getServer().getPluginCommand("time").setExecutor(new TimeCmd());
		getServer().getPluginCommand("realname").setExecutor(new RealnameCmd(this));
		getServer().getPluginCommand("removepots").setExecutor(new RemovepotsCmd(this));*/
		//getServer().getPluginCommand("invsee").setExecutor(new InvseeCmd(this));

		try {
			commands.clear();
			Reflections reflections = new Reflections("net.clashwars.cwcore.commands");
			for (Class<? extends CommandClass> clazz : reflections.getSubTypesOf(CommandClass.class)) {
				CommandClass cc = clazz.getConstructor(CWCore.class).newInstance(this);
				commands.add(cc);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
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
}
