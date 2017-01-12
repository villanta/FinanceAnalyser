package com.financeanalyser.model.filemanager;

import java.io.File;
import java.util.Properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FinanceConfig {

	private static final String USERNAME_PROPERTY_KEY = "USERNAME";
	private static final String DEFAULT_USERNAME = "Unknown user";

	private StringProperty userName = new SimpleStringProperty("");

	public FinanceConfig(Properties config) {
		load(config);
	}

	public StringProperty userNameProperty() {
		return this.userName;
	}

	public String getUserName() {
		return this.userNameProperty().get();
	}

	public void setUserName(final String userName) {
		this.userNameProperty().set(userName);
	}

	private void load(Properties config) {
		loadUsername(config);
		System.err.println("Username: " + userName.get());
	}

	private void loadUsername(Properties config) {
		userName.set(config.getProperty(USERNAME_PROPERTY_KEY, DEFAULT_USERNAME));
	}

	private void save(File file) {
		// TODO
	}

}
