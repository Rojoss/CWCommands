package net.clashwars.cwcore;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.clashwars.cwcore.bukkit.CWCorePlugin;
import net.clashwars.cwcore.bukkit.events.CmdEvents;
import net.clashwars.cwcore.bukkit.events.CoreEvents;
import net.clashwars.cwcore.bukkit.events.MainEvents;
import net.clashwars.cwcore.bukkit.events.MessageEvents;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.commands.internal.CommandsEnum;
import net.clashwars.cwcore.components.CustomEffect;
import net.clashwars.cwcore.config.AliasesConfig;
import net.clashwars.cwcore.config.Config;
import net.clashwars.cwcore.config.PluginConfig;
import net.clashwars.cwcore.config.WarpsConfig;
import net.clashwars.cwcore.constants.Mysql;
import net.clashwars.cwcore.entity.PlayerManager;
import net.clashwars.cwcore.runnables.SqlUpdateRunnable;
import net.clashwars.cwcore.sql.SqlConnection;
import net.clashwars.cwcore.sql.SqlInfo;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;

public class CWCore {
	private CWCorePlugin			cwc;
	private final Logger			log			= Logger.getLogger("Minecraft");

	private PlayerManager			pm;
	private SqlConnection			sql;
	private SqlInfo					sqlInfo;
	private Config					cfg;
	private AliasesConfig			aliasesCfg;
	private WarpsConfig				warpsCfg;
	private Permission				perm;
	private CustomEffect			effects;

	private boolean					autoRespawn;
	public static String			pf			= ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;

	private HashMap<String, Player>	viewList	= new HashMap<String, Player>();
	public static List<Command>		cmdList		= new ArrayList<Command>();
	public static List<Plugin>		plugins		= null;

	private SqlUpdateRunnable		sqlr;

	public CWCore(CWCorePlugin cwc) {
		this.cwc = cwc;
	}

	public void log(Object msg) {
		log.info("[CWCore " + getPlugin().getDescription().getVersion() + "]: " + msg.toString());
	}

	public void onDisable() {
		getServer().getScheduler().cancelTasks(getPlugin());
		cfg.load();

		log("Disabled.");
	}

	public void onEnable() {
		cfg = new PluginConfig(this);
		cfg.init();
		cfg.load();

		aliasesCfg = new AliasesConfig();
		aliasesCfg.init();
		aliasesCfg.load();

		warpsCfg = new WarpsConfig();
		warpsCfg.init();
		warpsCfg.load();

		sql = new SqlConnection();
		attemptSQLConnection();

		pm = new PlayerManager(this);
		pm.populate();
		
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (rsp != null) {
			perm = rsp.getProvider();
        } else {
        	log("Vault couldn't be loaded.");
        }

		effects = new CustomEffect();

		registerEvents();
		registerTasks();
		registerChannels();

		try {
			loadCommandsList();
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
		}

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

				sender.sendMessage(pf + ChatColor.RED + "insufficient permissions!" + ChatColor.GRAY + " - " + ChatColor.DARK_GRAY + "'"
						+ ChatColor.DARK_RED + permDisplay + ChatColor.DARK_GRAY + "'");
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
		pm.registerEvents(new CmdEvents(this), getPlugin());
	}

	private void registerTasks() {
		BukkitScheduler sch = getServer().getScheduler();

		sch.runTaskTimerAsynchronously(getPlugin(), (sqlr = new SqlUpdateRunnable()), 0, 600);
	}

	private void registerChannels() {
		Messenger msg = getPlugin().getServer().getMessenger();

		msg.registerIncomingPluginChannel(getPlugin(), "CWCore", new MessageEvents(this));
		msg.registerOutgoingPluginChannel(getPlugin(), "CWBungee");
	}

	@SuppressWarnings("unchecked")
	private void loadCommandsList() throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException {
		SimplePluginManager spm = (SimplePluginManager) getServer().getPluginManager();
		SimpleCommandMap cMap = null;
		Map<String, Command> knownCommands = null;
		if (spm != null) {
			Field cMF = spm.getClass().getDeclaredField("commandMap");
			cMF.setAccessible(true);
			cMap = (SimpleCommandMap) cMF.get(spm);
			Field knownCommandsField = cMap.getClass().getDeclaredField("knownCommands");

			knownCommandsField.setAccessible(true);

			knownCommands = (Map<String, Command>) knownCommandsField.get(cMap);
		}
		if (cMap != null) {
			for (Iterator<Map.Entry<String, Command>> it = knownCommands.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Command> entry = it.next();
				if (entry.getValue() instanceof PluginCommand) {
					PluginCommand c = (PluginCommand) entry.getValue();
					cmdList.add(c);
				}
			}
		}

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
		try {
			sql.connect(sqlInfo);
			sql.createTable(sqlInfo.getDb(), "users", Mysql.USERS);
		} catch (Exception e) {
			log("Could not connect to database.");
		}
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

	public AliasesConfig getAliasesConfig() {
		return aliasesCfg;
	}

	public WarpsConfig getWarpsConfig() {
		return warpsCfg;
	}

	public HashMap<String, Player> getViewList() {
		return viewList;
	}

	/* PlayerManager */
	public PlayerManager getPlayerManager() {
		return pm;
	}

	/* Other */

	public String getPrefix() {
		return pf;
	}

	public List<Plugin> getPlugins() {
		return plugins;
	}

	public List<Command> getCmdList() {
		return cmdList;
	}

	public Permission getPermissions() {
		return perm;
	}

	public boolean getAutoRespawn() {
		return autoRespawn;
	}

	public void setAutoRespawn(boolean autoRespawn) {
		this.autoRespawn = autoRespawn;
	}

	public CustomEffect getEffects() {
		return effects;
	}

}
