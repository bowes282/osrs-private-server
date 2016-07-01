package nl.osrs.mongo;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class MongoManager {
	private static MongoClient client = new MongoClient();
	
	public static synchronized FindIterable<Document> executeQuery(MongoQuery query) {
		MongoDatabase database = client.getDatabase("osrs");
		
		FindIterable<Document> result = query.execute(database);
		
		return result;
	}
	
}
