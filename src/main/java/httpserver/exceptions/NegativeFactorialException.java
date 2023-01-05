package httpserver.exceptions;

public class NegativeFactorialException extends Exception {
    public NegativeFactorialException() {
        super("Error while performing operation Factorial: not supported for the negative number");
    }
}
