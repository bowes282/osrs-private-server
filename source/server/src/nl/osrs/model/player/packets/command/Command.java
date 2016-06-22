package nl.osrs.model.player.packets.command;

import nl.osrs.model.player.Client;

public interface Command {
	public void execute(String command, Client client);
}
