var GameEngineClass = Java.type('nl.osrs.GameEngine');
var Client = Java.type('nl.osrs.model.player.Client');
var PlayerHandler = Java.type('nl.osrs.model.player.PlayerHandler');
var ScriptLoader = Java.type('nl.osrs.script.ScriptLoader');

function click(client, button) {
	switch (button) {
		case 58253:
		client.getPA().showInterface(15106);
		client.getItems().writeBonus();
		break;
		
		case 59004:
		client.getPA().removeAllWindows();
		break;
		
		case 70212:
			if (client.clanId > -1)
				GameEngineClass.clanChat.leaveClan(client.playerId, client.clanId);
			else
				client.sendMessage("You are not in a clan.");
		break;
		case 62137:
			if (client.clanId >= 0) {
				client.sendMessage("You are already in a clan.");
				break;
			}
			if (client.getOutStream() != null) {
				client.getOutStream().createFrame(187);
				client.flushOutStream();
			}	
		break;
		/**
		 * Clicking Options
		 * 9178 - 9181
		 */
		case 9178:
		
		break;
		
		case 9179:
			
		break;
		
		case 9180:
			
		break;
		
		case 9181:
			
		break;
		
		/**Dueling**/			
		case 26065: // no forfeit
		case 26040:
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(0);
		break;
		
		case 26066: // no movement
		case 26048:
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(1);
		break;
		
		case 26069: // no range
		case 26042:
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(2);
		break;
		
		case 26070: // no melee
		case 26043:
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(3);
		break;				
		
		case 26071: // no mage
		case 26041:
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(4);
		break;
			
		case 26072: // no drinks
		case 26045:
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(5);
		break;
		
		case 26073: // no food
		case 26046:
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(6);
		break;
		
		case 26074: // no prayer
		case 26047:	
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(7);
		break;
		
		case 26076: // obsticals
		case 26075:
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(8);
		break;
		
		case 2158: // fun weapons
		case 2157:
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(9);
		break;
		
		case 30136: // sp attack
		case 30137:
		client.duelSlot = -1;
		client.getTradeAndDuel().selectRule(10);
		break;	

		case 53245: //no helm
		client.duelSlot = 0;
		client.getTradeAndDuel().selectRule(11);
		break;
		
		case 53246: // no cape
		client.duelSlot = 1;
		client.getTradeAndDuel().selectRule(12);
		break;
		
		case 53247: // no ammy
		client.duelSlot = 2;
		client.getTradeAndDuel().selectRule(13);
		break;
		
		case 53249: // no weapon.
		client.duelSlot = 3;
		client.getTradeAndDuel().selectRule(14);
		break;
		
		case 53250: // no body
		client.duelSlot = 4;
		client.getTradeAndDuel().selectRule(15);
		break;
		
		case 53251: // no shield
		client.duelSlot = 5;
		client.getTradeAndDuel().selectRule(16);
		break;
		
		case 53252: // no legs
		client.duelSlot = 7;
		client.getTradeAndDuel().selectRule(17);
		break;
		
		case 53255: // no gloves
		client.duelSlot = 9;
		client.getTradeAndDuel().selectRule(18);
		break;
		
		case 53254: // no boots
		client.duelSlot = 10;
		client.getTradeAndDuel().selectRule(19);
		break;
		
		case 53253: // no rings
		client.duelSlot = 12;
		client.getTradeAndDuel().selectRule(20);
		break;
		
		case 53248: // no arrows
		client.duelSlot = 13;
		client.getTradeAndDuel().selectRule(21);
		break;
		
		
		case 26018:	
		var o = PlayerHandler.players[client.duelingWith];
		if(o == null) {
			client.getTradeAndDuel().declineDuel();
			return;
		}
		
		if(client.duelRule[2] && client.duelRule[3] && client.duelRule[4]) {
			client.sendMessage("You won't be able to attack the player with the rules you have set.");
			break;
		}
		client.duelStatus = 2;
		if(client.duelStatus == 2) {
			client.getPA().sendFrame126("Waiting for other player...", 6684);
			o.getPA().sendFrame126("Other player has accepted.", 6684);
		}
		if(o.duelStatus == 2) {
			o.getPA().sendFrame126("Waiting for other player...", 6684);
			client.getPA().sendFrame126("Other player has accepted.", 6684);
		}
		
		if(client.duelStatus == 2 && o.duelStatus == 2) {
			client.canOffer = false;
			o.canOffer = false;
			client.duelStatus = 3;
			o.duelStatus = 3;
			client.getTradeAndDuel().confirmDuel();
			o.getTradeAndDuel().confirmDuel();
		}
		break;
		
		case 25120:
		if(client.duelStatus == 5) {
			break;
		}
		var o1 = PlayerHandler.players[client.duelingWith];
		if(o1 == null) {
			client.getTradeAndDuel().declineDuel();
			return;
		}

		client.duelStatus = 4;
		if(o1.duelStatus == 4 && client.duelStatus == 4) {				
			client.getTradeAndDuel().startDuel();
			o1.getTradeAndDuel().startDuel();
			o1.duelCount = 4;
			client.duelCount = 4;

			client.getTaskScheduler().schedule(new Task({
				immediate: false,
				delay: 4,
				countdown: 4,
				running: true,

				execute: function() {
					if(System.currentTimeMillis() - client.duelDelay > 800 && client.duelCount > 0) {
						if(client.duelCount != 1) {
							client.forcedChat(""+(--client.duelCount));
							client.duelDelay = System.currentTimeMillis();
						} else {
							client.forcedChat("FIGHT!");
							client.duelCount = 0;
						}
					}
					if (client.duelCount == 0) {
						this.stop();
					}
				},
			}));

			client.duelDelay = System.currentTimeMillis();
			o1.duelDelay = System.currentTimeMillis();
		} else {
			client.getPA().sendFrame126("Waiting for other player...", 6571);
			o1.getPA().sendFrame126("Other player has accepted", 6571);
		}
		break;
		
		case 152:
		client.isRunning2 = !client.isRunning2;
		var frame = client.isRunning2 == true ? 1 : 0;
		client.getPA().sendFrame36(173,frame);
		break;
		
		case 9154:
		client.logout();
		break;
		
		case 13092:
		var ot = PlayerHandler.players[client.tradeWith];
		if(ot == null) {
			client.getTradeAndDuel().declineTrade();
			client.sendMessage("Trade declined as the other player has disconnected.");
			break;
		}
		client.getPA().sendFrame126("Waiting for other player...", 3431);
		ot.getPA().sendFrame126("Other player has accepted", 3431);	
		client.goodTrade= true;
		ot.goodTrade= true;
		
		for (var item in client.getTradeAndDuel().offeredItems) {
			if (item.id > 0) {
				if(ot.getItems().freeSlots() < client.getTradeAndDuel().offeredItems.size()) {					
					client.sendMessage(ot.playerName +" only has "+ot.getItems().freeSlots()+" free slots, please remove "+(client.getTradeAndDuel().offeredItems.size() - ot.getItems().freeSlots())+" items.");
					ot.sendMessage(client.playerName +" has to remove "+(client.getTradeAndDuel().offeredItems.size() - ot.getItems().freeSlots())+" items or you could offer them "+(client.getTradeAndDuel().offeredItems.size() - ot.getItems().freeSlots())+" items.");
					client.goodTrade= false;
					ot.goodTrade= false;
					client.getPA().sendFrame126("Not enough inventory space...", 3431);
					ot.getPA().sendFrame126("Not enough inventory space...", 3431);
						break;
				} else {
					client.getPA().sendFrame126("Waiting for other player...", 3431);				
					ot.getPA().sendFrame126("Other player has accepted", 3431);
					client.goodTrade= true;
					ot.goodTrade= true;
					}
				}	
			}	
			if (client.inTrade && !client.tradeConfirmed && ot.goodTrade && client.goodTrade) {
				client.tradeConfirmed = true;
				if(ot.tradeConfirmed) {
					client.getTradeAndDuel().confirmScreen();
					ot.getTradeAndDuel().confirmScreen();
					break;
				}
						  
			}


		break;
				
		case 13218:
		client.tradeAccepted = true;
		var ot1 = PlayerHandler.players[client.tradeWith];
			if (ot1 == null) {
				client.getTradeAndDuel().declineTrade();
				client.sendMessage("Trade declined as the other player has disconnected.");
				break;
			}
			
			if (client.inTrade && client.tradeConfirmed && ot1.tradeConfirmed && !client.tradeConfirmed2) {
				client.tradeConfirmed2 = true;
				if(ot1.tradeConfirmed2) {	
					client.acceptedTrade = true;
					ot1.acceptedTrade = true;
					client.getTradeAndDuel().giveItems();
					ot1.getTradeAndDuel().giveItems();
					break;
				}
			ot1.getPA().sendFrame126("Other player has accepted.", 3535);
			client.getPA().sendFrame126("Waiting for other player...", 3535);
			}
			
		break;		
		/* Rules Interface Buttons */
		case 125011: //Click agree
			if(!client.ruleAgreeButton) {
				client.ruleAgreeButton = true;
				client.getPA().sendFrame36(701, 1);
			} else {
				client.ruleAgreeButton = false;
				client.getPA().sendFrame36(701, 0);
			}
			break;
		case 125003://Accept
			if(client.ruleAgreeButton) {
				client.getPA().showInterface(3559);
				client.newPlayer = false;
			} else if(!client.ruleAgreeButton) {
				client.sendMessage("You need to click on you agree before you can continue on.");
			}
			break;
		case 125006://Decline
			client.sendMessage("You have chosen to decline, Client will be disconnected from the server.");
			break;
		/* End Rules Interface Buttons */
		/* Player Options */
		case 74176:
			if(!client.mouseButton) {
				client.mouseButton = true;
				client.getPA().sendFrame36(500, 1);
				client.getPA().sendFrame36(170,1);
			} else if(client.mouseButton) {
				client.mouseButton = false;
				client.getPA().sendFrame36(500, 0);
				client.getPA().sendFrame36(170,0);					
			}
			break;
		case 74184:
			if(!client.splitChat) {
				client.splitChat = true;
				client.getPA().sendFrame36(502, 1);
				client.getPA().sendFrame36(287, 1);
			} else {
				client.splitChat = false;
				client.getPA().sendFrame36(502, 0);
				client.getPA().sendFrame36(287, 0);
			}
			break;
		case 74180:
			if(!client.chatEffects) {
				client.chatEffects = true;
				client.getPA().sendFrame36(501, 1);
				client.getPA().sendFrame36(171, 0);
			} else {
				client.chatEffects = false;
				client.getPA().sendFrame36(501, 0);
				client.getPA().sendFrame36(171, 1);
			}
			break;
		case 74188:
			if(!client.acceptAid) {
				client.acceptAid = true;
				client.getPA().sendFrame36(503, 1);
				client.getPA().sendFrame36(427, 1);
			} else {
				client.acceptAid = false;
				client.getPA().sendFrame36(503, 0);
				client.getPA().sendFrame36(427, 0);
			}
			break;
		case 74192:
			if(!client.isRunning2) {
				client.isRunning2 = true;
				client.getPA().sendFrame36(504, 1);
				client.getPA().sendFrame36(173, 1);
			} else {
				client.isRunning2 = false;
				client.getPA().sendFrame36(504, 0);
				client.getPA().sendFrame36(173, 0);
			}
			break;
		case 74201://brightness1
			client.getPA().sendFrame36(505, 1);
			client.getPA().sendFrame36(506, 0);
			client.getPA().sendFrame36(507, 0);
			client.getPA().sendFrame36(508, 0);
			client.getPA().sendFrame36(166, 1);
			break;
		case 74203://brightness2
			client.getPA().sendFrame36(505, 0);
			client.getPA().sendFrame36(506, 1);
			client.getPA().sendFrame36(507, 0);
			client.getPA().sendFrame36(508, 0);
			client.getPA().sendFrame36(166,2);
			break;

		case 74204://brightness3
			client.getPA().sendFrame36(505, 0);
			client.getPA().sendFrame36(506, 0);
			client.getPA().sendFrame36(507, 1);
			client.getPA().sendFrame36(508, 0);
			client.getPA().sendFrame36(166,3);
			break;

		case 74205://brightness4
			client.getPA().sendFrame36(505, 0);
			client.getPA().sendFrame36(506, 0);
			client.getPA().sendFrame36(507, 0);
			client.getPA().sendFrame36(508, 1);
			client.getPA().sendFrame36(166,4);
			break;
		case 74206://area1
			client.getPA().sendFrame36(509, 1);
			client.getPA().sendFrame36(510, 0);
			client.getPA().sendFrame36(511, 0);
			client.getPA().sendFrame36(512, 0);
			break;
		case 74207://area2
			client.getPA().sendFrame36(509, 0);
			client.getPA().sendFrame36(510, 1);
			client.getPA().sendFrame36(511, 0);
			client.getPA().sendFrame36(512, 0);
			break;
		case 74208://area3
			client.getPA().sendFrame36(509, 0);
			client.getPA().sendFrame36(510, 0);
			client.getPA().sendFrame36(511, 1);
			client.getPA().sendFrame36(512, 0);
			break;
		case 74209://area4
			client.getPA().sendFrame36(509, 0);
			client.getPA().sendFrame36(510, 0);
			client.getPA().sendFrame36(511, 0);
			client.getPA().sendFrame36(512, 1);
			break;
			
		case 83093:
			client.getPA().showInterface(15106);
			break;
			
		case 15147:
		case 15151:
		case 15155:
		case 15159:
		case 15163:
		case 29017:
		case 29022:
		case 29026:
			client.getPA().removeAllWindows();
			ScriptLoader.executeScript('skills.smelting@smelt', client, button);
			break;

		//Attack
		case 6168:
		case 8234:
		case 9125:
		case 14218:
		case 21200:
		case 22228:
			client.fightMode = 0;
			break;
			
		//Strength
		case 6170:
		case 6171:
		case 8236:
		case 8237:
		case 9128:
		case 14221:
		case 21202:
		case 21203:
		case 22230:
		case 33020:
			client.fightMode = 2;
			break;
			
		//Defence
		case 6169:
		case 8235:
		case 9126:
		case 14219:
		case 18078:
		case 21201:
		case 22229:	
		case 33019:
			client.fightMode = 1;
			break;
			
		//Shared
		case 9127:
		case 14220:
		case 18077:
		case 18080:
		case 18079:
		case 33018:
			client.fightMode = 3;
			break;
	}
	if (client.isAutoButton(button))
		client.assignAutocast(button);
}
