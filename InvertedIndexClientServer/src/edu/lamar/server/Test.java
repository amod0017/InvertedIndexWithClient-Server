package edu.lamar.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

	public static void main(final String[] args) {
		System.out.println("Running jar");
		// try {
		// String line;
		//// Process p = Runtime.getRuntime().exec(
		//// "/usr/local/hadoop/bin/hadoop jar /home/hadoop/jar/test.jar
		// /user/hduser/myinput/input /user/hduser/newoutput");
		// Process p = Runtime.getRuntime().exec(
		// "/usr/local/hadoop/bin/hadoop fs -cat /user/hduser/newoutput/*");
		// BufferedReader bufferedReader = new BufferedReader(new
		// InputStreamReader(p.getInputStream()));
		// while ((line = bufferedReader.readLine()) != null) {
		// System.out.println("not null");
		// System.out.println(line);
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		System.out.println("done");
		final String s = "hadoop->document1,document2";
		final String[] s1 = s.split("->");
		System.out.println(Arrays.asList(s1[1].split(",")));
		final List<String> list = new ArrayList<>();
		list.add("am");
		list.add("ro");
		System.out.println(list.get(1));

	}
}
