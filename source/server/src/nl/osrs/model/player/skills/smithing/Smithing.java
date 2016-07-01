package nl.osrs.model.player.skills.smithing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.player.Client;
import nl.osrs.task.Task;

public class Smithing {
	private static Map<Bar, ArrayList<SmithingItem>> smithingItems;

	public static void startSmithing(Client client, int itemId, int amount) {
		SmithingItem item = getSmithingItem(itemId);
		
		if (!smithingPrerequisites(client, item))
			return;

		client.setSmithingAmountLeft(amount);

		client.getTaskScheduler().schedule(new Task(6, true) {
			@Override
			protected void execute() {
				if (!smithingPrerequisites(client, item) || client.getSmithingAmountLeft() <= 0) {
					this.stop();
					return;
				}
				
				client.startAnimation(898);

				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				client.getItems().deleteItem(item.getBar().getId(), item.getBarAmount());
				client.getItems().addItem(item.getItemId(), item.getAmount());
				
				client.setSmithingAmountLeft(client.getSmithingAmountLeft() - 1);
				
				client.sendMessage(getSmithingMessage(item));
				client.getPA().addSkillXP(item.getExp(), client.playerSmithing);

				if (!smithingPrerequisites(client, item) || client.getSmithingAmountLeft() <= 0) {
					this.stop();
					return;
				}
			}
			
			@Override
			public void onStop() {
				client.startAnimation(65535);
				client.setSmithingAmountLeft(0);
			}
		});
		
		client.getPA().closeAllWindows();
	}

	private static boolean smithingPrerequisites(Client client, SmithingItem item) {
		boolean success = true;
		
		if (!client.getItems().playerHasItem(2347)) {
			client.sendMessage("You need a hammer to smith bars into items.");
			success = false;
		}
		
		if (item.getBar() == null)
			success = false;
		else
			if (!client.getItems().playerHasItem(item.getBar().getId(), item.getBarAmount()))  {
				if (item.getBarAmount() > 1)
					client.sendMessage(getNotEnoughBarsMessage(item));
				success = false;
			}
		
		return success;
	}
	
	public static void openSmithingInterface(Client client, int itemId) {
		Bar bar = getBarForItemId(itemId);
		
		if (bar == null)
			return;
		
		for (int i = 1; i <= 5; i++)
			for (int j = 1; j <= 5; j++) {
				SmithingInterfaceItem item = SmithingInterfaceItem.getSmithingInterfaceItem(i, j);
				client.getPA().sendFrame126("", item.getItemTextLocation());
				client.getPA().sendFrame126("", item.getBarTextLocation());
				client.getPA().sendFrame34(-1, i, j, 1);
			}
		
		for (SmithingItem item : getSmithingItems(bar)) {
			String color = client.playerLevel[client.playerSmithing] >= item.getLevelRequirement() ? "@whi@" : "@bla@";
			client.getPA().sendFrame126(color + item.getItemName(), item.getInterfaceLocation().getItemTextLocation());
			
			boolean hasEnoughBars = client.getItems().playerHasItem(item.getBar().getId(), item.getBarAmount());
			color = hasEnoughBars ? "@gre@" : "@red@";
			String text = color + item.getBarAmount() + " Bar";
			
			if (item.getBarAmount() > 1)
				text += "s";
			
			client.getPA().sendFrame126(text, item.getInterfaceLocation().getBarTextLocation());
		
			client.getPA().sendFrame34(item.getItemId(), item.getRow() - 1, item.getColumn() + 1118, item.getAmount());
		}
		
		client.getPA().sendFrame34(-1, 4, 11460, 1);
		client.getPA().showInterface(994);
	}
	
	private static ArrayList<SmithingItem> getSmithingItems(Bar bar) {
		if (smithingItems == null)
			loadSmithingItems();
		
		return smithingItems.get(bar);
	}
	
	private static void loadSmithingItems() {
		smithingItems = new HashMap<>();
		ArrayList<SmithingItem> items = new ArrayList<>();

		items.add(new SmithingItem(Bar.BRONZE, 1, 1, 1, 1, 1205, 1)); //Dagger
		items.add(new SmithingItem(Bar.BRONZE, 1, 1, 2, 2, 1351, 1)); //Axe
		items.add(new SmithingItem(Bar.BRONZE, 1, 1, 3, 1, 1139, 1)); //Med helm
		items.add(new SmithingItem(Bar.BRONZE, 2, 1, 4, 6, 1155, 1)); //Full helm
		items.add(new SmithingItem(Bar.BRONZE, 1, 1, 5, 3, 39, 15)); //Arrowtips
		
		items.add(new SmithingItem(Bar.BRONZE, 2, 2, 1, 4, 1321, 1)); //Scimitar
		items.add(new SmithingItem(Bar.BRONZE, 1, 2, 2, 2, 1265, 1)); //Pickaxe
		items.add(new SmithingItem(Bar.BRONZE, 2, 2, 3, 5, 1103, 1)); //Chainbody
		items.add(new SmithingItem(Bar.BRONZE, 5, 2, 4, 10, 1117, 1)); //Platebody
		items.add(new SmithingItem(Bar.BRONZE, 1, 2, 5, 3, 9375, 10)); //Bronze bolts (unf)
		
		items.add(new SmithingItem(Bar.BRONZE, 3, 3, 1, 9, 1307, 1)); //2H
		items.add(new SmithingItem(Bar.BRONZE, 1, 3, 2, 8, 8844, 1)); //Defender
		items.add(new SmithingItem(Bar.BRONZE, 3, 3, 3, 3, 1087, 1)); //Plateskirt
		items.add(new SmithingItem(Bar.BRONZE, 3, 3, 4, 8, 1075, 1)); //Platelegs
		items.add(new SmithingItem(Bar.BRONZE, 1, 3, 5, 7, 7454, 1)); //Bronze gauntlets
		
		items.add(new SmithingItem(Bar.BRONZE, 2, 4, 1, 4, 11367, 1)); //Hasta
		items.add(new SmithingItem(Bar.BRONZE, 1, 4, 2, 5, 9420, 1)); //Limb
		items.add(new SmithingItem(Bar.BRONZE, 2, 4, 3, 4, 1173, 1)); //Sq shield
		items.add(new SmithingItem(Bar.BRONZE, 3, 4, 4, 9, 1189, 1)); //Kite shield
		items.add(new SmithingItem(Bar.BRONZE, 1, 4, 5, 7, 4119, 1)); //Bronze boots
		
		smithingItems.put(Bar.BRONZE, items);
	}
	
	private static SmithingItem getSmithingItem(int itemId) {
		for (Bar bar : smithingItems.keySet())
			for (SmithingItem item : smithingItems.get(bar))
				if (item.getItemId() == itemId)
					return item;
		return null;
	}
	
	private static Bar getBarForItemId(int item) {
		Bar bar = null;
		
		for (Bar temp : Bar.values())
			if (temp.getId() == item)
				bar = temp;
		
		return bar;
	}
	
	private static String getNotEnoughBarsMessage(SmithingItem item) {
		String message = "You need ";
		
		if (item.getBarAmount() > 1)
			message += item.getBarAmount() + " " + ItemHandler.getItemName(item.getBar().getId()).toLowerCase() + "s";
		else {
			message += "one " + ItemHandler.getItemName(item.getBar().getId()).toLowerCase();
		}
		
		if (item.getAmount() > 1)
			message += " to make some " + ItemHandler.getItemName(item.getItemId()).toLowerCase() + ".";
		else
			message += " to make a(n) " + ItemHandler.getItemName(item.getItemId()).toLowerCase() + ".";
		
		return message;
	}
	
	private static String getSmithingMessage(SmithingItem item) {
		String message = "You smith the " + ItemHandler.getItemName(item.getBar().getId()).toLowerCase();
		
		if (item.getBarAmount() > 1)
			message += "s";
		
		if (item.getAmount() > 1)
			message += " into some " + ItemHandler.getItemName(item.getItemId()).toLowerCase() + ".";
		else
			message += " into a(n) " + ItemHandler.getItemName(item.getItemId()).toLowerCase() + ".";
		
		return message;
	}
}
