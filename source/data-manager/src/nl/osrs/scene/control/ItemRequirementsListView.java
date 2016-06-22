package nl.osrs.scene.control;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import nl.osrs.item.Item;
import nl.osrs.item.ItemLoader;
import nl.osrs.item.ItemRequirement;
import nl.osrs.scene.item.ItemController;

public class ItemRequirementsListView extends ListView<ItemRequirement> {
	private Item item;
	
	public void initialize(Item item) {
		this.setItem(item);
		
		ArrayList<ItemRequirement> itemRequirements = item.getRequirements();
		
		if (itemRequirements == null)
			return;
		
		ObservableList<ItemRequirement> list = FXCollections.observableArrayList(itemRequirements);
		this.setItems(list);
		
		setListViewDisplay();
	}
	
	private void setListViewDisplay() {
		this.setCellFactory(new Callback<ListView<ItemRequirement>, ListCell<ItemRequirement>>() {
	        @Override
	        public ListCell<ItemRequirement> call(ListView<ItemRequirement> p) {
	            ListCell<ItemRequirement> cell = new ListCell<ItemRequirement>() {
	                @Override
	                protected void updateItem(ItemRequirement itemRequirement, boolean bln) {
	                    super.updateItem(itemRequirement, bln);
	                    
	                    if (itemRequirement != null)
	                    	setText(itemRequirement.getLevel() + " " + ItemLoader.getSkills().get(itemRequirement.getSkill()));
	    	            
	    	            ContextMenu contextMenu = new ContextMenu();
	    	            
	    	            MenuItem deleteCell = new MenuItem("Delete");
	    	            
	    	            deleteCell.setOnAction(e -> {
	    	            	p.getItems().remove(itemRequirement);
	    	            	ItemController.getSelectedItem().removeRequirement(itemRequirement);
	    	            });
	    	            
	    	            contextMenu.getItems().add(deleteCell);
	    	            
	    	            this.setContextMenu(contextMenu);
	                }
	            };
	            
	            cell.setOnMouseClicked(e -> {
	            	if (e.getButton() == MouseButton.SECONDARY)
	            		cell.getContextMenu().show(cell, e.getScreenX(), e.getScreenY());
	            });
	            
	            return cell;
	        }
	    });
	}

	public Item getItem() {
		return item;
	}

	private void setItem(Item item) {
		this.item = item;
	}
	
}
