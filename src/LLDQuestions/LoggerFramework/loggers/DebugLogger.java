package LLDQuestions.LoggerFramework.loggers;

import LLDQuestions.LoggerFramework.AbstractLogger;
import LLDQuestions.LoggerFramework.LogSubject;
import LLDQuestions.LoggerFramework.enums.LogLevel;

public class DebugLogger extends AbstractLogger {
    private LogSubject logSubject;

    public DebugLogger(LogSubject logSubject) {
        this.logSubject = logSubject;
    }

    @Override
    public void logMessage(LogLevel level, String message) {
        if (LogLevel.DEBUG.equals(level)) {
            System.out.println(message);
            logSubject.notifyLogObservers(message);
            nextLogger.logMessage(level, message);
        } else {
            nextLogger.logMessage(level, message);
        }
    }
}
