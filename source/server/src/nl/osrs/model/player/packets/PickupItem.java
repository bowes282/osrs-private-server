package nl.osrs.model.player.packets;

import nl.osrs.GameEngine;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.task.Task;

/**
 * Pickup Item
 **/
public class PickupItem implements PacketType {

	@Override
	public void processPacket(final Client c, int packetType, int packetSize) {
		c.walkingToItem = false;
		c.pItemY = c.getInStream().readSignedWordBigEndian();
		c.pItemId = c.getInStream().readUnsignedWord();
		c.pItemX = c.getInStream().readSignedWordBigEndian();
		if (Math.abs(c.getX() - c.pItemX) > 25 || Math.abs(c.getY() - c.pItemY) > 25) {
			c.resetWalkingQueue();
			return;
		}
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		c.getCombat().resetPlayerAttack();
		if(c.getX() == c.pItemX && c.getY() == c.pItemY) {
			GameEngine.itemHandler.removeGroundItem(c, c.pItemId, c.pItemX, c.pItemY, true);
		} else {
			c.walkingToItem = true;
			c.getTaskScheduler().schedule(new Task(1) {
				@Override
				public void execute() {
					if(!c.walkingToItem)
						this.stop();
					if(c.getX() == c.pItemX && c.getY() == c.pItemY) {
						GameEngine.itemHandler.removeGroundItem(c, c.pItemId, c.pItemX, c.pItemY, true);
						this.stop();
					}
				}
				@Override
				public void onStop() {
					c.walkingToItem = false;
				}
			});
		}
	}
}
