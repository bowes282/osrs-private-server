package nl.osrs.world;

import nl.osrs.GameEngine;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.Player;


/**
 * Handles still graphics
 * 
 * @author Graham
 * 
 */
public class StillGraphicsManager {

	/**
	 * Nothing to load, as of yet.
	 */
	public StillGraphicsManager() {
	}

	public void stillGraphics(Client curPlr, int absX, int absY,
			int heightLevel, int id, int pause) {
		for(Player p : GameEngine.getPlayerManager().getClientRegion((curPlr).currentRegion)){
			if (p == null)
				continue;
			if (!p.isActive)
				continue;
			if (p.disconnected)
				continue;
			Client c = (Client) p;
			if (c == curPlr || c.withinDistance(absX, absY, heightLevel)) {
				c.getPA().sendStillGraphics(id, heightLevel, absY, absX, pause);
			}
		}
	}

}
