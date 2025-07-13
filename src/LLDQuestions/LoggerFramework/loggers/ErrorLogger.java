package LLDQuestions.LoggerFramework.loggers;

import LLDQuestions.LoggerFramework.AbstractLogger;
import LLDQuestions.LoggerFramework.LogSubject;
import LLDQuestions.LoggerFramework.enums.LogLevel;

public class ErrorLogger extends AbstractLogger {

    public ErrorLogger(LogSubject logSubject) {
        super(logSubject);
    }

    @Override
    public void logMessage(LogLevel level, String message) {
        if (level.getLevel() >= LogLevel.ERROR.getLevel()) {
            logSubject.notifyLogObservers("Error:" + message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }
}
