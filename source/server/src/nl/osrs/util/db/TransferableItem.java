package nl.osrs.util.db;

public class TransferableItem {
	private int id;
	private String name;
	private String examine;
	private int value;
	private int equipmentSlot;
	private int[] attackValues = new int[5];
	private int[] defenceValues = new int[5];
	private int strength = 0;
	private int prayer = 0;
	
	public TransferableItem(int id) {
		this.setId(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExamine() {
		return examine;
	}

	public void setExamine(String examine) {
		this.examine = examine;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getEquipmentSlot() {
		return equipmentSlot;
	}

	public void setEquipmentSlot(int equipmentSlot) {
		this.equipmentSlot = equipmentSlot;
	}

	public int[] getAttackValues() {
		return attackValues;
	}

	public void setAttackValues(int[] attackValues) {
		this.attackValues = attackValues;
	}

	public int[] getDefenceValues() {
		return defenceValues;
	}

	public void setDefenceValues(int[] defenceValues) {
		this.defenceValues = defenceValues;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getPrayer() {
		return prayer;
	}

	public void setPrayer(int prayer) {
		this.prayer = prayer;
	}
	
	private boolean hasCombatStats() {
		for (int i : getAttackValues())
			if (i != 0)
				return true;
		
		for (int i : getDefenceValues())
			if (i != 0)
				return true;
		
		if (strength != 0 || prayer != 0)
			return true;
			
		return false;
	}
	
	public String toQuery() {
		if (hasCombatStats())
			return "INSERT INTO item(id, name, examine, value, equipment_slot, "
					+ "atk_stab, atk_slash, atk_crush, atk_magic, atk_range, def_stab, "
					+ "def_slash, def_crush, def_magic, def_range, strength, prayer) "
					+ "VALUES(" + getId() + ", '" + getName().replace("'", "\\'")
					+ "', '" + getExamine().replace("'", "\\'")
					+ "', " + getValue() + ", " + getEquipmentSlot() + ", "
					+ getAttackValues()[0] + ", " + getAttackValues()[1] + ", "
					+ getAttackValues()[2] + ", " + getAttackValues()[3] + ", "
					+ getAttackValues()[4] + ", " + getDefenceValues()[0] + ", "
					+ getDefenceValues()[1] + ", " + getDefenceValues()[2] + ", "
					+ getDefenceValues()[3] + ", " + getDefenceValues()[4] + ", "
					+ strength + ", " + prayer + ")";
		else
			return "INSERT INTO item(id, name, examine, value, equipment_slot) VALUES(" + getId()
			+ ", '" + getName().replace("'", "\\'") + "', '"
			+ getExamine().replace("'", "\\'") + "', " + getValue()
			+ ", " + getEquipmentSlot() + ")";
	}
}
