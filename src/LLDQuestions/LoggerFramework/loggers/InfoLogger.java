package LLDQuestions.LoggerFramework.loggers;

import java.lang.ProcessHandle.Info;

import LLDQuestions.LoggerFramework.AbstractLogger;
import LLDQuestions.LoggerFramework.LogSubject;
import LLDQuestions.LoggerFramework.enums.LogLevel;

public class InfoLogger extends AbstractLogger {

    private LogSubject logSubject;

    public InfoLogger(LogSubject logSubject) {
        this.logSubject = logSubject;
    }

    @Override
    public void logMessage(LogLevel level, String message) {
        if (LogLevel.INFO.equals(level)) {
            System.out.println(message);
            logSubject.notifyLogObservers(message);
            nextLogger.logMessage(level, message);
        } else {
            nextLogger.logMessage(level, message);
        }
    }
}
