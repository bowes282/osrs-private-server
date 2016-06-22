package nl.osrs.model.player.packets;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import nl.osrs.model.player.Client;

/**
 * Player manager
 * 
 * @author Daiki
 * @author Perfectworld
 * 
 */
@SuppressWarnings("all")
public class PlayerManager {

	private static PlayerManager singleton = null;
	private Map<Integer, Queue<Client>> playersByRegion = new HashMap<Integer, Queue<Client>>();;
	public final int areaSize = 26;

	public static PlayerManager getSingleton() {
		if (singleton == null) {
			singleton = new PlayerManager();
		}
		return singleton;
	}

	public void setupRegionPlayers() {
		int hash = 0;
		for (int i = 0; i < 9999; i += areaSize) {// each region will be 24
													// squares big lol this will
													// eb teh x axis
			for (int j = 0; j < 12500; j += areaSize) {// y axis
				int g = ((i / areaSize)) + ((j / areaSize) * 100);
				Queue<Client> he = new LinkedList<Client>();
				playersByRegion.put(g, he);
				hash++;
			}
		}
	}

	public Queue<Client> getClientRegion(int id) {
		return playersByRegion.get(id);
	}
}
