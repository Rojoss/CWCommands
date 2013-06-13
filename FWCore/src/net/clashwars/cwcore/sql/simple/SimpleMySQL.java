/*
 * 
 * $Id$
 * 
 * Software License Agreement (BSD License)
 * 
 * Copyright (c) 2011, The Daniel Morante Company, Inc.
 * All rights reserved.
 * 
 * Redistribution and use of this software in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *   Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 * 
 *   Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 * 
 *   Neither the name of The Daniel Morante Company, Inc. nor the names of its
 *   contributors may be used to endorse or promote products
 *   derived from this software without specific prior
 *   written permission of The Daniel Morante Company, Inc.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Simple MySQL Java Class
 * Makes it similair to PHP
 */
package net.clashwars.cwcore.sql.simple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;

/**
 * 
 * @author Daniel Morante edited by: Kevin Grossman
 */
public class SimpleMySQL {

    private List<Connection> mysql_connection     = new ArrayList<Connection>();

    private boolean          auto_reconnect       = true;
    private int              auto_reconnect_time  = 5000;
    private int              auto_reconnect_retry = 5;

    private String           username_local_cache = null;
    private String           password_local_cache = null;
    private String           hostname_local_cache = null;
    private String           database_local_cache = null;

    /**
     * Gets an existing SimpleMySQL instance.
     * 
     * @return
     */
    public static SimpleMySQL getInstance() {
        return SimpleMySQLHolder.INSTANCE;
    }

    private static class SimpleMySQLHolder {
        private static final SimpleMySQL INSTANCE = new SimpleMySQL();
    }

    /**
     * Enable automatic auto_reconnect if the MySQL Database Connection is lost
     * By default this is enabled.
     * 
     * @see #DisableReconnect()
     */
    public void EnableReconnect() {
        auto_reconnect = true;
    }

    /**
     * Disable automatic auto_reconnect if the MySQL Database Connection is lost
     * By default this is enabled
     * 
     * @see #EnableReconnect()
     */
    public void DisableReconnect() {
        auto_reconnect = false;
    }

    /**
     * Test whether or not automatic reconnect is currently enabled. By default
     * automatic auto_reconnect is enabled
     * 
     * @return true if automatic auto_reconnect is enabled, false if it is
     *         disabled
     * @see #EnableReconnect()
     * @see #DisableReconnect()
     */
    public boolean isReconnectEnabled() {
        return auto_reconnect;
    }

    /**
     * Sets the waiting time before attempting to auto_reconnect to the MySQL
     * Database server.
     * 
     * Default waiting time is 5 seconds
     * 
     * @param time
     *            in milliseconds
     */
    public void setReconnectTime(int time) {
        auto_reconnect_time = time;
    }

    /**
     * Returns the waiting time before attempting to auto_reconnect to the MySQL
     * Database server.
     * 
     * If this value was not changed with {@link #setReconnectTime(int)} the
     * default waiting time is 5 seconds
     * 
     * @return time in milliseconds
     * @see #setReconnectTime(int)
     */
    public int getReconnectTime() {
        return auto_reconnect_time;
    }

    /**
     * Sets the maximum number of automatic reconnection attempts before giving
     * up and throwing an exception.
     * 
     * Default number of attempts is 15
     * 
     * @param retry_times
     */
    public void setReconnectNumRetry(int retry_times) {
        auto_reconnect_retry = retry_times;
    }

    /**
     * Returns the maximum number of automatic reconnection attempts before
     * giving up and throwing an exception.
     * 
     * If this value was not changed with {@link #setReconnectNumRetry(int)} the
     * default number of attempts is 15
     * 
     * @return Number of retries
     * @see #setReconnectNumRetry(int)
     */
    public int getReconnectNumRetry() {
        return auto_reconnect_retry;
    }

    /**
     * Connect to the MySQL Server using default connection parameters.
     * 
     * <p>
     * <strong>HOST</strong>: mysql<br />
     * <strong>USER</strong>: root<br />
     * No Password
     * </p>
     * 
     * @return
     */
    public Connection connect() {
        return connect("mysql", "root", "");
    }

    /**
     * Connects to the MySQL Server using the given server, username, and
     * password.
     * 
     * @param server
     * @param username
     * @param password
     * @return True on a successful connection
     */
    public Connection connect(String server, String username, String password) {
        String mysql_connectionURL;
        String mysql_driver;

        // Cache the server, user, and password localy for auto-auto_reconnect
        //username_local_cache = username;
        //password_local_cache = password;
        //hostname_local_cache = server;

        try {
            // Load MySQL JDBC Driver
            mysql_driver = "com.mysql.jdbc.Driver";
            Class.forName(mysql_driver);

            // Open Connection
            mysql_connectionURL = "jdbc:mysql://" + server;
            return DriverManager.getConnection(mysql_connectionURL, username, password);
        } catch (Exception x) {
            System.err.println("Can  not connect to the MySQL Database Server. " + "Please check your configuration.\n\n" + "Hostname: " + server
                    + "\n" + "Username: " + username + "\n\n" + "Error: " + x.getLocalizedMessage());

        }
        return null;
    }

    /**
     * Connects to the MySQL Server using the given server, username, and
     * password. Auto selects the given database.
     * 
     * @param server
     * @param username
     * @param password
     * @param database
     * @return True on a successful connection.
     */
    public Connection connect(String server, String username, String password, String database) {
        // cache the database for auto-auto_reconnect
        //database_local_cache = database;

        Connection connect = connect(server, username, password);
        if (connect != null) {
            SelectDB(connect, database);
            return connect;
        }
        return null;
    }

    /**
     * Manually select the database to Query.
     * 
     * @param database
     * @return True on successful operation.
     */
    public boolean SelectDB(Connection connection, String database) {
        boolean result = true;
        try {
            connection.setCatalog(database);
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            result = false;
        }
        return result;
    }

    /**
     * Closes the current MySQL Connections.
     * 
     * @return True on close
     */
    public boolean close() {
        try {
            for (Connection con : mysql_connection) {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            }
            return true;
        } catch (Exception x) {
            System.err.println("Could not close connection: " + x.getLocalizedMessage());
            return false;
        }
    }

    /*
     * private Connection reconnect(String server, String username, String password, String database) throws SQLTransientConnectionException {
     * Connection connected = null;
     * try {
     * connected = connect(server, username, password, database);
     * } catch (Exception e) {
     * connected = null;
     * }
     * 
     * if (connected == null) {
     * throw new SQLTransientConnectionException("Unable to re-establish database connection, please try agian later.");
     * }
     * System.out.println("Database connection re-established");
     * return connected;
     * }
     * 
     * 
     * private synchronized void auto_reconnect() {
     * System.out.println("Attempting Auto-Reconnect...");
     * 
     * // Clean and desrtoy anything that may be left
     * try {
     * mysql_connection.close();
     * mysql_connection = null;
     * } catch (SQLException e) {
     * }
     * 
     * // On a sucesufull connection stop retrying
     * boolean connected = false;
     * 
     * // Attempt to reconnect up to the number of auto_reconnect_retry
     * int retries_left = auto_reconnect_retry;
     * while (retries_left > 0 && !connected) {
     * retries_left--;
     * System.out.println("Auto-Reconnect Attempt #" + (auto_reconnect_retry - retries_left) + " of " + auto_reconnect_retry);
     * try {
     * wait(auto_reconnect_time);
     * connected = reconnect(hostname_local_cache, username_local_cache, password_local_cache, database_local_cache);
     * } catch (InterruptedException i) {
     * System.err.println("Reconnect Canceled!");
     * } catch (SQLTransientConnectionException e) {
     * System.err.println("AUTO RECONNECT: " + e.getMessage());
     * } catch (Exception e) {
     * System.err.println("Unkown faliure: " + e.getLocalizedMessage());
     * }
     * }
     * }
     */

    /*
     * private void check_connection() {
     * Statement stmt;
     * ResultSet mysql_result;
     * try {
     * // Execute Query
     * stmt = mysql_connection.createStatement();
     * mysql_result = stmt.executeQuery("SELECT 1 from DUAL WHERE 1=0");
     * mysql_result.close();
     * } catch (CommunicationsException e) {
     * System.err.println("Database connection lost");
     * if (auto_reconnect) {
     * //auto_reconnect();
     * }
     * } catch (NullPointerException e) {
     * System.err.println("MySQL Database not connected!");
     * } catch (SQLTransientConnectionException e) {
     * System.err.println("Database connection problem");
     * if (auto_reconnect) {
     * //auto_reconnect();
     * }
     * } catch (SQLException e) {
     * System.err.println("Database Communications Error");
     * e.printStackTrace();
     * if (auto_reconnect) {
     * //auto_reconnect();
     * }
     * } finally {
     * mysql_result = null;
     * }
     * }
     */

    /**
     * Old style SimpleMySQL query.
     * 
     * @deprecated This method is replaced by the new {@link #Query(java.lang.String)} method.
     * @param Query
     * @return For SELECT type queries a Java SQL ResultSet object. all other
     *         type of queries will return null
     * @see #Query(java.lang.String)
     */
    @Deprecated
    public ResultSet query(String query) {
        return Query(query).getResultSet();
    }

    /**
     * Executes a simple Query on the MySQL database. You must first connect to
     * a MySQL database. If a MySQL database is not connected this will return
     * null.
     * 
     * @param Query
     * @return For SELECT type queries a SimpleMySQLResult. All other type of
     *         queries will return null
     * @see #connect()
     * @see #connect(java.lang.String, java.lang.String, java.lang.String)
     * @see #connect(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public SimpleMySQLResult Query(String query, int timeouts) {
        // Make sure connection is alive
        //check_connection();

        // Create Statement and result objects
        PreparedStatement stmt;
        ResultSet mysql_result;
        SimpleMySQLResult result = null;
        Connection con = getConnection();

        /*
         * We want to keep things simple, so...
         * 
         * Detect whether this is an INSERT, DELETE, or UPDATE statement And use
         * the executeUpdate() function
         * 
         * Or...
         * 
         * Detect whether this is a SELECT statment and use the executeQuery()
         * Function.
         */
        try {
            stmt = con.prepareStatement(query);
            if (query.startsWith("SELECT")) {
                // Use the "executeQuery" function becuase we have to retrive
                // data
                // Return the data as a resultset

                // Execute Query
                mysql_result = stmt.executeQuery();
                result = new SimpleMySQLResult(mysql_result);
            } else {
                // It's an UPDATE, INSERT, or DELETE statement
                // Use the "executeUpdate" function and return a null result

                // Execute Query
                //stmt = con.createStatement();
                stmt.executeUpdate();
            }
        } catch (NullPointerException y) {
            System.err.println("You are not connected to a MySQL server");
            /*if (timeouts <= auto_reconnect_retry) {
            	getConnection();
            	Query(query, timeouts + 1);
            }*/
        } catch (MySQLNonTransientConnectionException e) {
            System.err.println("MySQL server Connection was lost");
            if (timeouts <= auto_reconnect_retry) {
                getConnection();
                Query(query, timeouts + 1);
            }
        } catch (Exception x) {
            System.err.println("ERROR: " + x.getLocalizedMessage());
            if (timeouts <= auto_reconnect_retry) {
                getConnection();
                Query(query, timeouts + 1);
            }
        }

        // Return the SimpleMySQLResult Object or null
        return result;
    }

    public SimpleMySQLResult Query(String query) {
        return Query(query, 0);
    }

    public Connection getConnection(String server, String username, String password, String database) {
        for (Connection con : mysql_connection) {
            try {
                if (con != null && con.isValid(0)) {
                    return con;
                }
            } catch (SQLException e) {
                continue;
            }
        }

        username_local_cache = username;
        password_local_cache = password;
        hostname_local_cache = server;
        database_local_cache = database;

        Connection con = connect(server, username, password, database);
        if (con != null) {
            mysql_connection.add(con);
            Query("CREATE DATABASE IF NOT EXISTS " + database);
        }
        return con;
    }

    public Connection getConnection() {
        for (Connection con : mysql_connection) {
            try {
                if (con != null && con.isValid(0) && !con.isClosed()) {
                    return con;
                }
            } catch (SQLException e) {
                continue;
            }
        }

        Connection con = connect(hostname_local_cache, username_local_cache, password_local_cache, database_local_cache);
        if (con != null) {
            mysql_connection.add(con);
        }
        return con;
    }

    public void SelectDB(String db) {
        try {
            for (Connection con : mysql_connection) {
                if (con != null && con.isValid(0) && !con.isClosed()) {
                    con.setCatalog(db);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isFailed() {
        return mysql_connection.isEmpty();
    }
}
