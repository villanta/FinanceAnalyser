package com.financeanalyser.view;

import javax.swing.plaf.synth.SynthSeparatorUI;

import javafx.scene.Scene;

public class FinanceAnalyserScene extends Scene {

	private static final String CSS_LOCATION = "/com/financeanalyser/css/style.css";

	public FinanceAnalyserScene() {
		super(new MainPane());
		getStylesheets().add(loadStyle());
	}

	private String loadStyle() {
		String s = getClass().getResource(CSS_LOCATION).toExternalForm();
		System.err.println(s);
		return s;
	}
}
