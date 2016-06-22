package nl.osrs.scene.control;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import nl.osrs.item.Item;
import nl.osrs.item.ItemLoader;

public class ItemListView extends ListView<Item> {
	private FilteredList<Item> filteredList;
	
	public void initialize() {
		ArrayList<Item> items = ItemLoader.getItems();
		
		ObservableList<Item> list = FXCollections.observableArrayList(items);
		
		filteredList = new FilteredList<Item>(list);
		
		this.setItems(filteredList);
		
		setListViewDisplay();
	}
	
	public void setFilter(String filter) {
		if (filter == null)
			filteredList.setPredicate(s -> true);
		else
			filteredList.setPredicate(s -> s.getName().toLowerCase().contains(filter.toLowerCase()) || String.valueOf(s.getId()).toLowerCase().contains(filter.toLowerCase()));
	
	}
	
	private void setListViewDisplay() {
		this.setCellFactory(new Callback<ListView<Item>, ListCell<Item>>() {
	        @Override
	        public ListCell<Item> call(ListView<Item> p) {
	            ListCell<Item> cell = new ListCell<Item>() {
	                @Override
	                protected void updateItem(Item item, boolean bln) {
	                    super.updateItem(item, bln);
	                    
	                    if (item != null)
	                    	setText(item.getId() + ": " + item.getName());
	                    else
	                    	setText("");
	                }
	            };
	            return cell;
	        }
	    });
	}
	
}
