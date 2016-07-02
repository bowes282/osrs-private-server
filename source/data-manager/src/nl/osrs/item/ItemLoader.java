package nl.osrs.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import nl.osrs.cachemanager.CacheManager;
import nl.osrs.cachemanager.cache.ItemDefinition;
import nl.osrs.mongo.MongoManager;

public class ItemLoader {
	private static ArrayList<Item> items;
	
	public static ArrayList<Item> getItems() {
		if (items == null)
			loadItems();
		
		return items;
	}
	
	public static void loadItems() {
		FindIterable<Document> result = MongoManager.executeQuery(
				(MongoDatabase db) -> db.getCollection("items").find());
		
		ArrayList<Item> items = new ArrayList<>();
		ItemDefinition[] itemDefinitions = CacheManager.getItemDefinitions();
		
		for (Document doc : result) {
			Item item = convert(doc);
			
			ItemDefinition itemDefinition = itemDefinitions[item.getId()];
			
			if (itemDefinition == null)
				continue;
			
			if (item.getId() != itemDefinition.id) {
				System.err.println("Item " + item.getId() + " is out of sync...");
				continue;
			}
			
			if (itemDefinition.name == null)
				continue;
			
			item.setName(itemDefinition.name);
			
			items.add(item);
		}
		
		ItemLoader.items = items;
	}
	
	public static void saveItems(List<Item> items) {
		for (Item item : items)
			saveItem(item);
	}
	
	public static void saveItem(Item item) {
		Document toSave = convert(item);
		
		System.out.println("To save: " + toSave.toString());
		// Do something...
	}
	
	public static HashMap<Integer, String> getEquipmentSlots() {
		HashMap<Integer, String> equipmentSlots = new HashMap<>();
		
		FindIterable<Document> result = MongoManager.executeQuery(
				(MongoDatabase db) -> db.getCollection("equipmentSlotNames").find());
		
		for (Document doc : result)
			equipmentSlots.put(doc.getInteger("equipmentSlot"), doc.getString("name"));
		
		return equipmentSlots;
	}
	
	public static HashMap<Integer, String> getSkills() {
		HashMap<Integer, String> skills = new HashMap<>();
		
		FindIterable<Document> result = MongoManager.executeQuery(
				(MongoDatabase db) -> db.getCollection("skillNames").find());
		
		for (Document doc : result)
			skills.put(doc.getInteger("skill"), doc.getString("name"));
		
		return skills;
	}
	
	private static Document convert(Item item) {
		Document doc = new Document("id", item.getId());
		
		if (item.hasName())
			doc.append("name", item.getName());
		
		if (item.hasExamine())
			doc.append("examine", item.getExamine());
		
		if (item.hasValue() && item.getValue() != 0)
			doc.append("value", item.getValue());
		
		if (item.hasEquipmentSlot() && item.getEquipmentSlot() != 0)
			doc.append("equipmentSlot", item.getEquipmentSlot());
		
		if (item.hasBonuses()) {
			Document bonuses = new Document();
			
			int[] bonusesArray = new int[item.getBonuses().length];
			
			for (int i = 0; i < item.getBonuses().length; i++)
				bonusesArray[i] = item.getBonuses()[i];
			
			bonuses.append("bonuses", bonusesArray);
		}
		
		if (item.hasStandAnimation())
			doc.append("standAnimation", item.getStandAnimation());
		
		if (item.hasWalkAnimation())
			doc.append("walkAnimation", item.getWalkAnimation());
		
		if (item.hasRunAnimation())
			doc.append("runAnimation", item.getRunAnimation());
		
		if (item.hasAttackAnimation())
			doc.append("attackAnimation", item.getAttackAnimation());
		
		if (item.hasBlockAnimation())
			doc.append("blockAnimation", item.getBlockAnimation());
		
		if (item.hasAttackDelay())
			doc.append("attackDelay", item.getAttackDelay());
		
		if (item.hasRequirements()) {
			ArrayList<ItemRequirement> requirements = item.getRequirements();
			ArrayList<Document> itemRequirements = new ArrayList<>();
			
			for (ItemRequirement requirement : requirements)
				itemRequirements.add(new Document("skill", requirement.getSkill())
					.append("level", requirement.getLevel()));
			
			doc.append("requirements", itemRequirements);
		}

		if (item.hasBonuses()) {
			Document bonuses = new Document();
			
			int[] bonusesArray = new int[item.getBonuses().length];
			
			for (int i = 0; i < item.getBonuses().length; i++)
				bonusesArray[i] = item.getBonuses()[i];
			
			bonuses.append("bonuses", bonusesArray);
		}
		return doc;
	}
	
	@SuppressWarnings("unchecked")
	private static Item convert(Document document) {
		Item item = new Item(document.getInteger("id"));
		
		if (document.containsKey("examine"))
			item.setExamine(document.getString("examine"));
		
		if (document.containsKey("value"))
			item.setValue(document.getInteger("value"));
		
		if (document.containsKey("equipmentSlot"))
			item.setEquipmentSlot(document.getInteger("equipmentSlot"));
		
		if (document.containsKey("bonuses")) {
			// I'm sure that it returns an ArrayList<Integer>...
			ArrayList<Integer> itemBonuses = (ArrayList<Integer>) document.get("bonuses");
				
			int[] bonuses = new int[itemBonuses.size()];
			
			for (int i = 0 ; i < itemBonuses.size(); i++)
				bonuses[i] = itemBonuses.get(i);
			
			
			item.setBonuses(bonuses);
		}
		
		if (document.containsKey("standAnimation"))
			item.setStandAnimation(document.getInteger("standAnimation"));
		
		if (document.containsKey("walkAnimation"))
			item.setWalkAnimation(document.getInteger("walkAnimation"));
		
		if (document.containsKey("runAnimation"))
			item.setRunAnimation(document.getInteger("runAnimation"));
		
		if (document.containsKey("attackAnimation"))
			item.setAttackAnimation(document.getInteger("attackAnimation"));
		
		if (document.containsKey("blockAnimation"))
			item.setBlockAnimation(document.getInteger("blockAnimation"));
		
		if (document.containsKey("attackDelay"))
			item.setAttackDelay(document.getInteger("attackDelay"));

		if (document.containsKey("requirements")) {
			// I'm sure that it returns an ArrayList<Document>...
			for (Document doc : (ArrayList<Document>) document.get("requirements"))
				item.addRequirement(new ItemRequirement(doc.getInteger("skill"), doc.getInteger("level")));
		}
		
		return item;
	}
	
}
