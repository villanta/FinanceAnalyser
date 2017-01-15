package com.financeanalyser.model.data;

import java.time.LocalDate;

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
	private StringProperty transactionType = new SimpleStringProperty("");

	private StringProperty amountString = new SimpleStringProperty("");

	/**
	 * Constructor method, requires core information regarding the transaction.
	 * 
	 * @param date
	 * @param amount
	 * @param type
	 */
	public Transaction(LocalDate date, int amount, TransactionType type) {
		setDate(date);
		setCentAmount(amount);
		setType(type);
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
}