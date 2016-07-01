package nl.osrs;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import nl.osrs.cachemanager.CacheManager;
import nl.osrs.scene.SceneManager;
import nl.osrs.scene.controllers.ItemManagerController;

public class DataManager extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Data Manager");
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> Platform.exit());
		
		Platform.setImplicitExit(false);
		
		new SceneManager(stage);
		new CacheManager();
		new ItemManagerController().loadScene();
	}
	
	public static void main(String[] args) {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
	    mongoLogger.setLevel(Level.SEVERE);
	    
		launch(args);
	}
	
}
