package ist.meic.pa;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Stack;

public class DebugMonitor {
	static class StackEntry {
		String methodName;
		Object instance;
		Object[] args;
		
		public StackEntry(String methodName, Object instance, Object[] args) {
			this.instance = instance;
			this.args = args;
			this.methodName = methodName;
		}
		

		public Object getMethodName() {
			return methodName;
		}
		
		public Object getInstance() {
			return instance;
		}

		public Object[] getArgs() {
			return args;
		}
		
		public String argsToSignature() {
			String str = "(";
			boolean first = true;

			for(Object arg : args) {
				if(first) {
					first = false;
				} else {
					str += ", ";
				}
				if(arg instanceof Object[]) {
					str += Arrays.toString((Object[])arg);
				}else{
					str += arg.toString();
				}
			}
			str+=")";
			return str;
		}
		
		public String instanceFields() {
			Field[] fields = instance.getClass().getDeclaredFields();
			String str = "";
			for(Field field : fields) {
				str += field.getName()+" ";
			}
			return str;
		}
		
		public String callSignature() {
			return methodName+argsToSignature();
		}
		
	}
	
	/*private static Stack<StackEntry> callHistory = new Stack<StackEntry>();
	private static Stack<StackEntry> callStack = new Stack<StackEntry>();*/
	
	private static Stack<StackEntry> callHistory = new Stack<StackEntry>();
	private static Stack<StackEntry> callStack = new Stack<StackEntry>();

	public static void enterMethod(String methodName, Object instance, Object[] args) {
		
		StackEntry entry = new StackEntry(methodName, instance, args);
		
		callStack.push(entry);
		callHistory.push(entry);

	}
	
	public static void leaveMethod() {
		callStack.pop();
	}
	
	public static void info() {
		StackEntry top = callStack.lastElement();
		
		Object calledObject = top.getInstance();
		System.out.print("Called Object:");
		if(calledObject==null) {
			System.out.println("null");
		}else{
			System.out.println(calledObject.toString());
		}
		
		System.out.println("       Fields:"+top.instanceFields());
		
		System.out.println("Call stack:");
		for(int i=callStack.size(); i>0; i--) {
			StackEntry se = callStack.elementAt(i-1);
			System.out.println(se.callSignature());
		}
	}


}
