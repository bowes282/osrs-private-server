package nl.osrs.cachemanager;

import java.io.IOException;
import java.util.logging.Level;

import nl.osrs.cachemanager.cache.Cache;
import nl.osrs.cachemanager.cache.ItemDefinition;

public class CacheManager {
	private static Cache cache;
	
	public CacheManager() throws IOException {
		java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger( "org.mongodb.driver" );
	    mongoLogger.setLevel(Level.SEVERE);
	    
		CacheManager.cache = new Cache();
	}
	
	public static ItemDefinition[] getItemDefinitions() {
		return cache.loadItemDefinitions();
	}
	
}
