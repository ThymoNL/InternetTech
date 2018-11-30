package protocol;

public class UnexpectedCommandException extends Exception {
	public UnexpectedCommandException() {
	}

	public UnexpectedCommandException(String message) {
		super(message);
	}

	public UnexpectedCommandException(String expected, String actual) {
		super("Expected " + expected + ". Got " + actual + " instead.");
	}
}
