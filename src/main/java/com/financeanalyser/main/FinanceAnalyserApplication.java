package com.financeanalyser.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.view.FinanceAnalyserScene;

import javafx.application.Application;
import javafx.stage.Stage;

public class FinanceAnalyserApplication extends Application {

	private static final Logger LOG = LogManager.getLogger(FinanceAnalyserApplication.class);

	public static void main(String[] args) {
		LOG.info("Initialising application...");

		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(new FinanceAnalyserScene());
		stage.show();
		LOG.debug("Showing stage...");
	}
}