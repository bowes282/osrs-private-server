package nl.osrs.model.player.packets;

import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.model.player.PlayerHandler;
import nl.osrs.util.Misc;


/**
 * Clicking stuff (interfaces)
 **/
public class ClickingStuff implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		if (c.inTrade) {
			if(!c.acceptedTrade) {
				Misc.println("trade reset");
				c.getTradeAndDuel().declineTrade();
			}
		}

		Client o = (Client) PlayerHandler.players[c.duelingWith];
		if(o != null) {
			if(c.duelStatus >= 1 && c.duelStatus <= 4) {
				c.getTradeAndDuel().declineDuel();
				o.getTradeAndDuel().declineDuel();
			}
		}
		
		if(c.duelStatus == 6) {
			c.getTradeAndDuel().claimStakedItems();		
		}
	
	}
		
}
