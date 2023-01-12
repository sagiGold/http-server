package httpserver.loggers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggerController {
    private final LoggersWrapper loggersWrapper;
    private long startingTime;

    public LoggerController(LoggersWrapper loggersWrapper) {
        this.loggersWrapper = loggersWrapper;
    }

    @GetMapping("logs/level")
    public ResponseEntity getLogLevel(@RequestParam("logger-name") String loggerName) {
        startingTime = System.currentTimeMillis();

        ResponseEntity response;
        Logger logger = loggersWrapper.getLogger(loggerName);

        loggersWrapper.handleRequest("/logs/level", "GET");

        if (logger == null) {
            response = ResponseEntity.badRequest().body("Couldn't find logger: " + loggerName);
        }
        else {
            response = ResponseEntity.ok(logger.getLevel().toString());
        }

        loggersWrapper.handleRequestDuration(System.currentTimeMillis() - startingTime);

        return response;
    }

    @PutMapping("logs/level")
    public ResponseEntity setLogLevel(@RequestParam("logger-name") String loggerName, @RequestParam("logger-level") String level) {
        startingTime = System.currentTimeMillis();
        loggersWrapper.handleRequest("/logs/level", "PUT");

        ResponseEntity response;
        Logger logger = loggersWrapper.getLogger(loggerName);

        if (logger == null || Level.getLevel(level) == null) {
            response = ResponseEntity.badRequest().body("Couldn't find logger: " + loggerName + " or invalid level :" + level);
        }
        else {
            loggersWrapper.setLoggerLevel(loggerName, level);
            response = ResponseEntity.ok(logger.getLevel().toString());
        }

        loggersWrapper.handleRequestDuration(System.currentTimeMillis() - startingTime);

        return response;
    }
}
