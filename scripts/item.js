// Declare imports as such. Don't forget to append 'Class' at the end of the name to avoid conflicts.
// var FooClass = Java.type('path.to.class.Foo');
var ScriptLoader = Java.type('nl.osrs.script.ScriptLoader');

/**
 * Handles all item interactions.
 */

var click = function(c, item, itemSlot) {
	if (!checkRequest(c))
		return false;

	switch (item.getId()) {

		default:
			c.sendMessage("Nothing interesting happens...");
			break;

	}

	return true;
}

var useItem = function(c, usedItem, usedItemSlot, usedOnItem, usedOnItemSlot) {
	if (!checkRequest(c))
		return false;

	switch (usedItem.getId()) {

		case 1755:
			if (ScriptLoader.executeScript("skills.crafting@cut", c, usedItem, usedItemSlot, usedOnItem, usedOnItemSlot) == true)
				break;

		default:
			c.sendMessage("Nothing interesting happens...");
			break;

	}

	return true;
}

var useItemOnGroundItem = function(c, item, itemSlot, groundItem, groundItemX, groundItemY) {
	if (!checkRequest(c))
		return false;

	if (!c.goodDistance(groundItemX, groundItemY, c.absX, c.absY, 1))
		return false;

	switch (item.getId()) {

		default:
			c.sendMessage("Nothing interesting happens...");
			break;

	}

	return true;
}

var checkRequest = function(c) {
	if (c != undefined && c != null)
		return true;

	print("ItemScript: Invalid client");
	return false;
}
