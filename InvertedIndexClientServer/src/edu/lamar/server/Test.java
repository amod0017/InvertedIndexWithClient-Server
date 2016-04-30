package edu.lamar.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

	public static void main(String[] args) {
		System.out.println("Running jar");
		try {
			String line;
//			Process p = Runtime.getRuntime().exec(
//					"/usr/local/hadoop/bin/hadoop jar /home/hadoop/jar/test.jar /user/hduser/myinput/input /user/hduser/newoutput");
			Process p = Runtime.getRuntime().exec(
					"/usr/local/hadoop/bin/hadoop fs -cat /user/hduser/newoutput/*");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println("not null");
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("done");
	}
}
