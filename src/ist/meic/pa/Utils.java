package ist.meic.pa;

import java.io.BufferedReader;

public class Utils {

	public static String readLine(BufferedReader input) {
		try {
			return input.readLine();
		} catch(Exception e) {
			return "";
		}
	}
}
