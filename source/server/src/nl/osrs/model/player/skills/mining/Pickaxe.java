package nl.osrs.model.player.skills.mining;

public enum Pickaxe {
	BRONZE(0, 1, 1265, 6747),
	IRON(1, 1, 1267, 6748),
	STEEL(2, 11, 1269, 6749),
	MITHRIL(3, 21, 1273, 6751),
	ADAMANT(4, 31, 1271, 6750),
	RUNE(5, 41, 1275, 6746);
	
	private int force;
	private int level;
	private int item;
	private int animation;
	
	private Pickaxe(int force, int level, int item, int animation) {
		this.force = force;
		this.level = level;
		this.item = item;
		this.animation = animation;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getItem() {
		return item;
	}
	
	public boolean isBetterThanOrEqualTo(Pickaxe pickaxe) {
		if (pickaxe == null)
			return true;
		return this.force >= pickaxe.force;
	}
	
	public static Pickaxe getPickaxe(int item) {
		for (Pickaxe pickaxe : Pickaxe.values())
			if (pickaxe.item == item)
				return pickaxe;
		return null;
	}

	public int getAnimation() {
		return animation;
	}
}
