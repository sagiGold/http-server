package httpserver.calculators;

import httpserver.JsonObject;
import httpserver.exceptions.DivisionByZeroException;
import httpserver.exceptions.TooManyArgumentsException;
import httpserver.exceptions.NegativeFactorialException;
import httpserver.exceptions.NotEnoughArgumentsException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.HttpStatus.CONFLICT;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

@RestController
public class IndependentCalc {
    @PostMapping(value = "/independent/calculate", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity Calculate(@RequestBody JsonObject jsonObj) {
        int res = 0;
        Map<String,Object> resultObj = new HashMap<>();

        try {
            //TODO: change strings to Enum
            switch (jsonObj.getOperation()) {
                case "plus":
                    checkNumOfArgs(jsonObj);
                    res = jsonObj.getArguments()[0] + jsonObj.getArguments()[1];
                    break;
                case "minus":
                    checkNumOfArgs(jsonObj);
                    res = jsonObj.getArguments()[0] - jsonObj.getArguments()[1];
                    break;
                case "times":
                    checkNumOfArgs(jsonObj);
                    res = jsonObj.getArguments()[0] * jsonObj.getArguments()[1];
                    break;
                case "divide":
                    checkNumOfArgs(jsonObj);
                    if (jsonObj.getArguments()[1] == 0) {
                        throw new DivisionByZeroException();
                    }
                    res = jsonObj.getArguments()[0] / jsonObj.getArguments()[1];
                    break;
                case "pow":
                    checkNumOfArgs(jsonObj);
                    res = (int) Math.pow(jsonObj.getArguments()[0], jsonObj.getArguments()[1]);
                    break;
                case "abs":
                    checkNumOfArgs(jsonObj);
                    res = Math.abs(jsonObj.getArguments()[0]);
                    break;
                case "fact":
                    checkNumOfArgs(jsonObj);
                    if (jsonObj.getArguments()[0] < 0) {
                        throw new NegativeFactorialException();
                    }
                    res = 1;
                    for (int i = 1; i <= jsonObj.getArguments()[0]; i++) {
                        res *= i;
                    }
                    break;
                default:
                    resultObj.put("error-message", "Error: unknown operation: " + jsonObj.getOperation());
                    return ResponseEntity.status(CONFLICT).body(resultObj);
            }
        } catch (NotEnoughArgumentsException e) {
            return ResponseEntity.status(CONFLICT).body("Error: Not enough arguments to perform the operation " + e.getMessage());
        } catch (TooManyArgumentsException e) {
            return ResponseEntity.status(CONFLICT).body("Error: Too many arguments to perform the operation " + e.getMessage());
        } catch (NegativeFactorialException | DivisionByZeroException e) {
            return ResponseEntity.status(CONFLICT).body(e.getMessage());
        }

        resultObj.put("result", res);
        return ResponseEntity.ok(resultObj);
    }

    private void checkNumOfArgs(JsonObject jsonObj) throws NotEnoughArgumentsException, TooManyArgumentsException {
        int numOfArgsNeeded = 2;

        if (Objects.equals(jsonObj.getOperation(), "fact") | Objects.equals(jsonObj.getOperation(), "abs")) {
            numOfArgsNeeded = 1;
        }

        if (jsonObj.getArguments().length < numOfArgsNeeded) {
            throw new NotEnoughArgumentsException(jsonObj.getOperation());
        } else if (jsonObj.getArguments().length > numOfArgsNeeded) {
            throw new TooManyArgumentsException(jsonObj.getOperation());
        }
    }
}
