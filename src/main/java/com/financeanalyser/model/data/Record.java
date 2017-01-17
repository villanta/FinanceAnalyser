package com.financeanalyser.model.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Record {

	private List<Transaction> records;
	private String typeFilterString = "";
	private String ammountFilterString = "";
	private String dateFilterString = "";
	private String nameFilterString = "";
	private String noteFilterString = "";

	public Record() {
		this.records = new ArrayList<>();
	}

	public List<Transaction> getRecord() {
		return records;
	}

	public boolean addTransaction(Transaction t) {
		return records.add(t);
	}

	public ObservableList<Transaction> getFilteredRecord() {
		List<Transaction> transactions = records.stream().filter(this::applyTypeFilter).filter(this::applyAmountFilter)
				.filter(this::applyDateFilter).filter(this::applyNameFilter).filter(this::applyNoteFilter)
				.collect(Collectors.toList());

		ObservableList<Transaction> list = FXCollections.observableArrayList();
		list.addAll(transactions);
		return list;
	}

	public void setTypeFilter(String typeFilterString) {
		this.typeFilterString = typeFilterString.toUpperCase();
	}

	public void setAmountFilter(String ammountFilterString) {
		this.ammountFilterString = ammountFilterString.toUpperCase();
	}

	public void setDateFilter(String dateFilterString) {
		this.dateFilterString = dateFilterString.toUpperCase();
	}

	public void setNameFilter(String nameFilterString) {
		this.nameFilterString = nameFilterString.toUpperCase();
	}

	public void setNoteFilter(String noteFilterString) {
		this.noteFilterString = noteFilterString.toUpperCase();
	}

	public String getTypeFilterString() {
		return typeFilterString;
	}

	public String getAmountFIlterString() {
		return ammountFilterString;
	}

	public String getDateFilterString() {
		return dateFilterString;
	}

	public String getNameFilterString() {
		return nameFilterString;
	}

	public String getNoteFilterString() {
		return noteFilterString;
	}

	private boolean applyTypeFilter(Transaction transaction) {
		return typeFilterString.isEmpty() || transaction.getType().toString().toUpperCase().contains(typeFilterString);
	}

	private boolean applyAmountFilter(Transaction transaction) {
		if (ammountFilterString.isEmpty()) {
			return true;
		} else {
			double realAmmount = transaction.getCentAmount() / 100.0;
			return String.format("%.2f", realAmmount).toUpperCase().contains(ammountFilterString);
		}
	}

	private boolean applyDateFilter(Transaction transaction) {
		if (dateFilterString.isEmpty()) {
			return true;
		} else {
			LocalDate date = transaction.getDate();

			// return true if string matches day, month or numeric date string.
			return transaction.getDateString().toUpperCase().contains(dateFilterString)
					|| getDayOfWeek(date).toUpperCase().contains(dateFilterString)
					|| getMonth(date).toUpperCase().contains(dateFilterString);
		}
	}

	private String getMonth(LocalDate date) {
		switch (date.getMonth()) {
		case APRIL:
			return "april";
		case AUGUST:
			return "august";
		case DECEMBER:
			return "december";
		case FEBRUARY:
			return "february";
		case JANUARY:
			return "january";
		case JULY:
			return "july";
		case JUNE:
			return "june";
		case MARCH:
			return "march";
		case MAY:
			return "may";
		case NOVEMBER:
			return "november";
		case OCTOBER:
			return "october";
		case SEPTEMBER:
			return "september";
		default:
			return "UNKNOWN";
		}
	}

	private String getDayOfWeek(LocalDate date) {
		switch (date.getDayOfWeek()) {
		case FRIDAY:
			return "friday";
		case MONDAY:
			return "monday";
		case SATURDAY:
			return "saturday";
		case SUNDAY:
			return "sunday";
		case THURSDAY:
			return "thursday";
		case TUESDAY:
			return "tuesday";
		case WEDNESDAY:
			return "wednesday";
		default:
			return "UNKNOWN";
		}
	}

	private boolean applyNameFilter(Transaction transaction) {
		return nameFilterString.isEmpty() || transaction.getName().toUpperCase().contains(nameFilterString);
	}

	private boolean applyNoteFilter(Transaction transaction) {
		return noteFilterString.isEmpty() || transaction.getNote().toUpperCase().contains(noteFilterString);
	}
}
