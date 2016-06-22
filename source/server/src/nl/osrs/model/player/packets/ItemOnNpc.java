package nl.osrs.model.player.packets;

import nl.osrs.model.item.UseItem;
import nl.osrs.model.npc.NPCHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;


public class ItemOnNpc implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int itemId = c.getInStream().readSignedWordA();
		int i = c.getInStream().readSignedWordA();
		int slot = c.getInStream().readSignedWordBigEndian();
		int npcId = NPCHandler.npcs[i].npcType;
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		UseItem.ItemonNpc(c, itemId, npcId, slot);
	}
}
