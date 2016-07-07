package nl.osrs.util;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import nl.osrs.mongo.MongoManager;

public class Skills {
	
	public static String getSkillName(int skill) {
		FindIterable<Document> result = MongoManager.executeQuery(
				(MongoDatabase db) -> db.getCollection("skillNames").find(
						new Document("skill", skill)));
		
		return result.first().getString("name");
	}

	public static ArrayList<Integer> getSkillIds() {
		FindIterable<Document> result = MongoManager.executeQuery(
				(MongoDatabase db) -> db.getCollection("skillNames").find());
		
		ArrayList<Integer> skillids = new ArrayList<>();
		
		for (Document doc : result)
			skillids.add(doc.getInteger("skill"));
		
		return skillids;
	}
	
}
