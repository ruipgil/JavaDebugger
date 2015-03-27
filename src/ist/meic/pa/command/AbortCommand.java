package ist.meic.pa.command;

public class AbortCommand {
	public static void execute(Throwable t, String[] args) {
		System.exit(1);
	}
}
