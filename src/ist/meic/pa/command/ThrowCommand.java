package ist.meic.pa.command;

public class ThrowCommand {

	public static Object execute(Throwable t, String[] args) throws Throwable {
		throw t;
	}

}