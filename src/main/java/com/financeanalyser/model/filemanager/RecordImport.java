package com.financeanalyser.model.filemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.model.data.Transaction;
import com.financeanalyser.model.filemanager.transactionreader.BarclaysTransactionReader;
import com.financeanalyser.model.filemanager.transactionreader.HalifaxTransactionReader;
import com.financeanalyser.model.filemanager.transactionreader.TransactionReader;
import com.financeanalyser.view.viewswitchcontroller.FAViewSwitchController;

public class RecordImport {
	
	private static final Logger LOG = LogManager.getLogger(RecordImport.class);
	
	public List<Transaction> importFile(FAViewSwitchController viewSwitchController) {
		File file = FileManager.showFilePicker(false, viewSwitchController, FileManager.CSV_FILTER); // open file
		List<String> lines = readFileToLines(file); // read file
		List<Transaction> importedFile = process(lines); // process contents
		
		return importedFile;
	}
	
	private List<String> readFileToLines(File file) {
		List<String> lines = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String line;
			while((line = br.readLine()) != null) {
				if(!line.startsWith("#"))
					lines.add(line);
			}
		} catch(IOException e) {
			LOG.error("Error while reading imported file to lines.", e);
		}
		
		return lines;
	}
	
	public List<Transaction> process(List<String> lines) {
		LOG.info("processing");
		//TransactionReader reader = new HalifaxTransactionReader();
		TransactionReader reader = new BarclaysTransactionReader();
		return lines.stream().map(reader::parseTransaction).collect(Collectors.toList());	
	}
	

	
	
}
