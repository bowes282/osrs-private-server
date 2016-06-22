// Declare imports as such. Don't forget to add 'Class' at the end to avoid conflicts.
var MiningClass = Java.type('nl.osrs.model.player.skills.mining.Mining');

var click = function(c) {
	if (c == undefined || c == null) {
		print("Invalid client");
		return;
	}

	if (!c.isInRangeOfClickedObject())
		return;

	switch (c.objectId) {

		case 26814:
		c.getPA().openSmeltingInterface();
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
		case 2010:
		case 2011:
		case 2012:
		case 2013:
		case 2014:
		case 2015:
		case 2016:
		case 2017:
		MiningClass.startMining(c);
		break;
		
		default:
		c.sendMessage("Nothing interesting happens...");
		break;

	}

	return true;
}