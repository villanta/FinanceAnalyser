package com.financeanalyser.view.components.controls;

import java.io.IOException;
import java.time.LocalDate;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.model.data.Transaction;
import com.financeanalyser.view.AdvancedFilterListener;
import com.financeanalyser.view.AdvancedFilterListener.FilterType;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class AdvancedFilter extends AnchorPane {

	private static final Logger LOG = LogManager.getLogger(AdvancedFilter.class);
	private static final String FILTER_PANE_FXML_LOCATION = "/com/financeanalyser/view/components/AdvancedFilter.fxml";
	private static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");

	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;

	@FXML
	private TextField fromAmmountField;
	@FXML
	private TextField toAmmountField;

	private AdvancedFilterListener listener;

	private LocalDate startDate;
	private LocalDate endDate;

	private int min = 0;
	private int max = Integer.MAX_VALUE;

	public AdvancedFilter() {
		super();
		loadFXML();
	}

	public void setListener(AdvancedFilterListener listener) {
		this.listener = listener;
	}

	@FXML
	protected void startDateUpdated(ActionEvent event) {
		startDate = startDatePicker.getValue();
		updateDateFilter();
		event.consume();
	}

	@FXML
	protected void endDateUpdated(ActionEvent event) {
		endDate = endDatePicker.getValue();
		updateDateFilter();
		event.consume();
	}

	@FXML
	protected void fromAmmountUpdated(ActionEvent event) {
		updateFromAmmount();
		updateToAmmount();
		event.consume();
	}

	@FXML
	protected void fromAmmountUpdatedKB(KeyEvent event) {
		updateFromAmmount();
		updateToAmmount();
	}

	@FXML
	protected void toAmmountUpdated(ActionEvent event) {
		updateToAmmount();
		updateFromAmmount();
		event.consume();
	}

	@FXML
	protected void toAmmountUpdatedKB(KeyEvent event) {
		updateToAmmount();
		updateFromAmmount();
	}

	private void updateAmmountFilter() {
		Function<Transaction, Boolean> filter = transaction -> {
			boolean isMinValid = Math.abs(transaction.getCentAmount()) >= min;
			boolean isMaxValid = Math.abs(transaction.getCentAmount()) <= max;

			return isMinValid && isMaxValid;
		};
		listener.updateAdvancedFilter(FilterType.AMMOUNT, filter);
	}

	private void updateDateFilter() {
		Function<Transaction, Boolean> filter = transaction -> {
			boolean isStartValid = startDate == null || !transaction.getDate().isBefore(startDate);
			boolean isEndValid = endDate == null || !transaction.getDate().isAfter(endDate);

			return isStartValid && isEndValid;
		};
		listener.updateAdvancedFilter(FilterType.DATE, filter);
	}

	private void updateFromAmmount() {
		try {
			Double value = Double.parseDouble(fromAmmountField.getText());
			if (value < 0 || value > max / 100.0) {
				throw null;
			}
			min = (int) (100 * value);
			fromAmmountField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
		} catch (NumberFormatException | NullPointerException e) {
			fromAmmountField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
			min = 0;
			LOG.trace("Invalid from ammount.", e);
		}
		updateAmmountFilter();
	}

	private void updateToAmmount() {
		try {
			Double value = Double.parseDouble(toAmmountField.getText());
			if (value < 0 || value < min / 100.0) {
				throw null;
			}
			max = (int) (100 * value);
			toAmmountField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
		} catch (NumberFormatException | NullPointerException e) {
			toAmmountField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
			max = Integer.MAX_VALUE;
			LOG.trace("Invalid to ammount.", e);
		}
		updateAmmountFilter();
	}

	private void loadFXML() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FILTER_PANE_FXML_LOCATION));
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
