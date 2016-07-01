// Declare imports as such. Don't forget to append 'Class' at the end of the name to avoid conflicts.
var SmithingClass = Java.type('nl.osrs.model.player.skills.smithing.Smithing');
var ScriptLoader = Java.type('nl.osrs.script.ScriptLoader');

/**
 * Handles all object interactions.
 */

function click(c) {
	if (!checkRequest(c))
		return false;

	if (!isInRange(c)) {
		return false;
	}

	switch (c.objectId) {

		case 14367:
				c.getPA().openUpBank();
				break;

		case 26814:
			ScriptLoader.executeScript("skills.smelting@openSmeltingInterface", c);
			break;

		case 2090:
		case 2091:
		case 2092:
		case 2093:
		case 2094:
		case 2095:
		case 2096:
		case 2097:
		case 2098:
		case 2099:
		case 2100:
		case 2101:
		case 2102:
		case 2103:
		case 2104:
		case 2105:
		case 2106:
		case 2107:
			ScriptLoader.executeScript("skills.mining@mine", c)
			break;
		
		default:
			c.sendMessage("Nothing interesting happens...");
			break;

	}

	return true;
}

function useItem(c, item) {
	if (!checkRequest(c))
		return false;

	if (!isInRange(c)) {
		return false;
	}

	switch (c.objectId) {

		case 2783:
			// SmithingClass.openSmithingInterface(c, item.getId());
			ScriptLoader.executeScript("skills.smithing@openSmithingInterface", c, item.getId());
			break;

		default:
			c.sendMessage("Nothing interesting happens...");
			break;

	}

	return true;
}

function checkRequest(c) {
	if (c != undefined && c != null)
		return true;

	print("ObjectScript: Invalid client");
	return false;
}

function isInRange(c) {
	var xDistance;
	var yDistance;

	switch (c.objectId) {

		default:
		xDistance = 1;
		yDistance = 1;
		break;

	}

	return c.isInRangeOfClickedObject(xDistance, yDistance);
}
