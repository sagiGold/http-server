package httpserver.calculators;

import httpserver.JsonObject;
import httpserver.exceptions.DivisionByZeroException;
import httpserver.exceptions.TooManyArgumentsException;
import httpserver.exceptions.NegativeFactorialException;
import httpserver.exceptions.NotEnoughArgumentsException;

import httpserver.loggers.LoggersWrapper;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.HttpStatus.CONFLICT;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

@RestController
public class IndependentCalc {
    private long startingTime;
    private final String serverErrMsg = "Server encountered an error ! message: ";
    private final Logger indLogger;
    private final LoggersWrapper requestLogger;

    public IndependentCalc(LoggersWrapper LoggerWrapper) {
        this.requestLogger = LoggerWrapper;
        indLogger = requestLogger.getLogger("independent-logger");
    }

    @PostMapping(value = "/independent/calculate", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity Calculate(@RequestBody JsonObject jsonObj) {
        startingTime = System.currentTimeMillis();
        requestLogger.handleRequest("/independent/calculate", "POST");

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
                    String errMsg = "Error: unknown operation: " + jsonObj.getOperation();
                    indLogger.error(serverErrMsg + errMsg + " | request #" + (requestLogger.getNumOfRequests()));
                    resultObj.put("error-message", errMsg);
                    return ResponseEntity.status(CONFLICT).body(resultObj);
            }
        } catch (NotEnoughArgumentsException e) {
            String errMsg = "Error: Not enough arguments to perform the operation " + e.getMessage();
            indLogger.error(serverErrMsg + errMsg + " | request #" + (requestLogger.getNumOfRequests()));
            resultObj.put("error-message", errMsg);
            return ResponseEntity.status(CONFLICT).body(resultObj);
        } catch (TooManyArgumentsException e) {
            String errMsg = "Error: Too many arguments to perform the operation " + e.getMessage();
            indLogger.error(serverErrMsg + errMsg + " | request #" + (requestLogger.getNumOfRequests()));
            resultObj.put("error-message", errMsg);
            return ResponseEntity.status(CONFLICT).body(resultObj);
        } catch (NegativeFactorialException | DivisionByZeroException e) {
            indLogger.error(serverErrMsg + e.getMessage() + " | request #" + (requestLogger.getNumOfRequests()));
            resultObj.put("error-message", e.getMessage());
            return ResponseEntity.status(CONFLICT).body(resultObj);
        }

        resultObj.put("result", res);
        requestLogger.handleRequestDuration(System.currentTimeMillis() - startingTime);
        indLogger.info("Performing operation " + jsonObj.getOperation() + ". Result is " + res + " | request #" + (requestLogger.getNumOfRequests()));
        indLogger.debug("Performing operation: " + jsonObj.getOperation()
                + "(" + Arrays.toString(jsonObj.getArguments()).replace("[", "").replace("]", "").replace(" ","")
                + ") = " + res + " | request #" + (requestLogger.getNumOfRequests()));

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
