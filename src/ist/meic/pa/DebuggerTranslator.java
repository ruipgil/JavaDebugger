package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class DebuggerTranslator implements Translator {
	
	String mainClass;
	public DebuggerTranslator(String mainClass) {
		this.mainClass = mainClass; 
	}

	/**
	 * When a class is loaded, each method should be:
	 * <ul>
	 * <li>surrounded by a try-catch
	 * <li>the parameters with which it was called must be recorded
	 */
	@Override
	public void onLoad(ClassPool pool, final String className)
			throws NotFoundException {
		CtClass ctClass = pool.get(className);
		CtMethod[] methods = ctClass.getDeclaredMethods();
		//CtClass exceptionType = ClassPool.getDefault().get("java.lang.Exception");
		
		try {
			for (CtMethod method : methods) {
				
				//surround(method);
				//method.addCatch("{ throw $e; }", exceptionType);
				methodCall(method);
				
				if(method.getName().equals("main") && ctClass.getName().equals(mainClass)) {
					/*method.insertBefore("{ "+DebugMonitor.class.getName()+".enterMethod(\""+className+"."+method.getName()+"\", null, $args); }");
					method.insertAfter("{ "+DebugMonitor.class.getName()+".leaveMethod(); }");
					method.addCatch("{ throw $e; }", exceptionType);*/
					String name = method.getName();
					String newName = name+"$original";
					//final String completeMethodName = method.getDeclaringClass().getName()+"." + method.getName();
					method.setName(newName);
					method = CtNewMethod.copy(method, name, ctClass, null);

					method.setBody("{ "+DebugMonitor.class.getName()+".methodCall(\""+method.getDeclaringClass()+".new\", null, $args, \""+method.getDeclaringClass().getName()+"\", \""+newName+"\", \""+method.getSignature()+"\"); }");
					//method.setBody("{ System.out.println(\"djhasdkjsah\"); }");
					ctClass.addMethod(method);
				}
			}
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	/*private void surround(CtMethod method) throws CannotCompileException {
		String completeMethodName = method.getClass().getName() + "." + method.getName();
		boolean isStatic = Modifier.isStatic(method.getModifiers());
		final String before =
				"{"
				+ DebugMonitor.class.getName() + ".enterMethod(\""+completeMethodName+"\", " + (isStatic ? null : "$0") + ", $args);"
				+ "$_ = $proceed($$);"+
				"}";
		method.insertBefore(before);
		
		final String after =
				"{"
				+ "$_ = $proceed($$);"
				+ DebugMonitor.class.getName() + ".leaveMethod();" +
				"}";
		method.insertAfter(after);
	}*/
	
	private void methodCall(CtMethod method) throws CannotCompileException {
		final String debugMonitor = DebugMonitor.class.getName();
		final String completeMethodName = method.getDeclaringClass().getName()+"." + method.getName();
		//final boolean isStatic = Modifier.isStatic(method.getModifiers());

		method.instrument(new ExprEditor() {
			public void edit(MethodCall mc) {

				if(completeMethodName.startsWith("ist.meic.pa.") || completeMethodName.startsWith("javassist.")) {
					//System.out.println("  - Skipping "+mc.getClassName());
					return;
				}

				//System.out.println("  + Injecting in "+mc.getClassName());
				
				final String template = 
						"{"+
						"  $_ = ($r)"+debugMonitor+".methodCall(\""+completeMethodName+"\", $0, $args, \""+mc.getClassName()+"\", \""+mc.getMethodName()+"\" , \"" + mc.getSignature() + "\");"+
						"}";
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
