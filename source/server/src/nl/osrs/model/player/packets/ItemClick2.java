package nl.osrs.model.player.packets;

import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.script.ScriptLoader;

public class ItemClick2 implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int itemId = c.getInStream().readSignedWordA();
		int itemSlot = c.getInStream().readUnsignedByteA();
		
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		
		if (itemId != c.playerItems[itemSlot] - 1)
			return;
		
		c.clickedItemType = 2;
		
		ScriptLoader.executeScript("item", "click", c, ItemHandler.getItem(itemId), itemSlot);
	}
}