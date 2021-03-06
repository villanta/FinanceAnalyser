package com.financeanalyser.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum TransactionType {
	SALARY_BASIC("Basic salary"), SALARY_BONUS("Bonus salary"), RENT("Rent"), BILLS("Bills"), GROCERIES(
			"Groceries"), TAKEAWAY("Takeaway"), ENTERTAINMENT("Entertainment"), CASH("Cash"), TRAVEL(
					"Travel"), CLOTHING("Clothing"), HEALTH_ADMIN("Health and Admin"), SAVINGS("Savings"), TRANSFER_IN(
							"Transfer in"), TRANSFER_OUT("Transfer out"), OTHER(
									"Other"), UNKNOWN("Unknown"), HOME("Home Furniture and Electronics");

	private String label;

	TransactionType(String label) {
		this.label = label;
	}

	public static List<TransactionType> getAllTypes() {
		List<TransactionType> types = new ArrayList<>();

		types.add(SALARY_BASIC);
		types.add(SALARY_BONUS);
		types.add(RENT);
		types.add(BILLS);
		types.add(GROCERIES);
		types.add(TAKEAWAY);
		types.add(ENTERTAINMENT);
		types.add(CASH);
		types.add(TRAVEL);
		types.add(CLOTHING);
		types.add(HEALTH_ADMIN);
		types.add(SAVINGS);
		types.add(TRANSFER_IN);
		types.add(TRANSFER_OUT);
		types.add(HOME);
		types.add(OTHER);

		return types;
	}

	public static TransactionType typeOf(String s) {
		Optional<TransactionType> opType = TransactionType.getAllTypes().stream()
				.filter(type -> s.equals(type.toString())).findFirst();
		if (opType.isPresent()) {
			return opType.get();
		} else {
			return UNKNOWN;
		}
	}

	@Override
	public String toString() {
		return label;
	}
}
