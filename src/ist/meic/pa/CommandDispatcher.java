package ist.meic.pa;

public class CommandDispatcher {
	
	DebugMonitorProxy dm;
	public CommandDispatcher(DebugMonitorProxy dm) {
		this.dm = dm;
	}
	
	public boolean exec(String[] command) {
		if(command.length < 1) {
			return false;
		}
		if(command[0].equals("Abort")) {
			return abort();
		} else if(command[0].equals("Info")) {
			return info();
		} else if(command[0].equals("Throw")) {
			return throwCmd();
		} else if(command[0].equals("Return") && command.length > 1) {
			return returnCmd(command[1]);
		} else if(command[0].equals("Get") && command.length > 1) {
			return get(command[1]);
		} else if(command[0].equals("Set") && command.length > 2) {
			return set(command[1], command[2]);
		} else if(command[0].equals("Retry")) {
			return retry();
		} else {
			System.out.println("Invalid command.");
			return false;
		}
	}
	
	/**
	 * Does nothing.
	 */
	public boolean abort() {
		return true;
	}
	/**
	 * Print info like this:
	 * <code>
	 * Called Object:{{class}}.{{instance}}
	 *        Fields:{{field}}
	 * Call stack:
	 * {{methodN}}({{arguments of methodN}})
	 * {{ ... }}
	 * {{methodMain}}({{arguments of method main}})
	 * </code>
	 */
	public boolean info() {
		dm.info();
		return false;
	}
	/**
	 * Re-throws the exception to the next handler.
	 */
	public boolean throwCmd() {
		return true;
	}
	/**
	 * Ignores the execution and returns a value.
	 * @param value Value to return.
	 */
	public boolean returnCmd(String value) {
		return true;
	}
	/**
	 * Read a field.
	 * @param fieldName Name of the field to read.
	 */
	public boolean get(String fieldName) {
		return true;
	}
	/**
	 * Sets the value of a field.
	 * @param fieldName Name of the field to be set.
	 * @param newValue New value of the field.
	 */
	public boolean set(String fieldName, String newValue) {
		return true;
	}
	/**
	 * Repeats the method call that was interrupted.
	 * @return
	 */
	public boolean retry() {
		return true;
	}
}
