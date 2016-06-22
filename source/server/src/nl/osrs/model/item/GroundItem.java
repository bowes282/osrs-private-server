package nl.osrs.model.item;

public class GroundItem {

	/**
	 * The Item ID.
	 */
	public int itemId;

	/**
	 * The coordinates of the item.
	 */
	public int itemX;
	public int itemY;

	/**
	 * The amount of the item.
	 */
	public int itemAmount;

	/**
	 * Controls the ground item(s).
	 */
	public int itemController;

	/**
	 * Tick usage when re-spawning ground items.
	 */
	public int hideTicks;
	public int removeTicks;

	/**
	 * Name of item.
	 */
	public String ownerName;

	/**
	 * Handles the usage of ground items.
	 * 
	 * @param id
	 * @param x
	 * @param y
	 * @param amount
	 * @param controller
	 * @param hideTicks
	 * @param name
	 */
	public GroundItem(int id, int x, int y, int amount, int controller,
			int hideTicks, String name) {
		this.itemId = id;
		this.itemX = x;
		this.itemY = y;
		this.itemAmount = amount;
		this.itemController = controller;
		this.hideTicks = hideTicks;
		this.ownerName = name;
	}

	/**
	 * Item ID.
	 * 
	 * @return item
	 */

	public int getItemId() {
		return this.itemId;
	}

	/**
	 * Item coordinate X.
	 * 
	 * @return Coordinate X
	 */
	public int getItemX() {
		return this.itemX;
	}

	/**
	 * Item coordinate Y.
	 * 
	 * @return Coordinate Y
	 */
	public int getItemY() {
		return this.itemY;
	}

	/**
	 * Item amount.
	 * 
	 * @return Amount of item
	 */
	public int getItemAmount() {
		return this.itemAmount;
	}

	/**
	 * Controls ground item.
	 * 
	 * @return
	 */
	public int getItemController() {
		return this.itemController;
	}

	/**
	 * Item name.
	 * 
	 * @return
	 */
	public String getName() {
		return this.ownerName;
	}

}