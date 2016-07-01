package nl.osrs.model.player.packets;

import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.model.player.Player;
import nl.osrs.script.ScriptLoader;
import nl.osrs.util.Misc;

/**
 * Clicking most buttons
 **/
public class ClickingButtons implements PacketType {
	
	public static Player p;
	@Override
	public void processPacket(final Client c, int packetType, int packetSize) {
		int actionButtonId = Misc.hexToInt(c.getInStream().buffer, 0, packetSize);
		//int actionButtonId = c.getInStream().readShort();
		if (c.isDead)
			return;
		if(c.playerRights == 3)	
			Misc.println(c.playerName+ " - actionbutton: "+actionButtonId);
		
		ScriptLoader.executeScript("actionButton", "click", c, actionButtonId);
	}
}