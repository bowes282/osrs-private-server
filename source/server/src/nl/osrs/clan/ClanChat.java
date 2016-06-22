package nl.osrs.clan;

import nl.osrs.GameEngine;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.util.Misc;

/**
 * Chat
 **/
public class ClanChat implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		String textSent = Misc.longToPlayerName2(c.getInStream().readQWord());
		textSent = textSent.replaceAll("_", " ");
		// c.sendMessage(textSent);
		GameEngine.clanChat.handleClanChat(c, textSent);
	}
}
