package nl.osrs.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * MySQL Class
 * @author Ryan / Lmctruck30
 *
 */

public class MysqlManager {

	/** MySQL Connection */
	public static Connection conn = null;
	public static Statement statement = null;
	public static ResultSet results = null;
	
	public static String MySQLDataBase = "osrs";
	public static String MySQLURL = "localhost";
	public static String MySQLUser = "root";
	public static String MySQLPassword = "";
	
	/**
	 * Creates a Connection to the MySQL Database
	 */
	private synchronized static Connection createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://riotscape.com/riot_forum", "riot_forum", "ryan16");
		} catch(Exception e) {			
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public synchronized static Connection getConnection() {
		if (conn != null)
			return conn;
		else
			return createConnection();
	}
	
	public synchronized static void destroyConnection() {
		try {
			statement.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
