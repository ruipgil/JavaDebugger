package ist.meic.pa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
	
	private static Stack<StackEntry> callHistory = new Stack<StackEntry>();
	private static Stack<StackEntry> callStack = new Stack<StackEntry>();
	public static Object ret;

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
	
	public static Class<?> convertFromWrapperToPrimitive(Class<?> wrapper) {
		String wName = wrapper.getName();
		if(wName.equals("java.lang.Integer")) {
			return int.class;
		} else if(wName.equals("java.lang.Float")) {
			return float.class;
		} else if(wName.equals("java.lang.Double")) {
			return double.class;
		} else if(wName.equals("java.lang.Long")) {
			return long.class;
		} else if(wName.equals("java.lang.Boolean")) {
			return boolean.class;
		} else if(wName.equals("java.lang.Character")) {
			return char.class;
		} else if(wName.equals("java.lang.Byte")) {
			return byte.class;
		} else if(wName.equals("java.lang.Void")) {
			return void.class;
		} else if(wName.equals("java.lang.Short")) {
			return short.class;
		} else {
			return wrapper;
		}
	}
	
	public static boolean isPrimitive (char c) {
		if(Character.toString(c).equals("Z") || Character.toString(c).equals("B")
				|| Character.toString(c).equals("C") || Character.toString(c).equals("S")
				|| Character.toString(c).equals("I") || Character.toString(c).equals("J")
				|| Character.toString(c).equals("F") || Character.toString(c).equals("D")
				|| Character.toString(c).equals("V")) {	
				return true;
		} else{
			return false;
		}
	}
	
	public static String nextSigType (String sig) {
		if(isPrimitive(sig.charAt(1))){
			StringBuilder sb = new StringBuilder(sig);
			sb.deleteCharAt(1);
			return sb.toString();
		} else if (Character.toString(sig.charAt(1)).equals("L")){
			String[] parts = sig.split(";" , 2);
			return "(" + parts[1];
		} else if (Character.toString(sig.charAt(1)).equals("[")){
			/**
			 * String is like "([[[[IDI..." We want to remove all instances of "[" and the next letter also
			 */
			while(Character.toString(sig.charAt(1)).equals("[")){
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
	
	public static Object methodCall(String name, Object target, Object[] args, String classToCall, String methodToCall, String signature) throws Throwable {
		// if the size is 0 we are in main.
		System.out.println("#"+name+" "+target);
		//System.out.println("SIGNATURE: " + signature); //String like (I)D
		Class<?>[] parameterType = new Class<?>[args.length];
		for(int i=0; i<args.length; i++) {
			System.out.println("Primitive: "+args[i].getClass().isPrimitive()+", "+args[i].getClass());
			//System.out.println(Character.toString(signature.charAt(i+1)).equals("I"));
			if( isPrimitive(signature.charAt(1)) ){
				//System.out.println(signature.charAt(1));
				parameterType[i] = convertFromWrapperToPrimitive(args[i].getClass());
			} else{
				parameterType[i] = args[i].getClass();
			}
			signature = nextSigType(signature);
		}
		Class<?> c;
		System.out.println(">"+classToCall+"."+methodToCall+" over "+target+" "+name);
		try {
			
			c = Class.forName(classToCall);
			Method m = c.getDeclaredMethod(methodToCall, parameterType);
			m.setAccessible(true);
			return m.invoke(target, args);
		} catch (ClassNotFoundException | SecurityException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			REPL(e.getTargetException());
		} catch (Throwable t) {
			/*return */REPL(t);
		}
		
		return new Object();

	}

	public static void get(String str){
		StackEntry top = callStack.lastElement();
		Field[] fields = top.getInstance().getClass().getDeclaredFields();
		for(Field f : fields){
			//TODO verificar se � a variavel de input da funcao (esta a retornar valores de todas);
			f.setAccessible(true);;
				try {
					System.out.println("Field: "+ f.getName() + " value: " + f.getInt(top.getInstance()));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public static void REPL(Throwable t) throws Throwable {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		System.out.println(t);
		do {
			System.out.print("DebuggerCLI:>");
			String[] command = readLine(input).split(" ");
			
			if(command[0].equals("Abort")){
				System.exit(1);
			} else if(command[0].equals("Info")) {
				info();
			} else if(command[0].equals("Throw")) {
				throw t;
			} else if(command[0].equals("Get") && command.length > 1) {
				get(command[1]);
			} else if(command[0].equals("Set") && command.length > 2) {
				System.out.println("TODO");
			} else if(command[0].equals("Return") && command.length > 1) {
				setReturn((Object)command[1]);
				return;
			} else if(command[0].equals("Retry")) {
				System.out.println("RETRY");
			} else {
				System.out.println("Invalid command");
			}

		} while(true);
	}
	
	public static void setReturn(Object r) {
		ret = r;
	}
	public static Object getReturn() {
		return ret;
	}
	public static boolean hasReturn() {
		return ret != null;
	}
	
	public static String readLine(BufferedReader input) {
		try {
			return input.readLine();
		} catch(Exception e) {
			return "";
		}
	}


}
