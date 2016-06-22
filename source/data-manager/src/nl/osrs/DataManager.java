package nl.osrs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.osrs.scene.SceneManager;

public class DataManager extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/landing.fxml"));
		Scene scene = new Scene(root);
		
		stage.setTitle("Data Manager");
		stage.setResizable(false);
		new SceneManager(stage);
		SceneManager.switchScene(scene);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
