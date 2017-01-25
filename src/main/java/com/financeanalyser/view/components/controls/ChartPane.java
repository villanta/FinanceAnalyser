package com.financeanalyser.view.components.controls;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import com.financeanalyser.model.data.Record;
import com.financeanalyser.view.components.listeners.PopupChartListener;
import com.financeanalyser.view.components.popovers.LineChartPopover;
import com.financeanalyser.view.components.popovers.PieChartPopover;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class ChartPane extends AnchorPane implements PopupChartListener {

	private static final Logger LOG = LogManager.getLogger(ChartPane.class);
	private static final String CHART_PANE_FXML_LOCATION = "/com/financeanalyser/view/components/ChartPane.fxml";

	@FXML
	private Button pieChartButton;
	@FXML
	private Button lineChartButton;

	@FXML
	private FullScreenPane chartPane;

	private Record record;
	private PopOver pieChartPopover;
	private PopOver lineChartPopover;

	public ChartPane() {
		super();
		loadFXML();
		initialiseFX();
	}

	/**
	 * @return the record
	 */
	public Record getRecord() {
		return record;
	}

	/**
	 * @param record
	 *            the record to set
	 */
	public void setRecord(Record record) {
		this.record = record;
	}

	@FXML
	public void createPieChart(ActionEvent event) {
		if (pieChartPopover == null) {
			pieChartPopover = new PopOver(new PieChartPopover(record, this));
			pieChartPopover.setArrowLocation(ArrowLocation.TOP_LEFT);
		}
		pieChartPopover.show(pieChartButton);
		event.consume();
	}

	@FXML
	public void createLineChart(ActionEvent event) {
		if (lineChartPopover == null) {
			lineChartPopover = new PopOver(new LineChartPopover(record, this));
			lineChartPopover.setArrowLocation(ArrowLocation.TOP_LEFT);
		}
		lineChartPopover.show(lineChartButton);
		event.consume();
	}

	@Override
	public void setChartToDisplay(Node chart) {
		AnchorPane.setBottomAnchor(chart, 0.0);
		AnchorPane.setTopAnchor(chart, 0.0);
		AnchorPane.setLeftAnchor(chart, 0.0);
		AnchorPane.setRightAnchor(chart, 0.0);
		chartPane.removeFramedNode();
		chartPane.setFramedNode(chart);
	}

	private void loadFXML() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(CHART_PANE_FXML_LOCATION));
		loader.setController(this);
		try {
			AnchorPane pane = loader.load();
			setAnchorPaneZero(pane);
			super.getChildren().add(pane);
		} catch (IOException e) {
			LOG.error("Failed to load fxml for main pane.", e);
		}
	}

	private void initialiseFX() {
	}

	private void setAnchorPaneZero(Node pane) {
		AnchorPane.setBottomAnchor(pane, 0.0);
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
	}

}
