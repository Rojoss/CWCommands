package net.clashwars.cwcore.constants;

import net.clashwars.cwcore.sql.SqlType;
import net.clashwars.cwcore.sql.SqlValue;

public class Mysql {
	public static final SqlValue[]	USERS;

	static {
		SqlValue[] userTable = new SqlValue[12];
		userTable[0] = new SqlValue("id", SqlType.INTEGER, true);
		userTable[1] = new SqlValue("player", SqlType.TEXT, true);
		userTable[2] = new SqlValue("nick", SqlType.TEXT, true);
		userTable[3] = new SqlValue("tag", SqlType.TEXT, true);
		userTable[4] = new SqlValue("gm", SqlType.INTEGER, true);
		userTable[5] = new SqlValue("god", SqlType.BOOL, true);
		userTable[6] = new SqlValue("vanished", SqlType.BOOL, true);
		userTable[7] = new SqlValue("flying", SqlType.BOOL, true);
		userTable[8] = new SqlValue("walkSpeed", SqlType.FLOAT, true);
		userTable[9] = new SqlValue("flySpeed", SqlType.FLOAT, true);
		userTable[10] = new SqlValue("maxHealth", SqlType.INTEGER, true);
		userTable[11] = new SqlValue("powertools", SqlType.TEXT, true);
		USERS = userTable;
	}
}
