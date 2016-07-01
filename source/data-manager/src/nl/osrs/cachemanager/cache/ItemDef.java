package nl.osrs.cachemanager.cache;

import nl.osrs.cachemanager.stream.Stream;

public final class ItemDef {
	
	public ItemDef(int id) {
		this.id = id;
		setDefaults();
	}
	
	private void setDefaults() {
		dropModel = 0;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		modelZoom = 2000;
		modelRotationY = 0;
		modelRotationX = 0;
		modelPositionUp = 0;
		modelOffset1 = 0;
		modelOffset2 = 0;
		stackable = false;
		value = 1;
		membersObject = false;
		groundActions = null;
		itemActions = null;
		maleWieldModel = -1;
		maleArmModel = -1;
		femaleWieldModel = -1;
		femaleArmModel = -1;
		anInt175 = -1;
		anInt197 = -1;
		stackIDs = null;
		stackAmounts = null;
		certID = -1;
		team = 0;
	}

	public void readValues(Stream stream) {
		do {
			int i = stream.readUnsignedByte();
			if (i == 0)
				return;
			if (i == 1)
				dropModel = stream.readUnsignedWord();
			else if (i == 2)
				name = stream.readNewString();
			else if (i == 3)
				description = stream.readBytes();
			else if (i == 4)
				modelZoom = stream.readUnsignedWord();
			else if (i == 5)
				modelRotationY = stream.readUnsignedWord();
			else if (i == 6)
				modelRotationX = stream.readUnsignedWord();
			else if (i == 7) {
				modelOffset1 = stream.readUnsignedWord();
				if (modelOffset1 > 32767)
					modelOffset1 -= 0x10000;
			} else if (i == 8) {
				modelOffset2 = stream.readUnsignedWord();
				if (modelOffset2 > 32767)
					modelOffset2 -= 0x10000;
			} else if (i == 10)
				stream.readUnsignedWord();
			else if (i == 11)
				stackable = true;
			else if (i == 12)
				value = stream.readDWord();
			else if (i == 16)
				membersObject = true;
			else if (i == 23) {
				maleWieldModel = stream.readUnsignedWord();
				stream.readSignedByte();
			} else if (i == 24)
				maleArmModel = stream.readUnsignedWord();
			else if (i == 25) {
				femaleWieldModel = stream.readUnsignedWord();
				stream.readSignedByte();
			} else if (i == 26)
				femaleArmModel = stream.readUnsignedWord();
			else if (i >= 30 && i < 35) {
				if (groundActions == null)
					groundActions = new String[5];
				groundActions[i - 30] = stream.readNewString();
				if (groundActions[i - 30].equalsIgnoreCase("hidden"))
					groundActions[i - 30] = null;
			} else if (i >= 35 && i < 40) {
				if (itemActions == null)
					itemActions = new String[5];
				itemActions[i - 35] = stream.readNewString();
			} else if (i == 40) {
				int j = stream.readUnsignedByte();
				modifiedModelColors = new int[j];
				originalModelColors = new int[j];
				for (int k = 0; k < j; k++) {
					modifiedModelColors[k] = stream.readUnsignedWord();
					originalModelColors[k] = stream.readUnsignedWord();
				}

			} else if (i == 78)
				stream.readUnsignedWord();
			else if (i == 79)
				stream.readUnsignedWord();
			else if (i == 90)
				anInt175 = stream.readUnsignedWord();
			else if (i == 91)
				anInt197 = stream.readUnsignedWord();
			else if (i == 92)
				stream.readUnsignedWord();
			else if (i == 93)
				stream.readUnsignedWord();
			else if (i == 95)
				modelPositionUp = stream.readUnsignedWord();
			else if (i == 97)
				certID = stream.readUnsignedWord();
			else if (i == 98)
				stream.readUnsignedWord();
			else if (i >= 100 && i < 110) {
				if (stackIDs == null) {
					stackIDs = new int[10];
					stackAmounts = new int[10];
				}
				stackIDs[i - 100] = stream.readUnsignedWord();
				stackAmounts[i - 100] = stream.readUnsignedWord();
			} else if (i == 110)
				stream.readUnsignedWord();
			else if (i == 111)
				stream.readUnsignedWord();
			else if (i == 112)
				stream.readUnsignedWord();
			else if (i == 113)
				stream.readSignedByte();
			else if (i == 114)
				stream.readSignedByte();
			else if (i == 115)
				team = stream.readUnsignedByte();
		} while (true);
	}

	public int id;// anInt157
	public String name;// itemName
	public byte description[];// itemExamine
	public String itemActions[];// itemMenuOption
	public String groundActions[];
	public int value;// anInt155
	
	public int maleWieldModel;// maleWieldModel
	public int femaleWieldModel;// femWieldModel
	public int maleArmModel;// maleArmModel
	public int femaleArmModel;// femArmModel
	public int dropModel;// dropModel
	public int modelPositionUp;// modelPositionUp
	public int modelRotationX;// modelRotateRight
	public int modelRotationY;// modelRotateUp
	public int modelZoom;
	public int modelOffset1;
	public int modelOffset2;
	public boolean stackable;// itemStackable
	public int[] stackIDs;// modelStack
	public int[] stackAmounts;// itemAmount
	
	public int[] originalModelColors;
	public int[] modifiedModelColors;
	
	public boolean membersObject;// aBoolean161
	
	public int anInt175;
	public int certID;
	
	public int anInt197;
	public int team;
	
	public static boolean isMembers = true;
	public static int totalItems;

}
