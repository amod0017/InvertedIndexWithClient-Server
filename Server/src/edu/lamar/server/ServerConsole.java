package edu.lamar.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConsole {
	static Server server;

	public ServerConsole(final Server server) {
		ServerConsole.server = server;
		try {
			server.listen();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void accept() {

		try {
			final BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
			String message;
			while (true) {
				message = fromConsole.readLine();
				if (message.contains("#")) {
					executeCommand(message);
					// return;
					continue;
				}
			}
		} catch (final Exception ex) {
			System.out.println("Unexpected error while reading from console!");
		}
	}

	private void display(final String message) {
		System.out.println(message);
	}

	private void executeCommand(final String message) {
		if (message.equals("#quit")) {
			try {
				ServerConsole.server.sendToAllClients("Server is quitting");
				display("Server is quitting");
				ServerConsole.server.close();
				System.exit(0);
			} catch (final IOException e) {
				e.printStackTrace();
				System.out.println("Error in quitting server");
			}
		} else if (message.equals("#stop")) {
			ServerConsole.server.sendToAllClients("Server stopped listening");
			display("Server stopped listening");
			ServerConsole.server.stopListening();
		}
	}

}
