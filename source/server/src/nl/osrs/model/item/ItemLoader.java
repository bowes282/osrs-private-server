package nl.osrs.model.item;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.Driver;

public class ItemLoader {
	private static Item[] items;
	
	public static Item[] loadItems() {
		items = new Item[12000];
		
		ResultSet itemsFromDatabase = null;
		
		try {
			itemsFromDatabase = selectItemsFromDatabase();
			
			loadItems(itemsFromDatabase);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return items;
	}
	
	private static void loadItems(ResultSet result) throws SQLException {
		while (result.next()) {
			Item item = new Item(result.getInt(1));
			item.setName(result.getString(2));
			item.setExamine(result.getString(3));
			item.setValue(result.getInt(4));
			item.setEquipmentSlot(result.getInt(5));
			
			int[] bonuses = {
					result.getInt(6),
					result.getInt(7),
					result.getInt(8),
					result.getInt(9),
					result.getInt(10),
					result.getInt(11),
					result.getInt(12),
					result.getInt(13),
					result.getInt(14),
					result.getInt(15),
					result.getInt(16),
					result.getInt(17),
			};
			
			item.setBonuses(bonuses);
			
			items[item.getId()] = item;
		}
	}
	
	private static ResultSet selectItemsFromDatabase() throws SQLException {
		@SuppressWarnings("unused")
		Driver driver = new Driver();
		
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/osrs", "root", "");
	
		Statement statement = connection.createStatement();
		
		return statement.executeQuery("SELECT * FROM item");
	}
}
