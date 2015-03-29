package ist.meic.pa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Stack;

public class DebugMonitor {

	private static Stack<StackEntry> callStack = new Stack<StackEntry>();

	public static Stack<StackEntry> getCallStack() {
		return callStack;
	}

	public static void enterMethod(String methodName, Object instance,
			Object[] args, String signature) {

		StackEntry entry = new StackEntry(methodName, instance, args, signature);
		callStack.push(entry);

	}

	public static void leaveMethod() {
		callStack.pop();
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

	public static boolean isPrimitive(char c) {
		if (Character.toString(c).equals("Z")
				|| Character.toString(c).equals("B")
				|| Character.toString(c).equals("C")
				|| Character.toString(c).equals("S")
				|| Character.toString(c).equals("I")
				|| Character.toString(c).equals("J")
				|| Character.toString(c).equals("F")
				|| Character.toString(c).equals("D")
				|| Character.toString(c).equals("V")) {
			return true;
		} else {
			return false;
		}
	}

	public static String nextSigType(String sig) {
		if (isPrimitive(sig.charAt(1))) {
			StringBuilder sb = new StringBuilder(sig);
			sb.deleteCharAt(1);
			return sb.toString();
		} else if (Character.toString(sig.charAt(1)).equals("L")) {
			String[] parts = sig.split(";", 2);
			return "(" + parts[1];
		} else if (Character.toString(sig.charAt(1)).equals("[")) {
			/**
			 * String is like "([[[[IDI..." We want to remove all instances of
			 * "[" and the next letter also
			 */
			while (Character.toString(sig.charAt(1)).equals("[")) {
				StringBuilder sb = new StringBuilder(sig);
				sb.deleteCharAt(1);
				sig = sb.toString();
			}
			StringBuilder sb = new StringBuilder(sig);
			sb.deleteCharAt(1);
			return sb.toString();

		}
		return null;
	}

	public static Object methodCall(Object target, Object[] args,
			String classToCall, String methodToCall, String signature)
			throws Throwable {
		enterMethod(classToCall + "." + methodToCall, target, args, signature);

		String s = signature;
		Class<?>[] parameterType = new Class<?>[args.length];

		for (int i = 0; i < args.length; i++) {
			if (isPrimitive(signature.charAt(1))) {
				parameterType[i] = convertFromWrapperToPrimitive(args[i]
						.getClass());
			} else {
				parameterType[i] = args[i].getClass();
			}
			s = nextSigType(s);
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
			// e.printStackTrace();
		} catch (InvocationTargetException e) {

			Throwable efe = e.getTargetException();
			try {
				Object result = REPL(efe);
				leaveMethod();
				return result;
			} catch (DebuggerRetryException r) {
				leaveMethod();
				return methodCall(target, args, classToCall, methodToCall,
						signature);
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
				if (result != null) {
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

	public static Object returnCmd(Object r, String signature) {
		Object result = new Object();
		try {
			String s = (String) r;

			String[] parts = signature.split("\\)", 2);
			if (parts[1].equals("Z")) {
				result = new Boolean(s);
			} else if (parts[1].equals("B")) {
				result = new Byte(s);
			} else if (parts[1].equals("C")) {
				result = new Character(s.charAt(0));
			} else if (parts[1].equals("S")) {
				result = new Short(s);
			} else if (parts[1].equals("I")) {
				result = new Integer(s);
			} else if (parts[1].equals("J")) {
				result = new Long(s);
			} else if (parts[1].equals("F")) {
				result = new Float(s);
			} else if (parts[1].equals("D")) {
				result = new Double(s);
			} else if (parts[1].equals("V")) {
				result = null;
			} else {

				// Necessary?
				result = null;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}

}
