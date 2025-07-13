package LLDQuestions.LoggerFramework;

import LLDQuestions.LoggerFramework.enums.LogLevel;

public abstract class AbstractLogger {
    protected AbstractLogger nextLogger;
    protected LogSubject logSubject;

    public AbstractLogger(LogSubject logSubject) {
        this.logSubject = logSubject;
    }

    public void setNext(AbstractLogger logger) {
        this.nextLogger = logger;
    }

    public abstract void logMessage(LogLevel level, String message);

}
