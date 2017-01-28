package com.financeanalyser.model.filemanager.transactionreader;

import com.financeanalyser.model.data.Transaction;

public interface TransactionReader {
	Transaction parseTransaction(String transaction);
	
	@Override
	String toString();
}
