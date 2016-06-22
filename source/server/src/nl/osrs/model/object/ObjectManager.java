package nl.osrs.model.object;

import nl.osrs.model.player.Client;

/**
 * @author Sanity
 */

public class ObjectManager {
	
	public void process() {
		//Do nothing...
	}
	
	public void loadObjects(Client c) {
		if (c == null)
			return;
		loadCustomSpawns(c);
	}
	
	public void loadCustomSpawns(Client c) {
		c.getPA().checkObjectSpawn(-1, 0, 0, 0, 10);
		c.getPA().checkObjectSpawn(26814, 2650, 2660, 1, 10);
		
		//Remove Entrana Shit
		c.getPA().checkObjectSpawn(-1, 2814, 3368, 1, 10);
		c.getPA().checkObjectSpawn(-1, 2814, 3366, 1, 10);
		c.getPA().checkObjectSpawn(-1, 2813, 3369, 1, 10);
		c.getPA().checkObjectSpawn(-1, 2812, 3367, 1, 10);
		c.getPA().checkObjectSpawn(-1, 2814, 3364, 1, 10);
		c.getPA().checkObjectSpawn(-1, 2818, 3363, 1, 10);
		c.getPA().checkObjectSpawn(-1, 2819, 3362, 1, 10);
		c.getPA().checkObjectSpawn(-1, 2829, 3352, 1, 10);
		c.getPA().checkObjectSpawn(-1, 2829, 3353, 1, 10);
		c.getPA().checkObjectSpawn(-1, 2830, 3353, 1, 10);
		c.getPA().checkObjectSpawn(-1, 2831, 3353, 1, 10);
		
		//Home ores
		c.getPA().checkObjectSpawn(2090, 2654, 2658, 1, 10); //Copper
		c.getPA().checkObjectSpawn(2094, 2654, 2657, 1, 10); //Tin
		c.getPA().checkObjectSpawn(2092, 2653, 2657, 1, 10); //Iron
		c.getPA().checkObjectSpawn(2096, 2652, 2657, 1, 10); //Coal
		c.getPA().checkObjectSpawn(2102, 2651, 2657, 1, 10); //Mithril
		c.getPA().checkObjectSpawn(2104, 2650, 2657, 1, 10); //Adamantite
		c.getPA().checkObjectSpawn(2106, 2649, 2657, 1, 10); //Runite
		
		//Entrana
		c.getPA().checkObjectSpawn(2783, 2829, 3353, 1, 10); //Anvil
		c.getPA().checkObjectSpawn(2783, 2829, 3351, 1, 10); //Anvil
		
		//Entrana dungeon
		c.getPA().checkObjectSpawn(1747, 2827, 9771, 1, 10); //Ladder
		c.getPA().checkObjectSpawn(2090, 2832, 9778, 1, 10);
		c.getPA().checkObjectSpawn(2090, 2833, 9780, 1, 10);
		c.getPA().checkObjectSpawn(2090, 2833, 9779, 1, 10);
		c.getPA().checkObjectSpawn(2094, 2830, 9781, 1, 10);
		c.getPA().checkObjectSpawn(2094, 2829, 9780, 1, 10);
		c.getPA().checkObjectSpawn(2094, 2829, 9778, 1, 10);
		
		if (c.heightLevel == 1)
			c.getPA().checkObjectSpawn(-1, 0, 0, 0, 10);
	}

}