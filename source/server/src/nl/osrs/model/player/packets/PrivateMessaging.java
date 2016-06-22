package nl.osrs.model.player.packets;

import nl.osrs.Settings;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.model.player.PlayerHandler;
import nl.osrs.util.Misc;

/**
 * Private messaging, friends etc
 **/
@SuppressWarnings("all")
public class PrivateMessaging implements PacketType {

	public final int ADD_FRIEND = 188, SEND_PM = 126, REMOVE_FRIEND = 215,
			CHANGE_PM_STATUS = 95, REMOVE_IGNORE = 59, ADD_IGNORE = 133;

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		switch (packetType) {

		case ADD_FRIEND:
			c.friendUpdate = true;
			long friendToAdd = c.getInStream().readQWord();
			boolean canAdd = true;

			for (int i1 = 0; i1 < c.friends.length; i1++) {
				if (c.friends[i1] != 0 && c.friends[i1] == friendToAdd) {
					canAdd = false;
					c.sendMessage(friendToAdd
							+ " is already on your friends list.");
				}
			}
			if (canAdd == true) {
				for (int i1 = 0; i1 < c.friends.length; i1++) {
					if (c.friends[i1] == 0) {
						c.friends[i1] = friendToAdd;
						for (int i2 = 1; i2 < Settings.MAX_PLAYERS; i2++) {
							if (PlayerHandler.players[i2] != null
									&& PlayerHandler.players[i2].isActive
									&& Misc.playerNameToInt64(PlayerHandler.players[i2].playerName) == friendToAdd) {
								Client o = (Client) PlayerHandler.players[i2];
								if (o != null) {
									if (PlayerHandler.players[i2].privateChat == 0
											|| (PlayerHandler.players[i2].privateChat == 1 && o
													.getPA()
													.isInPM(Misc
															.playerNameToInt64(c.playerName)))) {
										c.getPA().loadPM(friendToAdd, 1);
										break;
									}
								}
							}
						}
						break;
					}
				}
			}
			break;

		case SEND_PM:
			long sendMessageToFriendId = c.getInStream().readQWord();
			byte pmchatText[] = new byte[100];
			int pmchatTextSize = (byte) (packetSize - 8);
			c.getInStream().readBytes(pmchatText, pmchatTextSize, 0);
			for (int i1 = 0; i1 < c.friends.length; i1++) {
				if (c.friends[i1] == sendMessageToFriendId) {
					boolean pmSent = false;

					for (int i2 = 1; i2 < Settings.MAX_PLAYERS; i2++) {
						if (PlayerHandler.players[i2] != null
								&& PlayerHandler.players[i2].isActive
								&& Misc.playerNameToInt64(PlayerHandler.players[i2].playerName) == sendMessageToFriendId) {
							Client o = (Client) PlayerHandler.players[i2];
							if (o != null) {
								if (PlayerHandler.players[i2].privateChat == 0
										|| (PlayerHandler.players[i2].privateChat == 1 && o
												.getPA()
												.isInPM(Misc
														.playerNameToInt64(c.playerName)))) {
									o.getPA()
											.sendPM(Misc
													.playerNameToInt64(c.playerName),
													c.playerRights, pmchatText,
													pmchatTextSize);
									pmSent = true;
								}
							}
							break;
						}
					}
					if (!pmSent) {
						c.sendMessage("That player is currently offline.");
						break;
					}
				}
			}
			break;

		case REMOVE_FRIEND:
			c.friendUpdate = true;
			long friendToRemove = c.getInStream().readQWord();

			for (int i1 = 0; i1 < c.friends.length; i1++) {
				if (c.friends[i1] == friendToRemove) {
					for (int i2 = 1; i2 < Settings.MAX_PLAYERS; i2++) {
						Client o = (Client) PlayerHandler.players[i2];
						if (o != null) {
							if (c.friends[i1] == Misc
									.playerNameToInt64(PlayerHandler.players[i2].playerName)) {
								o.getPA().updatePM(c.playerId, 0);
								break;
							}
						}
					}
					c.friends[i1] = 0;
					break;
				}
			}
			break;

		case REMOVE_IGNORE:
			int i = c.getInStream().readDWord();
			int i2 = c.getInStream().readDWord();
			int i3 = c.getInStream().readDWord();
			// for other status changing
			c.getPA().handleStatus(i, i2, i3);
			break;

		case CHANGE_PM_STATUS:
			int tradeAndCompete = c.getInStream().readUnsignedByte();
			c.privateChat = c.getInStream().readUnsignedByte();
			int publicChat = c.getInStream().readUnsignedByte();
			for (int i1 = 1; i1 < Settings.MAX_PLAYERS; i1++) {
				if (PlayerHandler.players[i1] != null
						&& PlayerHandler.players[i1].isActive == true) {
					Client o = (Client) PlayerHandler.players[i1];
					if (o != null) {
						o.getPA().updatePM(c.playerId, 1);
					}
				}
			}
			break;

		case ADD_IGNORE:
			// TODO: fix this so it works :)
			break;

		}

	}
}
