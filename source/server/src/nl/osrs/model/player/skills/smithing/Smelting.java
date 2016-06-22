package nl.osrs.model.player.skills.smithing;

import nl.osrs.model.item.ItemHandler;
import nl.osrs.model.player.Client;
import nl.osrs.model.player.PlayerAssistant;
import nl.osrs.task.Task;
import nl.osrs.util.Misc;

public class Smelting {
	
	public static void startSmelting(Client client, int actionButton) {
		Bar bar = Bar.getBar(actionButton);
		
		if (bar == null)
			return;
		
		client.getTaskScheduler().schedule(new Task(6, true) {
			@Override
			protected void execute() {
				if (!smeltingPrerequisites(client, bar, true)) {
					this.stop();
					return;
				}
				
				client.startAnimation(899);
				
				String smeltMessage = "You smelt the " + ItemHandler.getItemName(bar.getRequirement().getRequiredOre1()).toLowerCase();
				
				if (bar.getRequirement().getRequiredOre2() != null)
					smeltMessage += " and the " + ItemHandler.getItemName(bar.getRequirement().getRequiredOre2()).toLowerCase();
				
				client.sendMessage(smeltMessage + " ...");
				
				try {
					Thread.sleep(2100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				client.getItems().deleteItem(bar.getRequirement().getRequiredOre1(), bar.getRequirement().getRequiredOreAmount1());
				if (bar.getRequirement().getRequiredOre2() != null && bar.getRequirement().getRequiredOreAmount2() != null)
					client.getItems().deleteItem(bar.getRequirement().getRequiredOre2(), bar.getRequirement().getRequiredOreAmount2());
				
				if (bar == Bar.IRON && Misc.random(1) < 1) {
					client.sendMessage("... but you are unsuccessful in retrieving an iron bar.");
					return;
				}
				
				client.getItems().addItem(bar.getId(), 1);
				client.getItems();
				client.sendMessage("... and retrieve a " + ItemHandler.getItemName(bar.getId()).toLowerCase() + ".");
				client.getPA().addSkillXP(bar.getExp(), client.playerSmithing);

				if (!smeltingPrerequisites(client, bar, false)) {
					this.stop();
					return;
				}
			}
			
			@Override
			public void onStop() {
				client.startAnimation(65535);
			}
		});
	}
	
	private static boolean smeltingPrerequisites(Client client, Bar bar, boolean printMessage) {
		boolean success = true;
		
		if (!client.getItems().playerHasItem(bar.getRequirement().getRequiredOre1(), bar.getRequirement().getRequiredOreAmount1())) {
			if (printMessage) {
				client.getItems();
				client.sendMessage("You need " + bar.getRequirement().getRequiredOreAmount1() + " " + ItemHandler.getItemName(bar.getRequirement().getRequiredOre1()).toLowerCase() + " to smelt a " + bar.name().toLowerCase() + " bar.");
			}
			success = false;
		}
		
		if (bar.getRequirement().getRequiredOre2() != null && bar.getRequirement().getRequiredOreAmount2() != null)
			if (!client.getItems().playerHasItem(bar.getRequirement().getRequiredOre2(), bar.getRequirement().getRequiredOreAmount2())) {
				if (printMessage) {
					client.getItems();
					client.sendMessage("You need " + bar.getRequirement().getRequiredOreAmount2() + " " + ItemHandler.getItemName(bar.getRequirement().getRequiredOre2()).toLowerCase() + " to smelt a " + bar.name().toLowerCase() + " bar.");
				}
				success = false;
			}
		
		if (PlayerAssistant.getLevelForXP(client.playerXP[13]) < bar.getRequirement().getLevel()) {
			if (printMessage)
				client.sendMessage("You need a smithing level of " + bar.getRequirement().getLevel() + " to smelt a " + bar.name().toLowerCase() + " bar.");
			success = false;
		}
		
		return success;
	}
	
}
