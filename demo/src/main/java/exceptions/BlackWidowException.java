package exceptions;

public class BlackWidowException extends Exception {
    public BlackWidowException(String message) {
        super(message);
    }

    public BlackWidowException(String message, Throwable cause) {
        super(message, cause);
    }
}