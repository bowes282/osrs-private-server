package nl.osrs.model.player.packets;

import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.script.ScriptLoader;
import nl.osrs.task.Task;

/**
 * Click Object
 */
public class ClickObject implements PacketType {
	public static final int FIRST_CLICK = 132, SECOND_CLICK = 252,
			THIRD_CLICK = 70;

	@Override
	public void processPacket(final Client c, int packetType, int packetSize) {
		c.clickObjectType = c.objectX = c.objectId = c.objectY = 0;
		c.objectYOffset = c.objectXOffset = 0;
		c.getPA().resetFollow();
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		
		switch (packetType) {
			case FIRST_CLICK:
				c.objectX = c.getInStream().readSignedWordBigEndianA();
				c.objectId = c.getInStream().readUnsignedWord();
				c.objectY = c.getInStream().readUnsignedWordA();
				c.clickObjectType = 1;
				break;
	
			case SECOND_CLICK:
				c.objectId = c.getInStream().readUnsignedWordBigEndianA();
				c.objectY = c.getInStream().readSignedWordBigEndian();
				c.objectX = c.getInStream().readUnsignedWordA();
				c.clickObjectType = 2;
				break;
	
			case THIRD_CLICK:
				c.objectX = c.getInStream().readSignedWordBigEndian();
				c.objectY = c.getInStream().readUnsignedWord();
				c.objectId = c.getInStream().readUnsignedWordBigEndianA();
				c.clickObjectType = 3;
				break;
		}
		
		adjustCoordinatesForObject(c);
		
		c.getTaskScheduler().schedule(new Task(true) {
			@Override
			public void execute() {
				c.turnPlayerTo(c.objectX, c.objectY);
				
				if (ScriptLoader.executeScript("object", "click", c))
					this.stop();
			}
			@Override
			public void onStop() {
				c.clickObjectType = 0;
			}
		});
	}
	
	/**
	 * This will be deprecated soon as we load the object's actual size
	 * etc. from the client!
	 * @param client The client to adjust the coordinates for
	 */
	private void adjustCoordinatesForObject(Client client) {
		switch (client.objectId) {
		case 2781:
			if (client.objectX == 2833 && client.objectY == 3351) {
				client.objectX++;
				client.objectY++;
			}
			break;
		}
	}
}
