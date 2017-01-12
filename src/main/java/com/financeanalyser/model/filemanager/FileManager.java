package com.financeanalyser.model.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileManager {

	private static final Logger LOG = LogManager.getLogger(FileManager.class);

	private static final String DEFAULT_WINDOWS_LOCATION_ENV = "APPDATA";
	private static final String APPLICATION_FOLDER = "/FinanceAnalyser";
	private static final String APPLICATION_CONFIG_FILE = "/config.props";

	private FinanceConfig financeConfig;
	private boolean isValid;

	/**
	 * Creates directory if it doesn't exist and initialises filestructure.
	 */
	public void initialise() {
		if (isApplicationDirectoryCreated()) {
			isValid = parseOrInitialiseConfig();
		} else {
			LOG.error("Failed to initialise root directory structure");
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
}
