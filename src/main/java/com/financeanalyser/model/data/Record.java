package com.financeanalyser.model.data;

import java.util.ArrayList;
import java.util.List;

public class Record {

	private List<Transaction> records;

	public Record() {
		this.records = new ArrayList<>();
	}

	public List<Transaction> getRecord() {
		return records;
	}

	public boolean addTransaction(Transaction t) {
		return records.add(t);
	}
}
