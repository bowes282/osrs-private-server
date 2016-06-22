package nl.osrs.model.player.skills.smithing;

public enum Bar {
	BRONZE(15147, 2349, 6.25, BarRequirement.BRONZE),
	IRON(15151, 2351, 12.5, BarRequirement.IRON),
	SILVER(15155, 2355, 13.67, BarRequirement.SILVER),
	STEEL(15159, 2353, 17.5, BarRequirement.STEEL),
	GOLD(15163, 2357, 22.5, BarRequirement.GOLD),
	MITHRIL(29017, 2359, 30, BarRequirement.MITHRIL),
	ADAMANTITE(29022, 2361, 37.5, BarRequirement.ADAMANTITE),
	RUNITE(29026, 2363, 50, BarRequirement.RUNITE);
	
	private int actionButton;
	private int id;
	
	private double exp;
	private BarRequirement requirement;
	
	private Bar(int actionButton, int id, double exp, BarRequirement requirement) {
		this.actionButton = actionButton;
		this.id = id;
		this.exp = exp;
		this.requirement = requirement;
	}
	
	public int getActionButton() {
		return actionButton;
	}
	
	public int getId() {
		return id;
	}
	
	public double getExp() {
		return exp;
	}
	
	public BarRequirement getRequirement() {
		return requirement;
	}
	
	public static Bar getBar(int actionButton) {
		for (Bar bar : Bar.values())
			if (bar.getActionButton() == actionButton)
				return bar;
		return null;
	}
}