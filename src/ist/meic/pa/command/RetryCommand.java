package ist.meic.pa.command;

import ist.meic.pa.DebuggerRetryException;

/**
 * RetryCommand
 * Executes the last method call.
 * 
 */
public class RetryCommand {

	public static Object execute(Throwable t, String[] args) throws Throwable {
		throw new DebuggerRetryException();
	}

}
