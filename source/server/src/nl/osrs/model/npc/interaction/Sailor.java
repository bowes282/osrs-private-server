package nl.osrs.model.npc.interaction;

import nl.osrs.model.player.Client;
import nl.osrs.util.Misc;

public class Sailor {
	
	/**
	 * Processes a player's request to the Sailor NPC.
	 * By default there are two interactions, teleporting Home or to Entrana.
	 * @param client
	 */
	public static void process(Client client) {
		if (Misc.isHome(client)) {
			client.getPA().spellTeleport(2834, 3336, 0);
		} else if (Misc.isOnEntrana(client)) {
			client.getPA().spellTeleport(2659, 2675, 0);
		}
	}
	
}
