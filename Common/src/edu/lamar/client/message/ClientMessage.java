package edu.lamar.client.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientMessage implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
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
