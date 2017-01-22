package com.financeanalyser.view.components.popovers;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;

import com.financeanalyser.model.data.Record;
import com.financeanalyser.model.data.Transaction;
import com.financeanalyser.model.data.TransactionType;
import com.financeanalyser.view.components.listeners.PopupChartListener;

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
import javafx.scene.control.Label;
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
		PopOver popover = ((PopOver) getScene().getWindow());
		popover.hide();
		event.consume();
	}

	private List<Transaction> getDateFilteredRecord(LocalDate startDate, LocalDate endDate) {
		return record.getFilteredRecord().stream().filter(transaction -> {
			return !transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate);
		}).collect(Collectors.toList());
	}

	private void createWeeklyChart(LocalDate startDate, LocalDate endDate) {
		List<Transaction> dateFilteredRecord = getDateFilteredRecord(startDate, endDate);
		if (dateFilteredRecord.isEmpty()) {
			returnWithFailMessage();
			return;
		}
		Map<String, Integer> typeAmmountMap = getTotalsByType(dateFilteredRecord);
		addUnspentType(typeAmmountMap);

		final double weekCount = (double) getWeeksCount(dateFilteredRecord);
		divideTotalBy(typeAmmountMap, weekCount);
		createPieChartWithData(typeAmmountMap, "Average weekly spending");
	}

	private void createMonthlyChart(LocalDate startDate, LocalDate endDate) {
		List<Transaction> dateFilteredRecord = getDateFilteredRecord(startDate, endDate);
		if (dateFilteredRecord.isEmpty()) {
			returnWithFailMessage();
			return;
		}
		Map<String, Integer> typeAmmountMap = getTotalsByType(dateFilteredRecord);

		addUnspentType(typeAmmountMap);
		final double monthCount = getMonthCount(dateFilteredRecord);
		divideTotalBy(typeAmmountMap, monthCount);
		createPieChartWithData(typeAmmountMap, "Average monthly spending");
	}

	private void createTotalChart(LocalDate startDate, LocalDate endDate) {
		List<Transaction> dateFilteredRecord = getDateFilteredRecord(startDate, endDate);
		if (dateFilteredRecord.isEmpty()) {
			returnWithFailMessage();
			return;
		}
		Map<String, Integer> typeAmmountMap = getTotalsByType(dateFilteredRecord);

		addUnspentType(typeAmmountMap);

		createPieChartWithData(typeAmmountMap, "Total spending");
	}

	private void returnWithFailMessage() {
		if (listener != null) {
			listener.setChartToDisplay(new Label("Failed to create chart with provided data."));
		}
		return;
	}

	private Map<String, Integer> getTotalsByType(List<Transaction> dateFilteredRecord) {
		Map<String, Integer> typeAmmountMap = new HashMap<>();

		for (TransactionType type : TransactionType.getAllTypes()) {
			List<Transaction> dateAndTypeFiltered = dateFilteredRecord.stream()
					.filter(transaction -> type.equals(transaction.getType())).collect(Collectors.toList());
			if (!dateAndTypeFiltered.isEmpty()) {
				int totalAmmount = dateAndTypeFiltered.stream().mapToInt(t -> t.getCentAmount()).sum();
				typeAmmountMap.put(type.toString(), totalAmmount);
			}
		}
		return typeAmmountMap;
	}

	private void createPieChartWithData(Map<String, Integer> typeAmmountMap, String title) {
		PieChart chart = new PieChart();
		chart.setTitle(title);
		List<Data> data = typeAmmountMap.entrySet().stream()
				.map(entry -> new Data(
						entry.getKey().concat(String.format(": %.2f", (Math.abs(entry.getValue() / 100.0)))),
						Math.abs(entry.getValue())))
				.collect(Collectors.toList());
		chart.getData().addAll(data);

		listener.setChartToDisplay(chart);
	}

	private void addUnspentType(Map<String, Integer> typeAmmountMap) {
		int unspent = typeAmmountMap.entrySet().stream().mapToInt(entry -> entry.getValue()).sum();

		typeAmmountMap.remove(TransactionType.SALARY_BASIC.toString());
		typeAmmountMap.remove(TransactionType.SALARY_BONUS.toString());
		typeAmmountMap.remove(TransactionType.TRANSFER_IN.toString());

		typeAmmountMap.put("Unspent", unspent);
	}

	private void divideTotalBy(Map<String, Integer> typeAmmountMap, final double amountToDivideBy) {
		typeAmmountMap.entrySet().stream().forEach(
				entry -> typeAmmountMap.put(entry.getKey(), (int) ((double) entry.getValue() / amountToDivideBy)));
	}

	private int getWeeksCount(List<Transaction> dateFilteredRecord) {
		LocalDate opStartDate = dateFilteredRecord.stream().sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate()))
				.findFirst().get().getDate();

		LocalDate opEndDate = dateFilteredRecord.stream().sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
				.findFirst().get().getDate();

		int weeks = 0;
		if (DayOfWeek.MONDAY.equals(opEndDate.getDayOfWeek())) {
			weeks++;
		}
		while (!(opEndDate = opEndDate.minusDays(1)).isBefore(opStartDate)) {
			if (DayOfWeek.MONDAY.equals(opEndDate.getDayOfWeek())) {
				weeks++;
			}
		}
		if (!DayOfWeek.MONDAY.equals(opEndDate.getDayOfWeek())) {
			weeks++;
		}

		LOG.info("Weeks: " + weeks);
		return weeks;
	}

	private double getMonthCount(List<Transaction> dateFilteredRecord) {
		LocalDate opStartDate = dateFilteredRecord.stream().sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate()))
				.findFirst().get().getDate();

		LocalDate opEndDate = dateFilteredRecord.stream().sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
				.findFirst().get().getDate();

		int days = 0;
		while (!(opEndDate = opEndDate.minusDays(1)).isBefore(opStartDate)) {
			days++;
		}

		LOG.info("Days: " + days);
		return days / 30.5;
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