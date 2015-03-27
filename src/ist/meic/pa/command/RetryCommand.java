package ist.meic.pa.command;

import ist.meic.pa.DebuggerRetryException;

public class RetryCommand {

	public static void execute(Throwable t, String[] args) throws Throwable {
		throw new DebuggerRetryException();
	}

}
