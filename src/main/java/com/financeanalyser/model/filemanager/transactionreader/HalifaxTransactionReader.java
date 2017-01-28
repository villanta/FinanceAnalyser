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
		TransactionType type = parseType(transactionFields[1], transactionFields[4]);
		int amount = parseAmount(transactionFields[5], transactionFields[6], type);
		String name = parseName(transactionFields[4]);
		String note = "imported transaction"; // TODO find more meaningful note

		if (TransactionType.TRANSFER_IN.equals(type) && amount < 0) {
			type = TransactionType.TRANSFER_OUT;
		}

		return new Transaction(date, amount, type, name, note);
	}
	
	@Override
	public String toString() {
		return "Halifax transaction reader";
	}

	private String parseName(String name) {
		return name.trim();
	}

	private TransactionType parseType(String transactionType, String name) {
		List<String> cashList = Arrays.asList("CASHPOINT", "ABN-AMRO", "LNK ", "HFX  TRAFFORD", "SBY  WARWICK");
		List<String> groceryList = Arrays.asList("SPAR", "TESCO", "MORRIS", "ONE STOP", "ASDA", "ALDI", "LIDL",
				"POUNDWORLD", "WAITROSE");
		List<String> entertainmentList = Arrays.asList("AMAZON", "NETFLIX", "ODEON", "GOOGLE", "STEAMGAMES",
				"CINEWORLD", "HUMBLEBUNDLE", "ENTERTAIN", "UDEMY", "JACVAPOUR", "RIOTGAM", "WARGAMING", "SPOTIFY",
				"VUE BSL", "ALTERNATE", "MAPLIN", "LH TRADING", "PAYPAL", "CARPHONE", "NRGIT", "IN-STORE", "SCAN",
				"STYAL", "LADBROKES", "PATH OF EXILE", "SKRILL", "PLAYSTATION", "TOYS R US", "GAME", "CCP GAMES",
				"USENET");
		List<String> rentList = Arrays.asList("LETTINGS");
		List<String> billsList = Arrays.asList("STOCKPORT MBC", "VIRGIN", "H3G", "O2", "UNITED UTILITIES W",
				"GIFFGAFF");
		List<String> takeawayList = Arrays.asList("JOLLY SAILER", "KFC", "SUBWAY", "JUST-EAT", "JUST EAT", "WASABI",
				"ICESTONE GELATO", "FRANKIE & BENNYS", "MCDONALDS", "THOMAS", "WELCOME BREAK", "DOMINOS", "FIVE GUYS",
				"BURGER KING", "THE RED LION", "PLATFORM 5", "WHITE HART", "FUZION", "NANDOS", "LEMON GRASS",
				"BELLA ITALIA", "INDIAN RELISH", "PIZZA HUT", "VILLA TOSCANA", "DELIVEROO", "HARRY RAMSDEN",
				"MARCH HARE", "HUNGRYHOUSE", "CALAIS SEAWAYS", "B & M RETAIL", "TAKEAWAY.JE", "KOSMONAUT", "DUNKERQUE",
				"RED HOT WORLD", "ROADCHEF", "ALBERT DOCK");
		List<String> clothingList = Arrays.asList("MARKS & SPENCER", "MARKS&SPENCER", "HIGH AND MIGHTY", "BIGDUDECLOTH",
				"DEBENHAMS");
		List<String> travelList = Arrays.asList("UBER", "NCP", "EUROLYNX", "WEST COAST", "TRAVEL", "RYANAIR", "TFGM");
		List<String> healthAdminList = Arrays.asList("VISION", "HM PASSPORT", "POST OFFICE", "O/D FEE", "FLORIST",
				"AMERICAN EXPRESS", "SHAWBROOK", "EYE 2 C", "ADMIRAL", "BOOTS OPTICIANS", "PURE GYM", "SCORAH CHEMISTS",
				"OPTIMAX");
		List<String> savingsList = Arrays.asList("J ELLESMERE");
		List<String> transferList = Arrays.asList("LESNJAKOVIC", "PINGIT", "ELLESMERE", "BROCK", "SNOWDEN", "S+LE",
				"MORECAMBE", "STEFAN", "ARJUN KRISHNAN", "F-773101", "WWW.MANCHESTER.AC");
		List<String> homeFurnitureAndElectronicsList = Arrays.asList("SOFAWORKS", "IKEA", "ARGOS", "DUNELM",
				"CLOSE BROTHERS", "VLS RE CLOSE BROS", "PRTNR FIN", "STAPLES", "JOHN LEWIS");

		if (cashList.stream().filter(cash -> name.toUpperCase().contains(cash)).findFirst().isPresent()) {
			return TransactionType.CASH;
		} else if ("BANK_GIRO_CREDIT".equals(transactionType.toUpperCase())) {
			return TransactionType.SALARY_BASIC;
		} else if (groceryList.stream().filter(grocery -> name.toUpperCase().contains(grocery)).findFirst()
				.isPresent()) {
			return TransactionType.GROCERIES;
		} else if (entertainmentList.stream().filter(entertainment -> name.toUpperCase().contains(entertainment))
				.findFirst().isPresent()) {
			return TransactionType.ENTERTAINMENT;
		} else if (rentList.stream().filter(rent -> name.toUpperCase().contains(rent)).findFirst().isPresent()) {
			return TransactionType.RENT;
		} else if (billsList.stream().filter(bill -> name.toUpperCase().contains(bill)).findFirst().isPresent()) {
			return TransactionType.BILLS;
		} else if (takeawayList.stream().filter(takeaway -> name.toUpperCase().contains(takeaway)).findFirst()
				.isPresent()) {
			return TransactionType.TAKEAWAY;
		} else if (clothingList.stream().filter(clothing -> name.toUpperCase().contains(clothing)).findFirst()
				.isPresent()) {
			return TransactionType.CLOTHING;
		} else if (travelList.stream().filter(travel -> name.toUpperCase().contains(travel)).findFirst().isPresent()) {
			return TransactionType.TRAVEL;
		} else if (healthAdminList.stream().filter(healthAdmin -> name.toUpperCase().contains(healthAdmin)).findFirst()
				.isPresent()) {
			return TransactionType.HEALTH_ADMIN;
		} else if (savingsList.stream().filter(savings -> name.toUpperCase().contains(savings)).findFirst()
				.isPresent()) {
			return TransactionType.SAVINGS;
		} else if (transferList.stream().filter(transfer -> name.toUpperCase().contains(transfer)).findFirst()
				.isPresent()) {
			return TransactionType.TRANSFER_IN;
		} else if (homeFurnitureAndElectronicsList.stream()
				.filter(homeFurniture -> name.toUpperCase().contains(homeFurniture)).findFirst().isPresent()) {
			return TransactionType.HOME;
		}

		return TransactionType.OTHER;
	}

	private int parseAmount(String moneyOut, String moneyIn, TransactionType tt) {
		if (moneyIn.isEmpty())
			return -parseInt(moneyOut);
		else if (moneyOut.isEmpty())
			return parseInt(moneyIn);
		else
			return 0;
	}

	private int parseInt(String transactionAmount) {
		try {
			double amount = Double.parseDouble(transactionAmount.trim());
			return (int) (amount * 100);
		} catch (NumberFormatException e) {
			LOG.warn("Failed to read value from String:" + transactionAmount);
			return 0;
		}
	}

	private LocalDate parseDate(String dateString) {
		// Expected format: 14/01/2017
		String[] dateFields = dateString.split("/");
		return LocalDate.of(Integer.parseInt(dateFields[2]), Integer.parseInt(dateFields[1]),
				Integer.parseInt(dateFields[0]));

	}
}
