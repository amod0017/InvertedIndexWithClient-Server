package edu.lamar.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.lamar.client.message.ClientMessage;
import edu.lamar.client.message.Operator;

public class Server extends AbstractServer {

	private static final String INVERTED_INDEX_JAR = "/home/hadoop/jar/test.jar";
	private static final String HADOOP_HOME = "/usr/local/hadoop/bin/hadoop";

	public static void main(final String[] args) {
		final Server sv = new Server(5555);
		try {
			sv.listen(); // Start listening for connections
			sv.console.accept();
		} catch (final Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

	private final List<String> documentList = new ArrayList<String>();

	final String hadoopInputPath = "/user/hduser/myinput/input";

	final String hadoopOutputPath = "/user/hduser/newoutput";

	private final ServerConsole console;

	public Server(final int port) {
		super(port);
		initialize();
		console = new ServerConsole(this);

	}

	private void getAllDocumentsName() {
		try {
			final Process p = Runtime.getRuntime().exec(Server.HADOOP_HOME + " fs -ls" + hadoopInputPath
					+ "| awk '{print $8}' | awk -F\"/\" '{print $NF}'");
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				documentList.add(line);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleMessageFromClient(final Object msg, final ConnectionToClient client) {
		final ClientMessage clientMessage = (ClientMessage) msg;
		if (clientMessage.getOperator() == (Operator.NONE)) {
			// just get the word with grep command
			try {
				String line;
				String outputToReturn = "";
				final Process p = Runtime.getRuntime().exec(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath
						+ "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(0));
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					outputToReturn = outputToReturn + line;
				}
				sendToAllClients(outputToReturn);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method will initialize the server to get ready for searching
	 * keywords. Following are the steps that needs to be done: 1. Run inverted
	 * index. 2. Fetch all the documents name
	 */

	private void initialize() {
		runInvertedIndex();
		getAllDocumentsName();
	}

	public void runInvertedIndex() {
		try {
			Runtime.getRuntime().exec(Server.HADOOP_HOME + " jar " + Server.INVERTED_INDEX_JAR + " " + hadoopInputPath
					+ " " + hadoopOutputPath);
			Thread.sleep(2000);
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

	}

}
