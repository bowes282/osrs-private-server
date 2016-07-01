package nl.osrs.util;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import nl.osrs.mongo.MongoManager;

public class EquipmentSlots {
	
	public static String getEquipmentSlotName(int slot) {
		FindIterable<Document> result = MongoManager.executeQuery(
				(MongoDatabase db) -> db.getCollection("equipmentSlotNames").find(
						new Document("equipmentSlot", slot)));
		
		return result.first().getString("name");
	}

	public static ArrayList<Integer> getEquipmenSlotIds() {
		FindIterable<Document> result = MongoManager.executeQuery(
				(MongoDatabase db) -> db.getCollection("equipmentSlotNames").find());
		
		ArrayList<Integer> equipmentSlotIds = new ArrayList<>();
		
		for (Document doc : result)
			equipmentSlotIds.add(doc.getInteger("equipmentSlot"));
		
		return equipmentSlotIds;
	}

}
