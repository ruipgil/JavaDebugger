package ist.meic.pa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Stack;

/**
 *  DebugMonitor Class
 * 	Stores all method calls in the CallStack
 *  Contains functions enterMethod and leaveMethod to manipulate CallStack data.
 *  Function REPL represents Read Eval Print Loop. Contains the debugger interface.
 *
 */
public class DebugMonitor {

	private static Stack<StackEntry> callStack = new Stack<StackEntry>();

	public static Stack<StackEntry> getCallStack() {
		return callStack;
	}

	public static void enterMethod(String methodName, Object instance,
			Object[] args, Object[] argsType, Object resultType) {
		
		if(callStack.size()==0) {
			methodName = methodName.replace("$original", "");
		}
		
		StackEntry entry = new StackEntry(methodName, instance, args, argsType, resultType);
		callStack.push(entry);

	}

	public static void leaveMethod() {
		callStack.pop();
	}

	public static Object methodCall(Object target, Object[] args,
			String classToCall, String methodToCall, Object[] argsType, Object resultType)
			throws Throwable {
		enterMethod(classToCall + "." + methodToCall, target, args, argsType, resultType);

		Class<?>[] parameterType = new Class<?>[args.length];

		for (int i = 0; i < args.length; i++) {
			if (((Class) argsType[i]).isPrimitive()){
				parameterType[i] = Utils.convertFromWrapperToPrimitive(args[i]
						.getClass());
			} else {
				parameterType[i] = args[i].getClass();
			}
		}

		try {
			Class<?> c = Class.forName(classToCall);
			Method m = c.getDeclaredMethod(methodToCall, parameterType);
			m.setAccessible(true);
			Object result = m.invoke(target, args);
			leaveMethod();
			return result;
		} catch (ClassNotFoundException | SecurityException
				| NoSuchMethodException | IllegalArgumentException
				| IllegalAccessException e) {
			System.out.println("Unexpected exception!");
		} catch (InvocationTargetException e) {

			Throwable efe = e.getTargetException();
			try {
				Object result = REPL(efe);
				leaveMethod();
				return result;
			} catch (DebuggerRetryException r) {
				leaveMethod();
				return methodCall(target, args, classToCall, methodToCall,
						argsType, resultType);
			} catch (Throwable t) {
				leaveMethod();
				throw t;
			}

		}

		return new Object();

	}

	public static Object REPL(Throwable t) throws Throwable {

		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println(t);
		do {
			System.out.print("DebuggerCLI:>");
			String[] command = Utils.readLine(input).split(" ");
			String className = "ist.meic.pa.command." + command[0] + "Command";

			try {
				String[] args = Arrays.copyOfRange(command, 1, command.length);
				Class<?> c = Class.forName(className);
				Class<?>[] paramsType = { Throwable.class, String[].class };
				Method m = c.getDeclaredMethod("execute", paramsType);
				Object result = m.invoke(null, t, args);

				if ( command[0].equals("Return") || result!=null ) {
					return result;
				}
			} catch (ClassNotFoundException | SecurityException
					| NoSuchMethodException | IllegalArgumentException
					| IllegalAccessException e) {
				System.out.println("Invalid command!");
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		} while (true);
	}
}
