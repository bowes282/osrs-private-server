package nl.osrs.item;

import java.util.ArrayList;

public class Item {
	private int id;
	private String name;
	private String examine;
	private int value;
	private int equipmentSlot;
	private int[] bonuses;
	private int standAnimation;
	private int walkAnimation;
	private int runAnimation;
	private int attackAnimation;
	private int blockAnimation;
	private int attackDelay;
	private ArrayList<ItemRequirement> requirements;
	
	public Item(int id) {
		this.setId(id);
	}

	public int getId() {
		return id;
	}

	private void setId(int id) {
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

	public int[] getBonuses() {
		return bonuses;
	}

	public void setBonuses(int[] bonuses) {
		if (bonuses.length != 12)
			return;
		
		this.bonuses = bonuses;
	}

	public int getStandAnimation() {
		return standAnimation;
	}

	public void setStandAnimation(int standAnimation) {
		this.standAnimation = standAnimation;
	}

	public int getWalkAnimation() {
		return walkAnimation;
	}

	public void setWalkAnimation(int walkAnimation) {
		this.walkAnimation = walkAnimation;
	}

	public int getRunAnimation() {
		return runAnimation;
	}

	public void setRunAnimation(int runAnimation) {
		this.runAnimation = runAnimation;
	}

	public int getAttackAnimation() {
		return attackAnimation;
	}

	public void setAttackAnimation(int attackAnimation) {
		this.attackAnimation = attackAnimation;
	}

	public int getBlockAnimation() {
		return blockAnimation;
	}

	public void setBlockAnimation(int blockAnimation) {
		this.blockAnimation = blockAnimation;
	}

	public int getAttackDelay() {
		return attackDelay;
	}

	public void setAttackDelay(int attackDelay) {
		this.attackDelay = attackDelay;
	}

	public ArrayList<ItemRequirement> getRequirements() {
		return requirements;
	}
	
	public void setRequirements(ArrayList<ItemRequirement> requirements) {
		this.requirements = requirements;
	}

	public void addRequirement(ItemRequirement requirement) {
		if (requirements == null)
			requirements = new ArrayList<>();
		
		ItemRequirement requirementForSameSkill = null;
		
		for (ItemRequirement temp : requirements)
			if (requirement.getSkill() == temp.getSkill())
				requirementForSameSkill = temp;
		
		if (requirementForSameSkill != null)
			removeRequirement(requirementForSameSkill);

		requirements.add(requirement);
	}

	public void removeRequirement(ItemRequirement requirement) {
		if (requirements == null)
			return;
		requirements.remove(requirement);
	}
}
