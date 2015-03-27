package ist.meic.pa.command;

import ist.meic.pa.DebugMonitor;
import ist.meic.pa.StackEntry;

import java.lang.reflect.Field;
import java.util.Stack;

public class SetCommand {

	public static void execute(Throwable t, String[] args) {
		Stack<StackEntry> callStack = DebugMonitor.getCallStack();
		String var = args[0];
		String value = args[1];

		StackEntry top = callStack.lastElement();
		Object instance = top.getInstance();
		Field[] fields = instance.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (var.equals(f.getName())) {
				f.setAccessible(true);
				String type = f.getType().getName();
				if (type == null) {
					return;
				}
				try {
					Object obj = null;
					//System.out.println("Type:" + type);
					switch (type) {
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
						if (value.length() == 1) {
							obj = value.charAt(0);
						} else {
							throw new NumberFormatException();
						}
						break;
					case "java.lang.String":
						obj = value;
						break;
					}
					f.set(instance, obj);

				} catch (NumberFormatException e) {
					System.out
							.println("The value you want to assign is not from type "
									+ type);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

		}
	}

}
