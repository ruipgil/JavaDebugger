package ist.meic.pa.command;

import ist.meic.pa.DebugMonitor;
import ist.meic.pa.StackEntry;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Stack;

/**
 * Return Command
 * Returns the given value. 
 *
 */
public class ReturnCommand {
	public static HashMap<String,String> types = new HashMap<String, String>();
	static { 
		types.put("boolean", "java.lang.Boolean");
		types.put("byte", "java.lang.Byte");
		types.put("char", "java.lang.Character");
		types.put("short", "java.lang.Short");
		types.put("int", "java.lang.Integer");
		types.put("long", "java.lang.Long");
		types.put("float", "java.lang.Float");
		types.put("double", "java.lang.Double");
		types.put("void", "java.lang.Void");
	}
	public static Object execute(Throwable t, String[] args) {
		if(args.length == 0) {
			return null;
		}
		
		Class<?>[] parameterTypeString = { String.class };
		Class<?>[] parameterTypeChar = { char.class };

		String r = args[0];
		Stack<StackEntry> callStack = DebugMonitor.getCallStack();
		StackEntry top = callStack.lastElement();
		Class<?> resultType = (Class<?>) top.getResultType() ;
	
		Object obj = null;
		try{
			String s = r;
			String type = types.get(resultType.toString());
			Class<?> cl = Class.forName(type);
			
			Constructor<?> ctor;
			
			
			if (type.equals("Character")) {
				ctor = cl.getConstructor(parameterTypeChar);
				if (s.length() == 1) {
					obj = ctor.newInstance(s.charAt(0));
				} else {
					throw new InputMisMatchException(type);
				}
			} else {
				ctor = cl.getConstructor(parameterTypeString);
				obj = ctor.newInstance(s);
			}
			
		}catch (Throwable e){
			System.out.println("Return must be from type " + resultType.toString() );
			return null;
		}
		return obj;
		
	}
}
