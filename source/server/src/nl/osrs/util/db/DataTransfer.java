package nl.osrs.util.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.Driver;

import nl.osrs.model.item.ItemWearing;
import nl.osrs.model.player.Player;

/**
 * This class serves to transfer information from CFG files to a MySQL database instance.
 * Run the queries in the 'database' folder to create the tables.
 * 
 * @author j.germeraad
 */
public class DataTransfer {
	private Connection connection;
	
	public DataTransfer() throws SQLException, ClassNotFoundException {
		@SuppressWarnings("unused")
		Driver driver = new Driver();
		
		this.connection = DriverManager.getConnection("jdbc:mysql://localhost/osrs", "root", "");
		
		this.transfer(DataTable.ITEM);
	}
	
	public boolean transfer(DataTable table) {
		String tableName = table.getTable();
		
		try {
			if (!isEmpty(tableName))
				empty(tableName);
			
			switch (table) {
			case ITEM:
				return transferItems();
			case NPC:
				return transferNPCs();
			case SPAWN:
				return transferSpawns();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean transferItems() throws IOException, SQLException {
		ArrayList<TransferableItem> items = new ArrayList<>();
		ArrayList<Integer> knownItemIds = new ArrayList<>();

		File source = new File("./Data/cfg/item.cfg");
		
		BufferedReader reader = new BufferedReader(new FileReader(source));
		
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			
			int spot = line.indexOf('=');
			
			if (spot > -1) {
				String token = line.substring(0, spot);
				token = token.trim();
				
				String token2 = line.substring(spot + 1);
				token2 = token2.trim();
				
				String token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				
				String[] token3 = token2_2.split("\t");
				
				if (token.equals("item")) {
					TransferableItem item = new TransferableItem(Integer.parseInt(token3[0]));
					
					if (knownItemIds.contains(item.getId()))
						continue;
					
					item.setName(token3[1].replaceAll("_", " "));
					item.setExamine(token3[2].replaceAll("_", " "));
					item.setValue((int) Double.parseDouble(token3[3]));
					item.setEquipmentSlot(getItemSlotForItemId(item.getId()));
					
					int[] attackBonuses = new int[5];
					int[] defenceBonuses = new int[5];
					
					for (int i = 0; i < 5; i++) {
						attackBonuses[i] = Integer.parseInt(token3[i + 6]);
						defenceBonuses[i] = Integer.parseInt(token3[i + 11]);
					}

					item.setStrength(Integer.parseInt(token3[16]));
					item.setPrayer(Integer.parseInt(token3[17]));
					
					knownItemIds.add(item.getId());
					items.add(item);
				}
			}
		}
		
		for (TransferableItem item : items)
			connection.createStatement().executeUpdate(item.toQuery());
		
		reader.close();
		
		return false;
	}
	
	private boolean contains(int[] array, int id) {
		for (int i : array)
			if (i == id)
				return true;
		return false;
	}

	private int getItemSlotForItemId(int id) {
		if (contains(ItemWearing.amulets, id))
			return Player.playerAmulet;
		
		if (contains(ItemWearing.arrows, id))
			return Player.playerArrows;
		
		if (contains(ItemWearing.body, id))
			return Player.playerChest;
		
		if (contains(ItemWearing.boots, id))
			return Player.playerFeet;
		
		if (contains(ItemWearing.capes, id))
			return Player.playerCape;
		
		if (contains(ItemWearing.gloves, id))
			return Player.playerHands;
		
		if (contains(ItemWearing.hats, id))
			return Player.playerHat;
		
		if (contains(ItemWearing.legs, id))
			return Player.playerLegs;
		
		if (contains(ItemWearing.rings, id))
			return Player.playerRing;
		
		if (contains(ItemWearing.shields, id))
			return Player.playerShield;
		
		return Player.playerWeapon;
	}

	private boolean transferNPCs() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean transferSpawns() {
		// TODO Auto-generated method stub
		return false;
	}

	private void empty(String table) throws SQLException {
		String query = "DELETE FROM " + table;
		
		connection.createStatement().executeUpdate(query);
	}
	
	private boolean isEmpty(String table) throws SQLException {
		String query = "SELECT COUNT(*) FROM " + table;
		
		Statement statement = connection.createStatement();
		
		ResultSet result = statement.executeQuery(query);
		
		int rowCount = 0;
		
		while (result.next())
			rowCount = result.getInt(1);
		
		return rowCount == 0;
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		new DataTransfer();
	}
	
}
