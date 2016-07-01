package nl.osrs.mongo;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public interface MongoQuery {
	
	public FindIterable<Document> execute(MongoDatabase database);
	
}
