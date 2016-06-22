package nl.osrs.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import nl.osrs.sql.QueryFactory;

public class ItemLoader {
	private static List<Item> items;
	private static Map<Integer, String> equipmentSlots;
	private static Map<Integer, String> skills;
	
	public static ArrayList<Item> getItems() {
		if (items == null)
			load();
		return (ArrayList<Item>) items;
	}
	
	public static Map<Integer, String> getEquipmentSlots() {
		if (equipmentSlots == null)
			load();
		return equipmentSlots;
	}
	
	public static Map<Integer, String> getSkills() {
		if (skills == null)
			load();
		return skills;
	}
	
	private static void load() {
		try {
			loadItems();
			loadItemRequirements();
			loadEquipmentSlotNames();
			loadSkillNames();
		} catch (CommunicationsException e) {
			System.err.println("Unable to connect to the database.");
			System.exit(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void loadItems() throws SQLException {
		items = new ArrayList<>();
		
		String query = "SELECT * FROM item";
		
		ResultSet result = QueryFactory.executeQuery(query);
		
		while (result.next()) {
			Item item = new Item(result.getInt(1));
			item.setName(result.getString(2));
			item.setExamine(result.getString(3));
			item.setValue(result.getInt(4));
			item.setEquipmentSlot(result.getInt(5));
			
			int[] bonuses = new int[12];
			
			for (int i = 0; i < 12; i++) {
				bonuses[i] = result.getInt(6 + i);
			}
			
			item.setBonuses(bonuses);
			item.setStandAnimation(result.getInt(18));
			item.setWalkAnimation(result.getInt(19));
			item.setRunAnimation(result.getInt(20));
			item.setAttackAnimation(result.getInt(21));
			item.setBlockAnimation(result.getInt(22));
			item.setAttackDelay(result.getInt(23));
			
			items.add(item);
		}
	}
	
	private static void loadItemRequirements() throws SQLException {
		if (items == null)
			loadItems();
		
		Map<Integer, ArrayList<ItemRequirement>> temp = new HashMap<>();
		
		String query = "SELECT * FROM item_requirements";
		
		ResultSet result = QueryFactory.executeQuery(query);
		
		while (result.next()) {
			int itemId = result.getInt(1);
			
			ItemRequirement requirement = new ItemRequirement(result.getInt(2), result.getInt(3));
			
			ArrayList<ItemRequirement> requirements = temp.get(itemId);
			
			if (requirements == null)
				requirements = new ArrayList<>();
			
			requirements.add(requirement);
			
			temp.put(itemId, requirements);
		}
		
		for (Item item : items)
			if (temp.containsKey(item.getId()))
				item.setRequirements(temp.get(item.getId()));
	}
	
	private static void loadEquipmentSlotNames() throws SQLException {
		equipmentSlots = new HashMap<>();
		
		String query = "SELECT * FROM item_equipment_slots";
		
		ResultSet result = QueryFactory.executeQuery(query);
		
		while (result.next()) {
			int equipmentSlot = result.getInt(1);
			String equipmentSlotName = result.getString(2);
			
			equipmentSlots.put(equipmentSlot, equipmentSlotName);
		}
	}

	private static void loadSkillNames() throws SQLException {
		skills = new HashMap<>();
		
		String query = "SELECT * FROM skills";
		
		ResultSet result = QueryFactory.executeQuery(query);
		
		while (result.next()) {
			int skill = result.getInt(1);
			String skillName = result.getString(2);
			
			skills.put(skill, skillName);
		}
	}
	
}
