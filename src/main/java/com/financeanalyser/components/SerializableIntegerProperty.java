package com.financeanalyser.components;

import java.io.Serializable;

import javafx.beans.property.SimpleIntegerProperty;

public class SerializableIntegerProperty extends SimpleIntegerProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3293772041407720197L;

	public SerializableIntegerProperty(int integer) {
		super(integer);
	}
}
