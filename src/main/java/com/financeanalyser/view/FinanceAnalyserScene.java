package com.financeanalyser.view;

import com.financeanalyser.model.filemanager.FileManager;
import com.financeanalyser.view.viewswitchcontroller.FAViewSwitchController;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class FinanceAnalyserScene extends Scene {

	private static final String CSS_LOCATION = "/com/financeanalyser/css/style.css";
	private FAViewSwitchController viewSwitchController;

	public FinanceAnalyserScene() {
		super(new AnchorPane());
		initialiseViewSwitchController();
		initialiseConfiguration();

		initialiseScene();

		initialiseHomeScreen();
	}

	private void initialiseConfiguration() {
		FileManager manager = new FileManager();
		manager.initialise();
		
		viewSwitchController.setFileManager(manager);
	}

	private void initialiseViewSwitchController() {
		viewSwitchController = new FAViewSwitchController(this);
	}

	private void initialiseScene() {
		getStylesheets().add(loadStyle());
	}

	private void initialiseHomeScreen() {
		viewSwitchController.homeScreen();
	}

	private String loadStyle() {
		return getClass().getResource(CSS_LOCATION).toExternalForm();
	}
}
