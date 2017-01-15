package com.financeanalyser.view.panes;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.model.data.Transaction;
import com.financeanalyser.model.data.TransactionType;
import com.financeanalyser.view.components.RecordsTable;
import com.financeanalyser.view.viewswitchcontroller.FAViewSwitchController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class RecordPane extends AnchorPane {

	@FXML
	private AnchorPane recordTablePane;
	@FXML
	private AnchorPane bottomPane;
	@FXML
	private AnchorPane sidePane;

	private static final Logger LOG = LogManager.getLogger(HomePane.class);
	private static final String RECORD_PANE_FXML_LOCATION = "/com/financeanalyser/view/RecordPane.fxml";
	private FAViewSwitchController viewSwitchController;
	private RecordsTable recordsTable;

	public RecordPane(FAViewSwitchController faViewSwitchController) {
		super();
		setAnchorPaneZero(this);
		this.viewSwitchController = faViewSwitchController;

		loadView();
		initialiseFX();
	}

	private void initialiseFX() {
		recordsTable = new RecordsTable();
		setAnchorPaneZero(recordsTable);
		recordTablePane.getChildren().add(recordsTable);
		
		Transaction transaction = new Transaction(LocalDate.now(), 950, TransactionType.CASH);
		transaction.setName("Withdrawal");
		transaction.setNote("Used for lunch.");
		
		recordsTable.getItems().add(transaction);
	}

	private void loadView() {
		setPrefSize(1920, 1080);

		FXMLLoader loader = new FXMLLoader(getClass().getResource(RECORD_PANE_FXML_LOCATION));
		loader.setController(this);
		try {	
			AnchorPane pane = loader.load();
			setAnchorPaneZero(pane);
			super.getChildren().add(pane);
		} catch (IOException e) {
			LOG.error("Failed to load fxml for main pane.", e);
		}
	}

	private void setAnchorPaneZero(Node pane) {
		AnchorPane.setBottomAnchor(pane, 0.0);
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
	}
}
