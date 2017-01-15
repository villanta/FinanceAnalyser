package com.financeanalyser.view.viewswitchcontroller;

import com.financeanalyser.model.data.Record;
import com.financeanalyser.model.filemanager.FileManager;
import com.financeanalyser.view.FinanceAnalyserScene;
import com.financeanalyser.view.panes.HomePane;
import com.financeanalyser.view.panes.RecordPane;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

public class FAViewSwitchController {
	
	private FinanceAnalyserScene applicationScene;
	private FileManager fileManager;

	public FAViewSwitchController(FinanceAnalyserScene financeAnalyserScene) {
		this.applicationScene = financeAnalyserScene;
	}
	
	public void setFileManager(FileManager manager) {
		this.fileManager = manager;
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}

	public void homeScreen() {
		HomePane pane = new HomePane(this);
		setAnchorPaneZero(pane);
		applicationScene.setRoot(pane);
	}



	public void recordOverviewScreen(Record record) {
		RecordPane pane = new RecordPane(this, record);
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

	public Window getApplicationStage() {
		return applicationScene.getWindow();
	}
}