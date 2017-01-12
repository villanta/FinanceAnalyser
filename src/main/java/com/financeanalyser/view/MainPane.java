package com.financeanalyser.view;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.model.filemanager.FileManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class MainPane extends AnchorPane {
	private static final Logger LOG = LogManager.getLogger(MainPane.class);

	private static final String MAIN_PANE_FXML_LOCATION = "/com/financeanalyser/view/MainPane.fxml";

	public MainPane() {
		super();

		initialiseConfiguration();
		loadView();
		initialiseFX();
	}

	private void initialiseFX() {
		// TODO Auto-generated method stub

	}

	private void initialiseConfiguration() {
		FileManager manager = new FileManager();
		manager.initialise();
	}

	private void loadView() {
		setPrefSize(1920, 1080);

		FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_PANE_FXML_LOCATION));
		loader.setController(this);
		try {
			AnchorPane pane = loader.load();
			super.getChildren().add(pane);
		} catch (IOException e) {
			LOG.error("Failed to load fxml for main pane.", e);
		}
	}
}