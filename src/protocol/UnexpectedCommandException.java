package protocol;

public class UnexpectedCommandException extends Exception {
	public UnexpectedCommandException() {
	}

	public UnexpectedCommandException(String message) {
		super(message);
	}
}
