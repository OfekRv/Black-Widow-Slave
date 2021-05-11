package exceptions;

public class BlackWidowListenerException extends BlackWidowException {
    public BlackWidowListenerException(String message) {
        super(message);
    }

    public BlackWidowListenerException(String message, Throwable cause) {
        super(message, cause);
    }
}