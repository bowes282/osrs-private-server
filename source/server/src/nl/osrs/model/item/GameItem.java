package nl.osrs.model.item;

public class GameItem {
	public int id, amount;
	public boolean stackable = false;

	public GameItem(int id, int amount) {
		if (ItemWearing.itemStackable[id]) {
			stackable = true;
		}
	this.id = id;
	this.amount = amount;
	}
}