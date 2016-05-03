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
		final Server sv = new Server(5555);
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
			// String line;
			// String outputToReturn = "";
			// System.out.println("executing: " + Server.HADOOP_HOME + " fs
			// -cat " + hadoopOutputPath + "/*|grep "
			// + clientMessage.getKeyWordsToBeSearched().get(0));
			// final Process p =
			// Runtime.getRuntime().exec(Server.HADOOP_HOME + " fs -cat " +
			// hadoopOutputPath
			// + "/*|grep " +
			// clientMessage.getKeyWordsToBeSearched().get(0));
			// final BufferedReader bufferedReader = new BufferedReader(new
			// InputStreamReader(p.getInputStream()));
			// while ((line = bufferedReader.readLine()) != null) {
			// final String[] s1 = line.split("->");
			// outputToReturn = outputToReturn + s1[1];
			// }
			// sendToAllClients(outputToReturn);
		} else if (clientMessage.getOperator() == Operator.NOT) {
			final Set<String> myOutput = new HashSet<>(documentList);
			myOutput.removeAll(myInvertedIndexOutPutCache.get(clientMessage.getKeyWordsToBeSearched().get(0)));
			sendToAllClients(myOutput.toString());
			// try {
			// String line;
			// final List<String> myKeywordContainingList = new
			// ArrayList<String>();
			// final List<String> myOutput = new ArrayList<String>();
			// myOutput.addAll(documentList);
			// System.out.println("executing: " + Server.HADOOP_HOME + " fs -cat
			// " + hadoopOutputPath + "/*|grep "
			// + clientMessage.getKeyWordsToBeSearched().get(0));
			// final Process p = Runtime.getRuntime().exec(Server.HADOOP_HOME +
			// " fs -cat " + hadoopOutputPath
			// + "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(0));
			// final BufferedReader bufferedReader = new BufferedReader(new
			// InputStreamReader(p.getInputStream()));
			// while ((line = bufferedReader.readLine()) != null) {
			// final String[] s1 = line.split("->");
			// myKeywordContainingList.addAll(Arrays.asList(s1[1].split(",")));
			// }
			// myOutput.removeAll(myKeywordContainingList);
			// sendToAllClients(myOutput.toString());
			// } catch (final IOException e) {
			// e.printStackTrace();
			// }
		} else if (clientMessage.getOperator() == Operator.OR) {
			// final String line;
			final Set<String> output = new HashSet<String>();
			output.addAll(myInvertedIndexOutPutCache.get(clientMessage.getKeyWordsToBeSearched().get(0)));
			output.addAll(myInvertedIndexOutPutCache.get(clientMessage.getKeyWordsToBeSearched().get(1)));
			sendToAllClients(output.toString());
			// try {
			// System.out.println("executing :" + Server.HADOOP_HOME + " fs -cat
			// " + hadoopOutputPath + "/*|grep "
			// + clientMessage.getKeyWordsToBeSearched().get(0));
			// final Process p1 = Runtime.getRuntime().exec(Server.HADOOP_HOME +
			// " fs -cat " + hadoopOutputPath
			// + "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(0));
			// BufferedReader bufferedReader = new BufferedReader(new
			// InputStreamReader(p1.getInputStream()));
			// while ((line = bufferedReader.readLine()) != null) {
			// final String[] s1 = line.split("->");
			// output.addAll(Arrays.asList(s1[1].split(",")));
			// }
			// System.out.println("executing : " + Server.HADOOP_HOME + " fs
			// -cat " + hadoopOutputPath + "/*|grep "
			// + clientMessage.getKeyWordsToBeSearched().get(1));
			// final Process p = Runtime.getRuntime().exec(Server.HADOOP_HOME +
			// " fs -cat " + hadoopOutputPath
			// + "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(1));
			// bufferedReader = new BufferedReader(new
			// InputStreamReader(p.getInputStream()));
			// while ((line = bufferedReader.readLine()) != null) {
			// final String[] s1 = line.split("->");
			// output.addAll(Arrays.asList(s1[1].split(",")));
			// }
			// sendToAllClients(output.toString());
			// } catch (final IOException e) {
			// e.printStackTrace();
			// }
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
			// String line;
			// final Set<String> output1 = new HashSet<String>();
			// final Set<String> output2 = new HashSet<String>();
			// final Set<String> output = new HashSet<String>();
			// try {
			// System.out.println(Server.HADOOP_HOME + " fs -cat " +
			// hadoopOutputPath + "/*|grep "
			// + clientMessage.getKeyWordsToBeSearched().get(0));
			// final Process p1 = Runtime.getRuntime().exec(Server.HADOOP_HOME +
			// " fs -cat " + hadoopOutputPath
			// + "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(0));
			// BufferedReader bufferedReader = new BufferedReader(new
			// InputStreamReader(p1.getInputStream()));
			// while ((line = bufferedReader.readLine()) != null) {
			// final String[] s1 = line.split("->");
			// output1.addAll(Arrays.asList(s1[1].split(",")));
			// }
			// System.out.println(Server.HADOOP_HOME + " fs -cat " +
			// hadoopOutputPath + "/*|grep "
			// + clientMessage.getKeyWordsToBeSearched().get(1));
			// final Process p = Runtime.getRuntime().exec(Server.HADOOP_HOME +
			// " fs -cat " + hadoopOutputPath
			// + "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(1));
			// bufferedReader = new BufferedReader(new
			// InputStreamReader(p.getInputStream()));
			// while ((line = bufferedReader.readLine()) != null) {
			// final String[] s1 = line.split("->");
			// output2.addAll(Arrays.asList(s1[1].split(",")));
			// }
			// for (final String string : output2) {
			// if (output1.contains(string)) {
			// output.add(string);
			// }
			// }
			// sendToAllClients(output.toString());
			// } catch (final IOException e) {
			// e.printStackTrace();
			// }
		}
	}

	/**
	 * This method will initialize the server to get ready for searching
	 * keywords. Following are the steps that needs to be done: 1. Run inverted
	 * index. 2. Fetch all the documents name
	 */

	private void initialize() {
		runInvertedIndex();
		// updateInvertedIndexCache();
		// try {
		// String line;
		// System.out.println("executing :" + Server.HADOOP_HOME + " fs -ls " +
		// hadoopInputPath + " awk \'{print $8}\'"
		// + " sed \'s/.*\\///\'");
		// final Process p = Runtime.getRuntime().exec(
		// Server.HADOOP_HOME + " fs -ls " + hadoopInputPath + "| awk \'{print
		// $8}\'" + " |sed \'s/.*\\///\'");
		// final BufferedReader bufferedReader = new BufferedReader(new
		// InputStreamReader(p.getInputStream()));
		// while ((line = bufferedReader.readLine()) != null) {
		// documentList.add(line);
		// }
		// } catch (final IOException e) {
		// e.printStackTrace();
		// }

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
				final String[] myRefactoredOutput = myCommandOutput.split("|");
				System.out.println(myRefactoredOutput);
				myInvertedIndexOutPutCache.put(myRefactoredOutput[0],
						new ArrayList<>(Arrays.asList(myRefactoredOutput[1].split("->"))));
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
