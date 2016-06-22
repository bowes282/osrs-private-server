package nl.osrs.model.player.packets;

import nl.osrs.model.item.UseItem;
import nl.osrs.model.npc.NPC;
import nl.osrs.model.npc.NPCHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
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
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		
		c.getTaskScheduler().schedule(new Task(1) {
			@Override
			public void execute() {		
				if(c.goodDistance(c.getX(), c.getY(), objectX, objectY, 1)) {
					UseItem.ItemonObject(c, objectId, objectX, objectY, itemId);
					this.stop();
				}
			}
		});
	}

}
