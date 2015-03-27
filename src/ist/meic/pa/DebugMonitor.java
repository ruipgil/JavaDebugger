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
			if(instance == null) {
				return "";
			}
			
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
		System.out.printf(" ++ ADD : %s\n", entry.callSignature());
		callStack.push(entry);
		callHistory.push(entry);

	}
	
	public static void leaveMethod() {
		StackEntry entry = callStack.pop();
		System.out.printf(" -- POP : %s\n", entry.callSignature());
	}
	
	public static void info() {

		StackEntry top = callStack.lastElement();
		
		Object calledObject = top.getInstance();

		System.out.println("Called Object:");
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
	
	public static Object methodCall(String currentMethod, Object target, Object[] args, String classToCall, String methodToCall, String signature) throws Throwable {
		System.out.printf("Enter info \n\tname:%s\n\ttarget:%s\n\targs:%s\n\tclassToCall:%s\n\tmethodToCall:%s\n",
				currentMethod.toString(), target, args.toString(), classToCall.toString(), methodToCall.toString());
		enterMethod(classToCall+"."+methodToCall, target, args);
		
		//System.out.println("SIGNATURE: " + signature); //String like (I)D
		Class<?>[] parameterType = new Class<?>[args.length];
		for(int i=0; i<args.length; i++) {
			//System.out.println("Primitive: "+args[i].getClass().isPrimitive()+", "+args[i].getClass());
			if( isPrimitive(signature.charAt(1)) ){
				parameterType[i] = convertFromWrapperToPrimitive(args[i].getClass());
			} else{
				parameterType[i] = args[i].getClass();
			}
			signature = nextSigType(signature);
		}

		try {
			Class<?> c = Class.forName(classToCall);
			Method m = c.getDeclaredMethod(methodToCall, parameterType);
			m.setAccessible(true);
			Object result = m.invoke(target, args);
			leaveMethod();
			return result;
		} catch (ClassNotFoundException | SecurityException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		} catch(InvocationTargetException e) {

			Throwable efe = e.getTargetException(); 
			try{
				Object result = REPL(efe, signature);
				leaveMethod();
				return result;
			} catch(DebuggerRetryException r) {
				leaveMethod();
				return methodCall(currentMethod, target, args, classToCall, methodToCall, signature);
			} catch(Throwable t) {
				leaveMethod();
				throw t;
			}

		}
		
		return new Object();

	}

	public static void get(String var){
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
	}

	public static void set(String var, String value){
		StackEntry top = callStack.lastElement();
		Object instance = top.getInstance();
		Field[] fields = instance.getClass().getDeclaredFields();
		for(Field f : fields){
			if(var.equals(f.getName())){
				f.setAccessible(true);
				String type = f.getType().getName();
				if (type == null){
					return;
				}
				try {
					Object obj = null;
					System.out.println("Type:" + type);
					switch(type) {
					case "double": 
						obj = Double.valueOf(value);
						break;
					case "int":
						obj = Integer.valueOf(value);
						break;
					case "long":
						obj = Long.valueOf(value);
						break;
					case "short":
						obj = Short.valueOf(value);
						break;
					case "float":
						obj = Float.valueOf(value);
						break;
					case "boolean":
						obj = Boolean.valueOf(value);
						break;
					case "char":
						if (value.length() == 1){
							obj = value.charAt(0);
						}else{
							throw new NumberFormatException();
						}
						break;
					case "java.lang.String":
						obj = value;
						break;
					}
					f.set(instance, obj);

				}catch(NumberFormatException e){
					System.out.println("The value you want to assign is not from type " + type );
				}catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}		
			}

		}
	}
		
	public static Object REPL(Throwable t, String signature) throws Throwable {

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
				set(command[1],command[2]);
			} else if(command[0].equals("Return") && command.length > 1) {
				return returnCmd(command[1], signature); //Ver se é string int etc
			} else if(command[0].equals("Retry")) {
				throw new DebuggerRetryException();
			} else {
				System.out.println("Invalid command");
			}

		} while(true);
	}
	
	public static Object returnCmd(Object r, String signature) {
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
