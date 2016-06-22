package nl.osrs.model.player.packets;

import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;

@SuppressWarnings("all")
public class ItemClick3 implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int itemId11 = c.getInStream().readSignedWordBigEndianA();
		int itemId1 = c.getInStream().readSignedWordA();
		int itemId = c.getInStream().readSignedWordA();
		switch (itemId) {
		}
	}
}