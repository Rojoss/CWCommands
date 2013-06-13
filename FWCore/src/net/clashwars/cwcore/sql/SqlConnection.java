package net.clashwars.cwcore.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import net.clashwars.cwcore.sql.simple.SimpleMySQL;
import net.clashwars.cwcore.sql.simple.SimpleMySQLResult;

public class SqlConnection {
    private SimpleMySQL sql;

    public SqlConnection() {
        sql = new SimpleMySQL();
    }

    public boolean connect(SqlInfo info) {
        return connect(info.getAddress(), info.getUser(), info.getPass(), info.getDb());
    }

    public boolean connect(String address, String user, String pass, String db) {
        Connection bool = sql.getConnection(address, user, pass, db);
        return bool != null;
    }

    public void selectdb(String db) {
        sql.SelectDB(db);
    }

    public boolean close() {
        return sql.close();
    }

    public SimpleMySQLResult createTable(String db, String tableName, SqlValue... vals) {
        String end = "(  ";
        for (SqlValue val : vals) {
            end += val.getValue() + " " + val.getType().getTypeName() + (val.isNotNull() ? " NOT NULL" : "") + " "
                    + (val.isAutoIncrement() ? "AUTO_INCREMENT" : "") + ",  ";
        }
        end = end.trim().substring(0, end.length() - 3) + ", PRIMARY KEY (" + vals[0].getValue() + ")) ";
        return sql.Query("CREATE TABLE IF NOT EXISTS " + db + "." + tableName + end + "ENGINE=MyISAM DEFAULT CHARSET=latin1");
    }

    public void set(String tableName, String[] struct, Object... vals) {
        String st = "(";

        for (String s : struct) {
            st += s + ", ";
        }
        st = st.substring(0, st.length() - 2) + ")";

        String parmVals = "VALUES(";
        for (int i = 0; i < vals.length; i++) {
            parmVals += "?,";
        }
        parmVals = parmVals.substring(0, parmVals.length() - 1) + ")";

        try {
            Connection con = sql.getConnection();
            PreparedStatement stmt = con.prepareStatement("INSERT IGNORE INTO " + tableName + " " + st + " " + parmVals);

            for (int i = 0; i < vals.length; i++) {
                stmt.setObject(i + 1, vals[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void set(String tableName, Object... vals) {
        String parmVals = "VALUES(";
        for (int i = 0; i < vals.length; i++) {
            parmVals += "?,";
        }
        parmVals = parmVals.substring(0, parmVals.length() - 1) + ")";

        try {
            Connection con = sql.getConnection();
            PreparedStatement stmt = con.prepareStatement("INSERT IGNORE INTO " + tableName + " " + parmVals);

            for (int i = 0; i < vals.length; i++) {
                stmt.setObject(i + 1, vals[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(String tableName, String column, Object val) {
        try {
            Connection con = sql.getConnection();
            PreparedStatement stmt = con.prepareStatement("DELETE FROM " + tableName + " WHERE " + column + "=?");

            stmt.setObject(1, val);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SimpleMySQLResult removeAll(String tableName) {
        return sql.Query("DELETE FROM " + tableName);
    }

    public void update(String tableName, String setColumn, Object setValue, String whereColumn, Object whereValue) {
        try {
            Connection con = sql.getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE " + tableName + " SET " + setColumn + "=? WHERE " + whereColumn + "=?");

            stmt.setObject(1, setValue);
            stmt.setObject(2, whereValue);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String tableName, String setColumn, String setValue) {
        try {
            Connection con = sql.getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE " + tableName + " SET " + setColumn + "=?");

            stmt.setObject(1, setValue);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Map<String, Object>> read(String tableName) {
        ArrayList<Map<String, Object>> vals = new ArrayList<Map<String, Object>>();

        SimpleMySQLResult res = sql.Query("SELECT * FROM " + tableName);

        if (res != null) {
            for (int i = 0; i < res.getNumRows(); i++) {
                vals.add(res.FetchAssoc());
            }

            res.close();
        }
        return vals;
    }

    /*public Map<String, Object> read(String tableName, String column, String value) {
    	SimpleMySQLResult res = sql.Query("SELECT * FROM " + tableName + " WHERE " + column + " = '" + value + "'");

    	if (res == null) {
    		return null;
        }

    	Map<String, Object> map = res.FetchAssoc();
    	res.close();
    	return map;
    }*/

    public ArrayList<Map<String, Object>> read(String tableName, String endQuery) {
        return read(sql.Query("SELECT * FROM " + tableName + " " + endQuery));
    }

    public ArrayList<Map<String, Object>> read(String tableName, String endQuery, Object... values) {
        try {
            Connection con = sql.getConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM " + tableName + " " + endQuery);

            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }

            return read(new SimpleMySQLResult(stmt.executeQuery()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<Map<String, Object>>();
    }

    public SimpleMySQLResult query(String query) {
        return sql.Query(query);
    }

    public ArrayList<Map<String, Object>> read(SimpleMySQLResult res) {
        ArrayList<Map<String, Object>> vals = new ArrayList<Map<String, Object>>();

        if (res != null) {
            for (int i = 0; i < res.getNumRows(); i++) {
                vals.add(res.FetchAssoc());
            }
            res.close();
        }

        return vals;
    }

    public SimpleMySQL getSimple() {
        return sql;
    }
}
