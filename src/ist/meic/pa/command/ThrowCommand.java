package ist.meic.pa.command;

/**
 * 
 * Throw Command 
 * Re-throws the exception so that it may be handled by the next handler
 * 
 */
public class ThrowCommand {

	public static Object execute(Throwable t, String[] args) throws Throwable {
		throw t;
	}

}
