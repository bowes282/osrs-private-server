package nl.osrs.model.player.packets;

import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;

/**
 * Slient Packet
 **/
public class SilentPacket implements PacketType {
	
	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		
	}	
}
