package nl.osrs.scene;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
	private static Stage stage;
	
	public SceneManager(Stage stage) {
		SceneManager.stage = stage;
	}
	
	public static Stage getStage() {
		return stage;
	}
	
	public static void switchScene(Scene scene) {
		SceneManager.stage.setScene(scene);
		SceneManager.stage.show();
	}
}
