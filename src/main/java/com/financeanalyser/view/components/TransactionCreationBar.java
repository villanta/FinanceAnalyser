package com.financeanalyser.view.components;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.financeanalyser.model.data.Transaction;
import com.financeanalyser.model.data.TransactionType;
import com.financeanalyser.view.components.listeners.TransactionCreationBarListener;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class TransactionCreationBar extends VBox {

	private HBox labelBox;
	private HBox fieldBox;

	private Label typeLabel;
	private Label ammountLabel;
	private Label dateLabel;
	private Label nameLabel;
	private Label noteLabel;

	private ComboBox<TransactionType> typeBox;
	private TextField ammountField;
	private DatePicker datePicker;
	private TextField nameField;
	private TextField noteField;
	private Button submitButton;

	private List<TransactionCreationBarListener> listeners = new ArrayList<>();

	public TransactionCreationBar() {
		super(20.0);
		setPadding(new Insets(20.0));
		initialiseFX();
	}

	public void addListener(TransactionCreationBarListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(TransactionCreationBarListener listener) {
		this.listeners.remove(listener);
	}

	protected void onSubmit(ActionEvent event) {
		Optional<TransactionType> type = getSelectedType();
		Optional<LocalDate> date = getSelectedDate();
		Optional<Integer> ammount = getSelectedAmmount();
		Optional<String> name = getSelectedName();
		String note = noteField.getText();

		if (type.isPresent() && date.isPresent() && ammount.isPresent() && name.isPresent()) {
			int actualAmmount = getActualAmount(type.get(), ammount.get());
			Transaction transaction = new Transaction(date.get(), actualAmmount, type.get(), name.get(), note);
			listeners.stream().forEach(listener -> listener.addRecord(transaction));
		} else {
			StringBuilder sb = new StringBuilder();
			if(!type.isPresent()) {
				sb.append("Type invalid\n");
			}
			if(!date.isPresent()) {
				sb.append("Date invalid\n");
			}
			if(!ammount.isPresent()) {
				sb.append("Ammount invalid\n");
			}
			if(!name.isPresent()) {
				sb.append("Name invalid\n");
			}
			System.err.println(sb.toString());
		}

		event.consume();
	}

	private int getActualAmount(TransactionType transactionType, Integer ammount) {
		if (TransactionType.SALARY_BASIC.equals(transactionType)
				|| TransactionType.SALARY_BONUS.equals(transactionType)) {
			return ammount;
		} else {
			return -ammount;
		}
	}

	private void initialiseFX() {
		initialiseLabels();
		initialiseFields();
		compose();

	}

	private void compose() {
		labelBox.getChildren().addAll(typeLabel, ammountLabel, dateLabel, nameLabel, noteLabel);
		fieldBox.getChildren().addAll(typeBox, ammountField, datePicker, nameField, noteField, submitButton);
		bindWidths();

		super.getChildren().addAll(labelBox, fieldBox);
	}

	private void bindWidths() {
		bindPrefWidthToWidth(typeLabel, typeBox);
		bindPrefWidthToWidth(ammountLabel, ammountField);
		bindPrefWidthToWidth(dateLabel, datePicker);
		bindPrefWidthToWidth(nameLabel, nameField);
		bindPrefWidthToWidth(noteLabel, noteField);
	}

	private void bindPrefWidthToWidth(Label label, TextField field) {
		label.prefWidthProperty().bind(field.widthProperty());
	}

	private void bindPrefWidthToWidth(Label label, ComboBox<TransactionType> box) {
		label.prefWidthProperty().bind(box.widthProperty());
	}

	private void bindPrefWidthToWidth(Label label, DatePicker picker) {
		label.prefWidthProperty().bind(picker.widthProperty());
	}

	private void initialiseFields() {
		fieldBox = new HBox(10.0);
		typeBox = new ComboBox<>();
		typeBox.getItems().setAll(TransactionType.getAllTypes());
		typeBox.setPromptText("Select a type...");

		ammountField = new TextField();
		ammountField.setPromptText("Please type an ammount");

		datePicker = new DatePicker(LocalDate.now());

		nameField = new TextField();
		nameField.setPromptText("Type a common transaction name, e.g. \"Electricity bill\".");

		noteField = new TextField();
		noteField.setPromptText(
				"Add a unique note regarding the transaction e.g. \"Withdrew cash to pay for a taxi.\".");

		submitButton = new Button("Add transaction");
		submitButton.setOnAction(this::onSubmit);
	}

	private Optional<String> getSelectedName() {
		String name = nameField.getText();
		if (name.length() > 0) {
			return Optional.of(name);
		} else {
			return Optional.empty();
		}
	}

	private Optional<Integer> getSelectedAmmount() {
		String ammountString = ammountField.getText();
		if (!ammountString.contains(".")) {
			return getSelectedIntAmmount(ammountString);
		} else {
			return getSelectedDoubleAmmount(ammountString);
		}
	}

	private Optional<Integer> getSelectedDoubleAmmount(String ammountString) {
		try {
			String[] split = ammountString.split("\\.");
			if (split.length != 2 || split[1].length() != 2) {
				return Optional.empty();
			}
			Double ammount = Double.parseDouble(ammountString);
			return Optional.of(Integer.valueOf((int) (ammount * 100)));
		} catch (NumberFormatException e) {
			return Optional.empty();			
		}
	}

	private Optional<Integer> getSelectedIntAmmount(String ammountString) {
		try {
			Integer ammount = Integer.parseInt(ammountString);
			return Optional.of(Integer.valueOf(ammount * 100));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	private Optional<LocalDate> getSelectedDate() {
		LocalDate date = datePicker.getValue();
		if (date == null) {
			return Optional.empty();
		} else {
			return Optional.of(date);
		}
	}

	private Optional<TransactionType> getSelectedType() {
		TransactionType type = typeBox.getSelectionModel().getSelectedItem();

		if (type == null) {
			return Optional.empty();
		} else {
			return Optional.of(type);
		}
	}

	private void initialiseLabels() {
		labelBox = new HBox(10.0);
		typeLabel = new Label("Transaction type");
		typeLabel.setTextAlignment(TextAlignment.CENTER);

		ammountLabel = new Label("Transaction ammount");
		ammountLabel.setTextAlignment(TextAlignment.CENTER);

		dateLabel = new Label("Date");
		dateLabel.setTextAlignment(TextAlignment.CENTER);

		nameLabel = new Label("Name");
		nameLabel.setTextAlignment(TextAlignment.CENTER);

		noteLabel = new Label("Note");
		noteLabel.setTextAlignment(TextAlignment.CENTER);
	}
}
