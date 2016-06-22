package nl.osrs.model.player.packets;

import java.util.HashMap;

import nl.osrs.GameEngine;
import nl.osrs.Settings;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.model.player.packets.command.Command;
import nl.osrs.util.Misc;

/**
 * Commands
 **/
public class Commands implements PacketType {
	private HashMap<String, Command> commands;
	
	public Commands() {
		initializeCommands();
	}
	
	private void initializeCommands() {
		commands = new HashMap<>();

		commands.put("npc",
				(String input, Client client) -> spawnTemporaryNpc(input, client));
		
		commands.put("spawn",
				(String input, Client client) -> spawnNpc(input, client));
		
		commands.put("item",
				(String input, Client client) -> spawnItem(input, client));
		
		commands.put("tele",
				(String input, Client client) -> teleport(input, client));
		
		commands.put("home",
				(String input, Client client) -> teleHome(input, client));
		
		commands.put("mypos",
				(String input, Client client) -> printPosition(input, client));
		
		commands.put("coords",
				(String input, Client client) -> printPosition(input, client));

		commands.put("bank",
				(String input, Client client) -> openBank(input, client));

		commands.put("objects",
				(String input, Client client) -> objectInfo(input, client));
		
		commands.put("anim", (String input, Client client) -> {
			String[] split = input.split(" ");
			int anim = 0;
			int ticks = 0;
			
			try {
				if (split.length > 1)
					anim = Integer.parseInt(split[1]);
				
				if (split.length > 2)
					ticks = Integer.parseInt(split[2]);
			} catch (NumberFormatException e) {
				client.sendMessage("Could not read animation request.");
			}
			
			if (ticks > 0)
				client.repeatAnimation(anim, ticks);
			else
				client.startAnimation(anim);
		});
		
		commands.put("refresh", (String input, Client client) -> client.getPA().refreshSkills());
		
	}
	
	private void spawnTemporaryNpc(String input, Client client) {
		if(input.length() > 4) {
			int npcId = Integer.parseInt(input.substring(4).split(" ")[0]);

			GameEngine.npcHandler.spawnNpc(client, npcId, client.absX, client.absY, client.heightLevel, 1, 3, 0, 1, 1, false, false);
		}
	}
	
	private void spawnNpc(String input, Client client) {
		if(input.length() > 6) {
			int npcId = Integer.parseInt(input.substring(6).split(" ")[0]);
		
			GameEngine.npcHandler.spawnNpc(client, npcId, client.absX, client.absY, client.heightLevel, 1, 50, 0, 1, 1, false, false);
		}
	}
	
	private void spawnItem(String input, Client client) {
		if(input.length() > 5) {
			int itemId = 0;
			int amount = 1;
			
			String[] parameters = input.substring(5).split(" ");
			
			if(parameters.length > 0)
				itemId = Integer.parseInt(parameters[0]);
			
			if(parameters.length > 1)
				amount = Integer.parseInt(parameters[1]);

			client.getItems().addItem(itemId, amount);
		}
	}
	
	private void teleport(String input, Client client) {
		if(input.length() > 5) {
			int x = 0;
			int y = 0;
			int height = 0;
			
			String[] parameters = input.substring(5).split(" ");
			
			if(parameters.length > 0)
				x = Integer.parseInt(parameters[0]);
			
			if(parameters.length > 1)
				y = Integer.parseInt(parameters[1]);
			
			if(parameters.length > 2)
				height = Integer.parseInt(parameters[2]);

			client.getPA().spellTeleport(x, y, height);
		}
	}
	
	private void teleHome(String input, Client client) {
		client.getPA().spellTeleport(2658 + Misc.random(1), 2659 + Misc.random(2), 0);
	}
	
	private void printPosition(String input, Client client) {
		GameEngine.clipboard.setClipboardContents(client.absX + ", " + client.absY);
		client.sendMessage("Player coordinates: " + client.absX + ", " + client.absY);
	}
	
	private void openBank(String input, Client client) {
		client.getPA().openUpBank();
	}
	
	private void objectInfo(String input, Client client) {
		
	}

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		String playerCommand = c.getInStream().readString().toLowerCase();
		
		Misc.println(c.playerName + " playerCommand: " + playerCommand);
		
		for(String command : commands.keySet())
			if(playerCommand.startsWith(command))
				commands.get(command).execute(playerCommand, c);
		
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
}
