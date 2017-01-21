package com.financeanalyser.view.panes;

import java.io.IOException;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.model.data.Record;
import com.financeanalyser.model.data.Transaction;
import com.financeanalyser.view.AdvancedFilterListener;
import com.financeanalyser.view.components.AdvancedFilter;
import com.financeanalyser.view.components.ChartPane;
import com.financeanalyser.view.components.RecordsTable;
import com.financeanalyser.view.components.TransactionCreationBar;
import com.financeanalyser.view.components.TransactionCreationBarListener;
import com.financeanalyser.view.viewswitchcontroller.FAViewSwitchController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class RecordPane extends AnchorPane implements TransactionCreationBarListener, AdvancedFilterListener {

	private static final int NOTE_COLUMN_ID = 4;
	private static final int NAME_COLUMN_ID = 3;
	private static final int DATE_COLUMN_ID = 2;
	private static final int AMMOUNT_COLUMN_ID = 1;
	private static final int TYPE_COLUMN_ID = 0;
	private static final Logger LOG = LogManager.getLogger(HomePane.class);
	private static final String RECORD_PANE_FXML_LOCATION = "/com/financeanalyser/view/RecordPane.fxml";

	@FXML
	private AnchorPane recordTablePane;
	@FXML
	private AnchorPane bottomPane;
	@FXML
	private AnchorPane sidePane;

	@FXML
	private TextField typeFilterField;
	@FXML
	private TextField amountFilterField;
	@FXML
	private TextField dateFilterField;
	@FXML
	private TextField nameFilterField;
	@FXML
	private TextField noteFilterField;

	@FXML
	private Label netLabel;
	@FXML
	private Label netValueLabel;

	@FXML
	private TransactionCreationBar bar;

	@FXML
	private AdvancedFilter filterPane;
	
	@FXML
	private ChartPane chartPane;

	private FAViewSwitchController viewSwitchController;
	private RecordsTable recordsTable;
	private Record record;

	public RecordPane(FAViewSwitchController faViewSwitchController, Record record) {
		super();
		setAnchorPaneZero(this);
		this.viewSwitchController = faViewSwitchController;
		this.record = record;

		loadFXML();
		initialiseFX();
	}

	@Override
	public void addRecord(Transaction transaction) {
		record.addTransaction(transaction);
		updateRecordsTable();
	}

	@FXML
	public void saveRecordAction(ActionEvent event) {
		if (viewSwitchController.getFileManager().saveRecord(record, viewSwitchController)) {
			// TODO
		} else {
			// TODO
		}
		event.consume();
	}

	@FXML
	public void typeFilterUpdate(KeyEvent event) {
		String filter = typeFilterField.getText();
		record.setTypeFilter(filter);
		updateRecordsTable();
	}

	@Override
	public void updateAdvancedFilter(FilterType type, Function<Transaction, Boolean> filterFunction) {
		if (FilterType.DATE.equals(type)) {
			record.setAdvancedDateFilterFunction(filterFunction);
		} else {
			record.setAdvancedAmmountFilterFunction(filterFunction);
		}
		updateRecordsTable();
	}

	@FXML
	public void amountFilterUpdate(KeyEvent event) {
		String filter = amountFilterField.getText();
		record.setAmountFilter(filter);
		updateRecordsTable();
	}

	@FXML
	public void dateFilterUpdate(KeyEvent event) {
		String filter = dateFilterField.getText();
		record.setDateFilter(filter);
		updateRecordsTable();
	}

	@FXML
	public void nameFilterUpdate(KeyEvent event) {
		String filter = nameFilterField.getText();
		record.setNameFilter(filter);
		updateRecordsTable();
	}

	@FXML
	public void noteFilterUpdate(KeyEvent event) {
		String filter = noteFilterField.getText();
		record.setNoteFilter(filter);
		updateRecordsTable();
	}

	private void initialiseFX() {
		initialiseTable();
		initialiseFilters();
		bar.addListener(this);
		initialiseNetLabels();
		initialiseFilterPane();
		initialiseChartPane();
		updateRecordsTable();
	}

	private void initialiseChartPane() {
		chartPane.setRecord(record);
	}

	private void initialiseFilterPane() {
		filterPane.setListener(this);
	}

	private void initialiseNetLabels() {
		netLabel.prefWidthProperty()
				.bind(recordsTable.getColumns().get(TYPE_COLUMN_ID).widthProperty()
						.add(recordsTable.getColumns().get(AMMOUNT_COLUMN_ID).widthProperty())
						.add(recordsTable.getColumns().get(DATE_COLUMN_ID).widthProperty()));
		netValueLabel.prefWidthProperty().bind(recordsTable.getColumns().get(NAME_COLUMN_ID).widthProperty());
	}

	private void initialiseFilters() {
		typeFilterField.prefWidthProperty().bind(recordsTable.getColumns().get(TYPE_COLUMN_ID).widthProperty());
		amountFilterField.prefWidthProperty().bind(recordsTable.getColumns().get(AMMOUNT_COLUMN_ID).widthProperty());
		dateFilterField.prefWidthProperty().bind(recordsTable.getColumns().get(DATE_COLUMN_ID).widthProperty());
		nameFilterField.prefWidthProperty().bind(recordsTable.getColumns().get(NAME_COLUMN_ID).widthProperty());
		noteFilterField.prefWidthProperty().bind(recordsTable.getColumns().get(NOTE_COLUMN_ID).widthProperty());
	}

	private void updateRecordsTable() {
		recordsTable.setItems(record.getFilteredRecord());
		updateNetValue();
	}

	private void updateNetValue() {
		int total = record.getFilteredRecord().stream().mapToInt(transaction -> transaction.getCentAmount()).sum();
		String readable = String.format("%.2f", total / 100.0);
		netValueLabel.setText(readable);
	}

	private void initialiseTable() {
		recordsTable = new RecordsTable();
		setAnchorPaneZero(recordsTable);
		recordTablePane.getChildren().add(recordsTable);
	}

	private void loadFXML() {
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
