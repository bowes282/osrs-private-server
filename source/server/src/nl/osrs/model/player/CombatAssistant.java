package nl.osrs.model.player;

import nl.osrs.GameEngine;
import nl.osrs.Settings;
import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.npc.NPCHandler;
import nl.osrs.util.Misc;

@SuppressWarnings("all")
public class CombatAssistant {

	private Client c;

	public CombatAssistant(Client Client) {
		this.c = Client;
	}
	/**
	 * Attack Npcs
	 */
	public void attackNpc(int i) {
		if (NPCHandler.npcs[i] != null) {
			if (NPCHandler.npcs[i].isDead || NPCHandler.npcs[i].MaxHP <= 0) {
				c.usingMagic = false;
				c.faceUpdate(0);
				c.npcIndex = 0;
				return;
			}
			if (c.respawnTimer > 0) {
				c.npcIndex = 0;
				return;
			}
			if (NPCHandler.npcs[i].underAttackBy > 0
					&& NPCHandler.npcs[i].underAttackBy != c.playerId
					&& !NPCHandler.npcs[i].inMulti()) {
				c.npcIndex = 0;
				c.sendMessage("This monster is already in combat.");
				return;
			}
			if ((c.underAttackBy > 0 || c.underAttackBy2 > 0)
					&& c.underAttackBy2 != i && !c.inMulti()) {
				resetPlayerAttack();
				c.sendMessage("I am already under attack.");
				return;
			}
			if (NPCHandler.npcs[i].spawnedBy != c.playerId
					&& NPCHandler.npcs[i].spawnedBy > 0) {
				resetPlayerAttack();
				c.sendMessage("This monster was not spawned for you.");
				return;
			}
			c.followId2 = i;
			c.followId = 0;
			if (c.attackTimer <= 0) {
				boolean usingBow = false;
				boolean usingArrows = false;
				boolean usingOtherRangeWeapons = false;
				boolean usingCross = c.playerEquipment[c.playerWeapon] == 9185;
				c.bonusAttack = 0;
				c.rangeItemUsed = 0;
				c.projectileStage = 0;
				if (c.autocasting) {
					c.spellId = c.autocastId;
					c.usingMagic = true;
				}
				if (c.spellId > 0) {
					c.usingMagic = true;
				}
				c.attackTimer = getAttackDelay(ItemHandler
						.getItemName(c.playerEquipment[c.playerWeapon])
						.toLowerCase());
				c.specAccuracy = 1.0;
				c.specDamage = 1.0;
				if (!c.usingMagic) {
					for (int bowId : c.BOWS) {
						if (c.playerEquipment[c.playerWeapon] == bowId) {
							usingBow = true;
							for (int arrowId : c.ARROWS) {
								if (c.playerEquipment[c.playerArrows] == arrowId) {
									usingArrows = true;
								}
							}
						}
					}

					for (int otherRangeId : c.OTHER_RANGE_WEAPONS) {
						if (c.playerEquipment[c.playerWeapon] == otherRangeId) {
							usingOtherRangeWeapons = true;
						}
					}
				}
				if (armaNpc(i) && !usingCross && !usingBow && !c.usingMagic
						&& !usingCrystalBow() && !usingOtherRangeWeapons) {
					resetPlayerAttack();
					return;
				}
				if ((!c.goodDistance(c.getX(), c.getY(),
						NPCHandler.npcs[i].getX(), NPCHandler.npcs[i].getY(), 2) && (usingHally()
								&& !usingOtherRangeWeapons && !usingBow && !c.usingMagic))
						|| (!c.goodDistance(c.getX(), c.getY(),
								NPCHandler.npcs[i].getX(),
								NPCHandler.npcs[i].getY(), 4) && (usingOtherRangeWeapons
										&& !usingBow && !c.usingMagic))
						|| (!c.goodDistance(c.getX(), c.getY(),
								NPCHandler.npcs[i].getX(),
								NPCHandler.npcs[i].getY(), 1) && (!usingOtherRangeWeapons
										&& !usingHally() && !usingBow && !c.usingMagic))
						|| ((!c.goodDistance(c.getX(), c.getY(),
								NPCHandler.npcs[i].getX(),
								NPCHandler.npcs[i].getY(), 8) && (usingBow || c.usingMagic)))) {
					c.attackTimer = 2;
					return;
				}

				if (!usingCross
						&& !usingArrows
						&& usingBow
						&& (c.playerEquipment[c.playerWeapon] < 4212 || c.playerEquipment[c.playerWeapon] > 4223)) {
					c.sendMessage("You have run out of arrows!");
					c.stopMovement();
					c.npcIndex = 0;
					return;
				}
				if (correctBowAndArrows() < c.playerEquipment[c.playerArrows]
						&& Settings.CORRECT_ARROWS && usingBow
						&& !usingCrystalBow()
						&& c.playerEquipment[c.playerWeapon] != 9185) {
					c.sendMessage("You can't use "
							+ ItemHandler
							.getItemName(
									c.playerEquipment[c.playerArrows])
							.toLowerCase()
							+ "s with a "
							+ ItemHandler
							.getItemName(
									c.playerEquipment[c.playerWeapon])
							.toLowerCase() + ".");
					c.stopMovement();
					c.npcIndex = 0;
					return;
				}

				if (c.playerEquipment[c.playerWeapon] == 9185 && !properBolts()) {
					c.sendMessage("You must use bolts with a crossbow.");
					c.stopMovement();
					resetPlayerAttack();
					return;
				}

				if (usingBow
						|| c.usingMagic
						|| usingOtherRangeWeapons
						|| (c.goodDistance(c.getX(), c.getY(),
								NPCHandler.npcs[i].getX(),
								NPCHandler.npcs[i].getY(), 2) && usingHally())) {
					c.stopMovement();
				}

				if (!checkMagicReqs(c.spellId)) {
					c.stopMovement();
					c.npcIndex = 0;
					return;
				}

				c.faceUpdate(i);
				// c.specAccuracy = 1.0;
				// c.specDamage = 1.0;
				NPCHandler.npcs[i].underAttackBy = c.playerId;
				NPCHandler.npcs[i].lastDamageTaken = System.currentTimeMillis();
				if (c.usingSpecial && !c.usingMagic) {
					if (checkSpecAmount(c.playerEquipment[c.playerWeapon])) {
						c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
						c.lastArrowUsed = c.playerEquipment[c.playerArrows];
						activateSpecial(c.playerEquipment[c.playerWeapon], i);
						return;
					} else {
						c.sendMessage("You don't have the required special energy to use this attack.");
						c.usingSpecial = false;
						c.getItems().updateSpecialBar();
						c.npcIndex = 0;
						return;
					}
				}
				c.specMaxHitIncrease = 0;
				if (!c.usingMagic) {
					c.startAnimation(getWepAnim(ItemHandler
							.getItemName(c.playerEquipment[c.playerWeapon])
							.toLowerCase()));
				} else {
					c.startAnimation(c.MAGIC_SPELLS[c.spellId][2]);
				}
				c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
				c.lastArrowUsed = c.playerEquipment[c.playerArrows];
				if (!usingBow && !c.usingMagic && !usingOtherRangeWeapons) { // melee
					// hit
					// delay
					c.hitDelay = getHitDelay(ItemHandler
							.getItemName(c.playerEquipment[c.playerWeapon])
							.toLowerCase());
					c.projectileStage = 0;
					c.oldNpcIndex = i;
				}

				if (usingBow && !usingOtherRangeWeapons && !c.usingMagic
						|| usingCross) { // range hit delay
					if (usingCross)
						c.usingBow = true;
					if (c.fightMode == 2)
						c.attackTimer--;
					c.lastArrowUsed = c.playerEquipment[c.playerArrows];
					c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
					c.gfx100(getRangeStartGFX());
					c.hitDelay = getHitDelay(ItemHandler
							.getItemName(c.playerEquipment[c.playerWeapon])
							.toLowerCase());
					c.projectileStage = 1;
					c.oldNpcIndex = i;
					if (c.playerEquipment[c.playerWeapon] >= 4212
							&& c.playerEquipment[c.playerWeapon] <= 4223) {
						c.rangeItemUsed = c.playerEquipment[c.playerWeapon];
						c.crystalBowArrowCount++;
						c.lastArrowUsed = 0;
					} else {
						c.rangeItemUsed = c.playerEquipment[c.playerArrows];
						c.getItems().deleteArrow();
					}
					fireProjectileNpc();
				}

				if (usingOtherRangeWeapons && !c.usingMagic && !usingBow) { // knives,
					// darts,
					// etc
					// hit
					// delay
					c.rangeItemUsed = c.playerEquipment[c.playerWeapon];
					c.getItems().deleteEquipment();
					c.gfx100(getRangeStartGFX());
					c.lastArrowUsed = 0;
					c.hitDelay = getHitDelay(ItemHandler
							.getItemName(c.playerEquipment[c.playerWeapon])
							.toLowerCase());
					c.projectileStage = 1;
					c.oldNpcIndex = i;
					if (c.fightMode == 2)
						c.attackTimer--;
					fireProjectileNpc();
				}

				if (c.usingMagic) { // magic hit delay
					int pX = c.getX();
					int pY = c.getY();
					int nX = NPCHandler.npcs[i].getX();
					int nY = NPCHandler.npcs[i].getY();
					int offX = (pY - nY) * -1;
					int offY = (pX - nX) * -1;
					c.castingMagic = true;
					c.projectileStage = 2;
					if (c.MAGIC_SPELLS[c.spellId][3] > 0) {
						if (getStartGfxHeight() == 100) {
							c.gfx100(c.MAGIC_SPELLS[c.spellId][3]);
						} else {
							c.gfx0(c.MAGIC_SPELLS[c.spellId][3]);
						}
					}
					if (c.MAGIC_SPELLS[c.spellId][4] > 0) {
						c.getPA().createPlayersProjectile(pX, pY, offX, offY,
								50, 78, c.MAGIC_SPELLS[c.spellId][4],
								getStartHeight(), getEndHeight(), i + 1, 50);
					}
					c.hitDelay = getHitDelay(ItemHandler
							.getItemName(c.playerEquipment[c.playerWeapon])
							.toLowerCase());
					c.oldNpcIndex = i;
					c.oldSpellId = c.spellId;
					c.spellId = 0;
					if (!c.autocasting)
						c.npcIndex = 0;
				}

				if (usingBow && Settings.CRYSTAL_BOW_DEGRADES) { // crystal bow
					// degrading
					if (c.playerEquipment[c.playerWeapon] == 4212) { // new
						// crystal
						// bow
						// becomes
						// full
						// bow
						// on
						// the
						// first
						// shot
						c.getItems().wearItem(4214, 1, 3);
					}

					if (c.crystalBowArrowCount >= 250) {
						switch (c.playerEquipment[c.playerWeapon]) {

						case 4223: // 1/10 bow
							c.getItems().wearItem(-1, 1, 3);
							c.sendMessage("Your crystal bow has fully degraded.");
							if (!c.getItems().addItem(4207, 1)) {
								GameEngine.itemHandler.createGroundItem(c, 4207,
										c.getX(), c.getY(), 1, c.getId());
							}
							c.crystalBowArrowCount = 0;
							break;

						default:
							c.getItems().wearItem(
									++c.playerEquipment[c.playerWeapon], 1, 3);
							c.sendMessage("Your crystal bow degrades.");
							c.crystalBowArrowCount = 0;
							break;

						}
					}
				}
			}
		}
	}

	public void appendMultiChinchompaEffect(int playerId, boolean splashed) {
		if (PlayerHandler.players[playerId] != null) {
			Client c2 = (Client) PlayerHandler.players[playerId];
			if (c2.isDead || c2.respawnTimer > 0)
				return;
			if (checkMultiBarrageReqs(playerId)) {
				c.barrageCount++;
				if (Misc.random(calculateRangeAttack()) > Misc
						.random(calculateRangeDefence()) && !c.magicFailed) {
					if (getEndGfxHeight() == 100) { // end GFX
						c2.gfx100(909);
					} else {
						c2.gfx0(909);
					}
					int damage = Misc.random(calculateRangeAttack());

					if (c2.playerLevel[3] - damage < 0) {
						damage = c2.playerLevel[3];
					}
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE), 6);
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE / 3),
							3);
					PlayerHandler.players[playerId].handleHitMask(damage);
					PlayerHandler.players[playerId].dealDamage(damage);
					PlayerHandler.players[playerId].damageTaken[c.playerId] += damage;
					c2.getPA().refreshSkill(3);
					multiSpellEffect(playerId, damage);
				} else {
					c2.gfx100(85);
				}
			}
		}
	}

	public void delayedHit(int i) { // npc hit delay
		if (NPCHandler.npcs[i] != null) {
			if (NPCHandler.npcs[i].isDead) {
				c.npcIndex = 0;
				return;
			}
			NPCHandler.npcs[i].facePlayer(c.playerId);

			if (NPCHandler.npcs[i].underAttackBy > 0
					&& GameEngine.npcHandler.getsPulled(i)) {
				NPCHandler.npcs[i].killerId = c.playerId;
			} else if (NPCHandler.npcs[i].underAttackBy < 0
					&& !GameEngine.npcHandler.getsPulled(i)) {
				NPCHandler.npcs[i].killerId = c.playerId;
			}
			c.lastNpcAttacked = i;
			if (c.projectileStage == 0) { // melee hit damage
				applyNpcMeleeDamage(i, 1);
				if (c.doubleHit) {
					applyNpcMeleeDamage(i, 2);
				}
			}

			if (!c.castingMagic && c.projectileStage > 0) { // range hit damage
				int damage = Misc.random(rangeMaxHit());
				int damage2 = -1;
				if (c.lastWeaponUsed == 11235 || c.bowSpecShot == 1)
					damage2 = Misc.random(rangeMaxHit());
				boolean ignoreDef = false;
				if (Misc.random(5) == 1 && c.lastArrowUsed == 9243) {
					ignoreDef = true;
					NPCHandler.npcs[i].gfx0(758);
				}

				if (Misc.random(NPCHandler.npcs[i].defence) > Misc
						.random(10 + calculateRangeAttack()) && !ignoreDef) {
					damage = 0;
				} else if (NPCHandler.npcs[i].npcType == 2881
						|| NPCHandler.npcs[i].npcType == 2883 && !ignoreDef) {
					damage = 0;
				}

				if (Misc.random(4) == 1 && c.lastArrowUsed == 9242
						&& damage > 0) {
					NPCHandler.npcs[i].gfx0(754);
					damage = NPCHandler.npcs[i].HP / 5;
					c.handleHitMask(c.playerLevel[3] / 10);
					c.dealDamage(c.playerLevel[3] / 10);
					c.gfx0(754);
				}

				if (c.lastWeaponUsed == 11235 || c.bowSpecShot == 1) {
					if (Misc.random(NPCHandler.npcs[i].defence) > Misc
							.random(10 + calculateRangeAttack()))
						damage2 = 0;
				}
				if (c.dbowSpec) {
					NPCHandler.npcs[i].gfx100(1100);
					if (damage < 8)
						damage = 8;
					if (damage2 < 8)
						damage2 = 8;
					c.dbowSpec = false;
				}
				if (damage > 0 && Misc.random(5) == 1
						&& c.lastArrowUsed == 9244) {
					damage *= 1.45;
					NPCHandler.npcs[i].gfx0(756);
				}

				if (NPCHandler.npcs[i].HP - damage < 0) {
					damage = NPCHandler.npcs[i].HP;
				}
				if (NPCHandler.npcs[i].HP - damage <= 0 && damage2 > 0) {
					damage2 = 0;
				}
				if (c.fightMode == 3) {
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE / 2),
							4);
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE / 2),
							1);
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE / 3),
							3);
					c.getPA().refreshSkill(1);
					c.getPA().refreshSkill(3);
					c.getPA().refreshSkill(4);
				} else {
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE), 4);
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE / 3),
							3);
					c.getPA().refreshSkill(3);
					c.getPA().refreshSkill(4);
				}
				if (damage > 0) {

				}
				boolean dropArrows = true;

				for (int noArrowId : c.NO_ARROW_DROP) {
					if (c.lastWeaponUsed == noArrowId) {
						dropArrows = false;
						break;
					}
				}
				if (dropArrows) {
					c.getItems().dropArrowNpc();
				}
				NPCHandler.npcs[i].underAttack = true;
				NPCHandler.npcs[i].hitDiff = damage;
				NPCHandler.npcs[i].HP -= damage;
				if (damage2 > -1) {
					NPCHandler.npcs[i].hitDiff2 = damage2;
					NPCHandler.npcs[i].HP -= damage2;
					c.totalDamageDealt += damage2;
				}
				if (c.killingNpcIndex != c.oldNpcIndex) {
					c.totalDamageDealt = 0;
				}
				c.killingNpcIndex = c.oldNpcIndex;
				c.totalDamageDealt += damage;
				NPCHandler.npcs[i].hitUpdateRequired = true;
				if (damage2 > -1)
					NPCHandler.npcs[i].hitUpdateRequired2 = true;
				NPCHandler.npcs[i].updateRequired = true;

			} else if (c.projectileStage > 0) { // magic hit damage
				int damage = Misc.random(c.MAGIC_SPELLS[c.oldSpellId][6]);
				if (godSpells()) {
					if (System.currentTimeMillis() - c.godSpellDelay < Settings.GOD_SPELL_CHARGE) {
						damage += Misc.random(10);
					}
				}
				boolean magicFailed = false;
				// c.npcIndex = 0;
				int bonusAttack = getBonusAttack(i);
				if (Misc.random(NPCHandler.npcs[i].defence) > 10
						+ Misc.random(mageAtk()) + bonusAttack) {
					damage = 0;
					magicFailed = true;
				} else if (NPCHandler.npcs[i].npcType == 2881
						|| NPCHandler.npcs[i].npcType == 2882) {
					damage = 0;
					magicFailed = true;
				}

				if (NPCHandler.npcs[i].HP - damage < 0) {
					damage = NPCHandler.npcs[i].HP;
				}

				c.getPA().addSkillXP(
						(c.MAGIC_SPELLS[c.oldSpellId][7] + damage
								* Settings.MAGIC_EXP_RATE), 6);
				c.getPA().addSkillXP(
						(c.MAGIC_SPELLS[c.oldSpellId][7] + damage
								* Settings.MAGIC_EXP_RATE / 3), 3);
				c.getPA().refreshSkill(3);
				c.getPA().refreshSkill(6);
				if (damage > 0) {

				}
				if (getEndGfxHeight() == 100 && !magicFailed) { // end GFX
					NPCHandler.npcs[i].gfx100(c.MAGIC_SPELLS[c.oldSpellId][5]);
				} else if (!magicFailed) {
					NPCHandler.npcs[i].gfx0(c.MAGIC_SPELLS[c.oldSpellId][5]);
				}

				if (magicFailed) {
					NPCHandler.npcs[i].gfx100(85);
				}
				if (!magicFailed) {
					int freezeDelay = getFreezeTime();// freeze
					if (freezeDelay > 0 && NPCHandler.npcs[i].freezeTimer == 0) {
						NPCHandler.npcs[i].freezeTimer = freezeDelay;
					}
					switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
					case 12901:
					case 12919: // blood spells
					case 12911:
					case 12929:
						int heal = Misc.random(damage / 2);
						if (c.playerLevel[3] + heal >= c.getPA().getLevelForXP(
								c.playerXP[3])) {
							c.playerLevel[3] = c.getPA().getLevelForXP(
									c.playerXP[3]);
						} else {
							c.playerLevel[3] += heal;
						}
						c.getPA().refreshSkill(3);
						break;
					}

				}
				NPCHandler.npcs[i].underAttack = true;
				if (c.MAGIC_SPELLS[c.oldSpellId][6] != 0) {
					NPCHandler.npcs[i].hitDiff = damage;
					NPCHandler.npcs[i].HP -= damage;
					NPCHandler.npcs[i].hitUpdateRequired = true;
					c.totalDamageDealt += damage;
				}
				c.killingNpcIndex = c.oldNpcIndex;
				NPCHandler.npcs[i].updateRequired = true;
				c.usingMagic = false;
				c.castingMagic = false;
				c.oldSpellId = 0;
			}
		}

		if (c.bowSpecShot <= 0) {
			c.oldNpcIndex = 0;
			c.projectileStage = 0;
			c.doubleHit = false;
			c.lastWeaponUsed = 0;
			c.bowSpecShot = 0;
		}
		if (c.bowSpecShot >= 2) {
			c.bowSpecShot = 0;
			// c.attackTimer =
			// getAttackDelay(ItemHandler.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
		}
		if (c.bowSpecShot == 1) {
			fireProjectileNpc();
			c.hitDelay = 2;
			c.bowSpecShot = 0;
		}
	}

	public void applyNpcMeleeDamage(int i, int damageMask) {
		int damage = Misc.random(calculateMeleeMaxHit());
		if (NPCHandler.npcs[i].HP - damage < 0) {
			damage = NPCHandler.npcs[i].HP;
		}
		if (c.fightMode == 3) {
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE / 3), 0);
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE / 3), 1);
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE / 3), 2);
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE / 3), 3);
			c.getPA().refreshSkill(0);
			c.getPA().refreshSkill(1);
			c.getPA().refreshSkill(2);
			c.getPA().refreshSkill(3);
		} else {
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE), c.fightMode);
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE / 3), 3);
			c.getPA().refreshSkill(c.fightMode);
			c.getPA().refreshSkill(3);
		}
		if (damage > 0) {

		}
		NPCHandler.npcs[i].underAttack = true;
		// Server.npcHandler.npcs[i].killerId = c.playerId;
		c.killingNpcIndex = c.npcIndex;
		c.lastNpcAttacked = i;
		switch (c.specEffect) {
		case 4:
			if (damage > 0) {
				if (c.playerLevel[3] + damage > PlayerAssistant.getLevelForXP(c.playerXP[3]))
					if (c.playerLevel[3] > PlayerAssistant.getLevelForXP(c.playerXP[3]))
						;
					else
						c.playerLevel[3] = PlayerAssistant.getLevelForXP(c.playerXP[3]);
				else
					c.playerLevel[3] += damage;
				c.getPA().refreshSkill(3);
			}
			break;

		}
		switch (damageMask) {
		case 1:
			NPCHandler.npcs[i].hitDiff = damage;
			NPCHandler.npcs[i].HP -= damage;
			c.totalDamageDealt += damage;
			NPCHandler.npcs[i].hitUpdateRequired = true;
			NPCHandler.npcs[i].updateRequired = true;
			break;

		case 2:
			NPCHandler.npcs[i].hitDiff2 = damage;
			NPCHandler.npcs[i].HP -= damage;
			c.totalDamageDealt += damage;
			NPCHandler.npcs[i].hitUpdateRequired2 = true;
			NPCHandler.npcs[i].updateRequired = true;
			c.doubleHit = false;
			break;

		}
	}

	public void fireProjectileNpc() {
		if (c.oldNpcIndex > 0) {
			if (NPCHandler.npcs[c.oldNpcIndex] != null) {
				c.projectileStage = 2;
				int pX = c.getX();
				int pY = c.getY();
				int nX = NPCHandler.npcs[c.oldNpcIndex].getX();
				int nY = NPCHandler.npcs[c.oldNpcIndex].getY();
				int offX = (pY - nY) * -1;
				int offY = (pX - nX) * -1;
				c.getPA().createPlayersProjectile(pX, pY, offX, offY, 50,
						getProjectileSpeed(), getRangeProjectileGFX(), 43, 31,
						c.oldNpcIndex + 1, getStartDelay());
				if (usingDbow())
					c.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50,
							getProjectileSpeed(), getRangeProjectileGFX(), 60,
							31, c.oldNpcIndex + 1, getStartDelay(), 35);
			}
		}
	}

	/**
	 * Attack Players, same as npc tbh xD
	 **/

	public void attackPlayer(int i) {

		if (PlayerHandler.players[i] != null) {

			if (PlayerHandler.players[i].isDead) {
				resetPlayerAttack();
				return;
			}

			if (c.respawnTimer > 0 || PlayerHandler.players[i].respawnTimer > 0) {
				resetPlayerAttack();
				return;
			}

			/*
			 * if (c.teleTimer > 0 || Server.playerHandler.players[i].teleTimer
			 * > 0) { resetPlayerAttack(); return; }
			 */

			if (!c.getCombat().checkReqs()) {
				return;
			}

			if (c.getPA().getWearingAmount() < 4 && c.duelStatus < 1) {
				c.sendMessage("You must be wearing at least 4 items to attack someone.");
				resetPlayerAttack();
				return;
			}
			boolean sameSpot = c.absX == PlayerHandler.players[i].getX()
					&& c.absY == PlayerHandler.players[i].getY();
			if (!c.goodDistance(PlayerHandler.players[i].getX(),
					PlayerHandler.players[i].getY(), c.getX(), c.getY(), 25)
					&& !sameSpot) {
				resetPlayerAttack();
				return;
			}

			if (PlayerHandler.players[i].respawnTimer > 0) {
				PlayerHandler.players[i].playerIndex = 0;
				resetPlayerAttack();
				return;
			}

			if (PlayerHandler.players[i].heightLevel != c.heightLevel) {
				resetPlayerAttack();
				return;
			}
			// c.sendMessage("Made it here0.");
			c.followId = i;
			c.followId2 = 0;
			if (c.attackTimer <= 0) {
				c.usingBow = false;
				c.specEffect = 0;
				c.usingRangeWeapon = false;
				c.rangeItemUsed = 0;
				boolean usingBow = false;
				boolean usingArrows = false;
				boolean usingOtherRangeWeapons = false;
				boolean usingCross = c.playerEquipment[c.playerWeapon] == 9185;
				c.projectileStage = 0;

				if (c.absX == PlayerHandler.players[i].absX
						&& c.absY == PlayerHandler.players[i].absY) {
					if (c.freezeTimer > 0) {
						resetPlayerAttack();
						return;
					}
					c.followId = i;
					c.attackTimer = 0;
					return;
				}

				/*
				 * if ((c.inPirateHouse() &&
				 * !Server.playerHandler.players[i].inPirateHouse()) ||
				 * (Server.playerHandler.players[i].inPirateHouse() &&
				 * !c.inPirateHouse())) { resetPlayerAttack(); return; }
				 */
				// c.sendMessage("Made it here1.");
				if (!c.usingMagic) {
					for (int bowId : c.BOWS) {
						if (c.playerEquipment[c.playerWeapon] == bowId) {
							usingBow = true;
							for (int arrowId : c.ARROWS) {
								if (c.playerEquipment[c.playerArrows] == arrowId) {
									usingArrows = true;
								}
							}
						}
					}

					for (int otherRangeId : c.OTHER_RANGE_WEAPONS) {
						if (c.playerEquipment[c.playerWeapon] == otherRangeId) {
							usingOtherRangeWeapons = true;
						}
					}
				}
				if (c.autocasting) {
					c.spellId = c.autocastId;
					c.usingMagic = true;
				}
				// c.sendMessage("Made it here2.");
				if (c.spellId > 0) {
					c.usingMagic = true;
				}
				c.attackTimer = getAttackDelay(ItemHandler
						.getItemName(c.playerEquipment[c.playerWeapon])
						.toLowerCase());

				if (c.duelRule[9]) {
					boolean canUseWeapon = false;
					for (int funWeapon : Settings.FUN_WEAPONS) {
						if (c.playerEquipment[c.playerWeapon] == funWeapon) {
							canUseWeapon = true;
						}
					}
					if (!canUseWeapon) {
						c.sendMessage("You can only use fun weapons in this duel!");
						resetPlayerAttack();
						return;
					}
				}
				// c.sendMessage("Made it here3.");
				if (c.duelRule[2] && (usingBow || usingOtherRangeWeapons)) {
					c.sendMessage("Range has been disabled in this duel!");
					return;
				}
				if (c.duelRule[3]
						&& (!usingBow && !usingOtherRangeWeapons && !c.usingMagic)) {
					c.sendMessage("Melee has been disabled in this duel!");
					return;
				}

				if (c.duelRule[4] && c.usingMagic) {
					c.sendMessage("Magic has been disabled in this duel!");
					resetPlayerAttack();
					return;
				}

				if ((!c.goodDistance(c.getX(), c.getY(),
						PlayerHandler.players[i].getX(),
						PlayerHandler.players[i].getY(), 4) && (usingOtherRangeWeapons
								&& !usingBow && !c.usingMagic))
						|| (!c.goodDistance(c.getX(), c.getY(),
								PlayerHandler.players[i].getX(),
								PlayerHandler.players[i].getY(), 2) && (!usingOtherRangeWeapons
										&& usingHally() && !usingBow && !c.usingMagic))
						|| (!c.goodDistance(c.getX(), c.getY(),
								PlayerHandler.players[i].getX(),
								PlayerHandler.players[i].getY(),
								getRequiredDistance()) && (!usingOtherRangeWeapons
										&& !usingHally() && !usingBow && !c.usingMagic))
						|| (!c.goodDistance(c.getX(), c.getY(),
								PlayerHandler.players[i].getX(),
								PlayerHandler.players[i].getY(), 10) && (usingBow || c.usingMagic))) {
					// c.sendMessage("Setting attack timer to 1");
					c.attackTimer = 1;
					if (!usingBow && !c.usingMagic && !usingOtherRangeWeapons
							&& c.freezeTimer > 0)
						resetPlayerAttack();
					return;
				}

				if (!usingCross
						&& !usingArrows
						&& usingBow
						&& (c.playerEquipment[c.playerWeapon] < 4212 || c.playerEquipment[c.playerWeapon] > 4223)
						&& !c.usingMagic) {
					c.sendMessage("You have run out of arrows!");
					c.stopMovement();
					resetPlayerAttack();
					return;
				}
				if (correctBowAndArrows() < c.playerEquipment[c.playerArrows]
						&& Settings.CORRECT_ARROWS && usingBow
						&& !usingCrystalBow()
						&& c.playerEquipment[c.playerWeapon] != 9185
						&& !c.usingMagic) {
					c.sendMessage("You can't use "
							+ ItemHandler
							.getItemName(
									c.playerEquipment[c.playerArrows])
							.toLowerCase()
							+ "s with a "
							+ ItemHandler
							.getItemName(
									c.playerEquipment[c.playerWeapon])
							.toLowerCase() + ".");
					c.stopMovement();
					resetPlayerAttack();
					return;
				}
				if (c.playerEquipment[c.playerWeapon] == 9185 && !properBolts()
						&& !c.usingMagic) {
					c.sendMessage("You must use bolts with a crossbow.");
					c.stopMovement();
					resetPlayerAttack();
					return;
				}

				if (usingBow || c.usingMagic || usingOtherRangeWeapons
						|| usingHally()) {
					c.stopMovement();
				}

				if (!checkMagicReqs(c.spellId)) {
					c.stopMovement();
					resetPlayerAttack();
					return;
				}

				c.faceUpdate(i + 32768);

				if (c.duelStatus != 5) {
					if (!c.attackedPlayers.contains(c.playerIndex)
							&& !PlayerHandler.players[c.playerIndex].attackedPlayers
							.contains(c.playerId)) {
						c.attackedPlayers.add(c.playerIndex);
						c.isSkulled = true;
						c.skullTimer = Settings.SKULL_TIMER;
						c.headIconPk = 0;
						c.getPA().requestUpdates();
					}
				}
				c.specAccuracy = 1.0;
				c.specDamage = 1.0;
				c.delayedDamage = c.delayedDamage2 = 0;
				if (c.usingSpecial && !c.usingMagic) {
					if (c.duelRule[10] && c.duelStatus == 5) {
						c.sendMessage("Special attacks have been disabled during this duel!");
						c.usingSpecial = false;
						c.getItems().updateSpecialBar();
						resetPlayerAttack();
						return;
					}
					if (checkSpecAmount(c.playerEquipment[c.playerWeapon])) {
						c.lastArrowUsed = c.playerEquipment[c.playerArrows];
						activateSpecial(c.playerEquipment[c.playerWeapon], i);
						c.followId = c.playerIndex;
						return;
					} else {
						c.sendMessage("You don't have the required special energy to use this attack.");
						c.usingSpecial = false;
						c.getItems().updateSpecialBar();
						c.playerIndex = 0;
						return;
					}
				}

				if (!c.usingMagic) {
					c.startAnimation(getWepAnim(ItemHandler
							.getItemName(c.playerEquipment[c.playerWeapon])
							.toLowerCase()));
					c.mageFollow = false;
				} else {
					c.startAnimation(c.MAGIC_SPELLS[c.spellId][2]);
					c.mageFollow = true;
					c.followId = c.playerIndex;
				}
				PlayerHandler.players[i].underAttackBy = c.playerId;
				PlayerHandler.players[i].logoutDelay = System
						.currentTimeMillis();
				PlayerHandler.players[i].singleCombatDelay = System
						.currentTimeMillis();
				PlayerHandler.players[i].killerId = c.playerId;
				c.lastArrowUsed = 0;
				c.rangeItemUsed = 0;
				if (!usingBow && !c.usingMagic && !usingOtherRangeWeapons) { // melee
					// hit
					// delay
					c.followId = PlayerHandler.players[c.playerIndex].playerId;
					c.getPA().followPlayer();
					c.hitDelay = getHitDelay(ItemHandler
							.getItemName(c.playerEquipment[c.playerWeapon])
							.toLowerCase());
					c.delayedDamage = Misc.random(calculateMeleeMaxHit());
					c.projectileStage = 0;
					c.oldPlayerIndex = i;
				}

				if (usingBow && !usingOtherRangeWeapons && !c.usingMagic
						|| usingCross) { // range hit delay
					if (c.playerEquipment[c.playerWeapon] >= 4212
							&& c.playerEquipment[c.playerWeapon] <= 4223) {
						c.rangeItemUsed = c.playerEquipment[c.playerWeapon];
						c.crystalBowArrowCount++;
					} else {
						c.rangeItemUsed = c.playerEquipment[c.playerArrows];
						c.getItems().deleteArrow();
					}
					if (c.fightMode == 2)
						c.attackTimer--;
					if (usingCross)
						c.usingBow = true;
					c.usingBow = true;
					c.followId = PlayerHandler.players[c.playerIndex].playerId;
					c.getPA().followPlayer();
					c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
					c.lastArrowUsed = c.playerEquipment[c.playerArrows];
					c.gfx100(getRangeStartGFX());
					c.hitDelay = getHitDelay(ItemHandler
							.getItemName(c.playerEquipment[c.playerWeapon])
							.toLowerCase());
					c.projectileStage = 1;
					c.oldPlayerIndex = i;
					fireProjectilePlayer();
				}

				if (usingOtherRangeWeapons) { // knives, darts, etc hit delay
					c.rangeItemUsed = c.playerEquipment[c.playerWeapon];
					c.getItems().deleteEquipment();
					c.usingRangeWeapon = true;
					c.followId = PlayerHandler.players[c.playerIndex].playerId;
					c.getPA().followPlayer();
					c.gfx100(getRangeStartGFX());
					if (c.fightMode == 2)
						c.attackTimer--;
					c.hitDelay = getHitDelay(ItemHandler
							.getItemName(c.playerEquipment[c.playerWeapon])
							.toLowerCase());
					c.projectileStage = 1;
					c.oldPlayerIndex = i;
					fireProjectilePlayer();
				}

				if (c.usingMagic) { // magic hit delay
					int pX = c.getX();
					int pY = c.getY();
					int nX = PlayerHandler.players[i].getX();
					int nY = PlayerHandler.players[i].getY();
					int offX = (pY - nY) * -1;
					int offY = (pX - nX) * -1;
					c.castingMagic = true;
					c.projectileStage = 2;
					if (c.MAGIC_SPELLS[c.spellId][3] > 0) {
						if (getStartGfxHeight() == 100) {
							c.gfx100(c.MAGIC_SPELLS[c.spellId][3]);
						} else {
							c.gfx0(c.MAGIC_SPELLS[c.spellId][3]);
						}
					}
					if (c.MAGIC_SPELLS[c.spellId][4] > 0) {
						c.getPA().createPlayersProjectile(pX, pY, offX, offY,
								50, 78, c.MAGIC_SPELLS[c.spellId][4],
								getStartHeight(), getEndHeight(), -i - 1,
								getStartDelay());
					}
					if (c.autocastId > 0) {
						c.followId = c.playerIndex;
						c.followDistance = 5;
					}
					c.hitDelay = getHitDelay(ItemHandler
							.getItemName(c.playerEquipment[c.playerWeapon])
							.toLowerCase());
					c.oldPlayerIndex = i;
					c.oldSpellId = c.spellId;
					c.spellId = 0;
					Client o = (Client) PlayerHandler.players[i];
					if (c.MAGIC_SPELLS[c.oldSpellId][0] == 12891 && o.isMoving) {
						// c.sendMessage("Barrage projectile..");
						c.getPA().createPlayersProjectile(pX, pY, offX, offY,
								50, 85, 368, 25, 25, -i - 1, getStartDelay());
					}
					if (Misc.random(mageAtk()) > Misc.random(o.getCombat()
							.mageDef())) {
						c.magicFailed = false;
					} else if (Misc.random(mageAtk()) < Misc.random(o
							.getCombat().mageDef())) {
						c.magicFailed = true;
					}
					int freezeDelay = getFreezeTime();// freeze time
					if (freezeDelay > 0
							&& PlayerHandler.players[i].freezeTimer <= -3
							&& !c.magicFailed) {
						PlayerHandler.players[i].freezeTimer = freezeDelay;
						o.resetWalkingQueue();
						o.sendMessage("You have been frozen.");
						o.frozenBy = c.playerId;
					}
					if (!c.autocasting && c.spellId <= 0)
						c.playerIndex = 0;
				}

				if (usingBow && Settings.CRYSTAL_BOW_DEGRADES) { // crystal bow
					// degrading
					if (c.playerEquipment[c.playerWeapon] == 4212) { // new
						// crystal
						// bow
						// becomes
						// full
						// bow
						// on
						// the
						// first
						// shot
						c.getItems().wearItem(4214, 1, 3);
					}

					if (c.crystalBowArrowCount >= 250) {
						switch (c.playerEquipment[c.playerWeapon]) {

						case 4223: // 1/10 bow
							c.getItems().wearItem(-1, 1, 3);
							c.sendMessage("Your crystal bow has fully degraded.");
							if (!c.getItems().addItem(4207, 1)) {
								GameEngine.itemHandler.createGroundItem(c, 4207,
										c.getX(), c.getY(), 1, c.getId());
							}
							c.crystalBowArrowCount = 0;
							break;

						default:
							c.getItems().wearItem(
									++c.playerEquipment[c.playerWeapon], 1, 3);
							c.sendMessage("Your crystal bow degrades.");
							c.crystalBowArrowCount = 0;
							break;
						}
					}
				}
			}
		}
	}

	public boolean usingCrystalBow() {
		return c.playerEquipment[c.playerWeapon] >= 4212
				&& c.playerEquipment[c.playerWeapon] <= 4223;
	}

	public void appendVengeance(int otherPlayer, int damage) {
		if (damage <= 0)
			return;
		Player o = PlayerHandler.players[otherPlayer];
		o.forcedText = "Taste Vengeance!";
		o.forcedChatUpdateRequired = true;
		o.updateRequired = true;
		o.vengOn = false;
		if ((o.playerLevel[3] - damage) > 0) {
			damage = (int) (damage * 0.75);
			if (damage > c.playerLevel[3]) {
				damage = c.playerLevel[3];
			}
			c.setHitDiff2(damage);
			c.setHitUpdateRequired2(true);
			c.playerLevel[3] -= damage;
			c.getPA().refreshSkill(3);
		}
		c.updateRequired = true;
	}

	public void playerDelayedHit(int i) {
		if (PlayerHandler.players[i] != null) {
			if (PlayerHandler.players[i].isDead || c.isDead
					|| PlayerHandler.players[i].playerLevel[3] <= 0
					|| c.playerLevel[3] <= 0) {
				c.playerIndex = 0;
				return;
			}
			if (PlayerHandler.players[i].respawnTimer > 0) {
				c.faceUpdate(0);
				c.playerIndex = 0;
				return;
			}
			Client o = (Client) PlayerHandler.players[i];
			o.getPA().removeAllWindows();
			if (o.playerIndex <= 0 && o.npcIndex <= 0) {
				if (o.autoRet == 1) {
					o.playerIndex = c.playerId;
				}
			}
			if (o.attackTimer <= 3 || o.attackTimer == 0 && o.playerIndex == 0
					&& !c.castingMagic) { // block animation
				o.startAnimation(o.getCombat().getBlockEmote());
			}
			if (o.inTrade) {
				o.getTradeAndDuel().declineTrade();
			}
			if (c.projectileStage == 0) { // melee hit damage
				applyPlayerMeleeDamage(i, 1);
				if (c.doubleHit) {
					applyPlayerMeleeDamage(i, 2);
				}
			}

			if (!c.castingMagic && c.projectileStage > 0) { // range hit damage
				int damage = Misc.random(rangeMaxHit());
				int damage2 = -1;
				if (c.lastWeaponUsed == 11235 || c.bowSpecShot == 1)
					damage2 = Misc.random(rangeMaxHit());
				boolean ignoreDef = false;
				if (Misc.random(4) == 1 && c.lastArrowUsed == 9243) {
					ignoreDef = true;
					o.gfx0(758);
				}
				if (Misc.random(10 + o.getCombat().calculateRangeDefence()) > Misc
						.random(10 + calculateRangeAttack()) && !ignoreDef) {
					damage = 0;
				}
				if (Misc.random(4) == 1 && c.lastArrowUsed == 9242
						&& damage > 0) {
					PlayerHandler.players[i].gfx0(754);
					damage = NPCHandler.npcs[i].HP / 5;
					c.handleHitMask(c.playerLevel[3] / 10);
					c.dealDamage(c.playerLevel[3] / 10);
					c.gfx0(754);
				}

				if (c.lastWeaponUsed == 11235 || c.bowSpecShot == 1) {
					if (Misc.random(10 + o.getCombat().calculateRangeDefence()) > Misc
							.random(10 + calculateRangeAttack()))
						damage2 = 0;
				}

				if (c.dbowSpec) {
					o.gfx100(1100);
					if (damage < 8)
						damage = 8;
					if (damage2 < 8)
						damage2 = 8;
					c.dbowSpec = false;
				}
				if (damage > 0 && Misc.random(5) == 1
						&& c.lastArrowUsed == 9244) {
					damage *= 1.45;
					o.gfx0(756);
				}
				if (o.prayerActive[17]
						&& System.currentTimeMillis() - o.protRangeDelay > 1500) { // if
					// prayer
					// active
					// reduce
					// damage
					// by
					// half
					damage = damage * 60 / 100;
					if (c.lastWeaponUsed == 11235 || c.bowSpecShot == 1)
						damage2 = damage2 * 60 / 100;
				}
				if (PlayerHandler.players[i].playerLevel[3] - damage < 0) {
					damage = PlayerHandler.players[i].playerLevel[3];
				}
				if (PlayerHandler.players[i].playerLevel[3] - damage - damage2 < 0) {
					damage2 = PlayerHandler.players[i].playerLevel[3] - damage;
				}
				if (damage < 0)
					damage = 0;
				if (damage2 < 0 && damage2 != -1)
					damage2 = 0;
				if (o.vengOn) {
					appendVengeance(i, damage);
					appendVengeance(i, damage2);
				}
				if (damage > 0)
					applyRecoil(damage, i);
				if (damage2 > 0)
					applyRecoil(damage2, i);
				if (c.fightMode == 3) {
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE / 3),
							4);
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE / 3),
							1);
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE / 3),
							3);
					c.getPA().refreshSkill(1);
					c.getPA().refreshSkill(3);
					c.getPA().refreshSkill(4);
				} else {
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE), 4);
					c.getPA().addSkillXP((double) (damage * Settings.RANGE_EXP_RATE / 3),
							3);
					c.getPA().refreshSkill(3);
					c.getPA().refreshSkill(4);
				}
				boolean dropArrows = true;

				for (int noArrowId : c.NO_ARROW_DROP) {
					if (c.lastWeaponUsed == noArrowId) {
						dropArrows = false;
						break;
					}
				}
				if (dropArrows) {
					c.getItems().dropArrowPlayer();
				}
				PlayerHandler.players[i].underAttackBy = c.playerId;
				PlayerHandler.players[i].logoutDelay = System
						.currentTimeMillis();
				PlayerHandler.players[i].singleCombatDelay = System
						.currentTimeMillis();
				PlayerHandler.players[i].killerId = c.playerId;
				// Server.playerHandler.players[i].setHitDiff(damage);
				// Server.playerHandler.players[i].playerLevel[3] -= damage;
				PlayerHandler.players[i].dealDamage(damage);
				PlayerHandler.players[i].damageTaken[c.playerId] += damage;
				c.killedBy = PlayerHandler.players[i].playerId;
				PlayerHandler.players[i].handleHitMask(damage);
				if (damage2 != -1) {
					// Server.playerHandler.players[i].playerLevel[3] -=
					// damage2;
					PlayerHandler.players[i].dealDamage(damage2);
					PlayerHandler.players[i].damageTaken[c.playerId] += damage2;
					PlayerHandler.players[i].handleHitMask(damage2);

				}
				o.getPA().refreshSkill(3);

				// Server.playerHandler.players[i].setHitUpdateRequired(true);
				PlayerHandler.players[i].updateRequired = true;
				applySmite(i, damage);
				if (damage2 != -1)
					applySmite(i, damage2);

			} else if (c.projectileStage > 0) { // magic hit damage
				int damage = Misc.random(c.MAGIC_SPELLS[c.oldSpellId][6]);
				if (godSpells()) {
					if (System.currentTimeMillis() - c.godSpellDelay < Settings.GOD_SPELL_CHARGE) {
						damage += 10;
					}
				}
				// c.playerIndex = 0;
				if (c.magicFailed)
					damage = 0;

				if (o.prayerActive[16]
						&& System.currentTimeMillis() - o.protMageDelay > 1500) { // if
					// prayer
					// active
					// reduce
					// damage
					// by
					// half
					damage = damage * 60 / 100;
				}
				if (PlayerHandler.players[i].playerLevel[3] - damage < 0) {
					damage = PlayerHandler.players[i].playerLevel[3];
				}
				if (o.vengOn)
					appendVengeance(i, damage);
				if (damage > 0)
					applyRecoil(damage, i);
				c.getPA().addSkillXP(
						(c.MAGIC_SPELLS[c.oldSpellId][7] + damage
								* Settings.MAGIC_EXP_RATE), 6);
				c.getPA().addSkillXP(
						(c.MAGIC_SPELLS[c.oldSpellId][7] + damage
								* Settings.MAGIC_EXP_RATE / 3), 3);
				c.getPA().refreshSkill(3);
				c.getPA().refreshSkill(6);

				if (getEndGfxHeight() == 100 && !c.magicFailed) { // end GFX
					PlayerHandler.players[i]
							.gfx100(c.MAGIC_SPELLS[c.oldSpellId][5]);
				} else if (!c.magicFailed) {
					PlayerHandler.players[i]
							.gfx0(c.MAGIC_SPELLS[c.oldSpellId][5]);
				} else if (c.magicFailed) {
					PlayerHandler.players[i].gfx100(85);
				}

				if (!c.magicFailed) {
					if (System.currentTimeMillis()
							- PlayerHandler.players[i].reduceStat > 35000) {
						PlayerHandler.players[i].reduceStat = System
								.currentTimeMillis();
						switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
						case 12987:
						case 13011:
						case 12999:
						case 13023:
							PlayerHandler.players[i].playerLevel[0] -= ((o
									.getPA()
									.getLevelForXP(
											PlayerHandler.players[i].playerXP[0]) * 10) / 100);
							break;
						}
					}

					switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
					case 12445: // teleblock
						if (System.currentTimeMillis() - o.teleBlockDelay > o.teleBlockLength) {
							o.teleBlockDelay = System.currentTimeMillis();
							o.sendMessage("You have been teleblocked.");
							if (o.prayerActive[16]
									&& System.currentTimeMillis()
									- o.protMageDelay > 1500)
								o.teleBlockLength = 150000;
							else
								o.teleBlockLength = 300000;
						}
						break;

					case 12901:
					case 12919: // blood spells
					case 12911:
					case 12929:
						int heal = damage / 4;
						if (c.playerLevel[3] + heal > c.getPA().getLevelForXP(
								c.playerXP[3])) {
							c.playerLevel[3] = c.getPA().getLevelForXP(
									c.playerXP[3]);
						} else {
							c.playerLevel[3] += heal;
						}
						c.getPA().refreshSkill(3);
						break;

					case 1153:
						PlayerHandler.players[i].playerLevel[0] -= ((o.getPA()
								.getLevelForXP(
										PlayerHandler.players[i].playerXP[0]) * 5) / 100);
						o.sendMessage("Your attack level has been reduced!");
						PlayerHandler.players[i].reduceSpellDelay[c.reduceSpellId] = System
								.currentTimeMillis();
						o.getPA().refreshSkill(0);
						break;

					case 1157:
						PlayerHandler.players[i].playerLevel[2] -= ((o.getPA()
								.getLevelForXP(
										PlayerHandler.players[i].playerXP[2]) * 5) / 100);
						o.sendMessage("Your strength level has been reduced!");
						PlayerHandler.players[i].reduceSpellDelay[c.reduceSpellId] = System
								.currentTimeMillis();
						o.getPA().refreshSkill(2);
						break;

					case 1161:
						PlayerHandler.players[i].playerLevel[1] -= ((o.getPA()
								.getLevelForXP(
										PlayerHandler.players[i].playerXP[1]) * 5) / 100);
						o.sendMessage("Your defence level has been reduced!");
						PlayerHandler.players[i].reduceSpellDelay[c.reduceSpellId] = System
								.currentTimeMillis();
						o.getPA().refreshSkill(1);
						break;

					case 1542:
						PlayerHandler.players[i].playerLevel[1] -= ((o.getPA()
								.getLevelForXP(
										PlayerHandler.players[i].playerXP[1]) * 10) / 100);
						o.sendMessage("Your defence level has been reduced!");
						PlayerHandler.players[i].reduceSpellDelay[c.reduceSpellId] = System
								.currentTimeMillis();
						o.getPA().refreshSkill(1);
						break;

					case 1543:
						PlayerHandler.players[i].playerLevel[2] -= ((o.getPA()
								.getLevelForXP(
										PlayerHandler.players[i].playerXP[2]) * 10) / 100);
						o.sendMessage("Your strength level has been reduced!");
						PlayerHandler.players[i].reduceSpellDelay[c.reduceSpellId] = System
								.currentTimeMillis();
						o.getPA().refreshSkill(2);
						break;

					case 1562:
						PlayerHandler.players[i].playerLevel[0] -= ((o.getPA()
								.getLevelForXP(
										PlayerHandler.players[i].playerXP[0]) * 10) / 100);
						o.sendMessage("Your attack level has been reduced!");
						PlayerHandler.players[i].reduceSpellDelay[c.reduceSpellId] = System
								.currentTimeMillis();
						o.getPA().refreshSkill(0);
						break;
					}
				}

				PlayerHandler.players[i].logoutDelay = System
						.currentTimeMillis();
				PlayerHandler.players[i].underAttackBy = c.playerId;
				PlayerHandler.players[i].killerId = c.playerId;
				PlayerHandler.players[i].singleCombatDelay = System
						.currentTimeMillis();
				if (c.MAGIC_SPELLS[c.oldSpellId][6] != 0) {
					// Server.playerHandler.players[i].playerLevel[3] -= damage;
					PlayerHandler.players[i].dealDamage(damage);
					PlayerHandler.players[i].damageTaken[c.playerId] += damage;
					if (!c.magicFailed) {
						// Server.playerHandler.players[i].setHitDiff(damage);
						// Server.playerHandler.players[i].setHitUpdateRequired(true);
						PlayerHandler.players[i].handleHitMask(damage);
					}
				}
				applySmite(i, damage);
				c.killedBy = PlayerHandler.players[i].playerId;
				o.getPA().refreshSkill(3);
				PlayerHandler.players[i].updateRequired = true;
				c.usingMagic = false;
				c.castingMagic = false;
				if (o.inMulti() && multis()) {
					c.barrageCount = 0;
					for (int j = 0; j < PlayerHandler.players.length; j++) {
						if (PlayerHandler.players[j] != null) {
							if (j == o.playerId)
								continue;
							if (c.barrageCount >= 9)
								break;
							if (o.goodDistance(o.getX(), o.getY(),
									PlayerHandler.players[j].getX(),
									PlayerHandler.players[j].getY(), 1))
								appendMultiBarrage(j, c.magicFailed);
						}
					}
				}
				c.getPA().refreshSkill(3);
				c.getPA().refreshSkill(6);
				c.oldSpellId = 0;
			}
		}
		c.getPA().requestUpdates();
		int oldindex = c.oldPlayerIndex;
		if (c.bowSpecShot <= 0) {
			c.oldPlayerIndex = 0;
			c.projectileStage = 0;
			c.lastWeaponUsed = 0;
			c.doubleHit = false;
			c.bowSpecShot = 0;
		}
		if (c.bowSpecShot != 0) {
			c.bowSpecShot = 0;
		}
	}

	public boolean multis() {
		switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
		case 12891:
		case 12881:
		case 13011:
		case 13023:
		case 12919: // blood spells
		case 12929:
		case 12963:
		case 12975:
			return true;
		}
		return false;

	}

	public void appendMultiBarrage(int playerId, boolean splashed) {
		if (PlayerHandler.players[playerId] != null) {
			Client c2 = (Client) PlayerHandler.players[playerId];
			if (c2.isDead || c2.respawnTimer > 0)
				return;
			if (checkMultiBarrageReqs(playerId)) {
				c.barrageCount++;
				if (Misc.random(mageAtk()) > Misc.random(mageDef())
						&& !c.magicFailed) {
					if (getEndGfxHeight() == 100) { // end GFX
						c2.gfx100(c.MAGIC_SPELLS[c.oldSpellId][5]);
					} else {
						c2.gfx0(c.MAGIC_SPELLS[c.oldSpellId][5]);
					}
					int damage = Misc.random(c.MAGIC_SPELLS[c.oldSpellId][6]);
					if (c2.prayerActive[12]) {
						damage *= (int) (.60);
					}
					if (c2.playerLevel[3] - damage < 0) {
						damage = c2.playerLevel[3];
					}
					c.getPA().addSkillXP(
							(c.MAGIC_SPELLS[c.oldSpellId][7] + damage
									* Settings.MAGIC_EXP_RATE), 6);
					c.getPA().addSkillXP(
							(c.MAGIC_SPELLS[c.oldSpellId][7] + damage
									* Settings.MAGIC_EXP_RATE / 3), 3);
					// Server.playerHandler.players[playerId].setHitDiff(damage);
					// Server.playerHandler.players[playerId].setHitUpdateRequired(true);
					PlayerHandler.players[playerId].handleHitMask(damage);
					// Server.playerHandler.players[playerId].playerLevel[3] -=
					// damage;
					PlayerHandler.players[playerId].dealDamage(damage);
					PlayerHandler.players[playerId].damageTaken[c.playerId] += damage;
					c2.getPA().refreshSkill(3);
					multiSpellEffect(playerId, damage);
				} else {
					c2.gfx100(85);
				}
			}
		}
	}

	public void multiSpellEffect(int playerId, int damage) {
		switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
		case 13011:
		case 13023:
			if (System.currentTimeMillis()
					- PlayerHandler.players[playerId].reduceStat > 35000) {
				PlayerHandler.players[playerId].reduceStat = System
						.currentTimeMillis();
				PlayerHandler.players[playerId].playerLevel[0] -= ((PlayerAssistant
						.getLevelForXP(PlayerHandler.players[playerId].playerXP[0]) * 10) / 100);
			}
			break;
		case 12919: // blood spells
		case 12929:
			int heal = damage / 4;
			if (c.playerLevel[3] + heal >= c.getPA().getLevelForXP(
					c.playerXP[3])) {
				c.playerLevel[3] = c.getPA().getLevelForXP(c.playerXP[3]);
			} else {
				c.playerLevel[3] += heal;
			}
			c.getPA().refreshSkill(3);
			break;
		case 12891:
		case 12881:
			if (PlayerHandler.players[playerId].freezeTimer < -4) {
				PlayerHandler.players[playerId].freezeTimer = getFreezeTime();
				PlayerHandler.players[playerId].stopMovement();
			}
			break;
		}
	}

	public void applyPlayerMeleeDamage(int i, int damageMask) {
		Client o = (Client) PlayerHandler.players[i];
		if (o == null) {
			return;
		}
		int damage = Misc.random(calculateMeleeMaxHit());
		int damage2 = damage;
		damage = calculateDefenceDamageReduction(i, damage2);
		if (c.playerEquipment[c.playerWeapon] == 5698 && o.poisonDamage <= 0
				&& Misc.random(3) == 1)
			c.getPA().appendPoison(i);
		if (damageMask != 1) {
			damage = c.delayedDamage2;
			c.delayedDamage = 0;
		}
		if (c.playerEquipment[c.playerWeapon] == 5698
				&& o.poisonDamage <= 0 && Misc.random(3) == 1) {
			o.getPA().appendPoison(13);
			c.bonusAttack += damage / 3;
		} else {
			c.bonusAttack += damage / 3;
		}
		if (c.ssSpec && damageMask == 2) {
			damage = 5 + Misc.random(11);
			c.ssSpec = false;
		}
		if (PlayerHandler.players[i].playerLevel[3] - damage < 0) {
			damage = PlayerHandler.players[i].playerLevel[3];
		}
		if (o.vengOn && damage > 0)
			appendVengeance(i, damage);
		if (damage > 0)
			applyRecoil(damage, i);
		switch (c.specEffect) {
		case 1: // dragon scimmy special
			if (damage > 0) {
				if (o.prayerActive[16] || o.prayerActive[17]
						|| o.prayerActive[18]) {
					o.headIcon = -1;
					o.getPA().sendFrame36(c.PRAYER_GLOW[16], 0);
					o.getPA().sendFrame36(c.PRAYER_GLOW[17], 0);
					o.getPA().sendFrame36(c.PRAYER_GLOW[18], 0);
				}
				o.sendMessage("You have been injured!");
				o.stopPrayerDelay = System.currentTimeMillis();
				o.prayerActive[16] = false;
				o.prayerActive[17] = false;
				o.prayerActive[18] = false;
				o.getPA().requestUpdates();
			}
			break;
		case 2:
			if (damage > 0) {
				if (o.freezeTimer <= 0)
					o.freezeTimer = 30;
				o.gfx0(369);
				o.sendMessage("You have been frozen.");
				o.frozenBy = c.playerId;
				o.stopMovement();
				c.sendMessage("You freeze your enemy.");
			}
			break;
		case 3:
			if (damage > 0) {
				o.playerLevel[1] -= damage;
				o.sendMessage("You feel weak.");
				if (o.playerLevel[1] < 1)
					o.playerLevel[1] = 1;
				o.getPA().refreshSkill(1);
			}
			break;
		case 4:
			if (damage > 0) {
				if (c.playerLevel[3] + damage > PlayerAssistant.getLevelForXP(c.playerXP[3]))
					if (c.playerLevel[3] > PlayerAssistant.getLevelForXP(c.playerXP[3]))
						;
					else
						c.playerLevel[3] = PlayerAssistant.getLevelForXP(c.playerXP[3]);
				else
					c.playerLevel[3] += damage;
				c.getPA().refreshSkill(3);
			}
			break;
		}
		c.specEffect = 0;
		if (c.fightMode == 3) {
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE / 3), 0);
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE / 3), 1);
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE / 3), 2);
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE / 3), 3);
			c.getPA().refreshSkill(0);
			c.getPA().refreshSkill(1);
			c.getPA().refreshSkill(2);
			c.getPA().refreshSkill(3);
		} else {
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE), c.fightMode);
			c.getPA().addSkillXP((double) (damage * Settings.MELEE_EXP_RATE / 3), 3);
			c.getPA().refreshSkill(c.fightMode);
			c.getPA().refreshSkill(3);
		}
		PlayerHandler.players[i].logoutDelay = System.currentTimeMillis();
		PlayerHandler.players[i].underAttackBy = c.playerId;
		PlayerHandler.players[i].killerId = c.playerId;
		PlayerHandler.players[i].singleCombatDelay = System.currentTimeMillis();
		if (c.killedBy != PlayerHandler.players[i].playerId)
			c.killedBy = PlayerHandler.players[i].playerId;
		applySmite(i, damage);
		switch (damageMask) {
		case 1:
			/*
			 * if (!Server.playerHandler.players[i].getHitUpdateRequired()){
			 * Server.playerHandler.players[i].setHitDiff(damage);
			 * Server.playerHandler.players[i].setHitUpdateRequired(true); }
			 * else { Server.playerHandler.players[i].setHitDiff2(damage);
			 * Server.playerHandler.players[i].setHitUpdateRequired2(true); }
			 */
			// Server.playerHandler.players[i].playerLevel[3] -= damage;
			PlayerHandler.players[i].dealDamage(damage);
			PlayerHandler.players[i].damageTaken[c.playerId] += damage;
			PlayerHandler.players[i].updateRequired = true;
			o.getPA().refreshSkill(3);
			break;

		case 2:
			/*
			 * if (!Server.playerHandler.players[i].getHitUpdateRequired2()){
			 * Server.playerHandler.players[i].setHitDiff2(damage);
			 * Server.playerHandler.players[i].setHitUpdateRequired2(true); }
			 * else { Server.playerHandler.players[i].setHitDiff(damage);
			 * Server.playerHandler.players[i].setHitUpdateRequired(true); }
			 */
			// Server.playerHandler.players[i].playerLevel[3] -= damage;
			PlayerHandler.players[i].dealDamage(damage);
			PlayerHandler.players[i].damageTaken[c.playerId] += damage;
			PlayerHandler.players[i].updateRequired = true;
			c.doubleHit = false;
			o.getPA().refreshSkill(3);
			break;
		}
		PlayerHandler.players[i].handleHitMask(damage);
	}

	public void applySmite(int index, int damage) {
		if (!c.prayerActive[23])
			return;
		if (damage <= 0)
			return;
		if (PlayerHandler.players[index] != null) {
			Client c2 = (Client) PlayerHandler.players[index];
			c2.playerLevel[5] -= damage / 4;
			if (c2.playerLevel[5] <= 0) {
				c2.playerLevel[5] = 0;
				c2.getCombat().resetPrayers();
			}
			c2.getPA().refreshSkill(5);
		}

	}

	public void fireProjectilePlayer() {
		if (c.oldPlayerIndex > 0) {
			if (PlayerHandler.players[c.oldPlayerIndex] != null) {
				c.projectileStage = 2;
				int pX = c.getX();
				int pY = c.getY();
				int oX = PlayerHandler.players[c.oldPlayerIndex].getX();
				int oY = PlayerHandler.players[c.oldPlayerIndex].getY();
				int offX = (pY - oY) * -1;
				int offY = (pX - oX) * -1;
				if (!c.msbSpec)
					c.getPA().createPlayersProjectile(pX, pY, offX, offY, 50,
							getProjectileSpeed(), getRangeProjectileGFX(), 43,
							31, -c.oldPlayerIndex - 1, getStartDelay());
				else if (c.msbSpec) {
					c.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50,
							getProjectileSpeed(), getRangeProjectileGFX(), 43,
							31, -c.oldPlayerIndex - 1, getStartDelay(), 10);
					c.msbSpec = false;
				}
				if (usingDbow())
					c.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50,
							getProjectileSpeed(), getRangeProjectileGFX(), 60,
							31, -c.oldPlayerIndex - 1, getStartDelay(), 35);
			}
		}
	}

	public boolean usingDbow() {
		return c.playerEquipment[c.playerWeapon] == 11235;
	}

	/** Prayer **/

	public void activatePrayer(int i) {
		if (c.duelRule[7]) {
			for (int p = 0; p < c.PRAYER.length; p++) { // reset prayer glows
				c.prayerActive[p] = false;
				c.getPA().sendFrame36(c.PRAYER_GLOW[p], 0);
			}
			c.sendMessage("Prayer has been disabled in this duel!");
			return;
		}
		if (i == 24 && c.playerLevel[1] < 65) {
			c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
			c.sendMessage("You may not use this prayer yet.");
			return;
		}
		if (i == 25 && c.playerLevel[1] < 70) {
			c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
			c.sendMessage("You may not use this prayer yet.");
			return;
		}
		int[] defPray = { 0, 5, 13, 24, 25 };
		int[] strPray = { 1, 6, 14, 24, 25 };
		int[] atkPray = { 2, 7, 15, 24, 25 };
		int[] rangePray = { 3, 11, 19 };
		int[] magePray = { 4, 12, 20 };

		if (c.playerLevel[5] > 0 || !Settings.PRAYER_POINTS_REQUIRED) {
			if (c.getPA().getLevelForXP(c.playerXP[5]) >= c.PRAYER_LEVEL_REQUIRED[i]
					|| !Settings.PRAYER_LEVEL_REQUIRED) {
				boolean headIcon = false;
				switch (i) {
				case 0:
				case 5:
				case 13:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < defPray.length; j++) {
							if (defPray[j] != i) {
								c.prayerActive[defPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[defPray[j]], 0);
							}
						}
					}
					break;

				case 1:
				case 6:
				case 14:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;

				case 2:
				case 7:
				case 15:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;

				case 3:// range prays
				case 11:
				case 19:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;
				case 4:
				case 12:
				case 20:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;
				case 10:
					c.lastProtItem = System.currentTimeMillis();
					break;

				case 16:
				case 17:
				case 18:
					if (System.currentTimeMillis() - c.stopPrayerDelay < 5000) {
						c.sendMessage("You have been injured and can't use this prayer!");
						c.getPA().sendFrame36(c.PRAYER_GLOW[16], 0);
						c.getPA().sendFrame36(c.PRAYER_GLOW[17], 0);
						c.getPA().sendFrame36(c.PRAYER_GLOW[18], 0);
						return;
					}
					if (i == 16)
						c.protMageDelay = System.currentTimeMillis();
					else if (i == 17)
						c.protRangeDelay = System.currentTimeMillis();
					else if (i == 18)
						c.protMeleeDelay = System.currentTimeMillis();
				case 21:
				case 22:
				case 23:
					headIcon = true;
					for (int p = 16; p < 24; p++) {
						if (i != p && p != 19 && p != 20) {
							c.prayerActive[p] = false;
							c.getPA().sendFrame36(c.PRAYER_GLOW[p], 0);
						}
					}
					break;
				case 24:
				case 25:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[magePray[j]], 0);
							}
						}
						for (int j = 0; j < defPray.length; j++) {
							if (defPray[j] != i) {
								c.prayerActive[defPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[defPray[j]], 0);
							}
						}
					}
					break;
				}

				if (!headIcon) {
					if (c.prayerActive[i] == false) {
						c.prayerActive[i] = true;
						c.getPA().sendFrame36(c.PRAYER_GLOW[i], 1);
					} else {
						c.prayerActive[i] = false;
						c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
					}
				} else {
					if (c.prayerActive[i] == false) {
						c.prayerActive[i] = true;
						c.getPA().sendFrame36(c.PRAYER_GLOW[i], 1);
						c.headIcon = c.PRAYER_HEAD_ICONS[i];
						c.getPA().requestUpdates();
					} else {
						c.prayerActive[i] = false;
						c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
						c.headIcon = -1;
						c.getPA().requestUpdates();
					}
				}
			} else {
				c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
				c.getPA().sendFrame126(
						"You need a @blu@Prayer level of "
								+ c.PRAYER_LEVEL_REQUIRED[i] + " to use "
								+ c.PRAYER_NAME[i] + ".", 357);
				c.getPA().sendFrame126("Click here to continue", 358);
				c.getPA().sendFrame164(356);
			}
		} else {
			c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
			c.sendMessage("You have run out of prayer points!");
		}

	}

	/**
	 * Specials
	 **/

	public void activateSpecial(int weapon, int i) {
		if (NPCHandler.npcs[i] == null && c.npcIndex > 0) {
			return;
		}
		if (PlayerHandler.players[i] == null && c.playerIndex > 0) {
			return;
		}
		c.doubleHit = false;
		c.specEffect = 0;
		c.projectileStage = 0;
		c.specMaxHitIncrease = 2;
		if (c.npcIndex > 0) {
			c.oldNpcIndex = i;
		} else if (c.playerIndex > 0) {
			c.oldPlayerIndex = i;
			PlayerHandler.players[i].underAttackBy = c.playerId;
			PlayerHandler.players[i].logoutDelay = System.currentTimeMillis();
			PlayerHandler.players[i].singleCombatDelay = System
					.currentTimeMillis();
			PlayerHandler.players[i].killerId = c.playerId;
		}
		switch (weapon) {

		case 1305: // dragon long
			c.gfx100(248);
			c.startAnimation(1058);
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			c.specAccuracy = 1.10;
			c.specDamage = 1.20;
			break;

		case 1215: // dragon daggers
		case 1231:
		case 5680:
		case 5698:
			c.gfx100(252);
			c.startAnimation(1062);
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			c.doubleHit = true;
			c.specAccuracy = 1.30;
			c.specDamage = 1.05;
			break;

		case 11730:
			c.gfx100(1224);
			c.startAnimation(811);
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			c.doubleHit = true;
			c.ssSpec = true;
			c.specAccuracy = 1.30;
			break;

		case 4151: // whip
			if (NPCHandler.npcs[i] != null) {
				NPCHandler.npcs[i].gfx100(341);
			}
			c.specAccuracy = 1.10;
			c.startAnimation(1658);
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			break;

		case 11694: // ags
			c.startAnimation(4304);
			c.specDamage = 1.25;
			c.specAccuracy = 1.85;
			c.gfx0(1222);
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			break;

		case 11700:
			c.startAnimation(4302);
			c.gfx0(1221);
			c.specAccuracy = 1.25;
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			c.specEffect = 2;
			break;

		case 11696:
			c.startAnimation(4301);
			c.gfx0(1223);
			c.specDamage = 1.10;
			c.specAccuracy = 1.5;
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			c.specEffect = 3;
			break;

		case 11698:
			c.startAnimation(4303);
			c.gfx0(1220);
			c.specAccuracy = 1.25;
			c.specEffect = 4;
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			break;

		case 1249:
			c.startAnimation(405);
			c.gfx100(253);
			if (c.playerIndex > 0) {
				Client o = (Client) PlayerHandler.players[i];
				o.getPA().getSpeared(c.absX, c.absY);
			}
			break;

		case 3204: // d hally
			c.gfx100(282);
			c.startAnimation(1203);
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			if (NPCHandler.npcs[i] != null && c.npcIndex > 0) {
				if (!c.goodDistance(c.getX(), c.getY(),
						NPCHandler.npcs[i].getX(), NPCHandler.npcs[i].getY(), 1)) {
					c.doubleHit = true;
				}
			}
			if (PlayerHandler.players[i] != null && c.playerIndex > 0) {
				if (!c.goodDistance(c.getX(), c.getY(),
						PlayerHandler.players[i].getX(),
						PlayerHandler.players[i].getY(), 1)) {
					c.doubleHit = true;
					c.delayedDamage2 = Misc.random(calculateMeleeMaxHit());
				}
			}
			break;

		case 4153: // maul
			c.startAnimation(1667);
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			/*
			 * if (c.playerIndex > 0) gmaulPlayer(i); else gmaulNpc(i);
			 */
			c.gfx100(337);
			break;

		case 4587: // dscimmy
			c.gfx100(347);
			c.specEffect = 1;
			c.startAnimation(1872);
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			break;

		case 1434: // mace
			c.startAnimation(1060);
			c.gfx100(251);
			c.specMaxHitIncrease = 3;
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase()) + 1;
			c.specDamage = 1.35;
			c.specAccuracy = 1.15;
			break;

		case 859: // magic long
			c.usingBow = true;
			c.bowSpecShot = 3;
			c.rangeItemUsed = c.playerEquipment[c.playerArrows];
			c.getItems().deleteArrow();
			c.lastWeaponUsed = weapon;
			c.startAnimation(426);
			c.gfx100(250);
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			c.projectileStage = 1;
			if (c.fightMode == 2)
				c.attackTimer--;
			break;

		case 861: // magic short
			c.usingBow = true;
			c.bowSpecShot = 1;
			c.rangeItemUsed = c.playerEquipment[c.playerArrows];
			c.getItems().deleteArrow();
			c.lastWeaponUsed = weapon;
			c.startAnimation(1074);
			c.hitDelay = 3;
			c.projectileStage = 1;
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			if (c.fightMode == 2)
				c.attackTimer--;
			if (c.playerIndex > 0)
				fireProjectilePlayer();
			else if (c.npcIndex > 0)
				fireProjectileNpc();
			break;

		case 11235: // dark bow
			c.usingBow = true;
			c.dbowSpec = true;
			c.rangeItemUsed = c.playerEquipment[c.playerArrows];
			c.getItems().deleteArrow();
			c.getItems().deleteArrow();
			c.lastWeaponUsed = weapon;
			c.hitDelay = 3;
			c.startAnimation(426);
			c.projectileStage = 1;
			c.gfx100(getRangeStartGFX());
			c.hitDelay = getHitDelay(ItemHandler
					.getItemName(c.playerEquipment[c.playerWeapon])
					.toLowerCase());
			if (c.fightMode == 2)
				c.attackTimer--;
			if (c.playerIndex > 0)
				fireProjectilePlayer();
			else if (c.npcIndex > 0)
				fireProjectileNpc();
			c.specAccuracy = 1.75;
			c.specDamage = 1.50;
			break;
		}
		c.delayedDamage = Misc.random(calculateMeleeMaxHit());
		c.delayedDamage2 = Misc.random(calculateMeleeMaxHit());
		c.usingSpecial = false;
		c.getItems().updateSpecialBar();
	}

	public boolean checkSpecAmount(int weapon) {
		switch (weapon) {
		case 1249:
		case 1215:
		case 1231:
		case 5680:
		case 5698:
		case 1305:
		case 1434:
			if (c.specAmount >= 2.5) {
				c.specAmount -= 2.5;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		case 4151:
		case 11694:
		case 11698:
		case 4153:
			if (c.specAmount >= 5) {
				c.specAmount -= 5;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		case 3204:
			if (c.specAmount >= 3) {
				c.specAmount -= 3;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		case 1377:
		case 11696:
		case 11730:
			if (c.specAmount >= 10) {
				c.specAmount -= 10;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		case 4587:
		case 859:
		case 861:
		case 11235:
		case 11700:
			if (c.specAmount >= 5.5) {
				c.specAmount -= 5.5;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		default:
			return true; // incase u want to test a weapon
		}
	}

	public void resetPlayerAttack() {
		c.usingMagic = false;
		c.npcIndex = 0;
		c.faceUpdate(0);
		c.playerIndex = 0;
		c.getPA().resetFollow();
		// c.sendMessage("Reset attack.");
	}

	public int getCombatDifference(int combat1, int combat2) {
		if (combat1 > combat2) {
			return (combat1 - combat2);
		}
		if (combat2 > combat1) {
			return (combat2 - combat1);
		}
		return 0;
	}

	/**
	 * Get killer id
	 **/

	public int getKillerId(int playerId) {
		int oldDamage = 0;
		int count = 0;
		int killerId = 0;
		for (int i = 1; i < Settings.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				if (PlayerHandler.players[i].killedBy == playerId) {

					PlayerHandler.players[i].killedBy = 0;
				}
			}
		}
		return killerId;
	}

	double[] prayerData = { 1, // Thick Skin.
			1, // Burst of Strength.
			1, // Clarity of Thought.
			1, // Sharp Eye.
			1, // Mystic Will.
			2, // Rock Skin.
			2, // SuperHuman Strength.
			2, // Improved Reflexes.
			0.4, // Rapid restore.
			0.6, // Rapid Heal.
			0.6, // Protect Items.
			1.5, // Hawk eye.
			2, // Mystic Lore.
			4, // Steel Skin.
			4, // Ultimate Strength.
			4, // Incredible Reflexes.
			4, // Protect from Magic.
			4, // Protect from Missiles.
			4, // Protect from Melee.
			4, // Eagle Eye.
			4, // Mystic Might.
			1, // Retribution.
			2, // Redemption.
			6, // Smite.
			8, // Chivalry.
			8, // Piety.
	};

	public void handlePrayerDrain() {
		c.usingPrayer = false;
		double toRemove = 0.0;
		for (int j = 0; j < prayerData.length; j++) {
			if (c.prayerActive[j]) {
				toRemove += prayerData[j] / 20;
				c.usingPrayer = true;
			}
		}
		if (toRemove > 0) {
			toRemove /= (1 + (0.035 * c.playerBonus[11]));
		}
		c.prayerPoint -= toRemove;
		if (c.prayerPoint <= 0) {
			c.prayerPoint = 1.0 + c.prayerPoint;
			reducePrayerLevel();
		}

	}

	public void reducePrayerLevel() {
		if (c.playerLevel[5] - 1 > 0) {
			c.playerLevel[5] -= 1;
		} else {
			c.sendMessage("You have run out of prayer points!");
			c.playerLevel[5] = 0;
			resetPrayers();
			c.prayerId = -1;
		}
		c.getPA().refreshSkill(5);
	}

	public void resetPrayers() {
		for (int i = 0; i < c.prayerActive.length; i++) {
			c.prayerActive[i] = false;
			c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
		}
		c.headIcon = -1;
		c.getPA().requestUpdates();
	}

	/**
	 * Wildy and duel info
	 **/

	public boolean checkReqs() {
		if (PlayerHandler.players[c.playerIndex] == null) {
			return false;
		}
		if (c.playerIndex == c.playerId)
			return false;
		if (PlayerHandler.players[c.playerIndex].inDuelArena()
				&& c.duelStatus != 5 && !c.usingMagic) {
			if (c.arenas() || c.duelStatus == 5) {
				c.sendMessage("You can't challenge inside the arena!");
				return false;
			}
			c.getTradeAndDuel().requestDuel(c.playerIndex);
			return false;
		}
		if (c.duelStatus == 5
				&& PlayerHandler.players[c.playerIndex].duelStatus == 5) {
			if (PlayerHandler.players[c.playerIndex].duelingWith == c.getId()) {
				return true;
			} else {
				c.sendMessage("This isn't your opponent!");
				return false;
			}
		}
		if (!PlayerHandler.players[c.playerIndex].inWild()) {
			c.sendMessage("That player is not in the wilderness.");
			c.stopMovement();
			c.getCombat().resetPlayerAttack();
			return false;
		}
		if (!c.inWild()) {
			c.sendMessage("You are not in the wilderness.");
			c.stopMovement();
			c.getCombat().resetPlayerAttack();
			return false;
		}
		if (Settings.COMBAT_LEVEL_DIFFERENCE) {
			int combatDif1 = c.getCombat().getCombatDifference(c.combatLevel,
					PlayerHandler.players[c.playerIndex].combatLevel);
			if (combatDif1 > c.wildLevel
					|| combatDif1 > PlayerHandler.players[c.playerIndex].wildLevel) {
				c.sendMessage("Your combat level difference is too great to attack that player here.");
				c.stopMovement();
				c.getCombat().resetPlayerAttack();
				return false;
			}
		}

		if (Settings.SINGLE_AND_MULTI_ZONES) {
			if (!PlayerHandler.players[c.playerIndex].inMulti()) { // single
				// combat
				// zones
				if (PlayerHandler.players[c.playerIndex].underAttackBy != c.playerId
						&& PlayerHandler.players[c.playerIndex].underAttackBy != 0) {
					c.sendMessage("That player is already in combat.");
					c.stopMovement();
					c.getCombat().resetPlayerAttack();
					return false;
				}
				if (PlayerHandler.players[c.playerIndex].playerId != c.underAttackBy
						&& c.underAttackBy != 0 || c.underAttackBy2 > 0) {
					c.sendMessage("You are already in combat.");
					c.stopMovement();
					c.getCombat().resetPlayerAttack();
					return false;
				}
			}
		}
		return true;
	}

	public boolean checkMultiBarrageReqs(int i) {
		if (PlayerHandler.players[i] == null) {
			return false;
		}
		if (i == c.playerId)
			return false;
		if (!PlayerHandler.players[i].inWild()) {
			return false;
		}
		if (Settings.COMBAT_LEVEL_DIFFERENCE) {
			int combatDif1 = c.getCombat().getCombatDifference(c.combatLevel,
					PlayerHandler.players[i].combatLevel);
			if (combatDif1 > c.wildLevel
					|| combatDif1 > PlayerHandler.players[i].wildLevel) {
				c.sendMessage("Your combat level difference is too great to attack that player here.");
				return false;
			}
		}

		if (Settings.SINGLE_AND_MULTI_ZONES) {
			if (!PlayerHandler.players[i].inMulti()) { // single combat zones
				if (PlayerHandler.players[i].underAttackBy != c.playerId
						&& PlayerHandler.players[i].underAttackBy != 0) {
					return false;
				}
				if (PlayerHandler.players[i].playerId != c.underAttackBy
						&& c.underAttackBy != 0) {
					c.sendMessage("You are already in combat.");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Weapon stand, walk, run, etc emotes
	 **/

	public void getPlayerAnimIndex(String weaponName) {
		c.playerStandIndex = 0x328;
		c.playerTurnIndex = 0x337;
		c.playerWalkIndex = 0x333;
		c.playerTurn180Index = 0x334;
		c.playerTurn90CWIndex = 0x335;
		c.playerTurn90CCWIndex = 0x336;
		c.playerRunIndex = 0x338;

		if (weaponName.contains("halberd") || weaponName.contains("guthan") || weaponName.contains("hasta")) {
			c.playerStandIndex = 809;
			c.playerWalkIndex = 1146;
			c.playerRunIndex = 1210;
			return;
		}
		if (weaponName.contains("dharok")) {
			c.playerStandIndex = 0x811;
			c.playerWalkIndex = 0x67F;
			c.playerRunIndex = 0x680;
			return;
		}
		if (weaponName.contains("ahrim")) {
			c.playerStandIndex = 809;
			c.playerWalkIndex = 1146;
			c.playerRunIndex = 1210;
			return;
		}
		if (weaponName.contains("verac")) {
			c.playerStandIndex = 1832;
			c.playerWalkIndex = 1830;
			c.playerRunIndex = 1831;
			return;
		}
		if (weaponName.contains("wand") || weaponName.contains("staff")) {
			c.playerStandIndex = 809;
			c.playerRunIndex = 1210;
			c.playerWalkIndex = 1146;
			return;
		}
		if (weaponName.contains("karil")) {
			c.playerStandIndex = 2074;
			c.playerWalkIndex = 2076;
			c.playerRunIndex = 2077;
			return;
		}
		if (weaponName.contains("2h sword") || weaponName.contains("godsword")
				|| weaponName.contains("saradomin sw")) {
			c.playerStandIndex = 7047;
			c.playerWalkIndex = 7046;
			c.playerRunIndex = 7039;
			return;
		}
		if (weaponName.contains("bow")) {
			c.playerStandIndex = 808;
			c.playerWalkIndex = 819;
			c.playerRunIndex = 824;
			return;
		}

		switch (c.playerEquipment[c.playerWeapon]) {
		case 4151:
			c.playerStandIndex = 1832;
			c.playerWalkIndex = 1660;
			c.playerRunIndex = 1661;
			break;
		case 6528:
			c.playerStandIndex = 0x811;
			c.playerWalkIndex = 2064;
			c.playerRunIndex = 1664;
			break;
		case 4153:
			c.playerStandIndex = 1662;
			c.playerWalkIndex = 1663;
			c.playerRunIndex = 1664;
			break;
		case 11694:
		case 11696:
		case 11730:
		case 11698:
		case 11700:
			c.playerStandIndex = 4300;
			c.playerWalkIndex = 4306;
			c.playerRunIndex = 4305;
			break;
		case 1305:
			c.playerStandIndex = 809;
			break;
		}
	}

	/**
	 * Weapon emotes
	 **/

	public int getWepAnim(String weaponName) {
		if (c.playerEquipment[c.playerWeapon] <= 0) {
			switch (c.fightMode) {
			case 0:
				return 422;
			case 2:
				return 423;
			case 1:
				return 451;
			}
		}
		if (weaponName.contains("knife") || weaponName.contains("dart")
				|| weaponName.contains("javelin")
				|| weaponName.contains("thrownaxe")) {
			return 806;
		}
		if (weaponName.contains("halberd")) {
			return 440;
		}
		if (weaponName.startsWith("dragon dagger")) {
			return 402;
		}
		if (weaponName.endsWith("dagger")) {
			return 412;
		}
		if (weaponName.contains("2h sword") || weaponName.contains("godsword")
				|| weaponName.contains("aradomin sword")) {
			return 7041; //7049 Swipe low and 7048 Crush
		}
		if (weaponName.contains("sword")) {
			return 451;
		}
		if (weaponName.contains("karil")) {
			return 2075;
		}
		if (weaponName.contains("bow") && !weaponName.contains("'bow")) {
			return 426;
		}
		if (weaponName.contains("'bow"))
			return 4230;

		switch (c.playerEquipment[c.playerWeapon]) { // if you don't want to use
		// strings
		case 6522:
			return 2614;
		case 4153: // granite maul
			return 1665;
		case 4726: // guthan
			return 2080;
		case 11367:
		case 11369:
		case 11371:
		case 11373:
		case 11375:
		case 11377:
			return 412;
		case 4747: // torag
			return 0x814;
		case 4718: // dharok
			return 2067;
		case 4710: // ahrim
			return 406;
		case 4755: // verac
			return 2062;
		case 4734: // karil
			return 2075;
		case 4151:
			return 1658;
		case 6528:
			return 2661;
		default:
			return 451;
		}
	}

	/**
	 * Block emotes
	 */
	public int getBlockEmote() {
		if (c.playerEquipment[c.playerShield] >= 8844
				&& c.playerEquipment[c.playerShield] <= 8850) {
			return 4177;
		}
		
		switch (c.playerEquipment[c.playerWeapon]) {
		case 4755:
			return 2063;

		case 4153:
			return 1666;

		case 4151:
			return 1659;

		case 11694:
		case 11698:
		case 11700:
		case 11696:
		case 11730:
			return -1;
		default:
			return 404;
		}
	}

	/**
	 * Weapon and magic attack speed!
	 **/

	public int getAttackDelay(String s) {
		if (c.usingMagic) {
			switch (c.MAGIC_SPELLS[c.spellId][0]) {
			case 12871: // ice blitz
			case 13023: // shadow barrage
			case 12891: // ice barrage
				return 5;

			default:
				return 5;
			}
		}
		if (c.playerEquipment[c.playerWeapon] == -1)
			return 4;// unarmed

		switch (c.playerEquipment[c.playerWeapon]) {
		case 11235:
			return 9;
		case 11730:
			return 4;
		case 6528:
			return 7;
		}

		if (s.endsWith("greataxe"))
			return 7;
		else if (s.equals("torags hammers"))
			return 5;
		else if (s.equals("guthans warspear"))
			return 5;
		else if (s.equals("veracs flail"))
			return 5;
		else if (s.equals("ahrims staff"))
			return 6;
		else if (s.contains("staff")) {
			if (s.contains("zamarok") || s.contains("guthix")
					|| s.contains("saradomian") || s.contains("slayer")
					|| s.contains("ancient"))
				return 4;
			else
				return 5;
		} else if (s.contains("bow")) {
			if (s.contains("composite") || s.equals("seercull"))
				return 5;
			else if (s.contains("aril"))
				return 4;
			else if (s.contains("Ogre"))
				return 8;
			else if (s.contains("short") || s.contains("hunt")
					|| s.contains("sword"))
				return 4;
			else if (s.contains("long") || s.contains("crystal"))
				return 6;
			else if (s.contains("'bow"))
				return 7;

			return 5;
		} else if (s.contains("dagger"))
			return 4;
		else if (s.contains("godsword") || s.contains("2h"))
			return 6;
		else if (s.contains("longsword"))
			return 5;
		else if (s.contains("sword"))
			return 4;
		else if (s.contains("scimitar"))
			return 4;
		else if (s.contains("mace"))
			return 5;
		else if (s.contains("battleaxe"))
			return 6;
		else if (s.contains("pickaxe"))
			return 5;
		else if (s.contains("thrownaxe"))
			return 5;
		else if (s.contains("axe"))
			return 5;
		else if (s.contains("warhammer"))
			return 6;
		else if (s.contains("2h"))
			return 7;
		else if (s.contains("spear"))
			return 5;
		else if (s.contains("claw"))
			return 4;
		else if (s.contains("halberd"))
			return 7;

		// sara sword, 2400ms
		else if (s.equals("granite maul"))
			return 7;
		else if (s.equals("toktz-xil-ak"))// sword
			return 4;
		else if (s.equals("tzhaar-ket-em"))// mace
			return 5;
		else if (s.equals("tzhaar-ket-om"))// maul
			return 7;
		else if (s.equals("toktz-xil-ek"))// knife
			return 4;
		else if (s.equals("toktz-xil-ul"))// rings
			return 4;
		else if (s.equals("toktz-mej-tal"))// staff
			return 6;
		else if (s.contains("whip"))
			return 4;
		else if (s.contains("dart"))
			return 3;
		else if (s.contains("knife"))
			return 3;
		else if (s.contains("javelin"))
			return 6;
		return 5;
	}

	/**
	 * How long it takes to hit your enemy
	 **/
	public int getHitDelay(String weaponName) {
		if (c.usingMagic) {
			switch (c.MAGIC_SPELLS[c.spellId][0]) {
			case 12891:
				return 4;
			case 12871:
				return 6;
			default:
				return 4;
			}
		} else {

			if (weaponName.contains("knife") || weaponName.contains("dart")
					|| weaponName.contains("javelin")
					|| weaponName.contains("thrownaxe")) {
				return 3;
			}
			if (weaponName.contains("cross") || weaponName.contains("c'bow")) {
				return 4;
			}
			if (weaponName.contains("bow") && !c.dbowSpec) {
				return 4;
			} else if (c.dbowSpec) {
				return 4;
			}

			switch (c.playerEquipment[c.playerWeapon]) {
			case 6522: // Toktz-xil-ul
				return 3;

			default:
				return 2;
			}
		}
	}

	public int getRequiredDistance() {
		if (c.followId > 0 && c.freezeTimer <= 0 && !c.isMoving)
			return 2;
		else if (c.followId > 0 && c.freezeTimer <= 0 && c.isMoving) {
			return 3;
		} else {
			return 1;
		}
	}

	public boolean usingHally() {
		switch (c.playerEquipment[c.playerWeapon]) {
		case 3190:
		case 3192:
		case 3194:
		case 3196:
		case 3198:
		case 3200:
		case 3202:
		case 3204:
			return true;

		default:
			return false;
		}
	}

	/**
	 * Melee
	 **/

	public int calculateMeleeAttack() {
		int attackLevel = c.playerLevel[0];
		// 2, 5, 11, 18, 19
		if (c.prayerActive[2]) {
			attackLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerAttack]) * 0.05;
		} else if (c.prayerActive[7]) {
			attackLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerAttack]) * 0.1;
		} else if (c.prayerActive[15]) {
			attackLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerAttack]) * 0.15;
		} else if (c.prayerActive[24]) {
			attackLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerAttack]) * 0.15;
		} else if (c.prayerActive[25]) {
			attackLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerAttack]) * 0.2;
		}
		if (c.fullVoidMelee())
			attackLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerAttack]) * 0.1;
		attackLevel *= c.specAccuracy;
		// c.sendMessage("Attack: " + (attackLevel +
		// (c.playerBonus[bestMeleeAtk()] * 2)));
		int i = c.playerBonus[bestMeleeAtk()];
		i += c.bonusAttack;
		if (c.playerEquipment[c.playerAmulet] == 11128
				&& c.playerEquipment[c.playerWeapon] == 6528) {
			i *= 1.30;
		}
		return (int) (attackLevel + (attackLevel * 0.15) + (i + i * 0.05));
	}

	public int bestMeleeAtk() {
		if (c.playerBonus[0] > c.playerBonus[1]
				&& c.playerBonus[0] > c.playerBonus[2])
			return 0;
		if (c.playerBonus[1] > c.playerBonus[0]
				&& c.playerBonus[1] > c.playerBonus[2])
			return 1;
		return c.playerBonus[2] <= c.playerBonus[1]
				|| c.playerBonus[2] <= c.playerBonus[0] ? 0 : 2;
	}

	public int calculateMeleeMaxHit() {
		double maxHit = 0;
		int strBonus = c.playerBonus[10];
		int strength = c.playerLevel[2];
		int lvlForXP = PlayerAssistant.getLevelForXP(c.playerXP[2]);
		if (c.prayerActive[1]) {
			strength += (int) (lvlForXP * .05);
		} else if (c.prayerActive[6]) {
			strength += (int) (lvlForXP * .10);
		} else if (c.prayerActive[14]) {
			strength += (int) (lvlForXP * .15);
		} else if (c.prayerActive[24]) {
			strength += (int) (lvlForXP * .18);
		} else if (c.prayerActive[25]) {
			strength += (int) (lvlForXP * .23);
		}
		if (c.playerEquipment[c.playerHat] == 2526
				&& c.playerEquipment[c.playerChest] == 2520
				&& c.playerEquipment[c.playerLegs] == 2522) {
			maxHit += (maxHit * 10 / 100);
		}
		maxHit += 1.05D + strBonus * strength * 0.00175D;
		maxHit += strength * 0.11D;
		if (c.playerEquipment[c.playerWeapon] == 4718
				&& c.playerEquipment[c.playerHat] == 4716
				&& c.playerEquipment[c.playerChest] == 4720
				&& c.playerEquipment[c.playerLegs] == 4722) {
			maxHit += (c.getPA().getLevelForXP(c.playerXP[3]) - c.playerLevel[3]) / 2;
		}
		if (c.specDamage > 1)
			maxHit = (int) (maxHit * c.specDamage);
		if (maxHit < 0)
			maxHit = 1;
		if (c.fullVoidMelee())
			maxHit = (int) (maxHit * 1.10);
		if (c.playerEquipment[c.playerAmulet] == 11128
				&& c.playerEquipment[c.playerWeapon] == 6528) {
			maxHit *= 1.20;
		}
		return (int) Math.floor(maxHit);
	}

	public int calculateMeleeDefence() {
		int defenceLevel = c.playerLevel[1];
		int i = c.playerBonus[bestMeleeDef()];
		if (c.prayerActive[0]) {
			defenceLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerDefence]) * 0.05;
		} else if (c.prayerActive[5]) {
			defenceLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerDefence]) * 0.1;
		} else if (c.prayerActive[13]) {
			defenceLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerDefence]) * 0.15;
		} else if (c.prayerActive[24]) {
			defenceLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerDefence]) * 0.2;
		} else if (c.prayerActive[25]) {
			defenceLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerDefence]) * 0.25;
		}
		return (int) (defenceLevel + (defenceLevel * 0.15) + (i + i * 0.05));
	}

	public int bestMeleeDef() {
		if (c.playerBonus[5] > c.playerBonus[6]
				&& c.playerBonus[5] > c.playerBonus[7])
			return 5;
		if (c.playerBonus[6] > c.playerBonus[5]
				&& c.playerBonus[6] > c.playerBonus[7])
			return 6;
		return c.playerBonus[7] <= c.playerBonus[5]
				|| c.playerBonus[7] <= c.playerBonus[6] ? 5 : 7;
	}

	/**
	 * Range
	 **/

	public int calculateRangeAttack() {
		int attackLevel = c.playerLevel[4];
		attackLevel *= c.specAccuracy;
		if (c.fullVoidRange())
			attackLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerRanged]) * 0.1;
		if (c.prayerActive[3])
			attackLevel *= 1.05;
		else if (c.prayerActive[11])
			attackLevel *= 1.10;
		else if (c.prayerActive[19])
			attackLevel *= 1.15;
		// dbow spec
		if (c.fullVoidRange() && c.specAccuracy > 1.15) {
			attackLevel *= 1.75;
		}
		return (int) (attackLevel + (c.playerBonus[4] * 1.95));
	}

	public int calculateRangeDefence() {
		int defenceLevel = c.playerLevel[1];
		if (c.prayerActive[0]) {
			defenceLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerDefence]) * 0.05;
		} else if (c.prayerActive[5]) {
			defenceLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerDefence]) * 0.1;
		} else if (c.prayerActive[13]) {
			defenceLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerDefence]) * 0.15;
		} else if (c.prayerActive[24]) {
			defenceLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerDefence]) * 0.2;
		} else if (c.prayerActive[25]) {
			defenceLevel += PlayerAssistant.getLevelForXP(c.playerXP[c.playerDefence]) * 0.25;
		}
		return defenceLevel + c.playerBonus[9] + (c.playerBonus[9] / 2);
	}

	public boolean usingBolts() {
		return c.playerEquipment[c.playerArrows] >= 9130
				&& c.playerEquipment[c.playerArrows] <= 9145
				|| c.playerEquipment[c.playerArrows] >= 9230
				&& c.playerEquipment[c.playerArrows] <= 9245;
	}

	public int rangeMaxHit() {
		int rangeLevel = c.playerLevel[4];
		double modifier = 1.0;
		double wtf = c.specDamage;
		int itemUsed = c.usingBow ? c.lastArrowUsed : c.lastWeaponUsed;
		if (c.prayerActive[3])
			modifier += 0.05;
		else if (c.prayerActive[11])
			modifier += 0.10;
		else if (c.prayerActive[19])
			modifier += 0.15;
		if (c.fullVoidRange())
			modifier += 0.20;
		double c = modifier * rangeLevel;
		int rangeStr = getRangeStr(itemUsed);
		double max = (c + 8) * (rangeStr + 64) / 640;
		if (wtf != 1)
			max *= wtf;
		if (max < 1)
			max = 1;
		return (int) max;
	}

	public int getRangeStr(int i) {
		if (i == 4214)
			return 70;
		switch (i) {
		// bronze to rune bolts
		case 877:
			return 10;
		case 9140:
			return 46;
		case 9141:
			return 64;
		case 9142:
		case 9241:
		case 9240:
			return 82;
		case 9143:
		case 9243:
		case 9242:
			return 100;
		case 9144:
		case 9244:
		case 9245:
			return 115;
			// bronze to dragon arrows
		case 882:
			return 7;
		case 884:
			return 10;
		case 886:
			return 16;
		case 888:
			return 22;
		case 890:
			return 31;
		case 892:
		case 4740:
			return 49;
		case 11212:
			return 60;
			// knifes
		case 864:
			return 3;
		case 863:
			return 4;
		case 865:
			return 7;
		case 866:
			return 10;
		case 867:
			return 14;
		case 868:
			return 24;
		}
		return 0;
	}

	/*
	 * public int rangeMaxHit() { int rangehit = 0; rangehit += c.playerLevel[4]
	 * / 7.5; int weapon = c.lastWeaponUsed; int Arrows = c.lastArrowUsed; if
	 * (weapon == 4223) {//Cbow 1/10 rangehit = 2; rangehit += c.playerLevel[4]
	 * / 7; } else if (weapon == 4222) {//Cbow 2/10 rangehit = 3; rangehit +=
	 * c.playerLevel[4] / 7; } else if (weapon == 4221) {//Cbow 3/10 rangehit =
	 * 3; rangehit += c.playerLevel[4] / 6.5; } else if (weapon == 4220) {//Cbow
	 * 4/10 rangehit = 4; rangehit += c.playerLevel[4] / 6.5; } else if (weapon
	 * == 4219) {//Cbow 5/10 rangehit = 4; rangehit += c.playerLevel[4] / 6; }
	 * else if (weapon == 4218) {//Cbow 6/10 rangehit = 5; rangehit +=
	 * c.playerLevel[4] / 6; } else if (weapon == 4217) {//Cbow 7/10 rangehit =
	 * 5; rangehit += c.playerLevel[4] / 5.5; } else if (weapon == 4216) {//Cbow
	 * 8/10 rangehit = 6; rangehit += c.playerLevel[4] / 5.5; } else if (weapon
	 * == 4215) {//Cbow 9/10 rangehit = 6; rangehit += c.playerLevel[4] / 5; }
	 * else if (weapon == 4214) {//Cbow Full rangehit = 7; rangehit +=
	 * c.playerLevel[4] / 5; } else if (weapon == 6522) { rangehit = 5; rangehit
	 * += c.playerLevel[4] / 6; } else if (weapon == 9029) {//dragon darts
	 * rangehit = 8; rangehit += c.playerLevel[4] / 10; } else if (weapon == 811
	 * || weapon == 868) {//rune darts rangehit = 2; rangehit +=
	 * c.playerLevel[4] / 8.5; } else if (weapon == 810 || weapon == 867)
	 * {//adamant darts rangehit = 2; rangehit += c.playerLevel[4] / 9; } else
	 * if (weapon == 809 || weapon == 866) {//mithril darts rangehit = 2;
	 * rangehit += c.playerLevel[4] / 9.5; } else if (weapon == 808 || weapon ==
	 * 865) {//Steel darts rangehit = 2; rangehit += c.playerLevel[4] / 10; }
	 * else if (weapon == 807 || weapon == 863) {//Iron darts rangehit = 2;
	 * rangehit += c.playerLevel[4] / 10.5; } else if (weapon == 806 || weapon
	 * == 864) {//Bronze darts rangehit = 1; rangehit += c.playerLevel[4] / 11;
	 * } else if (Arrows == 4740 && weapon == 4734) {//BoltRacks rangehit = 3;
	 * rangehit += c.playerLevel[4] / 6; } else if (Arrows == 11212) {//dragon
	 * arrows rangehit = 4; rangehit += c.playerLevel[4] / 5.5; } else if
	 * (Arrows == 892) {//rune arrows rangehit = 3; rangehit += c.playerLevel[4]
	 * / 6; } else if (Arrows == 890) {//adamant arrows rangehit = 2; rangehit
	 * += c.playerLevel[4] / 7; } else if (Arrows == 888) {//mithril arrows
	 * rangehit = 2; rangehit += c.playerLevel[4] / 7.5; } else if (Arrows ==
	 * 886) {//steel arrows rangehit = 2; rangehit += c.playerLevel[4] / 8; }
	 * else if (Arrows == 884) {//Iron arrows rangehit = 2; rangehit +=
	 * c.playerLevel[4] / 9; } else if (Arrows == 882) {//Bronze arrows rangehit
	 * = 1; rangehit += c.playerLevel[4] / 9.5; } else if (Arrows == 9244) {
	 * rangehit = 8; rangehit += c.playerLevel[4] / 3; } else if (Arrows ==
	 * 9139) { rangehit = 12; rangehit += c.playerLevel[4] / 4; } else if
	 * (Arrows == 9140) { rangehit = 2; rangehit += c.playerLevel[4] / 7; } else
	 * if (Arrows == 9141) { rangehit = 3; rangehit += c.playerLevel[4] / 6; }
	 * else if (Arrows == 9142) { rangehit = 4; rangehit += c.playerLevel[4] /
	 * 6; } else if (Arrows == 9143) { rangehit = 7; rangehit +=
	 * c.playerLevel[4] / 5; } else if (Arrows == 9144) { rangehit = 7; rangehit
	 * += c.playerLevel[4] / 4.5; } int bonus = 0; bonus -= rangehit / 10;
	 * rangehit += bonus; if (c.specDamage != 1) rangehit *= c.specDamage; if
	 * (rangehit == 0) rangehit++; if (c.fullVoidRange()) { rangehit *= 1.10; }
	 * if (c.prayerActive[3]) rangehit *= 1.05; else if (c.prayerActive[11])
	 * rangehit *= 1.10; else if (c.prayerActive[19]) rangehit *= 1.15; return
	 * rangehit; }
	 */

	public boolean properBolts() {
		return c.playerEquipment[c.playerArrows] >= 9140
				&& c.playerEquipment[c.playerArrows] <= 9144
				|| c.playerEquipment[c.playerArrows] >= 9240
				&& c.playerEquipment[c.playerArrows] <= 9244;
	}

	public int correctBowAndArrows() {
		if (usingBolts())
			return -1;
		switch (c.playerEquipment[c.playerWeapon]) {

		case 839:
		case 841:
			return 882;

		case 843:
		case 845:
			return 884;

		case 847:
		case 849:
			return 886;

		case 851:
		case 853:
			return 888;

		case 855:
		case 857:
			return 890;

		case 859:
		case 861:
			return 892;

		case 4734:
		case 4935:
		case 4936:
		case 4937:
			return 4740;

		case 11235:
			return 11212;
		}
		return -1;
	}

	public int getRangeStartGFX() {
		switch (c.rangeItemUsed) {

		case 863:
			return 220;
		case 864:
			return 219;
		case 865:
			return 221;
		case 866: // knives
			return 223;
		case 867:
			return 224;
		case 868:
			return 225;
		case 869:
			return 222;

		case 806:
			return 232;
		case 807:
			return 233;
		case 808:
			return 234;
		case 809: // darts
			return 235;
		case 810:
			return 236;
		case 811:
			return 237;

		case 825:
			return 206;
		case 826:
			return 207;
		case 827: // javelin
			return 208;
		case 828:
			return 209;
		case 829:
			return 210;
		case 830:
			return 211;

		case 800:
			return 42;
		case 801:
			return 43;
		case 802:
			return 44; // axes
		case 803:
			return 45;
		case 804:
			return 46;
		case 805:
			return 48;

		case 882:
			return 19;

		case 884:
			return 18;

		case 886:
			return 20;

		case 888:
			return 21;

		case 890:
			return 22;

		case 892:
			return 24;

		case 11212:
			return 26;

		case 4212:
		case 4214:
		case 4215:
		case 4216:
		case 4217:
		case 4218:
		case 4219:
		case 4220:
		case 4221:
		case 4222:
		case 4223:
			return 250;

		}
		return -1;
	}

	public int getRangeProjectileGFX() {
		if (c.dbowSpec) {
			return 672;
		}
		if (c.bowSpecShot > 0) {
			switch (c.rangeItemUsed) {
			default:
				return 249;
			}
		}
		if (c.playerEquipment[c.playerWeapon] == 9185)
			return 27;
		switch (c.rangeItemUsed) {

		case 863:
			return 213;
		case 864:
			return 212;
		case 865:
			return 214;
		case 866: // knives
			return 216;
		case 867:
			return 217;
		case 868:
			return 218;
		case 869:
			return 215;

		case 806:
			return 226;
		case 807:
			return 227;
		case 808:
			return 228;
		case 809: // darts
			return 229;
		case 810:
			return 230;
		case 811:
			return 231;

		case 825:
			return 200;
		case 826:
			return 201;
		case 827: // javelin
			return 202;
		case 828:
			return 203;
		case 829:
			return 204;
		case 830:
			return 205;

		case 6522: // Toktz-xil-ul
			return 442;

		case 800:
			return 36;
		case 801:
			return 35;
		case 802:
			return 37; // axes
		case 803:
			return 38;
		case 804:
			return 39;
		case 805:
			return 40;

		case 882:
			return 10;

		case 884:
			return 9;

		case 886:
			return 11;

		case 888:
			return 12;

		case 890:
			return 13;

		case 892:
			return 15;

		case 11212:
			return 17;

		case 4740: // bolt rack
			return 27;

		case 4212:
		case 4214:
		case 4215:
		case 4216:
		case 4217:
		case 4218:
		case 4219:
		case 4220:
		case 4221:
		case 4222:
		case 4223:
			return 249;

		}
		return -1;
	}

	public int getProjectileSpeed() {
		if (c.dbowSpec)
			return 100;
		return 70;
	}

	public int getProjectileShowDelay() {
		switch (c.playerEquipment[c.playerWeapon]) {
		case 863:
		case 864:
		case 865:
		case 866: // knives
		case 867:
		case 868:
		case 869:

		case 806:
		case 807:
		case 808:
		case 809: // darts
		case 810:
		case 811:

		case 825:
		case 826:
		case 827: // javelin
		case 828:
		case 829:
		case 830:

		case 800:
		case 801:
		case 802:
		case 803: // axes
		case 804:
		case 805:

		case 4734:
		case 9185:
		case 4935:
		case 4936:
		case 4937:
			return 15;

		default:
			return 5;
		}
	}

	/**
	 * MAGIC
	 **/

	public boolean canHitMage(Client o) {
		return Misc.random(mageAtk()) > Misc.random(o.getCombat().mageDef());
	}

	public int mageAtk() {
		int attackLevel = c.playerLevel[6];
		if (c.fullVoidMage())
			attackLevel = (int) (attackLevel + PlayerAssistant.getLevelForXP(c.playerXP[6]) * 0.20000000000000001D);
		if (c.prayerActive[4])
			attackLevel = (int) (attackLevel * 1.05D);
		else if (c.prayerActive[12])
			attackLevel = (int) (attackLevel * 1.1000000000000001D);
		else if (c.prayerActive[20])
			attackLevel = (int) (attackLevel * 1.1499999999999999D);
		return (int) (attackLevel + c.playerBonus[3] * 2.25D);
	}

	public int mageDef() {
		int defenceLevel = c.playerLevel[1] / 2 + c.playerLevel[6] / 2;
		if (c.prayerActive[4])
			defenceLevel = (int) (defenceLevel + PlayerAssistant
					.getLevelForXP(c.playerXP[c.playerDefence]) * 0.050000000000000003D);
		else if (c.prayerActive[12])
			defenceLevel = (int) (defenceLevel + PlayerAssistant
					.getLevelForXP(c.playerXP[c.playerDefence]) * 0.10000000000000001D);
		else if (c.prayerActive[20])
			defenceLevel = (int) (defenceLevel + PlayerAssistant
					.getLevelForXP(c.playerXP[c.playerDefence]) * 0.14999999999999999D);
		return defenceLevel + c.playerBonus[8] + c.playerBonus[8] / 4;
	}

	public boolean wearingStaff(int runeId) {
		int wep = c.playerEquipment[c.playerWeapon];
		switch (runeId) {
		case 554:
			return wep == 1387;
		case 555:
			return wep == 1383;
		case 556:
			return wep == 1381;
		case 557:
			return wep == 1385;
		}
		return false;
	}

	public boolean checkMagicReqs(int spell) {
		if (c.usingMagic && Settings.RUNES_REQUIRED) { // check for runes
			if ((!c.getItems().playerHasItem(c.MAGIC_SPELLS[spell][8],
					c.MAGIC_SPELLS[spell][9]) && !wearingStaff(c.MAGIC_SPELLS[spell][8]))
					|| (!c.getItems().playerHasItem(c.MAGIC_SPELLS[spell][10],
							c.MAGIC_SPELLS[spell][11]) && !wearingStaff(c.MAGIC_SPELLS[spell][10]))
					|| (!c.getItems().playerHasItem(c.MAGIC_SPELLS[spell][12],
							c.MAGIC_SPELLS[spell][13]) && !wearingStaff(c.MAGIC_SPELLS[spell][12]))
					|| (!c.getItems().playerHasItem(c.MAGIC_SPELLS[spell][14],
							c.MAGIC_SPELLS[spell][15]) && !wearingStaff(c.MAGIC_SPELLS[spell][14]))) {
				c.sendMessage("You don't have the required runes to cast this spell.");
				return false;
			}
		}

		if (c.usingMagic && c.playerIndex > 0) {
			if (PlayerHandler.players[c.playerIndex] != null) {
				for (int r = 0; r < c.REDUCE_SPELLS.length; r++) {
					if (PlayerHandler.players[c.playerIndex].REDUCE_SPELLS[r] == c.MAGIC_SPELLS[spell][0]) {
						c.reduceSpellId = r;
						if ((System.currentTimeMillis() - PlayerHandler.players[c.playerIndex].reduceSpellDelay[c.reduceSpellId]) > PlayerHandler.players[c.playerIndex].REDUCE_SPELL_TIME[c.reduceSpellId]) {
							PlayerHandler.players[c.playerIndex].canUseReducingSpell[c.reduceSpellId] = true;
						} else {
							PlayerHandler.players[c.playerIndex].canUseReducingSpell[c.reduceSpellId] = false;
						}
						break;
					}
				}
				if (!PlayerHandler.players[c.playerIndex].canUseReducingSpell[c.reduceSpellId]) {
					c.sendMessage("That player is currently immune to this spell.");
					c.usingMagic = false;
					c.stopMovement();
					resetPlayerAttack();
					return false;
				}
			}
		}

		int staffRequired = getStaffNeeded();
		if (c.usingMagic && staffRequired > 0 && Settings.RUNES_REQUIRED) { // staff
			// required
			if (c.playerEquipment[c.playerWeapon] != staffRequired) {
				c.sendMessage("You need a "
						+ ItemHandler.getItemName(staffRequired).toLowerCase()
						+ " to cast this spell.");
				return false;
			}
		}

		if (c.usingMagic && Settings.MAGIC_LEVEL_REQUIRED) { // check magic level
			if (c.playerLevel[6] < c.MAGIC_SPELLS[spell][1]) {
				c.sendMessage("You need to have a magic level of "
						+ c.MAGIC_SPELLS[spell][1] + " to cast this spell.");
				return false;
			}
		}
		if (c.usingMagic && Settings.RUNES_REQUIRED) {
			if (c.MAGIC_SPELLS[spell][8] > 0) { // deleting runes
				if (!wearingStaff(c.MAGIC_SPELLS[spell][8]))
					c.getItems().deleteItem(c.MAGIC_SPELLS[spell][8],
							c.getItems().getItemSlot(c.MAGIC_SPELLS[spell][8]),
							c.MAGIC_SPELLS[spell][9]);
			}
			if (c.MAGIC_SPELLS[spell][10] > 0) {
				if (!wearingStaff(c.MAGIC_SPELLS[spell][10]))
					c.getItems()
					.deleteItem(
							c.MAGIC_SPELLS[spell][10],
							c.getItems().getItemSlot(
									c.MAGIC_SPELLS[spell][10]),
							c.MAGIC_SPELLS[spell][11]);
			}
			if (c.MAGIC_SPELLS[spell][12] > 0) {
				if (!wearingStaff(c.MAGIC_SPELLS[spell][12]))
					c.getItems()
					.deleteItem(
							c.MAGIC_SPELLS[spell][12],
							c.getItems().getItemSlot(
									c.MAGIC_SPELLS[spell][12]),
							c.MAGIC_SPELLS[spell][13]);
			}
			if (c.MAGIC_SPELLS[spell][14] > 0) {
				if (!wearingStaff(c.MAGIC_SPELLS[spell][14]))
					c.getItems()
					.deleteItem(
							c.MAGIC_SPELLS[spell][14],
							c.getItems().getItemSlot(
									c.MAGIC_SPELLS[spell][14]),
							c.MAGIC_SPELLS[spell][15]);
			}
		}
		return true;
	}

	public int getFreezeTime() {
		switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
		case 1572:
		case 12861: // ice rush
			return 10;

		case 1582:
		case 12881: // ice burst
			return 17;

		case 1592:
		case 12871: // ice blitz
			return 25;

		case 12891: // ice barrage
			return 33;

		default:
			return 0;
		}
	}

	public void freezePlayer(int i) {
	}

	public int getStartHeight() {
		switch (c.MAGIC_SPELLS[c.spellId][0]) {
		case 1562: // stun
			return 25;

		case 12939:// smoke rush
			return 35;

		case 12987: // shadow rush
			return 38;

		case 12861: // ice rush
			return 15;

		case 12951: // smoke blitz
			return 38;

		case 12999: // shadow blitz
			return 25;

		case 12911: // blood blitz
			return 25;

		default:
			return 43;
		}
	}

	public int getEndHeight() {
		switch (c.MAGIC_SPELLS[c.spellId][0]) {
		case 1562: // stun
			return 10;

		case 12939: // smoke rush
			return 20;

		case 12987: // shadow rush
			return 28;

		case 12861: // ice rush
			return 10;

		case 12951: // smoke blitz
			return 28;

		case 12999: // shadow blitz
			return 15;

		case 12911:
			return 10;// blood blitz

		default:
			return 31;
		}
	}

	public int getStartDelay() {
		switch (c.playerEquipment[c.playerWeapon]) {
		case 15241:
			return 0;
		}
		switch (c.MAGIC_SPELLS[c.spellId][0]) {
		case 1539:
			return 60;
		default:
			return 53;
		}
	}

	public int getStaffNeeded() {
		switch (c.MAGIC_SPELLS[c.spellId][0]) {
		case 1539:
			return 1409;

		case 12037:
			return 4170;

		case 1190:
			return 2415;

		case 1191:
			return 2416;

		case 1192:
			return 2417;

		default:
			return 0;
		}
	}

	public boolean godSpells() {
		switch (c.MAGIC_SPELLS[c.spellId][0]) {
		case 1190:
			return true;

		case 1191:
			return true;

		case 1192:
			return true;

		default:
			return false;
		}
	}

	public int getEndGfxHeight() {
		switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
		case 12987:
		case 12901:
		case 12861:
		case 12445:
		case 1192:
		case 13011:
		case 12919:
		case 12881:
		case 12999:
		case 12911:
		case 12871:
		case 13023:
		case 12929:
		case 12891:
			return 0;

		default:
			return 100;
		}
	}

	public int getStartGfxHeight() {
		switch (c.MAGIC_SPELLS[c.spellId][0]) {
		case 12871:
		case 12891:
			return 0;

		default:
			return 100;
		}
	}

	public void handleDfs() {
	}

	public void handleDfsNPC() {
	}

	public int getBonusAttack(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 2883:
			return Misc.random(50) + 30;
		case 2026:
		case 2027:
		case 2029:
		case 2030:
			return Misc.random(50) + 30;
		}
		return 0;
	}

	public void handleGmaulPlayer() {
		if (c.playerIndex > 0) {
			Client o = (Client) PlayerHandler.players[c.playerIndex];
			if (c.goodDistance(c.getX(), c.getY(), o.getX(), o.getY(),
					getRequiredDistance())) {
				if (checkReqs()) {
					if (checkSpecAmount(4153)) {
						boolean hit = Misc.random(calculateMeleeAttack()) > Misc
								.random(o.getCombat().calculateMeleeDefence());
						int damage = 0;
						if (hit)
							damage = Misc.random(calculateMeleeMaxHit());
						if (o.prayerActive[18]
								|| o.curseActive[9]
										&& System.currentTimeMillis()
										- o.protMeleeDelay > 1500)
							damage *= .6;
						o.handleHitMask(damage);
						c.startAnimation(1667);
						c.gfx100(340);
						o.dealDamage(damage);
					}
				}
			}
		}
	}

	public void applyRecoil(int damage, int i) {
		if (damage > 0
				&& PlayerHandler.players[i].playerEquipment[c.playerRing] == 2550) {
			int recDamage = damage / 10 + 1;
			if (!c.getHitUpdateRequired()) {
				c.setHitDiff(recDamage);
				removeRecoil(c);
				c.recoilHits = recDamage;
				c.setHitUpdateRequired(true);
			} else if (!c.getHitUpdateRequired2()) {
				c.setHitDiff2(recDamage);
				c.setHitUpdateRequired2(true);
			}
			c.dealDamage(recDamage);
			c.updateRequired = true;
		}
	}

	public void removeRecoil(Client c) {
		if (c.recoilHits >= 50) {
			c.getItems().removeItem(2550, c.playerRing);
			c.getItems().deleteItem(2550, c.getItems().getItemSlot(2550), 1);
			c.sendMessage("Your ring of recoil shaters!");
			c.recoilHits = 0;
		} else {
			c.recoilHits++;
		}
	}

	public boolean armaNpc(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 2558:
		case 2559:
		case 2560:
		case 2561:
			return true;
		}
		return false;
	}

	public static int finalMagicDamage(Client c) {
		double damage = c.MAGIC_SPELLS[c.oldSpellId][6] * 10;
		double damageMultiplier = 1;
		int level = c.playerLevel[c.playerMagic];
		if (level > PlayerAssistant.getLevelForXP(c.playerXP[6])
				&& PlayerAssistant.getLevelForXP(c.playerXP[6]) >= 95)
			damageMultiplier += .03 * ((level > 104 ? 104 : level) - 99);
		else
			damageMultiplier = 1;
		switch (c.playerEquipment[c.playerWeapon]) {
		case 18371: // Gravite Staff
			damageMultiplier += .05;
			break;
		case 4675: // Ancient Staff
		case 4710: // Ahrim's Staff
		case 4862: // Ahrim's Staff
		case 4864: // Ahrim's Staff
		case 4865: // Ahrim's Staff
		case 6914: // Master Wand
		case 8841: // Void Knight Mace
		case 13867: // Zuriel's Staff
		case 13869: // Zuriel's Staff (Deg)
			damageMultiplier += .10;
			break;
		case 15486: // Staff of Light
			damageMultiplier += .15;
			break;
		case 18355: // Chaotic Staff
			damageMultiplier += .20;
			break;
		}
		switch (c.playerEquipment[c.playerAmulet]) {
		case 18333: // Arcane Pulse
			damageMultiplier += .05;
			break;
		case 18334:// Arcane Blast
			damageMultiplier += .10;
			break;
		case 18335:// Arcane Stream
			damageMultiplier += .15;
			break;
		}
		damage *= damageMultiplier;
		return (int) damage;
	}

	/**
	 * @author Chris
	 * @param defence
	 */

	public boolean calculateBlockedHit(int defence) {
		if(defence > 450 && Misc.random(5) == 1)
			return true;
		if(defence > 400 && Misc.random(5) == 1)
			return true;
		if(defence > 350 && Misc.random(6) == 1)
			return true; 
		if(defence > 300 && Misc.random(6) == 1) 
			return true;
		if(Misc.random(6) == 1 && defence > 150) 
			return true;
		if(defence > 10 && Misc.random(7) == 1)
			return true;
		return false;
	}

	/**
	 * @author Chris
	 * @param i(Returns player index for meleeDefence())
	 * @param damage 
	 * @return
	 */
	public int calculateDefenceDamageReduction(int i, int damage) {
		Client o = (Client) PlayerHandler.players[i];
		int defence = o.getCombat().calculateMeleeDefence();
		if(calculateBlockedHit(defence))
			return 0;
		if(defence > 450) 
			return damage *= .635;
		if(defence > 400)
			return damage *= .655;
		if(defence > 350) 
			return damage *= .715;
		if(defence > 300)
			return damage *= .775;
		if(defence > 250)
			return damage *= .835;
		if(defence > 200)
			return damage *= .85;
		if(defence > 150)
			return damage *= .9125;
		if(defence > 100)
			return damage *= .975;
		if(defence > 10)
			return damage *= .99;
		return damage;
	}

}