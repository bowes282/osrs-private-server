package nl.osrs.model.player.skills.smithing;

public enum BarRequirement {
	BRONZE(1, 436, 1, 438, 1),
	IRON(15, 440, 1),
	SILVER(20, 442, 1),
	STEEL(30, 440, 1, 453, 2),
	GOLD(40, 444, 1),
	MITHRIL(50, 447, 1, 453, 4),
	ADAMANTITE(70, 449, 1, 453, 6),
	RUNITE(85, 451, 1, 453, 8);
	
	private int level;
	private Integer requiredOre1 = null;
	private Integer requiredOreAmount1 = null;
	private Integer requiredOre2 = null;
	private Integer requiredOreAmount2 = null;

	private BarRequirement(int level, int requiredOre1, int requiredOreAmount1) {
		this.level = level;
		this.requiredOre1 = requiredOre1;
		this.requiredOreAmount1 = requiredOreAmount1;
	}
	
	private BarRequirement(int level, int requiredOre1, int requiredOreAmount1,
			int requiredOre2, int requiredOreAmount2) {
		this.level = level;
		this.requiredOre1 = requiredOre1;
		this.requiredOreAmount1 = requiredOreAmount1;
		this.requiredOre2 = requiredOre2;
		this.requiredOreAmount2 = requiredOreAmount2;
	}
	
	public int getLevel() {
		return level;
	}
	
	public Integer getRequiredOre1() {
		return requiredOre1;
	}
	
	public Integer getRequiredOreAmount1() {
		return requiredOreAmount1;
	}
	
	public Integer getRequiredOre2() {
		return requiredOre2;
	}
	
	public Integer getRequiredOreAmount2() {
		return requiredOreAmount2;
	}
}
