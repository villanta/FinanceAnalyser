package com.financeanalyser.model.filemanager.transactionreader;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.model.data.Transaction;
import com.financeanalyser.model.data.TransactionType;

public class HalifaxTransactionReader implements TransactionReader {

	private static final Logger LOG = LogManager.getLogger(HalifaxTransactionReader.class);
	
	@Override
	public Transaction parseTransaction(String transaction) {
		String[] transactionFields = transaction.split(",");
		
		LocalDate date = parseDate(transactionFields[0]);
		int amount = parseAmount(transactionFields[5], transactionFields[6]);
		TransactionType type = parseType(transactionFields[1], transactionFields[4]);
		String name = parseName(transactionFields[4]);
		String note = "imported transaction"; //TODO find more meaningful note
		
		return new Transaction(date, amount, type, name, note);
	}

	private String parseName(String name) {
		return name.trim();
	}

	private TransactionType parseType(String transactionType, String name) {
		List<String> groceryList = Arrays.asList("SPAR", "CDE");
		List<String> entertainmentList = Arrays.asList("NETFLIX", "ODEON", "");
		List<String> rentList = Arrays.asList("LETTINGS");
		List<String> billsList = Arrays.asList("STOCKPORT MBC", "VIRGIN");
		List<String> takeawayList = Arrays.asList("JUST-EAT", "");
		
		if("CASHPOINT".equals(transactionType.toUpperCase()))
			return TransactionType.CASH;
		else if("BANK_GIRO_CREDIT".equals(transactionType.toUpperCase()))
			return TransactionType.SALARY_BASIC;
		else if(groceryList.stream().filter(grocery -> name.contains(grocery)).findFirst().isPresent())
			return TransactionType.GROCERIES;
		else if(entertainmentList.stream().filter(entertainment -> name.contains(entertainment)).findFirst().isPresent())
			return TransactionType.ENTERTAINMENT;
		else if(rentList.stream().filter(rent -> name.contains(rent)).findFirst().isPresent())
			return TransactionType.RENT;
		else if(billsList.stream().filter(bill -> name.contains(bill)).findFirst().isPresent())
			return TransactionType.BILLS;
		else if(takeawayList.stream().filter(takeaway -> name.contains(takeaway)).findFirst().isPresent())
			return TransactionType.TAKEAWAY;
				
		return TransactionType.OTHER;
	}

	private int parseAmount(String moneyOut, String moneyIn) {
		if(moneyIn.isEmpty())
			return parseInt(moneyOut);
		else
			return parseInt(moneyIn);
	}

	private int parseInt(String transactionAmount){
		try {
			double amount = Double.parseDouble(transactionAmount.trim());
			return (int) amount * 100;
		} catch (NumberFormatException e) {
			LOG.warn("Failed to read value from String:" + transactionAmount);
			return 0;
		}
	}
	
	private LocalDate parseDate(String dateString) {
		// Expected format: 14/01/2017
		String[] dateFields = dateString.split("/");
		return LocalDate.of(Integer.parseInt(dateFields[2]), Integer.parseInt(dateFields[1]), Integer.parseInt(dateFields[0]));
		
	}
}
