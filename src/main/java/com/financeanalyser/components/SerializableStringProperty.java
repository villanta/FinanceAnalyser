package com.financeanalyser.components;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;

public class SerializableStringProperty extends SimpleStringProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8695164631472630693L;

	public SerializableStringProperty(String string) {
		super(string);
	}
}
