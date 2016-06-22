package nl.osrs.scene.landing;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import nl.osrs.scene.SceneController;
import nl.osrs.scene.item.ItemController;

public class LandingController extends SceneController implements Initializable {
	@FXML private Button button;
	
	@FXML private void handleClick(ActionEvent event) {
		new ItemController().loadScene();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	@Override
	public void loadScene() {
		super.loadScene("resources/landing.fxml");
	}
}
