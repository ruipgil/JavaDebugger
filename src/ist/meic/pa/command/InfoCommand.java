package ist.meic.pa.command;

import ist.meic.pa.DebugMonitor;
import ist.meic.pa.StackEntry;

import java.util.Stack;

public class InfoCommand {
	public static void execute(Throwable t, String[] args) {
		Stack<StackEntry> callStack = DebugMonitor.getCallStack();
		StackEntry top = callStack.lastElement();
		
		Object calledObject = top.getInstance();

		System.out.print("Called Object:");
		if(calledObject==null) {
			System.out.println("null");
		}else{
			System.out.println(calledObject.toString());
		}
		
		String fields = top.instanceFields();
		System.out.println("       Fields:"+fields);
		
		System.out.println("Call stack:");
		for(int i=callStack.size(); i>0; i--) {
			StackEntry se = callStack.elementAt(i-1);
			System.out.println(se.callSignature());
		}
	}
}
