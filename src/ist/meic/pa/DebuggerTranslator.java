package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/**
 * DebugTranslator Class
 * Overrides Onload function to do instrumentation
 * MethodCall function adds called methods to the DebugMonitor CallStack.
 *
 */
public class DebuggerTranslator implements Translator {

	String mainClass;

	public DebuggerTranslator(String mainClass) {
		this.mainClass = mainClass;
	}

	@Override
	public void onLoad(ClassPool pool, final String className)
			throws NotFoundException {
		CtClass ctClass = pool.get(className);
		CtMethod[] methods = ctClass.getDeclaredMethods();

		try {
			for (CtMethod method : methods) {

				methodCall(method);

				if (method.getName().equals("main")
						&& ctClass.getName().equals(mainClass)) {
					String name = method.getName();
					String newName = name + "$original";

					method.setName(newName);
					method = CtNewMethod.copy(method, name, ctClass, null);

					method.setBody("{ " + DebugMonitor.class.getName()
							+ ".methodCall(null, $args, \""
							+ method.getDeclaringClass().getName() + "\", \""
							+ newName + "\", $sig , $type ); }");

					ctClass.addMethod(method);
				}
			}
		} catch (CannotCompileException e) {
			e.printStackTrace();
			return;
		}
	}

	private void methodCall(CtMethod method) throws CannotCompileException {
		final String debugMonitor = DebugMonitor.class.getName();
		final String completeMethodName = method.getDeclaringClass().getName()
				+ "." + method.getName();

		method.instrument(new ExprEditor() {
			public void edit(MethodCall mc) {

				if (completeMethodName.startsWith("ist.meic.pa.")
						|| completeMethodName.startsWith("javassist.")) {
					return;
				}

				final String template = "{" + "  $_ = ($r)" + debugMonitor
						+ ".methodCall($0, $args, \"" + mc.getClassName() + "\", \""
						+ mc.getMethodName() + "\" , $sig , $type );" + "}";
				try {
					mc.replace(template);
				} catch (CannotCompileException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {
	}

}
