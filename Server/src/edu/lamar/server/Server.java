package edu.lamar.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

	private final List<String> documentList = new ArrayList<String>();

	final String hadoopInputPath = "/user/hduser/myinput/input";

	final String hadoopOutputPath = "/user/hduser/newoutput";

	private final ServerConsole console;

	public Server(final int port) {
		super(port);
		initialize();
		console = new ServerConsole(this);

	}

	@Override
	protected void handleMessageFromClient(final Object msg, final ConnectionToClient client) {
		final ClientMessage clientMessage = (ClientMessage) msg;
		if (clientMessage.getOperator() == (Operator.NONE)) {
			System.out.println(clientMessage.getKeyWordsToBeSearched());
			// just get the word with grep command
			try {
				String line;
				String outputToReturn = "";
				final Process p = Runtime.getRuntime().exec(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath
						+ "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(0));
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					final String[] s1 = line.split("->");
					outputToReturn = outputToReturn + s1[1];
				}
				sendToAllClients(outputToReturn);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		} else if (clientMessage.getOperator() == Operator.NOT) {
			try {
				String line;
				final List<String> myKeywordContainingList = new ArrayList<String>();
				final List<String> myOutput = new ArrayList<String>();
				myOutput.addAll(documentList);
				final Process p = Runtime.getRuntime().exec(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath
						+ "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(0));
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					final String[] s1 = line.split("->");
					myKeywordContainingList.addAll(Arrays.asList(s1[1].split(",")));
				}
				myOutput.removeAll(myKeywordContainingList);
				sendToAllClients(myOutput.toString());
			} catch (final IOException e) {
				e.printStackTrace();
			}
		} else if (clientMessage.getOperator() == Operator.OR) {
			String line;
			final Set<String> output = new HashSet<String>();
			try {
				final Process p1 = Runtime.getRuntime().exec(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath
						+ "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(0));
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					final String[] s1 = line.split("->");
					output.addAll(Arrays.asList(s1[1].split(",")));
				}
				final Process p = Runtime.getRuntime().exec(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath
						+ "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(1));
				bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					final String[] s1 = line.split("->");
					output.addAll(Arrays.asList(s1[1].split(",")));
				}
				sendToAllClients(output.toString());
			} catch (final IOException e) {
				e.printStackTrace();
			}
		} else if (clientMessage.getOperator() == Operator.AND) {
			String line;
			final Set<String> output1 = new HashSet<String>();
			final Set<String> output2 = new HashSet<String>();
			final Set<String> output = new HashSet<String>();
			try {
				System.out.println(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath + "/*|grep "
						+ clientMessage.getKeyWordsToBeSearched().get(0));
				final Process p1 = Runtime.getRuntime().exec(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath
						+ "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(0));
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					final String[] s1 = line.split("->");
					output1.addAll(Arrays.asList(s1[1].split(",")));
				}
				System.out.println(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath + "/*|grep "
						+ clientMessage.getKeyWordsToBeSearched().get(1));
				final Process p = Runtime.getRuntime().exec(Server.HADOOP_HOME + " fs -cat " + hadoopOutputPath
						+ "/*|grep " + clientMessage.getKeyWordsToBeSearched().get(1));
				bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					final String[] s1 = line.split("->");
					output2.addAll(Arrays.asList(s1[1].split(",")));
				}
				for (final String string : output2) {
					if (output1.contains(string)) {
						output.add(string);
					}
				}
				sendToAllClients(output.toString());
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
		try {
			String line;
			System.out.println("executing :" + Server.HADOOP_HOME + " fs -ls " + hadoopInputPath + " awk \'{print $8}\'"
					+ " sed \'s/.*\\///\'");
			final Process p = Runtime.getRuntime().exec(
					Server.HADOOP_HOME + " fs -ls " + hadoopInputPath + " awk \'{print $8}\'" + " sed \'s/.*\\///\'");
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = bufferedReader.readLine()) != null) {
				documentList.add(line);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	public void runInvertedIndex() {
		try {
			System.out.println("executing :" + Server.HADOOP_HOME + " jar " + Server.INVERTED_INDEX_JAR + " "
					+ hadoopInputPath + " " + hadoopOutputPath);
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
