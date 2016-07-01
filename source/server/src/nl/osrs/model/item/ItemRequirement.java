package nl.osrs.model.item;

public class ItemRequirement {
	private int skill;
	private int level;
	
	public ItemRequirement(int skill, int level) {
		this.setSkill(skill);
		this.setLevel(level);
	}

	public int getSkill() {
		return skill;
	}

	public void setSkill(int skill) {
		this.skill = skill;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
