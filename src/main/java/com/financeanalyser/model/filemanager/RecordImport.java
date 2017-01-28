package com.financeanalyser.model.filemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.model.data.Transaction;
import com.financeanalyser.model.filemanager.transactionreader.BarclaysTransactionReader;
import com.financeanalyser.model.filemanager.transactionreader.HalifaxTransactionReader;
import com.financeanalyser.model.filemanager.transactionreader.TransactionReader;
import com.financeanalyser.view.viewswitchcontroller.FAViewSwitchController;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class RecordImport {

	private static final Logger LOG = LogManager.getLogger(RecordImport.class);
	private TransactionReader selectedReader = null;

	public Optional<List<Transaction>> importFile(FAViewSwitchController viewSwitchController) {
		File file = FileManager.showFilePicker(false, viewSwitchController, FileManager.CSV_FILTER); // open
																										// file
		List<String> lines = readFileToLines(file); // read file
		List<Transaction> importedFile = process(lines); // process contents

		if (selectedReader == null) {
			return Optional.empty();
		} else  if (importedFile.isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING, "No transactions were found in the selected file.");
			alert.showAndWait();
		} 
		
		return Optional.of(importedFile);
	}

	public void setSelectedReader(TransactionReader selectedItem) {
		this.selectedReader = selectedItem;
	}

	private List<Transaction> process(List<String> lines) {
		LOG.info("processing");

		List<TransactionReader> transactionReaders = Arrays.asList(new BarclaysTransactionReader(),
				new HalifaxTransactionReader());

		Stage readerSelector = new ReaderSelector(transactionReaders, this);
		readerSelector.showAndWait();
		if (selectedReader != null) {
			return lines.stream().map(selectedReader::parseTransaction).collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}		
	}

	private List<String> readFileToLines(File file) {
		List<String> lines = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#"))
					lines.add(line);
			}
		} catch (IOException e) {
			LOG.error("Error while reading imported file to lines.", e);
		}

		return lines;
	}

	class ReaderSelector extends Stage {

		private RecordImport recordImport;

		private List<TransactionReader> transactionReaders;

		private TransactionReader selectedItem = null;

		public ReaderSelector(List<TransactionReader> transactionReaders, RecordImport recordImport) {
			super();
			this.transactionReaders = transactionReaders;
			this.recordImport = recordImport;
			super.setScene(getSelectorScene());
		}

		private void okAction(ActionEvent event) {
			recordImport.setSelectedReader(selectedItem);
			super.close();
			event.consume();
		}

		private void cancelAction(ActionEvent event) {
			super.close();
			event.consume();
		}

		private Scene getSelectorScene() {
			Parent parent = getRoot();
			return new Scene(parent);
		}

		private Parent getRoot() {
			HBox box = new HBox();
			box.setPadding(new Insets(10.0));
			box.setSpacing(10.0);

			box.getChildren().add(getLabel());
			box.getChildren().add(getComboBox());
			box.getChildren().add(getOkButton());
			box.getChildren().add(getCancelButton());

			return box;
		}

		private Node getLabel() {
			return new Label("Select which reader you wish to use");
		}

		private Node getComboBox() {
			ComboBox<TransactionReader> box = new ComboBox<>();
			box.getItems().addAll(transactionReaders);
			box.getSelectionModel().selectedItemProperty().addListener((obsV, oldV, newV) -> selectedItem = newV);
			return box;
		}

		private Node getOkButton() {
			Button okButton = new Button("OK");

			okButton.setOnAction(this::okAction);

			return okButton;
		}

		private Node getCancelButton() {
			Button cancelButton = new Button("Cancel");

			cancelButton.setOnAction(this::cancelAction);

			return cancelButton;
		}
	}

}
