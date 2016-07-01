package nl.osrs.model.item;

import java.util.ArrayList;

public class Item {
	private int id;
	private String name = null;
	private String examine = null;
	private int value = 0;
	private int equipmentSlot = -1;
	private int[] bonuses = null;
	private int standAnimation = -1;
	private int walkAnimation = -1;
	private int runAnimation = -1;
	private int attackAnimation = -1;
	private int blockAnimation = -1;
	private int attackDelay = -1;
	private ArrayList<ItemRequirement> requirements = null;
	
	public Item(int id) {
		this.setId(id);
	}

	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}
	
	public boolean hasName() {
		return name != null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasExamine() {
		return examine != null;
	}

	public String getExamine() {
		return examine;
	}

	public void setExamine(String examine) {
		this.examine = examine;
	}
	
	public boolean hasValue() {
		return value > 0;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean hasEquipmentSlot() {
		return equipmentSlot != -1;
	}
	
	public int getEquipmentSlot() {
		return equipmentSlot;
	}

	public void setEquipmentSlot(int equipmentSlot) {
		this.equipmentSlot = equipmentSlot;
	}
	
	public boolean hasBonuses() {
		return bonuses != null;
	}

	public int[] getBonuses() {
		return bonuses;
	}

	public void setBonuses(int[] bonuses) {
		this.bonuses = bonuses;
	}
	
	public boolean hasStandAnimation() {
		return standAnimation != -1;
	}

	public int getStandAnimation() {
		return standAnimation;
	}

	public void setStandAnimation(int standAnimation) {
		this.standAnimation = standAnimation;
	}
	
	public boolean hasWalkAnimation() {
		return walkAnimation != -1;
	}

	public int getWalkAnimation() {
		return walkAnimation;
	}

	public void setWalkAnimation(int walkAnimation) {
		this.walkAnimation = walkAnimation;
	}
	
	public boolean hasRunAnimation() {
		return runAnimation != -1;
	}

	public int getRunAnimation() {
		return runAnimation;
	}

	public void setRunAnimation(int runAnimation) {
		this.runAnimation = runAnimation;
	}
	
	public boolean hasAttackAnimation() {
		return attackAnimation != -1;
	}

	public int getAttackAnimation() {
		return attackAnimation;
	}

	public void setAttackAnimation(int attackAnimation) {
		this.attackAnimation = attackAnimation;
	}
	
	public boolean hasBlockAnimation() {
		return blockAnimation != -1;
	}

	public int getBlockAnimation() {
		return blockAnimation;
	}

	public void setBlockAnimation(int blockAnimation) {
		this.blockAnimation = blockAnimation;
	}
	
	public boolean hasAttackDelay() {
		return attackDelay != -1;
	}

	public int getAttackDelay() {
		return attackDelay;
	}

	public void setAttackDelay(int attackDelay) {
		this.attackDelay = attackDelay;
	}
	
	public boolean hasRequirements() {
		return requirements != null;
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
