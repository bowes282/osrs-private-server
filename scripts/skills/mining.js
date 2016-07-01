var ItemHandler = Java.type('nl.osrs.model.item.ItemHandler');
var Task = Java.type('nl.osrs.task.Task');

function mine(client) {
	var objectId = client.objectId;
	var objectX = client.objectX;
	var objectY = client.objectY;
	var ore;
	var pickaxe;

	if (!canMine(client, (ore = findMineral(objectId)), (pickaxe = bestPickaxe(client))))
		return false;

	if (client.isMining())
		return false;

	client.getTaskScheduler().stopTasks();

	client.setMining(true);

	client.repeatAnimation(pickaxe.animation, 16, objectX, objectY);
		
	client.sendMessage("You swing your pickaxe at the rock...");

	client.getTaskScheduler().schedule(new Task({
		immediate: false,
		countdown: getTickFrequency(client, ore, pickaxe),
		running: true,

		execute: function() {
			if (!canMine(client, ore, pickaxe) || !client.isMining()) {
				client.getTaskScheduler().stopTasks();
				return;
			}

			if (Math.random() * ore.hardness >= 1) {
				this.countdown--;
				return;
			}

			client.sendMessage("... and manage to mine some " + ItemHandler.getItemName(ore.item).toLowerCase() + ".");
			client.getItems().addItem(ore.item, 1);
			client.getPA().addSkillXP(ore.exp, client.playerMining);

			if (!canMine(client, ore, pickaxe) || !client.isMining())
				client.getTaskScheduler().stopTasks();
		},

		tick: function() {
			if (this.running && this.countdown == 0) {
				this.countdown = getTickFrequency(client, ore, pickaxe);
				this.execute();
			}

			this.countdown--;

			return this.running;
		},

		onStop: function() {
			client.setMining(false);
		},
	}));
}

function getTickFrequency(client, ore, pickaxe) {
	var mining = client.playerLevel[client.playerMining];
	var oreHardness = ore.hardness;
	var pickaxeStrength = pickaxe.strength;

	var ticksUntilNextOre = (oreHardness - (mining / 60)) * 2;

	var speed = 1 + (pickaxeStrength / 20);

	ticksUntilNextOre /= speed

	if (ticksUntilNextOre < 2)
		ticksUntilNextOre = 2;

	ticksUntilNextOre = new java.lang.Integer(ticksUntilNextOre);

	return ticksUntilNextOre
}

function canMine(client, ore, pickaxe) {
	if (!hasInventorySpace(client))
		return false;

	if (ore == null)
		return false;

	if (!usablePickaxe(client, ore, pickaxe))
		return false;
	
	return true;
}

function hasInventorySpace(client) {
	if (client.getItems().freeSlots() > 0)
		return true;

	client.sendMessage("You have no space left in your inventory.");
	return false;
}

function findMineral(objectId) {
	for (var i = 0; i < minableObjects.length; i++) {
		var minableObject = minableObjects[i];

		if (minableObject[0] == objectId)
			return minableObject[1];
	}

	print("Unknown minable object: " + objectId);
	return null;
}

function bestPickaxe(client) {
	var bestPickaxe = null;

	for (var i = 0; i < pickaxes.length; i++) {
		var pickaxe = pickaxes[i];

		if (client.playerLevel[client.playerMining] < pickaxe.level) {
			if (bestPickaxe != null)
				continue;

			if (!hasPickaxe(client, pickaxe))
				continue;

			client.sendMessage("You need " + pickaxe.level + " mining to use a(n) " + ItemHandler.getItemName(pickaxe.item).toLowerCase() + ".");
			break;
		}

		if (hasPickaxe(client, pickaxe))
			bestPickaxe = pickaxe;
	}

	return bestPickaxe;
}

function hasPickaxe(client, pickaxe) {
	return client.getItems().playerHasItem(pickaxe.item, 1) || client.playerEquipment[client.playerWeapon] == pickaxe.item;
}

function usablePickaxe(client, ore, pickaxe) {
	var usable = true;

	if (pickaxe == null) {
		client.sendMessage("You don't have a pickaxe you can use.");
		return false;
	}

	if (pickaxe.pickaxeLevel < ore.pickaxe.pickaxeLevel) {
		client.sendMessage("You need at least a(n) " + ItemHandler.getItemName(ore.pickaxe.item).toLowerCase() + " to mine this ore.");
		usable = false;
	}

	return usable;
}

var pickaxes = [
	{ pickaxeLevel : 0, level: 1,  item: 1265, animation: 6747, strength: 2 },
	{ pickaxeLevel : 1, level: 1,  item: 1267, animation: 6748, strength: 4 },
	{ pickaxeLevel : 2, level: 11, item: 1269, animation: 6749, strength: 6 },
	{ pickaxeLevel : 3, level: 21, item: 1273, animation: 6751, strength: 8 },
	{ pickaxeLevel : 4, level: 31, item: 1271, animation: 6750, strength: 12 },
	{ pickaxeLevel : 5, level: 41, item: 1275, animation: 6746, strength: 16 },
];

var minerals = [
	{ item: 436, level: 1,  exp: 7.5,  pickaxe: pickaxes[0], hardness: 2 },
	{ item: 438, level: 1,  exp: 7.5,  pickaxe: pickaxes[0], hardness: 2 },
	{ item: 440, level: 15, exp: 15,   pickaxe: pickaxes[0], hardness: 3 },
	{ item: 453, level: 30, exp: 30,   pickaxe: pickaxes[1], hardness: 4 },
	{ item: 447, level: 55, exp: 70,   pickaxe: pickaxes[2], hardness: 5 },
	{ item: 449, level: 70, exp: 95,   pickaxe: pickaxes[3], hardness: 7 },
	{ item: 451, level: 85, exp: 120,  pickaxe: pickaxes[4], hardness: 9 },
];

var     copper = minerals[0];
var        tin = minerals[1];
var       iron = minerals[2];
var       coal = minerals[3];
var    mithril = minerals[4];
var adamantite = minerals[5];
var     runite = minerals[6];

var minableObjects = [
	[ 2090, copper ],
	[ 2091, copper ],
	[ 2094, tin ],
	[ 2095, tin ],
	[ 2092, iron ],
	[ 2093, iron ],
	[ 2096, coal ],
	[ 2097, coal ],
	[ 2102, mithril ],
	[ 2103, mithril ],
	[ 2104, adamantite ],
	[ 2105, adamantite ],
	[ 2106, runite ],
	[ 2107, runite ],
];
