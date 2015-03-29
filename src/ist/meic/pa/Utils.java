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

	public static Class<?> convertFromWrapperToPrimitive(Class<?> wrapper) {
		String wName = wrapper.getName();
		if (wName.equals("java.lang.Integer")) {
			return int.class;
		} else if (wName.equals("java.lang.Float")) {
			return float.class;
		} else if (wName.equals("java.lang.Double")) {
			return double.class;
		} else if (wName.equals("java.lang.Long")) {
			return long.class;
		} else if (wName.equals("java.lang.Boolean")) {
			return boolean.class;
		} else if (wName.equals("java.lang.Character")) {
			return char.class;
		} else if (wName.equals("java.lang.Byte")) {
			return byte.class;
		} else if (wName.equals("java.lang.Void")) {
			return void.class;
		} else if (wName.equals("java.lang.Short")) {
			return short.class;
		} else {
			return wrapper;
		}
	}

	//TODO REMOVE
	public static boolean isPrimitive(char c) {
		if (Character.toString(c).equals("Z")
				|| Character.toString(c).equals("B")
				|| Character.toString(c).equals("C")
				|| Character.toString(c).equals("S")
				|| Character.toString(c).equals("I")
				|| Character.toString(c).equals("J")
				|| Character.toString(c).equals("F")
				|| Character.toString(c).equals("D")
				|| Character.toString(c).equals("V")) {
			return true;
		} else {
			return false;
		}
	}

	//TODO REMOVE AND USE $TYPE
	public static String nextSigType(String sig) {
		if (isPrimitive(sig.charAt(1))) {
			StringBuilder sb = new StringBuilder(sig);
			sb.deleteCharAt(1);
			return sb.toString();
		} else if (Character.toString(sig.charAt(1)).equals("L")) {
			String[] parts = sig.split(";", 2);
			return "(" + parts[1];
		} else if (Character.toString(sig.charAt(1)).equals("[")) {
			/**
			 * String is like "([[[[IDI..." We want to remove all instances of
			 * "[" and the next letter also
			 */
			while (Character.toString(sig.charAt(1)).equals("[")) {
				StringBuilder sb = new StringBuilder(sig);
				sb.deleteCharAt(1);
				sig = sb.toString();
			}
			StringBuilder sb = new StringBuilder(sig);
			sb.deleteCharAt(1);
			return sb.toString();

		}
		return null;
	}
}
