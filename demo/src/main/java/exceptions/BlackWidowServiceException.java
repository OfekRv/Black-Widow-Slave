package exceptions;

public class BlackWidowServiceException extends BlackWidowException {
    public BlackWidowServiceException(String message) {
        super(message);
    }

    public BlackWidowServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}