package JsonModels;

public class IndependentJSON {
    private int[] arguments;
    private String operation;

    public IndependentJSON(int[] arguments, String operation) {
        this.arguments = arguments;
        this.operation = operation.toLowerCase();
    }

    public int[] getArguments() {
        return arguments;
    }

    public String getOperation() {
        return operation;
    }
}
