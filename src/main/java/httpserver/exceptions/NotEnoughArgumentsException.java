package httpserver.exceptions;

public class NotEnoughArgumentsException extends Exception {
    private int minNumOfArgsNeeded;
    public NotEnoughArgumentsException(String operation) {
        super(operation);
    }

    public NotEnoughArgumentsException(String op, int minNumOfArgsNeeded) {
        super(op);
        this.minNumOfArgsNeeded = minNumOfArgsNeeded;
    }

    public int getMinNumOfArgsNeeded() {
        return minNumOfArgsNeeded;
    }
}
