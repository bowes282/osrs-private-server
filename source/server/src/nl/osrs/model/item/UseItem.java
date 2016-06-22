package nl.osrs.model.item;

import nl.osrs.model.player.Client;
import nl.osrs.model.player.skills.smithing.Smithing;

public class UseItem {
	
	public static void ItemonObject(Client c, int objectID, int objectX,
			int objectY, int itemId) {
		if (!c.getItems().playerHasItem(itemId, 1))
			return;
		
		c.turnPlayerTo(objectX, objectY);
		
		switch (objectID) {
		case 2783:
			Smithing.openSmithingInterface(c, itemId);
			break;
		}
	}

	public static void ItemonItem(Client c, int itemUsed, int useWith) {
		switch (itemUsed) {
		}
	}

	public static void ItemonNpc(Client c, int itemId, int npcId, int slot) {
		switch (itemId) {
		}
	}
}