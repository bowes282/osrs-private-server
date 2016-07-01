var ItemHandler = Java.type('nl.osrs.model.item.ItemHandler');
var Thread = Java.type("java.lang.Thread");
var Task = Java.type('nl.osrs.task.Task');

function cut(client, usedItem, usedItemSlot, usedOnItem, usedOnItemSlot) {
	if (usedItem.getId() != 1755)
		return false;

	var cuttableGem = findGem(usedOnItem.getId());

	if (cuttableGem == null)
		return false;

	if (!canCut(client, cuttableGem, true))
		return true;

	client.getTaskScheduler().schedule(new Task({
		immediate: false,
		delay: 4,
		countdown: 0,
		running: true,

		execute: function() {
			if (!canCut(client, cuttableGem)) {
				client.getTaskScheduler().stopTasks();
				return;
			}

			client.startAnimation(cuttableGem.animation);

			Thread.sleep(2000);

			client.getItems().deleteItem(cuttableGem.uncut, 1);
			client.getItems().addItem(cuttableGem.cut, 1);
			client.sendMessage("You cut the " + ItemHandler.getItemName(cuttableGem.cut).toLowerCase() + ".");
			client.getPA().addSkillXP(cuttableGem.exp, client.playerCrafting);

			if (!canCut(client, cuttableGem))
				client.getTaskScheduler().stopTasks();
		},

		tick: function() {
			if (this.running && this.countdown == 0) {
				this.countdown = this.delay;
				this.execute();
			}

			this.countdown--;

			return this.running;
		},
	}));
}

function openJewellerySmeltingInterface(client, usedItem, usedItemSlot, objectId, objectX, objectY) {
	
}

function findGem(uncutGem) {
	for (var i = 0; i < cuttableGems.length; i++)
		if (cuttableGems[i].uncut == uncutGem)
			return cuttableGems[i];
	return null;
}

function canCut(client, cuttableGem, initial) {
	var canCut = true;

	if (!client.hasLevel(cuttableGem.level, client.playerCrafting)) {
		client.sendMessage("You need " + cuttableGem.level + " crafting to cut a(n) " + ItemHandler.getItemName(cuttableGem.cut).toLowerCase() + ".");
		canCut = false;
	}

	if (!client.getItems().playerHasItem(cuttableGem.uncut, 1)) {
		if (initial != undefined)
			client.sendMessage("You need a(n) " + ItemHandler.getItemName(cuttableGem.cut).toLowerCase() + ".");
		canCut = false;
	}

	return canCut;
}

var cuttableGems = [
	{ uncut: 1623, cut: 1607, level: 20, animation: 888, exp: 50 },
	{ uncut: 1621, cut: 1605, level: 27, animation: 889, exp: 67.5 },
	{ uncut: 1619, cut: 1603, level: 34, animation: 887, exp: 85 },
	{ uncut: 1617, cut: 1601, level: 43, animation: 886, exp: 107.5 },
	{ uncut: 1631, cut: 1615, level: 55, animation: 885, exp: 137.5 },
	{ uncut: 6571, cut: 6573, level: 67, animation: 2717, exp: 167.5 },
];