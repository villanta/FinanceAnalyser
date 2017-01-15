package com.financeanalyser.view.components;

import javafx.scene.control.TableView;

public class RecordsTable extends TableView<TransactionTableEntry> {

	
	public RecordsTable() {
		super();
		
		super.getItems().add(new TransactionTableEntry());
	}
}
