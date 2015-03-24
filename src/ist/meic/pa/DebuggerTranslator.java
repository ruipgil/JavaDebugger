package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.Modifier;

public class DebuggerTranslator implements Translator {

	/**
	 * When a class is loaded, each method should be:<ul>
	 * <li>surrounded by a try-catch
	 * <li>the parameters with which it was called must be recorded 
	 */
	@Override
	public void onLoad(ClassPool pool, final String className) throws NotFoundException,
			CannotCompileException {
		CtClass ctClass = pool.get(className);
		CtMethod[] methods = ctClass.getDeclaredMethods();
		CtClass expectionType = ClassPool.getDefault().get("java.lang.Exception");
		for(CtMethod method : methods) {
			method.addCatch("{ throw $e; }", expectionType );

			final String methodName = method.getName();
			
			boolean isStatic = Modifier.isStatic(method.getModifiers());
			final String template =
					"{"+
					"  "+DebugMonitor.class.getName()+".enterMethod(\"%s.%s\", "+(isStatic?null:"$0")+", $args);"+
					//"  $_ = $proceed($$);"+
					"}";
			method.insertBefore(String.format(template, className, methodName));
			
			/*method.instrument(new ExprEditor() {
				public void edit(MethodCall mc) {
					try {
						mc.replace(String.format(template, className, methodName));
					} catch (CannotCompileException e) {
						e.printStackTrace();
					}
				}
			});*/
			
			final String templateAfter =
					"{"+
					"  "+DebugMonitor.class.getName()+".leaveMethod();"+
					"}";
			method.insertAfter(templateAfter);
			
			//method.setModifier PUBLIC
		}
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {}
	
}
