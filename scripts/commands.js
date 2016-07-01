// Declare imports as such. Don't forget to append 'Class' at the end of the name to avoid conflicts.
var ItemLoaderClass = Java.type('nl.osrs.model.item.ItemLoader');
var ItemHandlerClass = Java.type('nl.osrs.model.item.ItemHandler');
var GameEngineClass = Java.type('nl.osrs.GameEngine');
var MiscClass = Java.type('nl.osrs.util.Misc');

/**
 * Processes all commands.
 */

var spawn = function(client, args) {
	if (!checkRequest(client))
		return false;

	var npcId = 0;

	if (args.length > 0)
		npcId = args[0];

	GameEngineClass.npcHandler.spawnNpc(client, npcId, client.absX, client.absY, client.heightLevel, 1, 50, 0, 1, 1, false, false);
}

var npc = spawn;

var item = function(client, args) {
	if (!checkRequest(client))
		return false;

	var itemId = 0;
	var amount = 1;
	
	if(args.length > 0)
		itemId = args[0];
	
	if(args.length > 1)
		amount = args[1];

	client.getItems().addItem(itemId, amount);
}

var pickup = item;

var tele = function(client, args) {
	if (!checkRequest(client))
		return false;

	var x = 0;
	var y = 0;
	var height = 0;
	
	if(args.length > 0)
		x = args[0];
	
	if(args.length > 1)
		y = args[1];
	
	if(args.length > 2)
		height = args[2];

	client.sendMessage("You teleport to @or2@" + x + "@bla@, @or2@" + y + "@bla@, @or2@" + height);
	client.getPA().spellTeleport(x, y, height);
}

var home = function(client, args) {
	if (!checkRequest(client))
		return false;

	client.sendMessage("@or2@You teleport home.");
	client.getPA().spellTeleport(2658 + MiscClass.random(1), 2659 + MiscClass.random(2), 0);
}

var mypos = function(client, args) {
	if (!checkRequest(client))
		return false;

	GameEngineClass.clipboard.setClipboardContents(client.absX + ", " + client.absY);
	client.sendMessage("Player coordinates: @or2@" + client.absX + "@bla@, @or2@" + client.absY);
}

var coords = mypos;

var bank = function(client, args) {
	if (!checkRequest(client))
		return false;

	client.sendMessage("@or2@You open your bank.");
	client.getPA().openUpBank();
}

var object = function(client, args) {
	if (!checkRequest(client))
		return false;

	if (args == undefined)
		args = [""];

	switch (args[0]) {

		case "help":
			client.sendMessage("Usage: @or2@::object <option> <arguments>");
			client.sendMessage("    where option is one of the following:");
			client.sendMessage("    - @or2@help@bla@: Displays command help.");
			client.sendMessage("    - @or2@spawn <objectId>@bla@: Spawns an object.");
			break;

		default:
			client.sendMessage("@bla@Invalid syntax. Use @or2@::object help@bla@ for more information.");
			break;

	}
}

var anim = function(client, args) {
	if (!checkRequest(client))
		return false;

	if (args == undefined)
		args = [""];

	if (args[0] == "") {
		client.sendMessage("@bla@Invalid syntax. Use as @or2@::anim <animationId>@bla@.");
		return false;
	}

	client.startAnimation(args[0]);
}

var emote = anim;

function find(client, args) {
	if (!checkRequest(client))
		return false;

	if (args == undefined)
		args = [""];

	switch (args[0]) {

		case "item":
			if (args.length < 2)
				client.sendMessage("@bla@Invalid syntax. Use @or2@::find help@bla@ for more information.");
			else {
				var filter = "";

				for (var i = 1; i < args.length; i++) {
					filter += args[i].toLowerCase();

					if (i < args.length - 1)
						filter += " ";
				}

				var items = ItemLoaderClass.getItems(filter);

				var message = "Searching for: @blu@\"" + filter + "\"";

				var maxItems = 7;

				if (items.size() > maxItems)
					message += "@bla@, showing first @blu@" + maxItems + "@bla@ results..."

				client.sendMessage(message);

				var iterator = items.iterator();
				while (iterator.hasNext() && maxItems > 0) {
					maxItems--;
					var item = iterator.next();
					client.sendMessage("@blu@" + item.getId() + "@bla@: " + item.getName());
				}
			}
			break;

		case "help":
			client.sendMessage("Usage: @or2@::find <option> <filter>");
			client.sendMessage("    where option is one of the following:");
			client.sendMessage("    - @or2@help@bla@: Displays command help.");
			client.sendMessage("    - @or2@item <name>@bla@: Searches for an item and prints the results.");
			break;

		default:
			client.sendMessage("@bla@Invalid syntax. Use @or2@::find help@bla@ for more information.");
			break;

	}
}

var reload = function(client, args) {
	if (!checkRequest(client))
		return false;

	if (args == undefined)
		args = [""];

	switch (args[0]) {

		case "items":
			ItemLoaderClass.loadItems();
			ItemHandlerClass.load();
			client.sendMessage("@or2@Reloaded all items.");
			break;

		case "help":
			client.sendMessage("Usage: @or2@::reload <dataset>");
			client.sendMessage("    where dataset is one of the following:");
			client.sendMessage("    - @or2@help@bla@: Displays command help.");
			client.sendMessage("    - @or2@items@bla@: Reloads all items.");
			break;

		default:
			client.sendMessage("@bla@Invalid syntax. Use @or2@::reload help@bla@ for more information.");
			break;

	}
}

function master(client) {
	if (!checkRequest(client))
		return false;

	for (var i = 0; i < client.playerLevel.length; i++)
		client.getPA().addSkillXP(40959, i);

	client.sendMessage("@or2@You are now level 120 in all skills.");
}

var empty = function(client) {
	if (!checkRequest(client))
		return false;

	client.sendMessage("@or2@You empty your inventory.");
	client.getItems().removeAllItems();
}

var checkRequest = function(c) {
	if (c != undefined && c != null)
		return true;

	print("CommandScript: Invalid client");
	return false;
}
