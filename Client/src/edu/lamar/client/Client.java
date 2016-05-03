package edu.lamar.client;

import java.io.IOException;
import java.util.ArrayList;

import edu.lamar.client.message.ClientMessage;
import edu.lamar.client.message.Operator;

public class Client extends AbstractClient {
	InvertedIndexGui myGui;

	public Client(final String host, final int port, final InvertedIndexGui frame) {
		super(host, port);
		myGui = frame;
	}

	public static void main(final String[] args) {
		final Client c = new Client("localhost", 1111, null);
		try {
			c.openConnection();
			c.sendToServer(new ClientMessage(new ArrayList<String>(), Operator.NONE));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleMessageFromServer(final Object msg) {
		final String serverMessage = (String) msg;
		System.out.println(serverMessage);
		// JOptionPane.showMessageDialog(myGui.getFrmInvertedIndex(),
		// serverMessage.getMyOutput());

		myGui.getResultPane().setEnabled(true);
		myGui.getResultPane().setEditable(true);
		myGui.getResultPane().append(serverMessage);
		myGui.getResultPane().setEditable(false);
		myGui.getFrmInvertedIndex().repaint();
	}

}
