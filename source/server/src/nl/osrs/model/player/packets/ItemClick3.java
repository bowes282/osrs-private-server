package nl.osrs.model.player.packets;

import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.script.ScriptLoader;

@SuppressWarnings("all")
public class ItemClick3 implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int itemId11 = c.getInStream().readSignedWordBigEndianA();
		int itemId1 = c.getInStream().readSignedWordA();
		int itemId = c.getInStream().readSignedWordA();

		
		System.out.println("Clicking a third option has not yet been implemented. Please notify a developer.");
		
		if (c.playerRights > 1)
			System.out.println("itemId11: " + itemId11 + ", itemId1: " + itemId1 + ", itemId: " + itemId);
		
		return;
		
//		c.getTaskScheduler().stopTasks();
//		c.getPA().removeAllWindows();
//		
//		if (itemId != c.playerItems[itemSlot] - 1)
//			return;
//		
//		c.clickedItemType = 3;
//		
//		ScriptLoader.executeScript("item", "click", c, ItemHandler.getItem(itemId), itemSlot);
		
	}
}