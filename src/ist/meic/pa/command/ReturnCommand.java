package ist.meic.pa.command;

import ist.meic.pa.DebugMonitor;
import ist.meic.pa.StackEntry;

import java.util.Stack;

public class ReturnCommand {
	public static Object execute(Throwable t, String[] args) {
		String r = args[0];
		Stack<StackEntry> callStack = DebugMonitor.getCallStack();
		StackEntry top = callStack.lastElement();
		String signature = top.getSignature() ;

		Object result = new Object();
		try{
			String s = (String)r;

			String[] parts = signature.split("\\)", 2);
			if(parts[1].equals("Z")){
				result = new Boolean(s);
			}else if (parts[1].equals("B")){
				result = new Byte(s);
			}else if (parts[1].equals("C")){
				result = new Character(s.charAt(0));
			}else if (parts[1].equals("S")){
				result = new Short(s);
			}else if (parts[1].equals("I")){
				result = new Integer(s);
			}else if (parts[1].equals("J")){
				result = new Long(s);
			}else if (parts[1].equals("F")){
				result = new Float(s);
			}else if (parts[1].equals("D")){
				result = new Double(s);
			}else if (parts[1].equals("V")) {
				result = null;
			}else {
				
				//Necessary?
				result = null;
			}
		}catch (Throwable e){
			e.printStackTrace();
		}
		return result;
	}
}
