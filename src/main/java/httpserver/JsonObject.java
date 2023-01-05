package httpserver;

public class JsonObject {
    private int[] arguments;
    private String operation;

    public JsonObject(int[] arguments, String operation) {
        this.arguments = arguments;
        if (operation != null) {
            this.operation = operation.toLowerCase();
        }
    }

    public int[] getArguments() {
        return arguments;
    }
    public String getOperation() {
        return operation;
    }
}
