var ItemHandler = Java.type('nl.osrs.model.item.ItemHandler');
var Task = Java.type('nl.osrs.task.Task');
var Thread = Java.type("java.lang.Thread");

function smelt(client, button) {
	var bar = getBar(button);

	if (bar == null)
		return false;

	if (!canSmelt(client, bar))
		return false;

	client.getTaskScheduler().schedule(new Task({
		immediate: false,
		delay: 6,
		countdown: 0,
		running: true,

		execute: function() {
			if (!canSmelt(client, bar)) {
				client.getTaskScheduler().stopTasks();
				return;
			}

			client.startAnimation(899);

			var smeltMessage = "You smelt the " + ItemHandler.getItemName(bar.requiredOres[0].item).toLowerCase();
			
			if (bar.requiredOres.length > 1)
				smeltMessage += " and the " + ItemHandler.getItemName(bar.requiredOres[1].item).toLowerCase();
			
			client.sendMessage(smeltMessage + " ...");

			Thread.sleep(2100);

			client.getItems().deleteItem(bar.requiredOres[0].item, bar.requiredOres[0].amount);
			if (bar.requiredOres.length > 1)
				client.getItems().deleteItem(bar.requiredOres[1].item, bar.requiredOres[1].amount);

			if (bar.item == 2351 && Math.random(1) < 0.5) {
				client.sendMessage("... but you are unsuccessful in retrieving an iron bar.");
				return;
			}

			client.getItems().addItem(bar.item, 1);
			client.sendMessage("... and retrieve a " + ItemHandler.getItemName(bar.item).toLowerCase() + ".");
			client.getPA().addSkillXP(bar.exp, client.playerSmithing);

			if (!canSmelt(client, bar))
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

function canSmelt(client, bar) {
	var canSmelt = true;

	if (!client.hasLevel(bar.level, client.playerSmithing)) {
		client.sendMessage("You need a smithing level of " + bar.level + " to make a(n) " + ItemHandler.getItemName(bar.item).toLowerCase() + ".");
		canSmelt = false;
	}

	bar.requiredOres.forEach(function(requiredOre) {
		if (!client.getItems().playerHasItem(requiredOre.item, requiredOre.amount)) {
			client.sendMessage("You need " + requiredOre.amount + "x " + ItemHandler.getItemName(requiredOre.item).toLowerCase() + " to make a(n) " + ItemHandler.getItemName(bar.item).toLowerCase() + ".");
			canSmelt = false;
		}
	})

	return canSmelt;
}

function openSmeltingInterface(client) {
	var pa = client.getPA();

	pa.sendFrame164(2400);
	pa.sendFrame246(2405, 150, 2349);
	pa.sendFrame246(2406, 150, 2351);
	pa.sendFrame246(2407, 150, 2355);
	pa.sendFrame246(2409, 150, 2353);
	pa.sendFrame246(2410, 150, 2357);
	pa.sendFrame246(2411, 150, 2359);
	pa.sendFrame246(2412, 150, 2361);
	pa.sendFrame246(2413, 150, 2363);
}

function getBar(button) {
	var bar;

	barsForButtons.forEach(function(barForButton) {
		if (barForButton.button == button)
			bar = barForButton.bar;
	});

	return bar;
}

var bars = [
	{ item: 2349, exp: 6.25,  level: 1,  requiredOres: [ { item: 436, amount: 1 }, { item: 438, amount: 1 } ] },
	{ item: 2351, exp: 12.5,  level: 15, requiredOres: [ { item: 440, amount: 1 } ] },
	{ item: 2355, exp: 13.67, level: 20, requiredOres: [ { item: 442, amount: 1 } ] },
	{ item: 2353, exp: 17.5,  level: 30, requiredOres: [ { item: 440, amount: 1 }, { item: 453, amount: 2 } ] },
	{ item: 2357, exp: 22.5,  level: 40, requiredOres: [ { item: 444, amount: 1 } ] },
	{ item: 2359, exp: 30,    level: 50, requiredOres: [ { item: 447, amount: 1 }, { item: 453, amount: 4 } ] },
	{ item: 2361, exp: 37.5,  level: 70, requiredOres: [ { item: 449, amount: 1 }, { item: 453, amount: 6 } ] },
	{ item: 2363, exp: 50,    level: 85, requiredOres: [ { item: 451, amount: 1 }, { item: 453, amount: 8 } ] },
]

var barsForButtons = [
	{ button: 15147, bar: bars[0] },
	{ button: 15151, bar: bars[1] },
	{ button: 15155, bar: bars[2] },
	{ button: 15159, bar: bars[3] },
	{ button: 15163, bar: bars[4] },
	{ button: 29017, bar: bars[5] },
	{ button: 29022, bar: bars[6] },
	{ button: 29026, bar: bars[7] },
];