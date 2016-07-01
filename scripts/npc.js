// Declare imports as such. Don't forget to append 'Class' at the end of the name to avoid conflicts.
var SailorClass = Java.type('nl.osrs.model.npc.interaction.Sailor');

/**
 * Handles all npc interactions.
 */

var click = function(c, npc) {
	if (!checkRequest(c, npc))
		return false;

	if (!isInRange(c, npc)) {
		return false;
	}

	switch (npc.npcType) {

		case 532:
			if (c.clickNpcType == 1)
				c.sendMessage("He doesn't seem like he would want to talk...");
			else if (c.clickNpcType == 2)
				c.getShops().openShop(1);
			break;

		case 1304:
			SailorClass.process(c);
			break;

		default:
			c.sendMessage("Nothing interesting happens...");
			break;

	}

	return true;
}

var useItem = function(c, npc, item, itemSlot) {
	if (!checkRequest(c, npc))
		return false;

	if (isInRange(c, npc) == false) {
		return false;
	}

	switch (npc.npcType) {

		default:
			c.sendMessage("Nothing interesting happens...");
			break;

	}

	return true;
}

var checkRequest = function(c, npc) {
	if (c != undefined && c != null && npc != undefined && npc != null)
		return true;

	print("NpcScript: Invalid client or npc");
	return false;
}

var isInRange = function(c, npc) {
	var xDistance;
	var yDistance;

	switch (npc.npcType) {

		default:
			xDistance = 1;
			yDistance = 1;
			break;

	}

	return Math.abs(c.absX - npc.absX) <= xDistance && Math.abs(c.absY - npc.absY) <= yDistance;
}
