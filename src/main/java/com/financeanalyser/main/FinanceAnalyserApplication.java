package com.financeanalyser.main;

import com.financeanalyser.view.FinanceAnalyserScene;

import javafx.application.Application;
import javafx.stage.Stage;

public class FinanceAnalyserApplication extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(new FinanceAnalyserScene());
		
		stage.show();
	}

}
