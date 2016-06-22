package nl.osrs.model.player.packets;


import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;


public class IdleLogout implements PacketType {
	
	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
	}
}