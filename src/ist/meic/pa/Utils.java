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

	
}
