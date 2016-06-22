package nl.osrs.model.player.packets;

import nl.osrs.GameEngine;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;

public class ChangeRegion implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		c.getPA().removeObjects();
		GameEngine.objectManager.loadObjects(c);
	}

}
