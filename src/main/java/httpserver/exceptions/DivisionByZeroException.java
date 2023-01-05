package httpserver.exceptions;

public class DivisionByZeroException extends Exception {
    public DivisionByZeroException() {
        super("Error while performing operation Divide: division by 0");
    }
}
