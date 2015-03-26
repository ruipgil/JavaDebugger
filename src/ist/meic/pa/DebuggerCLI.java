package ist.meic.pa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			cl.addTranslator(pool, translator);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CannotCompileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			cl.run(className, restArgs);
		} catch(Throwable e) {
			/*try {
				System.out.println(e);
				REPL(debugMonitor);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
		}
	}
}
