package com.financeanalyser.view.panes;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.view.viewswitchcontroller.FAViewSwitchController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class HomePane extends AnchorPane {
	private static final Logger LOG = LogManager.getLogger(HomePane.class);

	private static final String MAIN_PANE_FXML_LOCATION = "/com/financeanalyser/view/MainPane.fxml";

	private FAViewSwitchController viewSwitchController;

	public HomePane(FAViewSwitchController faViewSwitchController) {
		super();
		this.viewSwitchController = faViewSwitchController;

		loadView();
		initialiseFX();
	}
	
	@FXML
	public void createRecordAction(ActionEvent event) {
		System.err.println("Create");
		viewSwitchController.recordOverviewScreen();
		viewSwitchController.showCreateRecordDialog();
		event.consume();
	}
	
	@FXML
	public void loadRecordAction(ActionEvent event) {
		System.err.println("Load");
		event.consume();
	}

	private void initialiseFX() {
		// TODO Auto-generated method stub

	}

	private void loadView() {
		setPrefSize(1920, 1080);

		FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_PANE_FXML_LOCATION));
		loader.setController(this);
		try {
			AnchorPane pane = loader.load();
			AnchorPane.setBottomAnchor(pane, 0.0);
			AnchorPane.setTopAnchor(pane, 0.0);
			AnchorPane.setLeftAnchor(pane, 0.0);
			AnchorPane.setRightAnchor(pane, 0.0);
			super.getChildren().add(pane);
		} catch (IOException e) {
			LOG.error("Failed to load fxml for main pane.", e);
		}
	}
}