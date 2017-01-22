package com.financeanalyser.view.components.popovers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class LineChartPopover extends AnchorPane {

	private static final String UNSPENT = "Unspent";
	private static final Logger LOG = LogManager.getLogger(LineChartPopover.class);
	private static final String POPOVER_PANE_FXML_LOCATION = "/com/financeanalyser/view/components/popovers/LineChartPopover.fxml";

	private static final PseudoClass invalidClass = PseudoClass.getPseudoClass("invalid");

	private static final String MONTHLY = "Monthly";
	private static final String WEEKLY = "Weekly";

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

	public LineChartPopover(Record record, PopupChartListener listener) {
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
			case MONTHLY:
				createMonthlyChart(startDate, endDate);
				break;
			case WEEKLY:
				createWeeklyChart(startDate, endDate);
				break;
			default:
				setComboBoxInvalid(true);
				break;
			}
		}
		PopOver popover = (PopOver) getScene().getWindow();
		popover.hide();
		event.consume();
	}

	private List<Transaction> getDateFilteredRecord(LocalDate startDate, LocalDate endDate) {
		return record.getFilteredRecord().stream().filter(
				transaction -> !transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate))
				.collect(Collectors.toList());
	}

	private void createWeeklyChart(LocalDate startDate, LocalDate endDate) {
		List<Transaction> dateFilteredRecord = getDateFilteredRecord(startDate, endDate);
		if (dateFilteredRecord.isEmpty()) {
			returnWithFailMessage();
			return;
		}
		Map<String, List<SimpleEntry<String, Integer>>> typeAmmountMap = getPeriodTotalsByType(dateFilteredRecord);
		if (typeAmmountMap == null) {
			return;
		}
		createLineChartWithData(typeAmmountMap, "Average weekly spending");
	}

	private void createMonthlyChart(LocalDate startDate, LocalDate endDate) {
		List<Transaction> dateFilteredRecord = getDateFilteredRecord(startDate, endDate);
		if (dateFilteredRecord.isEmpty()) {
			returnWithFailMessage();
			return;
		}
		Map<String, List<SimpleEntry<String, Integer>>> typeAmmountMap = getPeriodTotalsByType(dateFilteredRecord);
		if (typeAmmountMap == null) {
			return;
		}
		createLineChartWithData(typeAmmountMap, "Average monthly spending");
	}

	private void returnWithFailMessage() {
		if (listener != null) {
			listener.setChartToDisplay(new Label("Failed to create chart with provided data."));
		}
		return;
	}

	private Map<String, List<SimpleEntry<String, Integer>>> getPeriodTotalsByType(
			List<Transaction> dateFilteredRecord) {
		if (MONTHLY.equals(typeBox.getSelectionModel().getSelectedItem())) {
			return getMonthlyValues(dateFilteredRecord);
		} else {
			// TODO return getWeeklyValues(dateFilteredRecord);
			return null;
		}
	}

	private Map<String, List<SimpleEntry<String, Integer>>> getMonthlyValues(List<Transaction> dateFilteredRecord) {
		Map<String, List<SimpleEntry<String, Integer>>> map = new HashMap<>();

		LocalDate startDate = dateFilteredRecord.stream().sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate()))
				.findFirst().get().getDate();
		LocalDate endDate = dateFilteredRecord.stream().sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
				.findFirst().get().getDate();

		List<String> dates = getMonthStringList(startDate, endDate);

		for (TransactionType type : TransactionType.getAllTypes()) {
			List<SimpleEntry<String, Integer>> list = new ArrayList<>();

			List<Transaction> dateAndTypeFiltered = dateFilteredRecord.stream()
					.filter(transaction -> type.equals(transaction.getType())).collect(Collectors.toList());
			List<Integer> monthlyTotals = new ArrayList<>();

			LocalDate startDateCopy = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), 1);
			while (startDateCopy.isBefore(endDate)) {
				LocalDate newStartDate = LocalDate.of(startDateCopy.getYear(), startDateCopy.getMonth(),
						startDateCopy.getDayOfMonth());
				LocalDate newEndDate = startDateCopy
						.plusDays(startDateCopy.getMonth().length(startDateCopy.isLeapYear()) - (long) 1);

				List<Transaction> monthTransactions = dateAndTypeFiltered.stream()
						.filter(t -> !t.getDate().isBefore(newStartDate) && !t.getDate().isAfter(newEndDate))
						.collect(Collectors.toList());
				if (monthTransactions.isEmpty()) {
					monthlyTotals.add(0);
				} else {
					int sum = monthTransactions.stream().mapToInt(t -> t.getCentAmount()).sum();
					monthlyTotals.add(sum);
				}
				startDateCopy = newEndDate.plusDays(1);
			}

			if (dates.size() == monthlyTotals.size()) {
				for (int i = 0; i < dates.size(); i++) {
					list.add(new SimpleEntry<String, Integer>(dates.get(i), monthlyTotals.get(i)));
				}
			}

			map.put(type.toString(), list);
		}

		addUnspentTypeMonthly(map, dates);

		return map;
	}

	private List<String> getMonthStringList(LocalDate startDate, LocalDate endDate) {
		List<String> months = new ArrayList<>();

		LocalDate startDateCopy = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), 1);
		while (startDateCopy.isBefore(endDate)) {
			months.add(startDateCopy.getMonth().toString() + " " + startDateCopy.getYear());
			startDateCopy = startDateCopy.plusDays(startDateCopy.getMonth().length(startDateCopy.isLeapYear()));
		}

		return months;
	}

	private void createLineChartWithData(Map<String, List<SimpleEntry<String, Integer>>> typeAmmountMap, String title) {
		LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
		chart.setTitle(title);
		ObservableList<Series<String, Number>> data = FXCollections.observableArrayList();

		typeAmmountMap.entrySet().stream().forEach(seriesSet -> {
			Series<String, Number> series = new Series<>();

			series.setName(seriesSet.getKey());

			List<SimpleEntry<String, Integer>> valList = seriesSet.getValue();
			valList.stream().forEach(dateValPair -> {
				if (seriesSet.getKey().equals(UNSPENT)) {
					series.getData()
							.add(new Data<String, Number>(dateValPair.getKey(), dateValPair.getValue() / 100.0));
				} else {
					series.getData().add(
							new Data<String, Number>(dateValPair.getKey(), Math.abs(dateValPair.getValue() / 100.0)));
				}
			});

			data.add(series);
		});
		chart.setData(data);

		listener.setChartToDisplay(chart);
	}

	private void addUnspentTypeMonthly(Map<String, List<SimpleEntry<String, Integer>>> typeAmmountMap,
			List<String> dates) {
		List<SimpleEntry<String, Integer>> unspentList = new ArrayList<>();

		for (int i = 0; i < dates.size(); i++) {
			IntegerProperty unspentMonthlyVal = new SimpleIntegerProperty(0);
			final int index = i;
			typeAmmountMap.entrySet().stream().forEach(entry -> entry.getValue().stream().forEach(simpleEntry -> {
				if (simpleEntry.getKey().equals(dates.get(index))) {
					unspentMonthlyVal.set(unspentMonthlyVal.get() + simpleEntry.getValue());
				}
			}));
			unspentList.add(new SimpleEntry<String, Integer>(dates.get(i), unspentMonthlyVal.get()));
		}

		typeAmmountMap.remove(TransactionType.SALARY_BASIC.toString());
		typeAmmountMap.remove(TransactionType.SALARY_BONUS.toString());
		typeAmmountMap.remove(TransactionType.TRANSFER_IN.toString());

		typeAmmountMap.put(UNSPENT, unspentList);
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
		typeBox.getItems().add(MONTHLY);
		typeBox.getItems().add(WEEKLY);
	}

	private void setAnchorPaneZero(Node pane) {
		AnchorPane.setBottomAnchor(pane, 10.0);
		AnchorPane.setTopAnchor(pane, 10.0);
		AnchorPane.setLeftAnchor(pane, 10.0);
		AnchorPane.setRightAnchor(pane, 10.0);
	}
}
