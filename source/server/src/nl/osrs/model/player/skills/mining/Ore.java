package nl.osrs.model.player.skills.mining;

public enum Ore {
	COPPER(436, 1, 17.5, Pickaxe.BRONZE),
	TIN(438, 1, 17.5, Pickaxe.BRONZE),
	IRON(440, 15, 35, Pickaxe.BRONZE),
	COAL(453, 30, 50, Pickaxe.IRON),
	MITHRIL(447, 55, 80, Pickaxe.STEEL),
	ADAMANTITE(449, 70, 95, Pickaxe.MITHRIL),
	RUNITE(451, 85, 120, Pickaxe.ADAMANT);
	
	int item;
	int level;
	double exp;
	Pickaxe pickaxeNeeded;
	
	private Ore(int item, int level, double exp, Pickaxe pickaxeNeeded) {
		this.item = item;
		this.level = level;
		this.exp = exp;
		this.pickaxeNeeded = pickaxeNeeded;
	}
	
	public int getItem() {
		return item;
	}
	
	public int getLevel() {
		return level;
	}
	
	public double getExp() {
		return exp;
	}
	
	public Pickaxe getPickaxe() {
		return pickaxeNeeded;
	}
}
