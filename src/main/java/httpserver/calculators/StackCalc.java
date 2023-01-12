package httpserver.calculators;

import httpserver.JsonObject;
import httpserver.exceptions.DivisionByZeroException;
import httpserver.exceptions.NegativeFactorialException;
import httpserver.exceptions.NotEnoughArgumentsException;

import httpserver.loggers.LoggersWrapper;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.CONFLICT;

import java.util.*;

@RestController
public class StackCalc {
    private Stack<Integer> stack = new Stack<>();
    private final Logger stackLogger;
    private final LoggersWrapper requestLogger;
    private long startingTime;
    private final String serverErrMsg = "Server encountered an error ! message: ";

    public StackCalc(LoggersWrapper loggerWrapper) {
        this.requestLogger = loggerWrapper;
        this.stackLogger = loggerWrapper.getLogger("stack-logger");
    }

    @GetMapping("/stack/size")
    public ResponseEntity Size() {
        startingTime = System.currentTimeMillis();
        String stackString = "[" + new StringBuilder(stack.toString().replaceAll("\\[|\\]|\\s", "")).reverse().toString() + "]";
        requestLogger.handleRequest("/stack/size", "GET");
        stackLogger.info("Stack size is " + stack.size() + " | request #" + (requestLogger.getNumOfRequests()));
        stackLogger.debug("Stack content (first == top): " +
                stackString + " | request #" + (requestLogger.getNumOfRequests()));
        requestLogger.handleRequestDuration(System.currentTimeMillis() - startingTime);

        return ResponseEntity.ok(Map.of("result", stack.size()));
    }

    @PutMapping(value = "/stack/arguments", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity AddArgs(@RequestBody JsonObject jsonObj) {
        startingTime = System.currentTimeMillis();
        int[] argsArray = jsonObj.getArguments();

        requestLogger.handleRequest("/stack/arguments", "PUT");
        for (int argument : argsArray) {
            stack.push(argument);
        }

        requestLogger.handleRequestDuration(System.currentTimeMillis() - startingTime);
        stackLogger.info("Adding total of " + argsArray.length + " argument(s) to the stack | Stack size: " + stack.size() + " | request #" + (requestLogger.getNumOfRequests()));
        stackLogger.debug("Adding arguments: " + Arrays.toString(argsArray).replace("[","").replace("]","").replace(" ","") + " | Stack size before " + (stack.size() - argsArray.length) + " | stack size after " + stack.size() + " | request #" + (requestLogger.getNumOfRequests()));

        return ResponseEntity.ok(Map.of("result", stack.size()));
    }

    @GetMapping(value = "/stack/operate")
    public ResponseEntity Calculate(@RequestParam String operation) {
        startingTime = System.currentTimeMillis();
        requestLogger.handleRequest("/stack/operate", "GET");

        Integer first_arg = null, second_arg = null, result = 0;

        try {
            switch (operation.toLowerCase()) {
                case "plus":
                    checkNumOfArgs(operation);
                    first_arg = stack.pop();
                    second_arg = stack.pop();
                    result = first_arg + second_arg;
                    break;
                case "minus":
                    checkNumOfArgs(operation);
                    first_arg = stack.pop();
                    second_arg = stack.pop();
                    result = first_arg - second_arg;
                    break;
                case "times":
                    checkNumOfArgs(operation);
                    first_arg = stack.pop();
                    second_arg = stack.pop();
                    result = first_arg * second_arg;
                    break;
                case "divide":
                    checkNumOfArgs(operation);
                    first_arg = stack.pop();
                    second_arg = stack.pop();

                    if (second_arg == 0) {
                        throw new DivisionByZeroException();
                    }
                    result = first_arg / second_arg;
                    break;
                case "pow":
                    checkNumOfArgs(operation);
                    first_arg = stack.pop();
                    second_arg = stack.pop();
                    result = (int) Math.pow(first_arg, second_arg);
                    break;
                case "abs":
                    checkNumOfArgs(operation);
                    first_arg = stack.pop();
                    result = Math.abs(first_arg);
                    break;
                case "fact":
                    checkNumOfArgs(operation);
                    first_arg = stack.pop();
                    if (first_arg < 0) {
                        stack.push(first_arg);
                        throw new NegativeFactorialException();
                    }
                    result = 1;
                    for (int i = 1; i <= first_arg; i++) {
                        result *= i;
                    }
                    break;
                default:
                    String errMsg = "Error: unknown operation: " + operation;
                    stackLogger.error(serverErrMsg + errMsg + " | request #" + (requestLogger.getNumOfRequests()));
                    return ResponseEntity.status(CONFLICT).body(Map.of("error-message", errMsg));
            }
        } catch (NotEnoughArgumentsException e) {
            String errMsg = "Error: cannot implement operation " + e.getMessage() + ". It requires "
                    + e.getMinNumOfArgsNeeded() + " arguments and the stack has only " + stack.size() + " arguments";
            stackLogger.error(serverErrMsg + errMsg + " | request #" + (requestLogger.getNumOfRequests()));
            return ResponseEntity.status(CONFLICT).body(Map.of("error-message", errMsg));
        } catch (NegativeFactorialException | DivisionByZeroException e) {
            stackLogger.error(serverErrMsg + e.getMessage() + " | request #" + (requestLogger.getNumOfRequests()));
            return ResponseEntity.status(CONFLICT).body(Map.of("error-message", e.getMessage()));
        }

        requestLogger.handleRequestDuration(System.currentTimeMillis() - startingTime);
        stackLogger.info("Performing operation " + operation + "." + " Result is " + result + " | stack size: " + stack.size() + " | request #" + (requestLogger.getNumOfRequests()));
        stackLogger.debug("Performing operation: " + operation + "(" + printUsedArgs(first_arg, second_arg) + ") = " + result + " | request #" + (requestLogger.getNumOfRequests()));

        return ResponseEntity.ok(Map.of("result", result));
    }

    @DeleteMapping("/stack/arguments")
    public ResponseEntity Delete(@RequestParam int count) {
        startingTime = System.currentTimeMillis();
        requestLogger.handleRequest("/stack/arguments", "DELETE");

        if (count <= stack.size()) {
            for (int i = 0; i < count; i++) {
                stack.pop();
            }

            requestLogger.handleRequestDuration(System.currentTimeMillis() - startingTime);
            stackLogger.info("Removing total " + count + " argument(s) from the stack | Stack size: " + stack.size() + " | request #" + (requestLogger.getNumOfRequests()));
            return ResponseEntity.ok(Map.of("result", stack.size()));
        } else {
            String errMsg = "Error: cannot remove " + count + " from the stack. It has only " + stack.size() + " arguments";
            stackLogger.error(serverErrMsg + errMsg + " | request #" + (requestLogger.getNumOfRequests()));
            return ResponseEntity.status(CONFLICT).body(Map.of("error-message", errMsg));
        }
    }

    private String printUsedArgs(Integer first_arg, Integer second_arg) {
        return null == second_arg ? first_arg.toString() : first_arg + "," + second_arg;
    }

    private void checkNumOfArgs(String op) throws NotEnoughArgumentsException {
        int minNumOfArgsNeeded = 2;

        if (Objects.equals(op, "fact") | Objects.equals(op, "abs")) {
            minNumOfArgsNeeded = 1;
        }

        if (stack.size() < minNumOfArgsNeeded) {
            throw new NotEnoughArgumentsException(op, minNumOfArgsNeeded);
        }
    }
}