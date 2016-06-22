package nl.osrs.model.player.skills.smithing;

public class SmithingInterfaceItem {
	private static SmithingInterfaceItem[] smithingInterfaceItems = null;
	
	public static SmithingInterfaceItem getSmithingInterfaceItem(int row, int column) {
		if (smithingInterfaceItems == null)
			loadSmithingInterfaceItems();
		
		return smithingInterfaceItems[(row - 1) * 5 + (column - 1)];
	}
	
	private static void loadSmithingInterfaceItems() {
		smithingInterfaceItems = new SmithingInterfaceItem[25];
		smithingInterfaceItems[0] = new SmithingInterfaceItem(1094, 1125);
		smithingInterfaceItems[1] = new SmithingInterfaceItem(1091, 1126);
		smithingInterfaceItems[2] = new SmithingInterfaceItem(1098, 1109);
		smithingInterfaceItems[3] = new SmithingInterfaceItem(1102, 1127);
		smithingInterfaceItems[4] = new SmithingInterfaceItem(1107, 1128);
		smithingInterfaceItems[5] = new SmithingInterfaceItem(1085, 1124);
		smithingInterfaceItems[6] = new SmithingInterfaceItem(1093, 1129);
		smithingInterfaceItems[7] = new SmithingInterfaceItem(1099, 1110);
		smithingInterfaceItems[8] = new SmithingInterfaceItem(1103, 1113);
		smithingInterfaceItems[9] = new SmithingInterfaceItem(1108, 1130);
		smithingInterfaceItems[10] = new SmithingInterfaceItem(1087, 1116);
		smithingInterfaceItems[11] = new SmithingInterfaceItem(1083, 1118);
		smithingInterfaceItems[12] = new SmithingInterfaceItem(1100, 1111);
		smithingInterfaceItems[13] = new SmithingInterfaceItem(1104, 1114);
		smithingInterfaceItems[14] = new SmithingInterfaceItem(1106, 1131);
		smithingInterfaceItems[15] = new SmithingInterfaceItem(1086, 1089);
		smithingInterfaceItems[16] = new SmithingInterfaceItem(1092, 1095);
		smithingInterfaceItems[17] = new SmithingInterfaceItem(1101, 1112);
		smithingInterfaceItems[18] = new SmithingInterfaceItem(1105, 1115);
		smithingInterfaceItems[19] = new SmithingInterfaceItem(1096, 1132);
		smithingInterfaceItems[20] = new SmithingInterfaceItem(1088, 1090);
		smithingInterfaceItems[21] = new SmithingInterfaceItem(8429, 8428);
		smithingInterfaceItems[22] = new SmithingInterfaceItem(11461, 11459);
		smithingInterfaceItems[23] = new SmithingInterfaceItem(13358, 13357);
		smithingInterfaceItems[24] = new SmithingInterfaceItem(1134, 1135);
	}
	
	private int itemTextLocation;
	private int barTextLocation;
	
	private SmithingInterfaceItem(int itemTextLocation, int barTextLocation) {
		this.setItemTextLocation(itemTextLocation);
		this.setBarTextLocation(barTextLocation);
	}
	
	public int getItemTextLocation() {
		return itemTextLocation;
	}
	private void setItemTextLocation(int itemTextLocation) {
		this.itemTextLocation = itemTextLocation;
	}
	public int getBarTextLocation() {
		return barTextLocation;
	}
	private void setBarTextLocation(int barTextLocation) {
		this.barTextLocation = barTextLocation;
	}
}
