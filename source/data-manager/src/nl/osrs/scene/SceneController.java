package nl.osrs.scene;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public abstract class SceneController {
	public abstract void loadScene();
	
	protected void switchScene(String fxmlFileName) {
		Scene scene = loadScene(fxmlFileName);
		
		SceneManager.switchScene(scene);
	}
	
	public Scene loadScene(String fxmlFileName) {
		Parent root = null;
		
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/" + fxmlFileName + ".fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Scene(root);
	}
}
