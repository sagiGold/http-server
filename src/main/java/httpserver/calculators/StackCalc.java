package httpserver.calculators;

import httpserver.JsonObject;
import httpserver.exceptions.DivisionByZeroException;
import httpserver.exceptions.NegativeFactorialException;
import httpserver.exceptions.NotEnoughArgumentsException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.CONFLICT;

import java.util.Map;
import java.util.Objects;
import java.util.Stack;


@RestController
public class StackCalc {
    private Stack<Integer> stack = new Stack<>();

    @GetMapping("/stack/size")
    public ResponseEntity Size() {
        return ResponseEntity.ok(Map.of("result", stack.size()));
    }

    @PutMapping(value = "/stack/arguments", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity AddArgs(@RequestBody JsonObject jsonObj) {
        for (int argument : jsonObj.getArguments()) {
            stack.push(argument);
        }

        return this.Size();
    }

    @GetMapping(value = "/stack/operate")
    public ResponseEntity Calculate(@RequestParam String operation) {
        int result = 0;
        int first_arg;
        int second_arg;

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
                    result = Math.abs(stack.pop());
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
                    return ResponseEntity.status(CONFLICT).body(Map.of("error-message", "Error: unknown operation: " + operation));
            }
        } catch (NotEnoughArgumentsException e) {
            return ResponseEntity.status(CONFLICT).body(Map.of("error-message", "Error: cannot implement operation " + e.getMessage() + ". It requires "
                    + e.getMinNumOfArgsNeeded() + " arguments and the stack has only " + stack.size() + " arguments"));
        } catch (NegativeFactorialException | DivisionByZeroException e) {
            return ResponseEntity.status(CONFLICT).body(Map.of("error-message", e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("result", result));
    }

    @DeleteMapping("/stack/arguments")
    public ResponseEntity Delete(@RequestParam int count) {
        if (count <= stack.size()) {
            for (int i = 0; i < count; i++) {
                stack.pop();
            }
            return this.Size();
        } else {
            return ResponseEntity.status(CONFLICT).body("Error: cannot remove " + count + " from the stack. It has only " + stack.size() + " arguments");
        }
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