package ist.meic.pa.command;

/**
 * The Abort Command
 * Aborts the execution of the program.
 * 
 */

public class AbortCommand {
	public static Object execute(Throwable t, String[] args) {
		System.exit(1);
		return null;
	}
}
