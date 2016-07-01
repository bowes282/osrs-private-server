package nl.osrs.model.player.packets;

import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.script.ScriptLoader;

public class ItemOnItem implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int usedWithSlot = c.getInStream().readUnsignedWord();
		int itemUsedSlot = c.getInStream().readUnsignedWordA();
		int useWith = c.playerItems[usedWithSlot] - 1;
		int itemUsed = c.playerItems[itemUsedSlot] - 1;
		
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		
		if (itemUsed != c.playerItems[itemUsedSlot] - 1)
			return;
		
		if (useWith != c.playerItems[usedWithSlot] - 1)
			return;
		
		ScriptLoader.executeScript("item", "useItem", c, ItemHandler.getItem(itemUsed), itemUsedSlot, ItemHandler.getItem(useWith), usedWithSlot);
	}

}
