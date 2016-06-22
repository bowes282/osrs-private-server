package nl.osrs.model.player.packets;

import nl.osrs.model.item.UseItem;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;

public class ItemOnItem implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int usedWithSlot = c.getInStream().readUnsignedWord();
		int itemUsedSlot = c.getInStream().readUnsignedWordA();
		int useWith = c.playerItems[usedWithSlot] - 1;
		int itemUsed = c.playerItems[itemUsedSlot] - 1;
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		UseItem.ItemonItem(c, itemUsed, useWith);
	}

}
