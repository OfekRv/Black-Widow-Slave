package exceptions;

public class BlackWidowPublisherException extends BlackWidowException {
    public BlackWidowPublisherException(String message) {
        super(message);
    }

    public BlackWidowPublisherException(String message, Throwable cause) {
        super(message, cause);
    }
}