package nl.osrs.scene.controllers;

import nl.osrs.scene.SceneController;

public class LoadingScreenController extends SceneController {

	@Override
	public void loadScene() {
		super.switchScene("LoadingScreen");
	}
	
}
