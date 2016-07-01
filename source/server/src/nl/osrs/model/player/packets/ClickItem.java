package nl.osrs.model.player.packets;

import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.script.ScriptLoader;

/**
 * Clicking an item, bury bone, eat food etc
 **/
@SuppressWarnings("all")
public class ClickItem implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int junk = c.getInStream().readSignedWordBigEndianA();
		int itemSlot = c.getInStream().readUnsignedWordA();
		int itemId = c.getInStream().readUnsignedWordBigEndian();
		
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		
		if (itemId != c.playerItems[itemSlot] - 1)
			return;
		
		c.clickedItemType = 1;
		
		ScriptLoader.executeScript("item", "click", c, ItemHandler.getItem(itemId), itemSlot);
	}
}
