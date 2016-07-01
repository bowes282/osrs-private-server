package nl.osrs.model.player.packets;

import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.script.ScriptLoader;
import nl.osrs.task.Task;

@SuppressWarnings("all")
public class ItemOnObject implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int a = c.getInStream().readUnsignedWord();
		int objectId = c.getInStream().readSignedWordBigEndian();
		int objectY = c.getInStream().readSignedWordBigEndianA();
		int b = c.getInStream().readUnsignedWord();
		int objectX = c.getInStream().readSignedWordBigEndianA();
		int itemId = c.getInStream().readUnsignedWord();
		
		c.objectId = objectId;
		c.objectX = objectX;
		c.objectY = objectY;
		
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		
		c.getTaskScheduler().schedule(new Task(1) {
			@Override
			public void execute() {
				c.turnPlayerTo(c.objectX, c.objectY);
				
				if (ScriptLoader.executeScript("object", "useItem", c, ItemHandler.getItem(itemId)))
					this.stop();
			}
		});
	}

}
