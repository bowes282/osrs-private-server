package nl.osrs.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.Driver;

public class QueryFactory {
	private static Connection connection;
	
	public static ResultSet executeQuery(String query) throws SQLException {
		Statement statement = getConnection().createStatement();
		return statement.executeQuery(query);
	}
	
	public static void executeUpdate(String query) throws SQLException {
		Statement statement = getConnection().createStatement();
		statement.executeUpdate(query);
	}
	
	private static Connection getConnection() throws SQLException {
		if (QueryFactory.connection == null)
			QueryFactory.connect();
		return connection;
	}
	
	private static void connect() throws SQLException {
		new Driver();
		connection = DriverManager.getConnection("jdbc:mysql://localhost/osrs", "root", "");
	}
	
}
