package nl.osrs.model.player.skills.smithing;

import nl.osrs.model.item.ItemHandler;

public class SmithingItem {
	private Bar bar;
	private int barAmount;
	
	private int levelRequirement;
	private int itemId;
	private int amount;
	
	private int row;
	private int column;
	
	private SmithingInterfaceItem interfaceLocation;
	
	public SmithingItem(Bar bar, int barAmount, int levelRequirement, int itemId, int amount, SmithingInterfaceItem interfaceLocation) {
		this.setBar(bar);
		this.setBarAmount(barAmount);
		this.setLevelRequirement(levelRequirement);
		this.setItemId(itemId);
		this.setInterfaceLocation(interfaceLocation);
	}
	
	public SmithingItem(Bar bar, int barAmount, int row, int column, int levelRequirement, int itemId, int amount) {
		this.setBar(bar);
		this.setBarAmount(barAmount);
		this.setRow(row);
		this.setColumn(column);
		this.setLevelRequirement(levelRequirement);
		this.setItemId(itemId);
		this.setAmount(amount);
		this.setInterfaceLocation(SmithingInterfaceItem.getSmithingInterfaceItem(row, column));
	}

	public Bar getBar() {
		return bar;
	}

	public void setBar(Bar bar) {
		this.bar = bar;
	}

	public int getBarAmount() {
		return barAmount;
	}

	public void setBarAmount(int barAmount) {
		this.barAmount = barAmount;
	}

	public int getLevelRequirement() {
		return levelRequirement;
	}

	public void setLevelRequirement(int levelRequirement) {
		this.levelRequirement = levelRequirement;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public String getItemName() {
		String itemName = ItemHandler.getItemName(itemId);
		
		itemName = itemName.substring(itemName.indexOf(' '));

		if (itemName.substring(0, 1).matches("/^[a-z]+$/"))
			itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
		else
			itemName = itemName.substring(0, 1) + itemName.substring(1, 2).toUpperCase() + itemName.substring(2);
		
		return itemName;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public SmithingInterfaceItem getInterfaceLocation() {
		return interfaceLocation;
	}

	public void setInterfaceLocation(SmithingInterfaceItem interfaceLocation) {
		this.interfaceLocation = interfaceLocation;
	}
	
	public double getExp() {
		return barAmount * bar.getExp();
	}
}
