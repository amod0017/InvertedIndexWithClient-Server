package edu.lamar.client.message;

import java.io.Serializable;

public class ServerMessage implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final String myOutput;

	public ServerMessage(final String output) {
		myOutput = output;
	}

	public String getMyOutput() {
		return myOutput;
	}
}
