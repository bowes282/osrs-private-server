package nl.osrs.model.player.packets;

import nl.osrs.GameEngine;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;

/**
 * Change Regions
 */
public class ChangeRegions implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		//Server.objectHandler.updateObjects(c);
		GameEngine.itemHandler.reloadItems(c);
		GameEngine.objectManager.loadObjects(c);
		c.saveFile = true;
		if(c.skullTimer > 0) {
			c.isSkulled = true;	
			c.headIconPk = 0;
			c.getPA().requestUpdates();
		}
	}
}