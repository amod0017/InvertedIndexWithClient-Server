package edu.lamar.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.lamar.client.message.ClientMessage;
import edu.lamar.client.message.Operator;

public class Server extends AbstractServer {

	private static final String INVERTED_INDEX_JAR = "/home/hadoop/jar/test.jar";
	private static final String HADOOP_HOME = "/usr/local/hadoop/bin/hadoop";

	public static void main(final String[] args) {
		final Server sv = new Server(1111);
		try {
			sv.listen(); // Start listening for connections
			sv.console.accept();
		} catch (final Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

	private final Map<String, List<String>> myInvertedIndexOutPutCache;

	private final Set<String> documentList = new HashSet<String>();

	final String hadoopInputPath = "/user/hduser/myinput/input";

	final String hadoopOutputPath = "/user/hduser/newoutput/*";

	private final ServerConsole console;

	public Server(final int port) {
		super(port);
		myInvertedIndexOutPutCache = new HashMap<String, List<String>>();
		initialize();
		console = new ServerConsole(this);
	}

	@Override
	protected void handleMessageFromClient(final Object msg, final ConnectionToClient client) {
		final ClientMessage clientMessage = (ClientMessage) msg;
		if (clientMessage.getOperator() == (Operator.NONE)) {
			System.out.println(clientMessage.getKeyWordsToBeSearched());
			sendToAllClients(myInvertedIndexOutPutCache.get(clientMessage.getKeyWordsToBeSearched().get(0)).toString());
		} else if (clientMessage.getOperator() == Operator.NOT) {
			final Set<String> myOutput = new HashSet<>(documentList);
			if ((clientMessage.getKeyWordsToBeSearched().get(0)) != null) {
				myOutput.removeAll(myInvertedIndexOutPutCache.get(clientMessage.getKeyWordsToBeSearched().get(0)));
			}
			sendToAllClients(myOutput.toString());
		} else if (clientMessage.getOperator() == Operator.OR) {
			// final String line;
			final Set<String> output = new HashSet<String>();
			output.addAll(myInvertedIndexOutPutCache.get(clientMessage.getKeyWordsToBeSearched().get(0)));
			output.addAll(myInvertedIndexOutPutCache.get(clientMessage.getKeyWordsToBeSearched().get(1)));
			sendToAllClients(output.toString());
		} else if (clientMessage.getOperator() == Operator.AND) {
			final Set<String> documentsOfFirstKeyword = new HashSet<>();
			final Set<String> documentsOfSecondKeyword = new HashSet<>();
			documentsOfFirstKeyword
					.addAll(myInvertedIndexOutPutCache.get(clientMessage.getKeyWordsToBeSearched().get(0)));
			documentsOfSecondKeyword
					.addAll(myInvertedIndexOutPutCache.get(clientMessage.getKeyWordsToBeSearched().get(1)));
			final Set<String> myFinalOutputToSend = new HashSet<>();
			for (final String string : documentsOfFirstKeyword) {
				if (documentsOfSecondKeyword.contains(string)) {
					myFinalOutputToSend.add(string);
				}
			}
			sendToAllClients(myFinalOutputToSend.toString());
		}
	}

	/**
	 * This method will initialize the server to get ready for searching
	 * keywords. Following are the steps that needs to be done: 1. Run inverted
	 * index. 2. Fetch all the documents name
	 */

	private void initialize() {
		runInvertedIndex();
	}

	public void runInvertedIndex() {
		try {
			System.out.println("executing :" + Server.HADOOP_HOME + " jar " + Server.INVERTED_INDEX_JAR + " "
					+ hadoopInputPath + " " + hadoopOutputPath);
			final Process p = Runtime.getRuntime().exec(Server.HADOOP_HOME + " jar " + Server.INVERTED_INDEX_JAR + " "
					+ hadoopInputPath + " " + hadoopOutputPath);
			p.waitFor();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		updateInvertedIndexCache();
	}

	private void updateInvertedIndexCache() {
		System.out.println("initializing");
		String myCommandOutput;
		Process p1;
		try {
			System.out.println(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath);
			p1 = Runtime.getRuntime().exec(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath);
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
			while ((myCommandOutput = bufferedReader.readLine()) != null) {
				System.out.println(myCommandOutput);
				final String[] myRefactoredOutput = myCommandOutput.split(" \\| ");
				for (final String string : myRefactoredOutput) {
					System.out.println(string);
				}
				myInvertedIndexOutPutCache.put(myRefactoredOutput[0],
						new ArrayList<>(Arrays.asList(myRefactoredOutput[1].split(" -> "))));
			}
			System.out.println("inverted index result");
			for (final String s : myInvertedIndexOutPutCache.keySet()) {
				System.out.println(myInvertedIndexOutPutCache.get(s));
				documentList.addAll(myInvertedIndexOutPutCache.get(s));
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		System.out.println("initialization completed");
	}

}
