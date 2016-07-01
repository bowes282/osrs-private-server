package nl.osrs.scene.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.util.Callback;
import nl.osrs.cachemanager.CacheManager;
import nl.osrs.cachemanager.cache.ItemDef;
import nl.osrs.item.Item;
import nl.osrs.item.ItemLoader;
import nl.osrs.scene.SceneController;
import nl.osrs.scene.SceneManager;

public class ItemManagerController extends SceneController implements Initializable {
	private static HashMap<Integer, String> equipmentSlots = ItemLoader.getEquipmentSlots();
	//private static HashMap<Integer, String> skills = ItemLoader.getSkills();
	
	private Item selectedItem;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ArrayList<Item> items = new ArrayList<>();
		ItemDef[] itemDefinitions = null;
		
		try {
			itemDefinitions = CacheManager.getItemDefinitions();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Item item : ItemLoader.getItems())
			if (itemDefinitions[item.getId()].name != null)
				items.add(item);
		
		initializeListView(items);
		initializeClearButton();
		initializeSearchField();
		
		specificInformationHolder.setExpandedPane(combatPane);
	}
	
	private void initializeListView(ArrayList<Item> items) {
		ObservableList<Item> list = FXCollections.observableArrayList(items);
		
		FilteredList<Item> filteredList = new FilteredList<Item>(list);
		
		listView.setItems(filteredList);

		listView.setCellFactory(new Callback<ListView<Item>, ListCell<Item>>() {
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
		
		listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
		    public void changed(ObservableValue<? extends Item> observable, Item oldValue, Item newValue) {
		    	selectedItem = listView.getSelectionModel().getSelectedItem();
		    	
		    	displaySelectedItem();
		    }
		});
		
		setListViewFilter(null);
	}
	
	private void setListViewFilter(String filter) {
		FilteredList<Item> filteredList = (FilteredList<Item>) listView.getItems();
		
		if (filter == null)
			filteredList.setPredicate(s -> true);
		else
			filteredList.setPredicate(s -> s.getName().toLowerCase().contains(filter.toLowerCase()) || String.valueOf(s.getId()).toLowerCase().contains(filter.toLowerCase()));
	}
	
	private void initializeSearchField() {
		searchField.textProperty().addListener(change -> {
			String filter = searchField.getText();
			
			if (filter == "")
				filter = null;
			
			setListViewFilter(filter);
		});
	}
	
	private void initializeClearButton() {
		clearButton.setOnAction(change -> {
			searchField.clear();
		});
	}
	
	private void displaySelectedItem() {
		if (selectedItem == null)
			return;

		idField.setText(String.valueOf(selectedItem.getId()));
		
		if (selectedItem.hasName())
			nameField.setText(selectedItem.getName());
		else
			nameField.setText("Unknown item");
		
		if (selectedItem.hasValue())
			valueField.setText(String.valueOf(selectedItem.getValue()));
		else
			valueField.setText("0");
		
		if (selectedItem.hasBonuses()) {
			int[] bonuses = selectedItem.getBonuses();
			
			stabAttackField.setText(String.valueOf(bonuses[0]));
			slashAttackField.setText(String.valueOf(bonuses[1]));
			crushAttackField.setText(String.valueOf(bonuses[2]));
			magicAttackField.setText(String.valueOf(bonuses[3]));
			rangedAttackField.setText(String.valueOf(bonuses[4]));
			stabDefenceField.setText(String.valueOf(bonuses[5]));
			slashDefenceField.setText(String.valueOf(bonuses[6]));
			crushDefenceField.setText(String.valueOf(bonuses[7]));
			magicDefenceField.setText(String.valueOf(bonuses[8]));
			rangedDefenceField.setText(String.valueOf(bonuses[9]));
			meleeStrengthField.setText(String.valueOf(bonuses[10]));
			rangedStrengthField.setText("NaN");
			prayerBonusField.setText(String.valueOf(bonuses[11]));
		} else {
			stabAttackField.setText("0");
			slashAttackField.setText("0");
			crushAttackField.setText("0");
			magicAttackField.setText("0");
			rangedAttackField.setText("0");
			stabDefenceField.setText("0");
			slashDefenceField.setText("0");
			crushDefenceField.setText("0");
			magicDefenceField.setText("0");
			rangedDefenceField.setText("0");
			meleeStrengthField.setText("0");
			rangedStrengthField.setText("NaN");
			prayerBonusField.setText("0");
		}
		
		loadEquipmentSlotField();
		
		checkSynchronization();
	}
	
	private void checkSynchronization() {
		try {
			ItemDef item = CacheManager.getItemDefinitions()[selectedItem.getId()];
			
			if (!item.name.equals(selectedItem.getName()))
				openUnsynchronizedItemNameAlert(item);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void openUnsynchronizedItemNameAlert(ItemDef item) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Synchronization Warning");
		alert.setHeaderText(null);
		alert.setContentText("Item name is '" + selectedItem.getName() + "' server sided and '" + item.name + "' client sided.");

		ButtonType chooseServerName = new ButtonType("Use '" + selectedItem.getName() + "'");
		ButtonType chooseClientName = new ButtonType("Use '" + item.name + "'");
		ButtonType ignoreWarning = new ButtonType("Ignore", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(chooseServerName, chooseClientName, ignoreWarning);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == chooseServerName)
		    System.out.println("Chose server name.");
		else if (result.get() == chooseClientName)
		    System.out.println("Chose client name.");
	}

	private void loadEquipmentSlotField() {
		ArrayList<String> equipmentSlots = new ArrayList<>();
		String selected = "Can not be equipped";
		
		equipmentSlots.add(selected);
		
		for (int i : ItemManagerController.equipmentSlots.keySet()) {
			if (selectedItem.hasEquipmentSlot() && selectedItem.getEquipmentSlot() == i)
				selected = i + ": " + ItemManagerController.equipmentSlots.get(i);
			equipmentSlots.add(i + ": " + ItemManagerController.equipmentSlots.get(i));
		}
		
		equipmentSlotField.setItems(FXCollections.observableArrayList(equipmentSlots));
		equipmentSlotField.getSelectionModel().select(selected);
	}
	
	@Override
	public void loadScene() {
		new LoadingScreenController().loadScene();
		ItemManagerController self = this;
		
		Task<Scene> loadTask = new Task<Scene>() {
			@Override
			protected Scene call() throws Exception {
				ItemLoader.getItems();
				CacheManager.getItemDefinitions();
				return self.loadScene("ItemManager");
			}
		};
		
		loadTask.setOnSucceeded(e -> {
			SceneManager.switchScene(loadTask.getValue());
		});
		
		new Thread(loadTask).start();
	}
	
	@FXML private ListView<Item> listView;
	@FXML private TextField searchField;
	@FXML private Button clearButton;
	
	@FXML private TitledPane generalInformationPane;
		@FXML private TextField idField;
		@FXML private TextField nameField;
		@FXML private TextField valueField;
	
	@FXML private Accordion specificInformationHolder;
		@FXML private TitledPane combatPane;
			@FXML private TitledPane generalCombatPane;
				@FXML private ComboBox<String> equipmentSlotField;
			@FXML private TitledPane bonusesPane;
				@FXML private TextField stabAttackField;
				@FXML private TextField slashAttackField;
				@FXML private TextField crushAttackField;
				@FXML private TextField magicAttackField;
				@FXML private TextField rangedAttackField;
				@FXML private TextField stabDefenceField;
				@FXML private TextField slashDefenceField;
				@FXML private TextField crushDefenceField;
				@FXML private TextField magicDefenceField;
				@FXML private TextField rangedDefenceField;
				@FXML private TextField meleeStrengthField;
				@FXML private TextField rangedStrengthField;
				@FXML private TextField prayerBonusField;
		@FXML private TitledPane interactionsPane;
			@FXML private TextField firstClickField;
			@FXML private TextField secondClickField;
			@FXML private TextField thirdClickField;
			@FXML private TextField useOnItemField;
			@FXML private TextField useOnNpcField;
			@FXML private TextField useOnObjectField;

}
