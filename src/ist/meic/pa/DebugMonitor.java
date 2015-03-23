package ist.meic.pa;

import java.util.List;
import java.util.LinkedList;

public class DebugMonitor {
	private static List<String> list = new LinkedList<String>();

	public static void record(String methodName, Object[] args) {
		String signature = methodName+"(";
		boolean first = true;
		for(Object arg : args) {
			if(first) {
				first = false;
			} else {
				signature += ", ";
			}
			signature += arg.toString();
		}
		signature+=")";
		
		list.add(signature);
		System.out.println(signature);
	}
}
