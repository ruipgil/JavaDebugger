package ist.meic.pa.command;

import ist.meic.pa.DebugMonitor;
import ist.meic.pa.StackEntry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

public class SetCommand {
	static Class<?>[] parameterTypeString = { String.class };
	static Class<?>[] parameterTypeChar = { char.class };
	
	public static Object execute(Throwable t, String[] args)
			throws InputMisMatchException {
		
		Stack<StackEntry> callStack = DebugMonitor.getCallStack();
		
		if(args.length<2) {
			return null;
		}
		
		String var = args[0];
		String value = args[1];
		
		StackEntry top = callStack.lastElement();
		Object instance = top.getInstance();
		Field[] fields = instance.getClass().getDeclaredFields();

		for (Field f : fields) {
			if (var.equals(f.getName())) {
				try {
					setField(f,instance,value);
					
				} catch (InputMisMatchException e) {
					System.out
							.println("The value you want to set must be from type "
									+ f.getType().getName());
				}
			}
		
		}
		return null;
	}
	
	public static void setField(Field f, Object instance, String value) throws InputMisMatchException{
		
		f.setAccessible(true);
		String type = f.getType().getName();
		
			try {

				Object typeObj = f.get(instance);
				Class<?> cl = typeObj.getClass();
				Constructor<?> ctor;
				Object obj = null;

				if (type.equals("char")) {
					ctor = cl.getConstructor(parameterTypeChar);
					if (value.length() == 1) {
						obj = ctor.newInstance(value.charAt(0));
					} else {
						throw new InputMisMatchException(type);
					}
				} else {
					ctor = cl.getConstructor(parameterTypeString);
					obj = ctor.newInstance(value);
				}
				f.set(instance, obj);
				
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				throw new InputMisMatchException();
			}
	}
	
	

}
