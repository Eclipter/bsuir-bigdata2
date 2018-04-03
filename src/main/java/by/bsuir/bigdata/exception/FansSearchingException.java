package by.bsuir.bigdata.exception;

public class FansSearchingException extends RuntimeException {

    public FansSearchingException() {
    }

    public FansSearchingException(String message) {
        super(message);
    }

    public FansSearchingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FansSearchingException(Throwable cause) {
        super(cause);
    }
}
