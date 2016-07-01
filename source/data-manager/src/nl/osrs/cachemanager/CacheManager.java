package nl.osrs.cachemanager;

import java.io.IOException;
import java.util.logging.Level;

import nl.osrs.cachemanager.cache.Cache;
import nl.osrs.cachemanager.cache.ItemDef;

public class CacheManager {
	private static Cache cache;
	
	private static ItemDef[] itemDefinitions = null;
	
	public CacheManager() throws IOException {
		java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger( "org.mongodb.driver" );
	    mongoLogger.setLevel(Level.SEVERE);
	    
		CacheManager.cache = new Cache();
	}
	
	public static ItemDef[] getItemDefinitions() throws IOException {
		if (itemDefinitions == null)
			itemDefinitions = cache.loadItemArchive();
		
		return itemDefinitions;
	}
	
}
