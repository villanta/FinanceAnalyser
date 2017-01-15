package com.financeanalyser.model.data;

import java.time.LocalDate;
import java.util.Optional;

import com.financeanalyser.components.SerializableStringProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Transaction {

	// cent amount, pennies, euro cents, dollar cents etc...
	private IntegerProperty centAmount = new SimpleIntegerProperty(0);

	// transaction type
	private TransactionType type;

	// a name that should be commong amongst similar things i.e. "Tesco" if of
	// type groceries
	private StringProperty name = new SimpleStringProperty("");

	private StringProperty note = new SimpleStringProperty("");

	private LocalDate date;

	/*
	 * Extra properties needed for table.
	 */
	private SerializableStringProperty transactionType = new SerializableStringProperty("");

	private SerializableStringProperty amountString = new SerializableStringProperty("");

	/**
	 * Constructor method, requires core information regarding the transaction.
	 * 
	 * @param date
	 * @param amount
	 * @param type
	 * @param name
	 * @param note
	 */
	public Transaction(LocalDate date, int amount, TransactionType type, String name, String note) {
		setDate(date);
		setCentAmount(amount);
		setType(type);
		setName(name);
		setNote(note);
	}

	public IntegerProperty centAmountProperty() {
		return this.centAmount;
	}

	public int getCentAmount() {
		return this.centAmountProperty().get();
	}

	public void setCentAmount(final int centAmount) {
		this.centAmountProperty().set(centAmount);
		double price = centAmount / 100.0;
		this.amountString.set(String.format("%.2f", price));
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public String getName() {
		return this.nameProperty().get();
	}

	public void setName(final String name) {
		this.nameProperty().set(name);
	}

	public StringProperty noteProperty() {
		return this.note;
	}

	public String getNote() {
		return this.noteProperty().get();
	}

	public void setNote(final String note) {
		this.noteProperty().set(note);
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
		this.transactionType.set(type.toString());
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getTransactionType() {
		return transactionType.get();
	}

	public String getAmount() {
		return amountString.get();
	}

	public String getDateString() {
		return date.toString();
	}

	public StringProperty transactionTypeProperty() {
		return transactionType;
	}

	public StringProperty amountProperty() {
		return amountString;
	}

	public String toFileString() {
		StringBuilder sb = new StringBuilder();

		// <type>,<amount>,<date>,<name>,<note>
		sb.append(type.toString());
		sb.append(",");

		sb.append(Integer.toString(getCentAmount()));
		sb.append(",");

		sb.append(date.toString());
		sb.append(",");

		sb.append(getName());
		sb.append(",");

		sb.append(getNote());
		sb.append("\n");

		return sb.toString();
	}

	public static Optional<Transaction> fromFileString(String line) {
		String[] parts = line.split(",");

		TransactionType type;
		int amount;
		LocalDate date;
		String name;
		String note;
		if (parts.length != 5) {
			return Optional.empty();
		} else {
			type = TransactionType.typeOf(parts[0]);
			amount = Integer.parseInt(parts[1]);

			String dateString = parts[2];
			String[] dateStringParts = dateString.split("-");
			date = LocalDate.of(Integer.parseInt(dateStringParts[0]), Integer.parseInt(dateStringParts[1]), Integer.parseInt(dateStringParts[2]));
			name = parts[3];
			note = parts[4];

			Transaction t = new Transaction(date, amount, type, name, note);
			return Optional.of(t);
		}
	}
}