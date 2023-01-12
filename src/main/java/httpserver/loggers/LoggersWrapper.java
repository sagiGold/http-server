package httpserver.loggers;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggersWrapper {
    private static final Logger requestLogger = LogManager.getLogger("request-logger");
    private static final Logger stackLogger = LogManager.getLogger("stack-logger");
    private static final Logger independentLogger = LogManager.getLogger("independent-logger");
    private static int numOfRequests = 0;

    public void handleRequest(String resource, String verb) {
        requestLogger.info("Incoming request | #" + (++numOfRequests) + " | resource: " + resource +
                " | HTTP Verb " + verb.toUpperCase() + " | request #" + numOfRequests);
    }

    public void handleRequestDuration(long duration) {
        requestLogger.debug("request #" + (numOfRequests) + " duration: " + duration + "ms" + " | request #"
                + (numOfRequests));
    }

    public Logger getLogger(String loggerName) {
        return LogManager.getLogger(loggerName);
    }

    public void setLoggerLevel(String loggerName, String level) {
        Configurator.setLevel(loggerName, level.toUpperCase());
    }

    public int getNumOfRequests() {
        return numOfRequests;
    }
}