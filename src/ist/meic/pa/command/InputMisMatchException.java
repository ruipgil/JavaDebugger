package ist.meic.pa.command;

/**
 * 
 * InputMisMatch Exception
 * Exception thrown when the argument type doesn't correspond to the expected one.
 *
 */

public class InputMisMatchException extends Exception{
	
	public InputMisMatchException(){
		super();
	}
	
	public InputMisMatchException(String message){
		super(message);
	}
	
}
