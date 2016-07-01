var ItemHandler = Java.type('nl.osrs.model.item.ItemHandler');
var Task = Java.type('nl.osrs.task.Task');
var Thread = Java.type("java.lang.Thread");

function smith(client, item, amount) {
	
}

function canSmith(client, smithableItem) {

}

function openSmithingInterface(client, bar) {
	var smithableItemList = getItemsForBar(bar);

	if (smithableItemList == null)
		return;

	for (var i = 0; i < 5; i++) {
		if (smithableItemList[i] == undefined)
			continue;

		for (var j = 0; j < 5; j++) {
			if (smithableItemList[i][j] == undefined)
				continue;

			var interfaceSpot = smithingInterface[i][j];
			var item = smithableItemList[i][j];
			var itemText = "";
			var barText = "";

			if (item != null) {
				var color = client.playerLevel[client.playerSmithing] >= item.level ? "@whi@" : "@bla@";
				var itemName = ItemHandler.getItemName(item.item);
				itemName = itemName.substring(itemName.indexOf(" ") + 1);

				itemText = color + itemName.substring(0, 1).toUpperCase() + itemName.substring(1);

				color = client.getItems().playerHasItem(bar, item.barAmount) ? "@gre@" : "@red@";
				barText = color + item.barAmount + " Bar";
				
				if (item.barAmount > 1)
					barText += "s";
			
				client.getPA().sendFrame34(item.item, i, j + 1119, item.amount);
			}

			client.getPA().sendFrame126(itemText, interfaceSpot.itemText);
			client.getPA().sendFrame126(barText, interfaceSpot.barText);
			client.getPA().sendFrame34(-1, i + 1, j + 1, 1);
		}
	}

		
	client.getPA().sendFrame34(-1, 4, 11460, 1);
	client.getPA().showInterface(994);

}

function getItemsForBar(bar) {
	for (var i = 0; i < smithableItems.length; i++)
		if (smithableItems[i][0] == bar)
			return smithableItems[i][1];
	return null;
}

var smithingInterface = [
	[
		{ itemText: 1094, barText: 1125 },
		{ itemText: 1091, barText: 1126 },
		{ itemText: 1098, barText: 1109 },
		{ itemText: 1102, barText: 1127 },
		{ itemText: 1107, barText: 1128 }
	], [
		{ itemText: 1085, barText: 1124 },
		{ itemText: 1093, barText: 1129 },
		{ itemText: 1099, barText: 1110 },
		{ itemText: 1103, barText: 1113 },
		{ itemText: 1108, barText: 1130 }
	], [
		{ itemText: 1087, barText: 1116 },
		{ itemText: 1083, barText: 1118 },
		{ itemText: 1100, barText: 1111 },
		{ itemText: 1104, barText: 1114 },
		{ itemText: 1106, barText: 1131 }
	], [
		{ itemText: 1086, barText: 1089 },
		{ itemText: 1092, barText: 1095 },
		{ itemText: 1101, barText: 1112 },
		{ itemText: 1105, barText: 1115 },
		{ itemText: 1096, barText: 1132 }
	], [
		{ itemText: 1088, barText: 1090 },
		{ itemText: 8429, barText: 8428 },
		{ itemText: 11461, barText: 11459 },
		{ itemText: 13358, barText: 13357 },
		{ itemText: 1134, barText: 1135 }
	]
];

var bronzeItems = [
	[
		{barAmount: 1, level: 1, item: 1205, amount: 1}, //Dagger
		{barAmount: 1, level: 2, item: 1351, amount: 1}, //Axe
		{barAmount: 1, level: 1, item: 1139, amount: 1}, //Med helm
		{barAmount: 2, level: 6, item: 1155, amount: 1}, //Full helm
		{barAmount: 1, level: 3, item: 39, amount: 15}, //Arrowtips
	], [
		{barAmount: 2, level: 4, item: 1321, amount: 1}, //Scimitar
		{barAmount: 1, level: 2, item: 1265, amount: 1}, //Pickaxe
		{barAmount: 2, level: 5, item: 1103, amount: 1}, //Chainbody
		{barAmount: 5, level: 10, item: 1117, amount: 1}, //Platebody
		{barAmount: 1, level: 3, item: 9375, amount: 10}, //Bronze bolts {unf}
	], [
		{barAmount: 3, level: 9, item: 1307, amount: 1}, //2H
		{barAmount: 1, level: 8, item: 8844, amount: 1}, //Defender
		{barAmount: 3, level: 3, item: 1087, amount: 1}, //Plateskirt
		{barAmount: 3, level: 8, item: 1075, amount: 1}, //Platelegs
		{barAmount: 1, level: 7, item: 7454, amount: 1}, //Bronze gauntlets
	], [
		{barAmount: 2, level: 4, item: 11367, amount: 1}, //Hasta
		{barAmount: 1, level: 5, item: 9420, amount: 1}, //Limb
		{barAmount: 2, level: 4, item: 1173, amount: 1}, //Sq shield
		{barAmount: 3, level: 9, item: 1189, amount: 1}, //Kite shield
		{barAmount: 1, level: 7, item: 4119, amount: 1}, //Bronze boots
	]
];

var ironItems = [
    [
        {barAmount: 1, level: 15, item: 1203, amount: 1}, //Dagger
        {barAmount: 1, level: 16, item: 1349, amount: 1}, //Axe
        {barAmount: 1, level: 18, item: 1137, amount: 1}, //Med helm
        {barAmount: 2, level: 22, item: 1153, amount: 1}, //Full helm
        {barAmount: 1, level: 20, item: 40, amount: 15}, //Arrowtips
    ], [
        {barAmount: 2, level: 20, item: 1323, amount: 1}, //Scimitar
        {barAmount: 1, level: 16, item: 1267, amount: 1}, //Pickaxe
        {barAmount: 2, level: 26, item: 1101, amount: 1}, //Chainbody
        {barAmount: 5, level: 33, item: 1115, amount: 1}, //Platebody
        {barAmount: 1, level: 18, item: 9140, amount: 10}, //Iron bolts {unf}
    ], [
        {barAmount: 3, level: 29, item: 1309, amount: 1}, //2H
        {barAmount: 1, level: 31, item: 8845, amount: 1}, //Defender
        {barAmount: 3, level: 31, item: 1081, amount: 1}, //Plateskirt
        {barAmount: 3, level: 31, item: 1067, amount: 1}, //Platelegs
        {barAmount: 1, level: 25, item: 7455, amount: 1}, //Iron gauntlets
    ], [
        {barAmount: 2, level: 23, item: 11369, amount: 1}, //Hasta
        {barAmount: 1, level: 23, item: 9423, amount: 1}, //Limb
        {barAmount: 2, level: 23, item: 1175, amount: 1}, //Sq shield
        {barAmount: 3, level: 27, item: 1191, amount: 1}, //Kite shield
        {barAmount: 1, level: 25, item: 4121, amount: 1}, //Iron boots
    ]
];

var steelItems = [
    [
        {barAmount: 1, level: 30, item: 1207, amount: 1}, //Dagger
        {barAmount: 1, level: 31, item: 1353, amount: 1}, //Axe
        {barAmount: 1, level: 33, item: 1141, amount: 1}, //Med helm
        {barAmount: 2, level: 37, item: 1157, amount: 1}, //Full helm
        {barAmount: 1, level: 35, item: 41, amount: 15}, //Arrowtips
    ], [
        {barAmount: 2, level: 35, item: 1325, amount: 1}, //Scimitar
        {barAmount: 1, level: 31, item: 1269, amount: 1}, //Pickaxe
        {barAmount: 2, level: 41, item: 1105, amount: 1}, //Chainbody
        {barAmount: 5, level: 48, item: 1119, amount: 1}, //Platebody
        {barAmount: 1, level: 33, item: 9378, amount: 10}, //Steel bolts {unf}
    ], [
        {barAmount: 3, level: 44, item: 1311, amount: 1}, //2H
        {barAmount: 1, level: 46, item: 8846, amount: 1}, //Defender
        {barAmount: 3, level: 46, item: 1083, amount: 1}, //Plateskirt
        {barAmount: 3, level: 46, item: 1069, amount: 1}, //Platelegs
        {barAmount: 1, level: 39, item: 7456, amount: 1}, //Steel gauntlets
    ], [
        {barAmount: 2, level: 38, item: 11371, amount: 1}, //Hasta
        {barAmount: 1, level: 36, item: 9425, amount: 1}, //Limb
        {barAmount: 2, level: 38, item: 1177, amount: 1}, //Sq shield
        {barAmount: 3, level: 42, item: 1193, amount: 1}, //Kite shield
        {barAmount: 1, level: 39, item: 4123, amount: 1}, //Steel boots
    ]
];

var mithrilItems = [
    [
        {barAmount: 1, level: 50, item: 1209, amount: 1}, //Dagger
        {barAmount: 1, level: 51, item: 1355, amount: 1}, //Axe
        {barAmount: 1, level: 53, item: 1143, amount: 1}, //Med helm
        {barAmount: 2, level: 57, item: 1159, amount: 1}, //Full helm
        {barAmount: 1, level: 55, item: 42, amount: 15}, //Arrowtips
    ], [
        {barAmount: 2, level: 55, item: 1329, amount: 1}, //Scimitar
        {barAmount: 1, level: 51, item: 1273, amount: 1}, //Pickaxe
        {barAmount: 2, level: 61, item: 1109, amount: 1}, //Chainbody
        {barAmount: 5, level: 68, item: 1121, amount: 1}, //Platebody
        {barAmount: 1, level: 53, item: 9379, amount: 10}, //Mithril bolts {unf}
    ], [
        {barAmount: 3, level: 64, item: 1315, amount: 1}, //2H
        {barAmount: 1, level: 66, item: 8848, amount: 1}, //Defender    1
        {barAmount: 3, level: 66, item: 1085, amount: 1}, //Plateskirt
        {barAmount: 3, level: 66, item: 1071, amount: 1}, //Platelegs
        {barAmount: 1, level: 59, item: 7458, amount: 1}, //Mithril gauntlets    1
    ], [
        {barAmount: 2, level: 58, item: 11373, amount: 1}, //Hasta
        {barAmount: 1, level: 56, item: 9427, amount: 1}, //Limb    2
        {barAmount: 2, level: 58, item: 1181, amount: 1}, //Sq shield
        {barAmount: 3, level: 62, item: 1197, amount: 1}, //Kite shield
        {barAmount: 1, level: 59, item: 4127, amount: 1}, //Mithril boots
    ]
];

var adamantItems = [
    [
        {barAmount: 1, level: 70, item: 1211, amount: 1}, //Dagger    2
        {barAmount: 1, level: 71, item: 1357, amount: 1}, //Axe    2
        {barAmount: 1, level: 73, item: 1145, amount: 1}, //Med helm    2
        {barAmount: 2, level: 77, item: 1161, amount: 1}, //Full helm    2
        {barAmount: 1, level: 75, item: 43, amount: 15}, //Arrowtips    2
    ], [
        {barAmount: 2, level: 75, item: 1331, amount: 1}, //Scimitar
        {barAmount: 1, level: 71, item: 1271, amount: 1}, //Pickaxe
        {barAmount: 2, level: 81, item: 1111, amount: 1}, //Chainbody    2
        {barAmount: 5, level: 88, item: 1123, amount: 1}, //Platebody    2
        {barAmount: 1, level: 73, item: 9380, amount: 10}, //Adamant bolts {unf}
    ], [
        {barAmount: 3, level: 84, item: 1317, amount: 1}, //2H    2
        {barAmount: 1, level: 86, item: 8849, amount: 1}, //Defender    1
        {barAmount: 3, level: 86, item: 1091, amount: 1}, //Plateskirt
        {barAmount: 3, level: 86, item: 1073, amount: 1}, //Platelegs
        {barAmount: 1, level: 79, item: 7459, amount: 1}, //Adamant gauntlets    1
    ], [
        {barAmount: 2, level: 78, item: 11375, amount: 1}, //Hasta    2
        {barAmount: 1, level: 76, item: 9429, amount: 1}, //Limb    2
        {barAmount: 2, level: 78, item: 1183, amount: 1}, //Sq shield    2
        {barAmount: 3, level: 82, item: 1199, amount: 1}, //Kite shield 2
        {barAmount: 1, level: 79, item: 4129, amount: 1}, //Adamant boots 2
    ]
];

var runeItems = [
    [
        {barAmount: 1, level: 85, item: 1213, amount: 1}, //Dagger    2
        {barAmount: 1, level: 86, item: 1359, amount: 1}, //Axe    2
        {barAmount: 1, level: 88, item: 1147, amount: 1}, //Med helm    2
        {barAmount: 2, level: 92, item: 1163, amount: 1}, //Full helm    2
        {barAmount: 1, level: 90, item: 44, amount: 15}, //Arrowtips    2
    ], [
        {barAmount: 2, level: 90, item: 1333, amount: 1}, //Scimitar
        {barAmount: 1, level: 86, item: 1275, amount: 1}, //Pickaxe
        {barAmount: 2, level: 96, item: 1113, amount: 1}, //Chainbody    2
        {barAmount: 5, level: 99, item: 1127, amount: 1}, //Platebody    2
        {barAmount: 1, level: 88, item: 9381, amount: 10}, //Rune bolts {unf}
    ], [
        {barAmount: 3, level: 99, item: 1319, amount: 1}, //2H    2
        {barAmount: 1, level: 99, item: 8850, amount: 1}, //Defender    1
        {barAmount: 3, level: 99, item: 1093, amount: 1}, //Plateskirt
        {barAmount: 3, level: 99, item: 1079, amount: 1}, //Platelegs
        {barAmount: 1, level: 94, item: 7460, amount: 1}, //Rune gauntlets    1
    ], [
        {barAmount: 2, level: 93, item: 11377, amount: 1}, //Hasta    2
        {barAmount: 1, level: 91, item: 9431, amount: 1}, //Limb    2
        {barAmount: 2, level: 93, item: 1185, amount: 1}, //Sq shield    2
        {barAmount: 3, level: 97, item: 1201, amount: 1}, //Kite shield 2
        {barAmount: 1, level: 94, item: 4131, amount: 1}, //Rune boots 2
    ]
];

var smithableItems = [
	[ 2349, bronzeItems ],
	[ 2351, ironItems ],
	[ 2353, steelItems ],
	[ 2359, mithrilItems ],
	[ 2361, adamantItems ],
	[ 2363, runeItems ]
];
