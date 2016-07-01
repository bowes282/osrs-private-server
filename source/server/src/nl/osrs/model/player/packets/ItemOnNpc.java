package nl.osrs.model.player.packets;

import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.npc.NPC;
import nl.osrs.model.npc.NPCHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.script.ScriptLoader;
import nl.osrs.task.Task;


public class ItemOnNpc implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int itemId = c.getInStream().readSignedWordA();
		int i = c.getInStream().readSignedWordA();
		int slot = c.getInStream().readSignedWordBigEndian();
		NPC npc = NPCHandler.npcs[i];
		
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		
		if (itemId != c.playerItems[slot] - 1)
			return;
		
		c.getTaskScheduler().schedule(new Task(1) {
			@Override
			public void execute() {
				c.turnPlayerTo(npc.absX, npc.absY);
				
				if (ScriptLoader.executeScript("npc", "useItem", c, npc, ItemHandler.getItem(itemId), slot))
					this.stop();
			}
		});
	}
}
