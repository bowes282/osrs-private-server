package nl.osrs.model.player.packets;

import nl.osrs.Settings;
import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.npc.NPC;
import nl.osrs.model.npc.NPCHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PacketType;
import nl.osrs.model.player.Player;
import nl.osrs.task.Task;

/**
 * Click NPC
 */
public class ClickNPC implements PacketType {
	public static final int ATTACK_NPC = 72, MAGE_NPC = 131, FIRST_CLICK = 155, SECOND_CLICK = 17, THIRD_CLICK = 21;
	@Override
	public void processPacket(final Client c, int packetType, int packetSize) {
		c.npcIndex = 0;
		c.npcClickIndex = 0;
		c.playerIndex = 0;
		c.clickNpcType = 0;
		c.getPA().resetFollow();
		c.getTaskScheduler().stopTasks();
		c.getPA().removeAllWindows();
		switch(packetType) {

		/**
		 * Attack npc melee or range
		 **/
		case ATTACK_NPC:
			if (!c.mageAllowed) {
				c.mageAllowed = true;
				c.sendMessage("I can't reach that.");
				break;
			}
			c.npcIndex = c.getInStream().readUnsignedWordA();
			if (NPCHandler.npcs[c.npcIndex] == null) {
				c.npcIndex = 0;
				break;
			}	
			if (NPCHandler.npcs[c.npcIndex].MaxHP == 0) {
				c.npcIndex = 0;
				break;
			}			
			if(NPCHandler.npcs[c.npcIndex] == null){
				break;
			}
			if (c.autocastId > 0)
				c.autocasting = true;			
			if (!c.autocasting && c.spellId > 0) {
				c.spellId = 0;
			}
			c.faceUpdate(c.npcIndex);			
			c.usingMagic = false;
			boolean usingBow = false;
			boolean usingOtherRangeWeapons = false;
			boolean usingArrows = false;
			boolean usingCross = c.playerEquipment[Player.playerWeapon] == 9185;
			if (c.playerEquipment[Player.playerWeapon] >= 4214 && c.playerEquipment[Player.playerWeapon] <= 4223)
				usingBow = true;
			for (int bowId : c.BOWS) {
				if(c.playerEquipment[Player.playerWeapon] == bowId) {
					usingBow = true;
					for (int arrowId : c.ARROWS) {
						if(c.playerEquipment[Player.playerArrows] == arrowId) {
							usingArrows = true;
						}
					}
				}
			}
			for (int otherRangeId : c.OTHER_RANGE_WEAPONS) {
				if(c.playerEquipment[Player.playerWeapon] == otherRangeId) {
					usingOtherRangeWeapons = true;
				}
			}
			if((usingBow || c.autocasting) && c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[c.npcIndex].getX(), NPCHandler.npcs[c.npcIndex].getY(), 7)) {
				c.stopMovement();
			}

			if(usingOtherRangeWeapons && c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[c.npcIndex].getX(), NPCHandler.npcs[c.npcIndex].getY(), 4)) {
				c.stopMovement();
			}
			if(!usingCross && !usingArrows && usingBow && c.playerEquipment[Player.playerWeapon] < 4212 && c.playerEquipment[Player.playerWeapon] > 4223 && !usingCross) {
				c.sendMessage("You have run out of arrows!");
				break;
			} 
			if(c.getCombat().correctBowAndArrows() < c.playerEquipment[Player.playerArrows] && Settings.CORRECT_ARROWS && usingBow && !c.getCombat().usingCrystalBow() && c.playerEquipment[Player.playerWeapon] != 9185) {
				c.getItems();
				c.getItems();
				c.sendMessage("You can't use "+ItemHandler.getItemName(c.playerEquipment[Player.playerArrows]).toLowerCase()+"s with a "+ItemHandler.getItemName(c.playerEquipment[Player.playerWeapon]).toLowerCase()+".");
				c.stopMovement();
				c.getCombat().resetPlayerAttack();
				return;
			}
			if (c.playerEquipment[Player.playerWeapon] == 9185 && !c.getCombat().properBolts()) {
				c.sendMessage("You must use bolts with a crossbow.");
				c.stopMovement();
				c.getCombat().resetPlayerAttack();
				return;				
			}

			if (c.followId > 0) {
				c.getPA().resetFollow();			
			}
			
			c.getTaskScheduler().schedule(new Task(1) {
				@Override
				public void execute() {
					NPC npc = NPCHandler.npcs[c.npcIndex];
					if((c.npcIndex > -1) && npc != null) {			
						if(c.goodDistance(c.getX(), c.getY(), npc.getX(), npc.getY(), 1)) {
							c.stopMovement();
							if (c.attackTimer <= 0) {
								c.getCombat().attackNpc(c.npcIndex);
								c.attackTimer++;
								this.stop();
							}
						}
					}
					if(c.npcIndex < 0) 
						this.stop();
				}
			});

			break;

			/**
			 * Attack npc with magic
			 **/
		case MAGE_NPC:
			if (!c.mageAllowed) {
				c.mageAllowed = true;
				c.sendMessage("I can't reach that.");
				break;
			}
			//c.usingSpecial = false;
			//c.getItems().updateSpecialBar();

			c.npcIndex = c.getInStream().readSignedWordBigEndianA();
			int castingSpellId = c.getInStream().readSignedWordA();
			c.usingMagic = false;

			if(NPCHandler.npcs[c.npcIndex] == null ){
				break;
			}

			if(NPCHandler.npcs[c.npcIndex].MaxHP == 0 || NPCHandler.npcs[c.npcIndex].npcType == 944){
				c.sendMessage("You can't attack this npc.");
				break;
			}

			for(int i = 0; i < c.MAGIC_SPELLS.length; i++){
				if(castingSpellId == c.MAGIC_SPELLS[i][0]) {
					c.spellId = i;
					c.usingMagic = true;
					break;
				}
			}

			/*if(!c.getCombat().checkMagicReqs(c.spellId)) {
				c.stopMovement();
				break;
			}*/

			if (c.autocasting)
				c.autocasting = false;

			if(c.usingMagic) {
				c.getTaskScheduler().schedule(new Task(1) {
					@Override
					public void execute() {
						NPC npc = NPCHandler.npcs[c.npcIndex];
						if((c.npcIndex > -1) && npc != null) {			
							if(c.goodDistance(c.getX(), c.getY(), npc.getX(), npc.getY(), 6)) {
								if (c.attackTimer <= 0) {
									c.stopMovement();
									c.getCombat().attackNpc(c.npcIndex);
									c.attackTimer++;
									this.stop();
								}
							}
						}
						if(c.npcIndex < 0) 
							this.stop();
					}
				});
			}

			break;

		case FIRST_CLICK:
			c.npcClickIndex = c.inStream.readSignedWordBigEndian();
			c.npcType = NPCHandler.npcs[c.npcClickIndex].npcType;
			if(c.goodDistance(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY(), c.getX(), c.getY(), 1)) {
				c.turnPlayerTo(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY());
				NPCHandler.npcs[c.npcClickIndex].facePlayer(c.playerId);
				c.getActions().firstClickNpc(c.npcType);	
			} else {
				c.clickNpcType = 1;
				c.getTaskScheduler().schedule(new Task(1) {
					@Override
					public void execute() {
						if((c.clickNpcType == 1) && NPCHandler.npcs[c.npcClickIndex] != null) {			
							if(c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY(), 1)) {
								c.turnPlayerTo(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY());
								NPCHandler.npcs[c.npcClickIndex].facePlayer(c.playerId);
								c.getActions().firstClickNpc(c.npcType);
								this.stop();
							}
						}
						if(c.clickNpcType == 0 || c.clickNpcType > 1) 
							this.stop();
					}
					@Override
					public void stop() {
						super.stop();
						c.clickNpcType = 0;
					}
				});
			}
			break;

		case SECOND_CLICK:
			c.npcClickIndex = c.inStream.readUnsignedWordBigEndianA();
			c.npcType = NPCHandler.npcs[c.npcClickIndex].npcType;
			if(c.goodDistance(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY(), c.getX(), c.getY(), 1)) {
				c.turnPlayerTo(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY());
				NPCHandler.npcs[c.npcClickIndex].facePlayer(c.playerId);
				c.getActions().secondClickNpc(c.npcType);	
			} else {
				c.clickNpcType = 2;
				c.getTaskScheduler().schedule(new Task(4) {
					@Override
					public void execute() {
						if((c.clickNpcType == 2) && NPCHandler.npcs[c.npcClickIndex] != null) {			
							if(c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY(), 1)) {
								c.turnPlayerTo(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY());
								NPCHandler.npcs[c.npcClickIndex].facePlayer(c.playerId);
								c.getActions().secondClickNpc(c.npcType);
								this.stop();
							}
						}
						if(c.clickNpcType < 2 || c.clickNpcType > 2) 
							this.stop();
					}
					@Override
					public void stop() {
						super.stop();
						c.clickNpcType = 0;
					}
				});
			}
			break;

		case THIRD_CLICK:
			c.npcClickIndex = c.inStream.readSignedWord();
			c.npcType = NPCHandler.npcs[c.npcClickIndex].npcType;
			if(c.goodDistance(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY(), c.getX(), c.getY(), 1)) {
				c.turnPlayerTo(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY());
				NPCHandler.npcs[c.npcClickIndex].facePlayer(c.playerId);
				c.getActions().thirdClickNpc(c.npcType);	
			} else {
				c.clickNpcType = 3;
				c.getTaskScheduler().schedule(new Task(4) {
					@Override
					public void execute() {
						if((c.clickNpcType == 3) && NPCHandler.npcs[c.npcClickIndex] != null) {			
							if(c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY(), 1)) {
								c.turnPlayerTo(NPCHandler.npcs[c.npcClickIndex].getX(), NPCHandler.npcs[c.npcClickIndex].getY());
								NPCHandler.npcs[c.npcClickIndex].facePlayer(c.playerId);
								c.getActions().thirdClickNpc(c.npcType);
								this.stop();
							}
						}
						if(c.clickNpcType < 3) 
							this.stop();
					}
					@Override
					public void stop() {
						super.stop();
						c.clickNpcType = 0;
					}
				});
			}
			break;
		}

	}
}
