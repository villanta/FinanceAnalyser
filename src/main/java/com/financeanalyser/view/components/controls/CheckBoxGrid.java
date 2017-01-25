package com.financeanalyser.view.components.controls;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CheckBoxGrid extends AnchorPane {

	private static final double DEFAULT_SPACING = 10.0;
	private ObservableList<SimpleEntry<String, Boolean>> items = FXCollections.observableArrayList();
	private List<CheckBox> checkBoxes = new ArrayList<>();
	private ListChangeListener<? super SimpleEntry<String, Boolean>> itemsListener;

	public CheckBoxGrid() {
		super();
		initialiseItemsListener();
	}

	public void setItems(ObservableList<SimpleEntry<String, Boolean>> items) {
		this.items.removeListener(itemsListener);
		this.items = items;
		this.items.addListener(itemsListener);
	}

	public ObservableList<SimpleEntry<String, Boolean>> getItems() {
		return items;
	}

	public boolean isItemSelected(String item) {
		return items.stream().filter(entry -> entry.getKey().equals(item) && entry.getValue()).count() == 1;
	}

	private void initialiseItemsListener() {
		itemsListener = this::updateGrid;
		items.addListener(itemsListener);
	}

	private void updateGrid(ListChangeListener.Change<? extends SimpleEntry<String, Boolean>> change) {
		super.getChildren().clear();
		this.checkBoxes.clear();
		super.getChildren().add(getRoot());
		super.autosize();
	}

	private Parent getRoot() {
		if (items.size() <= 3) {
			return getVBox();
		} else {
			return getGrid();
		}
	}

	private Parent getGrid() {
		GridPane gp = new GridPane();

		int columnCount = items.size() <= 6 ? 2 : 3;

		gp.setHgap(10.0);
		gp.setVgap(10.0);
		AnchorPane.setBottomAnchor(gp, 10.0);
		AnchorPane.setTopAnchor(gp, 10.0);
		AnchorPane.setLeftAnchor(gp, 10.0);
		AnchorPane.setRightAnchor(gp, 10.0);

		int columnIndex;
		int rowIndex;

		for (int i = 0; i < items.size(); i++) {
			columnIndex = i % columnCount;
			rowIndex = getRowFromIndexAndWidth(i, columnCount);

			Node n = createCheckBox(items.get(i).getKey());
			gp.add(n, columnIndex, rowIndex);
		}

		return gp;
	}

	private int getRowFromIndexAndWidth(int i, int columnCount) {
		int mod = i % columnCount;
		int divisible = i - mod;
		return divisible / columnCount;
	}

	private Parent getVBox() {
		VBox vbox = new VBox();
		vbox.setSpacing(DEFAULT_SPACING);

		items.stream().map(item -> item.getKey()).forEach(str -> vbox.getChildren().add(createCheckBox(str)));

		return vbox;
	}

	private Node createCheckBox(String str) {
		CheckBox checkbox = new CheckBox(str);
		checkBoxes.add(checkbox);
		checkbox.selectedProperty().addListener((obsV, oldV, newV) -> updateItem(checkbox.getText(), newV));
		return checkbox;
	}

	private void updateItem(String text, Boolean newV) {
		Optional<SimpleEntry<String, Boolean>> item = items.stream().filter(entry -> text.equals(entry.getKey()))
				.findFirst();
		if (item.isPresent()) {
			item.get().setValue(newV);
		}
	}
}