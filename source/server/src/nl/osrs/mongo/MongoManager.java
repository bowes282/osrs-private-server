package nl.osrs.mongo;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class MongoManager {
	private static MongoClient client = new MongoClient(
			new ServerAddress("localhost"),
			new MongoClientOptions.Builder()
			.codecRegistry(MongoClient.getDefaultCodecRegistry())
			.build());
	
	public static synchronized FindIterable<Document> executeQuery(MongoQuery query) {
		MongoDatabase database = client.getDatabase("osrs");
		
		FindIterable<Document> result = query.execute(database);
		
		return result;
	}
	
	public static synchronized void executeUpdate(MongoUpdate update) {
		MongoDatabase database = client.getDatabase("osrs");
		
		update.execute(database);
	}
	
}
