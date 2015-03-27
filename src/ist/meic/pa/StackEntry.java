package ist.meic.pa;

import java.lang.reflect.Field;
import java.util.Arrays;

public class StackEntry {
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

		for (Object arg : args) {
			if (first) {
				first = false;
			} else {
				str += ", ";
			}
			if (arg instanceof Object[]) {
				str += Arrays.toString((Object[]) arg);
			} else {
				str += arg.toString();
			}
		}
		str += ")";
		return str;
	}

	public String instanceFields() {
		if (instance == null) {
			return "";
		}

		Field[] fields = instance.getClass().getDeclaredFields();
		String str = "";
		for (Field field : fields) {
			str += field.getName() + " ";
		}
		return str;
	}

	public String callSignature() {
		return methodName + argsToSignature();
	}
}
