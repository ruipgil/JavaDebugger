package ist.meic.pa;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DebugMonitor {
	private static List<String> list = new LinkedList<String>();

	/*public static void record(String methodName) {
		String signature = methodName+"()";
		list.add(signature);
		System.out.println(signature);
	}*/

	public static void record(String methodName, Object[] args) {
		String signature = methodName+"(";
		boolean first = true;

		for(Object arg : args) {
			if(first) {
				first = false;
			} else {
				signature += ", ";
			}
			if(arg instanceof Object[]) {
				signature += Arrays.toString((Object[])arg);
			}else{
				signature += arg.toString();
			}
		}
		signature+=")";
		
		list.add(signature);
		System.out.println(signature);
	}
}
