package LLDQuestions.LoggerFramework.loggers;

import LLDQuestions.LoggerFramework.AbstractLogger;
import LLDQuestions.LoggerFramework.LogSubject;
import LLDQuestions.LoggerFramework.enums.LogLevel;

public class DebugLogger extends AbstractLogger {
    public DebugLogger(LogSubject logSubject) {
        super(logSubject);
    }

    @Override
    public void logMessage(LogLevel level, String message) {
        if (level.getLevel() >= LogLevel.DEBUG.getLevel()) {
            logSubject.notifyLogObservers("Debug:" + message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }
}
