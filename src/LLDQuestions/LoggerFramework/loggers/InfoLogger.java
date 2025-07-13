package LLDQuestions.LoggerFramework.loggers;

import LLDQuestions.LoggerFramework.AbstractLogger;
import LLDQuestions.LoggerFramework.LogSubject;
import LLDQuestions.LoggerFramework.enums.LogLevel;

public class InfoLogger extends AbstractLogger {

    public InfoLogger(LogSubject logSubject) {
        super(logSubject);
    }

    @Override
    public void logMessage(LogLevel level, String message) {
        if (level.getLevel() >= LogLevel.INFO.getLevel()) {
            logSubject.notifyLogObservers("INFO:" + message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }
}
