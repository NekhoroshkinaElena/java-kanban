package http;

public class EmptyApiTokenException extends RuntimeException {

    public EmptyApiTokenException(final String message) {
        super(message);
    }
}
