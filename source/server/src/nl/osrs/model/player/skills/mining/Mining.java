package nl.osrs.model.player.skills.mining;

import java.util.ArrayList;

import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.object.Object;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.Player;
import nl.osrs.model.player.PlayerAssistant;
import nl.osrs.task.Task;

public class Mining {
	
	public static void startMining(Client client) {
		Object object = new Object(client.objectId, client.objectX, client.objectY);
		
		Ore ore = getOre(object.getId());
		
		if (ore == null)
			return;
		
		Pickaxe bestPickaxe = getBestUsablePickaxe(client);
		
		if (!miningPrerequisites(client, bestPickaxe, ore))
			return;
		
		if (client.isMining())
			return;
		
		client.getTaskScheduler().stopTasks();
		
		client.setMining(true);
		
		client.repeatAnimation(bestPickaxe.getAnimation(), 16, object.getX(), object.getY());
		
		client.sendMessage("You swing your pickaxe at the rock...");
		
		client.getTaskScheduler().schedule(new Task(getTicksUntilNextExecute()) {

			@Override
			protected void execute() {
				if (!miningPrerequisites(client, bestPickaxe, ore) || !client.isMining()) {
					client.getTaskScheduler().stopTasks();
					return;
				}

				client.getItems();
				client.sendMessage("... and manage to mine some " + ItemHandler.getItemName(ore.getItem()).toLowerCase() + ".");
				client.getItems().addItem(ore.getItem(), 1);
				client.getPA().addSkillXP((int) ore.getExp(), client.playerMining);

				if (!miningPrerequisites(client, bestPickaxe, ore))
					client.getTaskScheduler().stopTasks();
			}
			
			@Override
			public void onStop() {
				client.setMining(false);
			}
			
		});
	}
	
	private static boolean miningPrerequisites(Client client, Pickaxe bestPickaxe, Ore ore) {
		boolean success = true;
		
		if (bestPickaxe == null) {
			client.sendMessage("You don't have a pickaxe you can use.");
			success = false;
		} else if (!bestPickaxe.isBetterThanOrEqualTo(ore.getPickaxe())) {
			client.getItems();
			client.sendMessage("You need at least a(n) " + ItemHandler.getItemName(ore.getPickaxe().getItem()).toLowerCase() + " to mine " + ore.name().toLowerCase() + ".");
			success = false;
		}
		
		if (PlayerAssistant.getLevelForXP(client.playerXP[client.playerMining]) < ore.getLevel()) {
			client.sendMessage("You need a mining level of " + ore.getLevel() + " to mine " + ore.name().toLowerCase() + ".");
			success = false;
		}
		
		if (client.getItems().freeSlots() < 1) {
			client.sendMessage("No more space in your inventory");
			success = false;
		}
		
		return success;
	}
	
	public static int getTicksUntilNextExecute() {
		return 6;
	}
	
	private static Pickaxe getBestUsablePickaxe(Client client) {
		ArrayList<Pickaxe> pickaxes = new ArrayList<>();
		Pickaxe bestPickaxe = null;

		Pickaxe temp = null;
		if ((temp = Pickaxe.getPickaxe(client.playerEquipment[Player.playerWeapon])) != null)
			if (PlayerAssistant.getLevelForXP(client.playerXP[client.playerMining]) >= temp.getLevel())
				pickaxes.add(temp);
		
		for (Pickaxe pickaxe : Pickaxe.values())
			if (client.getItems().playerHasItem(pickaxe.getItem()))
				if (PlayerAssistant.getLevelForXP(client.playerXP[client.playerMining]) >= pickaxe.getLevel())
					pickaxes.add(pickaxe);
		
		for (Pickaxe pickaxe : pickaxes)
			if (pickaxe.isBetterThanOrEqualTo(bestPickaxe))
				bestPickaxe = pickaxe;
		
		return bestPickaxe;
	}
	
	private static Ore getOre(int object) {
		switch (object) {
		case 2090:
		case 2091:
			return Ore.COPPER;
			
		case 2094:
		case 2095:
			return Ore.TIN;
			
		case 2092:
		case 2093:
			return Ore.IRON;
			
		case 2096:
		case 2097:
			return Ore.COAL;
			
		case 2102:
		case 2103:
			return Ore.MITHRIL;
			
		case 2104:
		case 2105:
			return Ore.ADAMANTITE;
			
		case 2106:
		case 2107:
			return Ore.RUNITE;
		
		default:
			return null;
		}
	}
	
}
