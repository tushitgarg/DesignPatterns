package LLDQuestions.LoggerFramework;


import LLDQuestions.LoggerFramework.enums.LogLevel;
import LLDQuestions.LoggerFramework.loggers.DebugLogger;
import LLDQuestions.LoggerFramework.loggers.ErrorLogger;
import LLDQuestions.LoggerFramework.loggers.InfoLogger;
import LLDQuestions.LoggerFramework.observers.ConsoleAppender;
import LLDQuestions.LoggerFramework.observers.FileAppender;

public class client {
    public static void main(String[] args) {
        LogSubject logSubject = new LogSubject();
        InfoLogger infoLogger = new InfoLogger(logSubject);
        ErrorLogger errorLogger = new ErrorLogger(logSubject);
        DebugLogger debugLogger = new DebugLogger(logSubject);
        infoLogger.setNext(debugLogger);
        debugLogger.setNext(errorLogger);
        
        ConsoleAppender consoleAppender = new ConsoleAppender();
        FileAppender fileAppender = new FileAppender();
        logSubject.registerLogObserver(consoleAppender);
        logSubject.registerLogObserver(fileAppender);

        infoLogger.logMessage(LogLevel.INFO, "Info message");
        infoLogger.logMessage(LogLevel.ERROR, "Error message");
        infoLogger.logMessage(LogLevel.DEBUG, "Debug message");
    }
}
