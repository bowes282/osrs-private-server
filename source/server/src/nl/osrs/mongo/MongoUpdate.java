package nl.osrs.mongo;

import com.mongodb.client.MongoDatabase;

public interface MongoUpdate {
	
	public void execute(MongoDatabase database);
	
}
