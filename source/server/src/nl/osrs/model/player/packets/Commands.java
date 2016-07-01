package nl.osrs.model.player.packets;

import nl.osrs.GameEngine;
import nl.osrs.Settings;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.script.ScriptLoader;
import nl.osrs.util.Misc;

/**
 * Commands
 **/
public class Commands implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		String playerCommand = c.getInStream().readString().toLowerCase();
		
		Misc.println(c.playerName + " playerCommand: " + playerCommand);
		
		String[] split = playerCommand.split(" ");
		
		String[] arguments = null;
		
		if (split.length > 1) {
			arguments = new String[split.length - 1];
			
			for (int i = 1; i < split.length; i++)
				arguments[i - 1] = split[i];
		}
		
		executeCommand(c, split[0], arguments);
		
		if (Settings.SERVER_DEBUG)
			if (playerCommand.startsWith("/") && playerCommand.length() > 1) {
				if (c.clanId >= 0) {
					System.out.println(playerCommand);
					playerCommand = playerCommand.substring(1);
					GameEngine.clanChat.playerMessageToClan(c.playerId,
							playerCommand, c.clanId);
				} else {
					if (c.clanId != -1)
						c.clanId = -1;
					c.sendMessage("You are not in a clan.");
				}
				return;
			}
	}
	
	private void executeCommand(Client client, String command, String[] arguments) {
		boolean success = false;
		
		try {
			if (arguments != null)
				success = ScriptLoader.executeScript("commands", command, client, arguments);
			else
				success = ScriptLoader.executeScript("commands", command, client);
		} catch (NullPointerException e) {
			//Supposedly an NPE may be thrown here if the function is null, but it's
			//obviously not... Very weird!
		}
				
		if (!success)
			client.sendMessage("Invalid command. Use ::commands for more information.");
	
	}
	
}
