package nl.osrs.scene.item;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import nl.osrs.item.Item;
import nl.osrs.item.ItemLoader;
import nl.osrs.item.ItemRequirement;
import nl.osrs.scene.SceneController;
import nl.osrs.scene.control.ItemListView;
import nl.osrs.scene.control.ItemRequirementsListView;
import nl.osrs.scene.control.NumberField;

public class ItemController extends SceneController implements Initializable {
	@FXML private ItemListView itemListView;
	
	@FXML private NumberField itemIdField;
	@FXML private TextField itemNameField;
	@FXML private NumberField itemValueField;
	@FXML private ComboBox<String> itemEquipmentSlotsField;
	
	@FXML private Accordion contentPanesContainer;
	@FXML private TitledPane bonusesPane;

	@FXML private NumberField stabAttackField;
	@FXML private NumberField slashAttackField;
	@FXML private NumberField crushAttackField;
	@FXML private NumberField magicAttackField;
	@FXML private NumberField rangedAttackField;
	@FXML private NumberField strengthField;
	
	@FXML private NumberField stabDefenceField;
	@FXML private NumberField slashDefenceField;
	@FXML private NumberField crushDefenceField;
	@FXML private NumberField magicDefenceField;
	@FXML private NumberField rangedDefenceField;
	@FXML private NumberField prayerField;
	
	@FXML private ComboBox<String> skillsField;
	@FXML private NumberField levelField;
	@FXML private Button addRequirementButton;
	
	@FXML private ItemRequirementsListView requirementsView;

	@FXML private NumberField standAnimationField;
	@FXML private NumberField walkAnimationField;
	@FXML private NumberField runAnimationField;
	@FXML private NumberField attackAnimationField;
	@FXML private NumberField blockAnimationField;
	
	@FXML private Button newItemButton;
	@FXML private Button clearButton;
	@FXML private TextField searchField;
	
	@FXML private MenuItem loadMenuItem;
	@FXML private MenuItem saveMenuItem;
	
	private static Item selectedItem;

	@Override public void initialize(URL location, ResourceBundle resources) {
		itemListView.initialize();
		setItemSelection();
		
		itemValueField.setRange(0, Integer.MAX_VALUE);

		stabAttackField.setRange(-100, 200);
		slashAttackField.setRange(-100, 200);
		crushAttackField.setRange(-100, 200);
		magicAttackField.setRange(-100, 200);
		rangedAttackField.setRange(-100, 200);
		strengthField.setRange(-100, 200);

		stabDefenceField.setRange(-100, 200);
		slashDefenceField.setRange(-100, 200);
		crushDefenceField.setRange(-100, 200);
		magicDefenceField.setRange(-100, 200);
		rangedDefenceField.setRange(-100, 200);
		prayerField.setRange(-100, 200);

		levelField.setRange(1, 120);

		searchField.textProperty().addListener(change -> {
			String filter = searchField.getText();
			
			if (filter == "")
				filter = null;
			
			itemListView.setFilter(filter);
		});
		
		clearButton.setOnAction(change -> {
			searchField.clear();
		});
		
		contentPanesContainer.setExpandedPane(bonusesPane);
		
		itemListView.getSelectionModel().select(0);
    }
	
	private void setItemSelection() {
		itemListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
		    public void changed(ObservableValue<? extends Item> observable, Item oldValue, Item newValue) {
		    	int selectedIndex = itemListView.getSelectionModel().getSelectedIndex();
		    	
		    	if (selectedIndex > -1)
		    		selectedItem = ItemLoader.getItems().get(selectedIndex);
		    	else
		    		selectedItem = null;
		    	
		    	displayItem();
		    }
		});
	}

	private void displayItem() {
		if (selectedItem == null)
			return;
		
		itemIdField.setText(selectedItem.getId());
		itemNameField.setText(selectedItem.getName());
		itemValueField.setText(selectedItem.getValue());
		
		ArrayList<String> equipmentSlots = new ArrayList<>();
		
		String current = null;
		
		for (Integer i : ItemLoader.getEquipmentSlots().keySet()) {
			String temp = i + ": " + ItemLoader.getEquipmentSlots().get(i);
			
			if (selectedItem.getEquipmentSlot() == i)
				current = temp;
			
			equipmentSlots.add(temp);
		}
		
		itemEquipmentSlotsField.getItems().setAll(equipmentSlots);
		
		if (current != null)
			itemEquipmentSlotsField.getSelectionModel().select(current);
		else
			itemEquipmentSlotsField.getSelectionModel().select(3);

		stabAttackField.setText(selectedItem.getBonuses()[0]);
		slashAttackField.setText(selectedItem.getBonuses()[1]);
		crushAttackField.setText(selectedItem.getBonuses()[2]);
		magicAttackField.setText(selectedItem.getBonuses()[3]);
		rangedAttackField.setText(selectedItem.getBonuses()[4]);
		strengthField.setText(selectedItem.getBonuses()[5]);

		stabDefenceField.setText(selectedItem.getBonuses()[6]);
		slashDefenceField.setText(selectedItem.getBonuses()[7]);
		crushDefenceField.setText(selectedItem.getBonuses()[8]);
		magicDefenceField.setText(selectedItem.getBonuses()[9]);
		rangedDefenceField.setText(selectedItem.getBonuses()[10]);
		prayerField.setText(selectedItem.getBonuses()[11]);
		
		ArrayList<String> skills = new ArrayList<>();
		
		for (Integer i : ItemLoader.getSkills().keySet())
			skills.add(i + ": " + ItemLoader.getSkills().get(i));
		
		skillsField.getItems().setAll(skills);
		skillsField.getSelectionModel().select(0);
		
		addRequirementButton.setOnAction(e -> addRequirement());
		
		requirementsView.initialize(selectedItem);

		standAnimationField.setText(selectedItem.getStandAnimation());
		walkAnimationField.setText(selectedItem.getWalkAnimation());
		runAnimationField.setText(selectedItem.getRunAnimation());
		attackAnimationField.setText(selectedItem.getAttackAnimation());
		blockAnimationField.setText(selectedItem.getBlockAnimation());
	}
	
	private void addRequirement() {
		int level = 1;
		
		if (levelField.getText() != "")
			level = levelField.getValue();
		
		String[] split = skillsField.getSelectionModel().getSelectedItem().split(": ");
		
		int skill = Integer.parseInt(split[0]);
		
		ItemRequirement itemRequirement = new ItemRequirement(skill, level);
		
		selectedItem.addRequirement(itemRequirement);
		
		requirementsView.initialize(selectedItem);
	}
	
	public static Item getSelectedItem() {
		return selectedItem;
	}

	@Override
	public void loadScene() {
		super.loadScene("resources/item.fxml");
	}
}
