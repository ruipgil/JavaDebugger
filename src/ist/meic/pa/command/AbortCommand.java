package ist.meic.pa.command;

public class AbortCommand {
	public static Object execute(Throwable t, String[] args) {
		System.exit(1);
		return null;
	}
}
