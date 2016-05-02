package edu.lamar.client;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import edu.lamar.client.message.ClientMessage;
import edu.lamar.client.message.Operator;
import edu.lamar.client.message.ServerMessage;

public class Client extends AbstractClient {
	InvertedIndexGui myGui;

	public Client(final String host, final int port, final InvertedIndexGui frame) {
		super(host, port);
		myGui = frame;
	}

	public static void main(final String[] args) {
		final Client c = new Client("localhost", 5555, null);
		try {
			c.sendToServer(new ClientMessage(new ArrayList<String>(), Operator.NONE));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleMessageFromServer(final Object msg) {
		final ServerMessage serverMessage = (ServerMessage) msg;
		JOptionPane.showMessageDialog(myGui.getFrmInvertedIndex(), serverMessage.getMyOutput());
	}

}
