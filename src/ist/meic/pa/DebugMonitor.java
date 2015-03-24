package ist.meic.pa;

import java.util.Arrays;
import java.util.Stack;

public class DebugMonitor {
	private static Stack<String> callHistory = new Stack<String>();
	private static Stack<String> callStack = new Stack<String>();

	public static void enterMethod(String methodName, Object[] args) {
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
		
		// save { objectInstance, fieldsName&values }
		
		callStack.push(signature);
		callHistory.push(signature);
	}
	
	public static void leaveMethod() {
		callStack.pop();
	}
	
	public static void printStackTrace() {
		System.out.println("Callstack");
		for(int i=callStack.size(); i>0; i--) {
			System.out.println(callStack.elementAt(i-1));
		}
		
		System.out.println("Callhistory");
		for(int i=callHistory.size(); i>0; i--) {
			System.out.println(callHistory.elementAt(i-1));
		}
	}
}
