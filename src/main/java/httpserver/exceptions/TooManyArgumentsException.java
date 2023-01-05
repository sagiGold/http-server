package httpserver.exceptions;

public class TooManyArgumentsException extends Exception {
    public TooManyArgumentsException(String operation) {
        super(operation);
    }
}
