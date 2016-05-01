package edu.lamar.client.message;

import java.util.ArrayList;
import java.util.List;

public class ClientMessage {
	private List<String> keywordsToBeSearched = new ArrayList<String>();
	private final Operator operator;

	public ClientMessage(final List<String> keyWords, final Operator typeOfOperator) {
		keywordsToBeSearched = keyWords;
		operator = typeOfOperator;
	}

	public List<String> getKeyWordsToBeSearched() {
		return keywordsToBeSearched;
	}

	public Operator getOperator() {
		return operator;
	}
}
