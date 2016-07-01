package nl.osrs.model.player.packets;

import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.script.ScriptLoader;
import nl.osrs.task.Task;

@SuppressWarnings("all")
public class ItemOnGroundItem implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int a1 = c.getInStream().readSignedWordBigEndian();
		int itemUsed = c.getInStream().readSignedWordA();
		int groundItem = c.getInStream().readUnsignedWord();
		int gItemY = c.getInStream().readSignedWordA();
		int itemUsedSlot = c.getInStream().readSignedWordBigEndianA();
		int gItemX = c.getInStream().readUnsignedWord();
		
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		
		c.getTaskScheduler().schedule(new Task(1, true) {
			@Override
			protected void execute() {
					if (ScriptLoader.executeScript("item",
							"useItemOnGroundItem", c, ItemHandler.getItem(itemUsed),
							itemUsedSlot, ItemHandler.getItem(groundItem), gItemX, gItemY))
						this.stop();
			}
		});
	}

}
