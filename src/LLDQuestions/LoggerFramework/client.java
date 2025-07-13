package LLDQuestions.LoggerFramework;


import LLDQuestions.LoggerFramework.loggers.DebugLogger;
import LLDQuestions.LoggerFramework.loggers.ErrorLogger;
import LLDQuestions.LoggerFramework.loggers.InfoLogger;
import LLDQuestions.LoggerFramework.observers.ConsoleAppender;
import LLDQuestions.LoggerFramework.observers.FileAppender;

public class client {
    public static void main(String[] args) {
        InfoLogger infoLogger = new InfoLogger();
        ErrorLogger errorLogger = new ErrorLogger();
        DebugLogger debugLogger = new DebugLogger();
        infoLogger.setNext(debugLogger);
        debugLogger.setNext(errorLogger);
        LogSubject logSubject = new LogSubject();
        ConsoleAppender consoleAppender = new ConsoleAppender();
        FileAppender fileAppender = new FileAppender();
        logSubject.registerLogObserver(consoleAppender);
        logSubject.registerLogObserver(fileAppender);
    }
}
