package ist.meic.pa.command;

import ist.meic.pa.DebugMonitor;
import ist.meic.pa.StackEntry;

import java.lang.reflect.Field;
import java.util.Stack;


/**
 * The Get Command 
 * Receives a variable and return it's value 
 *
 */
public class GetCommand {
	
	public static Object execute(Throwable t, String[] args) {
		Stack<StackEntry> callStack = DebugMonitor.getCallStack();
		if(args.length == 0) {
			return null;
		}
		String var = args[0];

		StackEntry top = callStack.lastElement();
		Object instance = top.getInstance();
		Field[] fields = instance.getClass().getDeclaredFields();
		for(Field f : fields){
			if(var.equals(f.getName())){
			f.setAccessible(true);
				try {
					System.out.println(f.get(instance));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
