package ist.meic.pa;

import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;

public class DebuggerCLI {
	public static void main(String[] args) throws ClassNotFoundException, NotFoundException {
		String className = args[0];
		String[] restArgs = Arrays.copyOfRange(args, 1, args.length);
		
		ClassPool pool = ClassPool.getDefault();
		DebuggerTranslator translator = new DebuggerTranslator();
		Loader cl = new Loader();
		
		try {
			cl.loadClass(DebugMonitor.class.getName());
			cl.addTranslator(pool, translator);
		} catch (ClassNotFoundException | NotFoundException | CannotCompileException e) {
			e.printStackTrace();
		}
		
		while(true) {
			try {
				cl.run(className, restArgs);
			} catch(Throwable e) {
				if(!e.getClass().getName().equals(DebuggerRetryException.class.getName())) {
					return;
				}
			}
		}
	}
}
