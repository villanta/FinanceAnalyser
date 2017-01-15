package com.financeanalyser.view.viewswitchcontroller;

import com.financeanalyser.view.FinanceAnalyserScene;
import com.financeanalyser.view.panes.HomePane;
import com.financeanalyser.view.panes.RecordPane;

import javafx.scene.layout.AnchorPane;

public class FAViewSwitchController {
	
	private FinanceAnalyserScene applicationScene;

	public FAViewSwitchController(FinanceAnalyserScene financeAnalyserScene) {
		this.applicationScene = financeAnalyserScene;
	}

	public void homeScreen() {
		HomePane pane = new HomePane(this);
		setAnchorPaneZero(pane);
		applicationScene.setRoot(pane);
	}



	public void recordOverviewScreen() {
		RecordPane pane = new RecordPane(this);
		setAnchorPaneZero(pane);
		applicationScene.setRoot(pane);
	}

	public void showCreateRecordDialog() {
		// TODO Auto-generated method stub
		
	}
	
	private void setAnchorPaneZero(AnchorPane pane) {
		AnchorPane.setBottomAnchor(pane, 0.0);
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
	}
}