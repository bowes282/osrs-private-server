package nl.osrs.scene;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public abstract class SceneController {
	public abstract void loadScene();
	
	protected void loadScene(String url) {
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource(url));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root);
		
		SceneManager.switchScene(scene);
	}
}
