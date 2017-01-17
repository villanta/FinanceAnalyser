package com.financeanalyser.view.components;

import com.financeanalyser.model.data.Transaction;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RecordsTable extends TableView<Transaction> {

	private static final double COLUMN_WIDTH_SMALL = 300.0;

	public RecordsTable() {
		super();

		initialiseColumns();
	}

	@SuppressWarnings("unchecked")
	private void initialiseColumns() {
		TableColumn<Transaction, String> typeColumn = createTypeColumn();
		TableColumn<Transaction, String> amountColumn = createAmountColumn();
		TableColumn<Transaction, String> dateColumn = createDateColumn();
		TableColumn<Transaction, String> nameColumn = createNameColumn();
		TableColumn<Transaction, String> noteColumn = createNoteColumn();

		this.getColumns().addAll(typeColumn, amountColumn, dateColumn, nameColumn, noteColumn);
		this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
	}

	private TableColumn<Transaction, String> createNoteColumn() {
		TableColumn<Transaction, String> noteColumn = new TableColumn<>("Note");
		noteColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("note"));
		noteColumn.getStyleClass().add("noteCol");
		noteColumn.setPrefWidth(1000.0);
		return noteColumn;
	}

	private TableColumn<Transaction, String> createDateColumn() {
		TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
		dateColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("dateString"));
		dateColumn.getStyleClass().add("dateCol");
		dateColumn.setMaxWidth(COLUMN_WIDTH_SMALL);
		return dateColumn;
	}

	private TableColumn<Transaction, String> createNameColumn() {
		TableColumn<Transaction, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("name"));
		nameColumn.getStyleClass().add("nameCol");
		nameColumn.setMaxWidth(COLUMN_WIDTH_SMALL);
		return nameColumn;
	}

	private TableColumn<Transaction, String> createAmountColumn() {
		TableColumn<Transaction, String> amountColumn = new TableColumn<>("Amount");
		amountColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("amount"));
		amountColumn.getStyleClass().add("amountCol");
		amountColumn.setMaxWidth(COLUMN_WIDTH_SMALL);
		return amountColumn;
	}

	private TableColumn<Transaction, String> createTypeColumn() {
		TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("transactionType"));
		typeColumn.getStyleClass().add("typeCol");
		typeColumn.setMaxWidth(COLUMN_WIDTH_SMALL);
		return typeColumn;
	}
}
