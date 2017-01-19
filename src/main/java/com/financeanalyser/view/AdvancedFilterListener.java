package com.financeanalyser.view;

import java.util.function.Function;

import com.financeanalyser.model.data.Transaction;

@FunctionalInterface
public interface AdvancedFilterListener {

	enum FilterType {
		DATE, AMMOUNT
	}

	void updateAdvancedFilter(FilterType type, Function<Transaction, Boolean> filterFunction);
}