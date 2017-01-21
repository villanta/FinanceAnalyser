package com.financeanalyser.view.components;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.model.data.Record;
import com.financeanalyser.model.data.Transaction;
import com.financeanalyser.model.data.TransactionType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;

public class PieChartPopover extends AnchorPane {

	private static final Logger LOG = LogManager.getLogger(PieChartPopover.class);
	private static final String POPOVER_PANE_FXML_LOCATION = "/com/financeanalyser/view/components/popovers/PieChartPopover.fxml";

	private static final PseudoClass invalidClass = PseudoClass.getPseudoClass("invalid");

	private static final String TOTAL = "Total";
	private static final String MONTHLY_AVERAGE = "Monthly average";
	private static final String WEEKLY_AVERAGE = "Weekly average";

	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;
	@FXML
	private ComboBox<String> typeBox;

	@FXML
	private Button createButton;

	private BooleanProperty startDateInvalid = new SimpleBooleanProperty(false);
	private BooleanProperty endDateInvalid = new SimpleBooleanProperty(false);
	private BooleanProperty typeInvalid = new SimpleBooleanProperty(false);

	private PopupChartListener listener;
	private Record record;

	public PieChartPopover(Record record, PopupChartListener listener) {
		super();
		this.record = record;
		this.listener = listener;
		loadFXML();
		initialiseFX();
	}

	@FXML
	protected void startDateUpdated(ActionEvent event) {
		setStartDateInvalid(startDatePicker.getValue() == null);
	}

	@FXML
	protected void endDateUpdated(ActionEvent event) {
		setEndDateInvalid(endDatePicker.getValue() == null);
	}

	@FXML
	protected void typeUpdated(ActionEvent event) {
		setComboBoxInvalid(typeBox.getSelectionModel().getSelectedItem() == null);
	}

	@FXML
	protected void createAction(ActionEvent event) {
		if (!startDateInvalid.get() || endDateInvalid.get() || typeInvalid.get()) {
			LocalDate startDate = startDatePicker.getValue();
			LocalDate endDate = endDatePicker.getValue();

			switch (typeBox.getSelectionModel().getSelectedItem()) {
			case TOTAL:
				createTotalChart(startDate, endDate);
				break;
			case MONTHLY_AVERAGE:
				createMonthlyChart(startDate, endDate);
				break;
			case WEEKLY_AVERAGE:
				createWeeklyChart(startDate, endDate);
				break;
			default:
				setComboBoxInvalid(true);
				break;
			}
		}
		event.consume();
	}

	private void createWeeklyChart(LocalDate startDate, LocalDate endDate) {

	}

	private List<Transaction> getDateFilteredRecord(LocalDate startDate, LocalDate endDate) {
		List<Transaction> dateFilteredRecord = record.getRecord().stream().filter(transaction -> {
			return !transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate);
		}).collect(Collectors.toList());
		return dateFilteredRecord;
	}

	private void createMonthlyChart(LocalDate startDate, LocalDate endDate) {
		// TODO Auto-generated method stub

	}

	private void createTotalChart(LocalDate startDate, LocalDate endDate) {
		List<Transaction> dateFilteredRecord = getDateFilteredRecord(startDate, endDate);

		Map<String, Integer> typeAmmountMap = new HashMap<>();

		for (TransactionType type : TransactionType.getAllTypes()) {
			Stream<Transaction> matchingTransactionTypeStream = dateFilteredRecord.stream()
					.filter(transaction -> type == transaction.getType());

			matchingTransactionTypeStream = dateFilteredRecord.stream()
					.filter(transaction -> type.equals(transaction.getType()));
			if (!(matchingTransactionTypeStream.count() == 0)) {
				matchingTransactionTypeStream = dateFilteredRecord.stream()
						.filter(transaction -> type.equals(transaction.getType()));
				int totalAmmount = matchingTransactionTypeStream.mapToInt(t -> t.getCentAmount()).sum();
				typeAmmountMap.put(type.toString(), totalAmmount);
			}
		}

		int salaryBasic = typeAmmountMap.getOrDefault(TransactionType.SALARY_BASIC.toString(), 0);
		typeAmmountMap.remove(TransactionType.SALARY_BASIC.toString());

		int salaryBonus = typeAmmountMap.getOrDefault(TransactionType.SALARY_BONUS.toString(), 0);
		typeAmmountMap.remove(TransactionType.SALARY_BONUS.toString());

		int totalSpent = 0;
		for (String type : typeAmmountMap.keySet()) {
			totalSpent += typeAmmountMap.get(type);
		}

		int totalUnspent = salaryBasic + salaryBonus - totalSpent;
		typeAmmountMap.put("Unspent", totalUnspent);

		createPieChartWithData(typeAmmountMap, "Total spending");
	}

	private void createPieChartWithData(Map<String, Integer> typeAmmountMap, String title) {
		PieChart chart = new PieChart();
		chart.setTitle(title);
		List<Data> data = typeAmmountMap.entrySet().stream().map(entry -> new Data(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
		chart.getData().addAll(data);

		// chart.setHe

		listener.setChartToDisplay(chart);
	}

	private void setStartDateInvalid(boolean invalid) {
		startDateInvalid.set(invalid);
		startDatePicker.pseudoClassStateChanged(invalidClass, invalid);
	}

	private void setEndDateInvalid(boolean invalid) {
		endDateInvalid.set(invalid);
		endDatePicker.pseudoClassStateChanged(invalidClass, invalid);
	}

	private void setComboBoxInvalid(boolean invalid) {
		typeInvalid.set(invalid);
		typeBox.pseudoClassStateChanged(invalidClass, invalid);
	}

	private void loadFXML() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(POPOVER_PANE_FXML_LOCATION));
		loader.setController(this);
		try {
			AnchorPane pane = loader.load();
			setAnchorPaneZero(pane);
			super.getChildren().add(pane);
			pane.autosize();
			super.autosize();
		} catch (IOException e) {
			LOG.error("Failed to load fxml for main pane.", e);
		}
	}

	private void initialiseFX() {
		initialiseComboBox();
	}

	private void initialiseComboBox() {
		typeBox.getItems().add(TOTAL);
		typeBox.getItems().add(MONTHLY_AVERAGE);
		typeBox.getItems().add(WEEKLY_AVERAGE);
	}

	private void setAnchorPaneZero(Node pane) {
		AnchorPane.setBottomAnchor(pane, 10.0);
		AnchorPane.setTopAnchor(pane, 10.0);
		AnchorPane.setLeftAnchor(pane, 10.0);
		AnchorPane.setRightAnchor(pane, 10.0);
	}
}