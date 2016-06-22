package nl.osrs.world;

import java.util.Map;
import java.util.HashMap;

public class WalkingCheck {
	private static Map<Integer, Boolean> tiles;
	
	public static Map<Integer, Boolean> getTiles() {
		if (tiles != null)
			return tiles;
		return loadTiles();
	}
	
	private static Map<Integer, Boolean> loadTiles() {
		tiles = new HashMap<Integer, Boolean>();
		
		
		
		return tiles;
	}
}
