package edu.lamar.server;

public class Server extends AbstractServer {

	public static void main(final String[] args) {
		final Server sv = new Server(5555);

		try {
			sv.listen(); // Start listening for connections
			sv.console.accept();
		} catch (final Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

	ServerConsole console;

	public Server(final int port) {
		super(port);
		initialize();
		console = new ServerConsole(this);

	}

	@Override
	protected void handleMessageFromClient(final Object msg, final ConnectionToClient client) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method will initialize the server to get ready for searching
	 * keywords. Following are the steps that needs to be done: 1. Run inverted
	 * index. 2. Fetch all the documents name
	 */

	private void initialize() {
		// TODO Auto-generated method stub

	}

}
