package ist.meic.pa;

import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;

public class DebuggerCLI {
	public static void main(String[] args) throws ClassNotFoundException,
			NotFoundException {
		String className = args[0];
		String[] restArgs = Arrays.copyOfRange(args, 1, args.length);

		ClassPool pool = ClassPool.getDefault();
		DebuggerTranslator translator = new DebuggerTranslator(className);
		Loader cl = new Loader();

		try {
			cl.loadClass(DebugMonitor.class.getName());
			cl.addTranslator(pool, translator);
		} catch (ClassNotFoundException | NotFoundException
				| CannotCompileException e) {
			e.printStackTrace();
		}

		try {
			cl.run(className, restArgs);
		} catch (Throwable e) {
			// unexpected throw, or simply, thrown from main
			System.out.println(e);
		}
	}
}
