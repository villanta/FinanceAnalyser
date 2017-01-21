package com.financeanalyser.model.filemanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.financeanalyser.model.data.Record;
import com.financeanalyser.model.data.Transaction;
import com.financeanalyser.view.viewswitchcontroller.FAViewSwitchController;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class FileManager {
	private static final Logger LOG = LogManager.getLogger(FileManager.class);

	private static final String DEFAULT_WINDOWS_LOCATION_ENV = "APPDATA";
	private static final String APPLICATION_FOLDER = "/FinanceAnalyser";
	private static final String APPLICATION_CONFIG_FILE = "/config.props";

	private FinanceConfig financeConfig;

	/**
	 * Creates directory if it doesn't exist and initialises filestructure.
	 */
	public boolean initialise() {
		if (isApplicationDirectoryCreated()) {
			return parseOrInitialiseConfig();
		} else {
			LOG.error("Failed to initialise root directory structure");
			return false;
		}
	}

	public FinanceConfig getConfig() {
		return financeConfig;
	}

	private boolean parseOrInitialiseConfig() {
		File configFile = new File(
				System.getenv(DEFAULT_WINDOWS_LOCATION_ENV) + APPLICATION_FOLDER + APPLICATION_CONFIG_FILE);

		Properties config = new Properties();
		if (configFile.exists() || createConfigFile(configFile)) {
			try {
				config.load(new FileInputStream(configFile));
				financeConfig = new FinanceConfig(config);
				return true;
			} catch (IOException e) {
				LOG.error("Failed to load config file.", e);
			}
		}
		return false;
	}

	private boolean createConfigFile(File configFile) {
		try {
			return configFile.createNewFile();
		} catch (IOException e) {
			LOG.warn("Failed to create configuration file.", e);
			return false;
		}
	}

	private boolean isApplicationDirectoryCreated() {
		File appdata = new File(System.getenv(DEFAULT_WINDOWS_LOCATION_ENV));
		File financeAnalyserData = new File(System.getenv(DEFAULT_WINDOWS_LOCATION_ENV) + APPLICATION_FOLDER);
		if (financeAnalyserData.exists()) {
			return true;
		} else if (appdata.exists()) {
			return financeAnalyserData.mkdirs();
		} else {
			return false;
		}
	}

	public boolean saveRecord(Record record, FAViewSwitchController viewSwitchController) {
		File file = showFilePicker(true, viewSwitchController);
		if (file == null) {
			return false;
		}
		try (FileWriter fw = new FileWriter(file)) {
			BufferedWriter bw = new BufferedWriter(fw);

			for (Transaction t : record.getRecord()) {
				bw.write(t.toFileString());
				bw.newLine();
			}
			bw.flush();
		} catch (FileNotFoundException e) {
			LOG.error("File not found...", e);
			return false;
		} catch (IOException e) {
			LOG.error("Failed to write to file...", e);
			return false;
		}

		return true;
	}

	public Optional<Record> openRecord(FAViewSwitchController viewSwitchController) {
		File file = showFilePicker(false, viewSwitchController);
		try (FileReader fr = new FileReader(file)) {
			BufferedReader br = new BufferedReader(fr);

			Record record = new Record();

			String line;
			while ((line = br.readLine()) != null) {
				Optional<Transaction> ot = Transaction.fromFileString(line);
				if (ot.isPresent()) {
					record.addTransaction(ot.get());
				}
			}

			return Optional.of(record);
		} catch (FileNotFoundException e) {
			LOG.error("File not found...", e);
			return Optional.empty();
		} catch (IOException e) {
			LOG.error("Failed to read file...", e);
			return Optional.empty();
		}
	}

	public static File showFilePicker(boolean isSave, FAViewSwitchController viewSwitchController) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getenv(DEFAULT_WINDOWS_LOCATION_ENV) + APPLICATION_FOLDER));
		fileChooser.setTitle("Open Record File");

		List<String> extensions = new ArrayList<>();
		extensions.add("*.rec");
		fileChooser.getExtensionFilters().clear();
		ExtensionFilter extensionFilter = new ExtensionFilter("Finance records files.", extensions);
		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setSelectedExtensionFilter(extensionFilter);
		Window stage = viewSwitchController.getApplicationStage();

		if (isSave) {
			return fileChooser.showSaveDialog(stage);
		} else {
			return fileChooser.showOpenDialog(stage);
		}
	}
}
