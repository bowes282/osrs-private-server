package nl.osrs.model.item;

import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;


/**
 * Wear Item
 **/
@SuppressWarnings("all")
public class WearItem implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		c.wearId = c.getInStream().readUnsignedWord();
		c.wearSlot = c.getInStream().readUnsignedWordA();
		c.interfaceId = c.getInStream().readUnsignedWordA();
		
		int oldCombatTimer = c.attackTimer;
		if (c.playerIndex > 0 || c.npcIndex > 0)
			c.getCombat().resetPlayerAttack();
		c.getItems().wearItem(c.wearId, c.wearSlot);
	}
}