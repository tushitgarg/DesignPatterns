package LLDQuestions.LoggerFramework;

import java.util.*;

/**
 * Main class to demonstrate the Logger Framework.
 * This implementation combines Chain of Responsibility and Observer patterns.
 * All classes are included here for a single, runnable example.
 */
public class LoggerFramework {
    public static void main(String[] args) {
        System.out.println("--- Logger Framework Demo ---\n");
        
        // Create the subject that will notify observers
        LogSubject logSubject = new LogSubject();
        
        // Create logger chain using Chain of Responsibility pattern
        InfoLogger infoLogger = new InfoLogger(logSubject);
        DebugLogger debugLogger = new DebugLogger(logSubject);
        ErrorLogger errorLogger = new ErrorLogger(logSubject);
        
        // Set up the chain: INFO -> DEBUG -> ERROR
        infoLogger.setNext(debugLogger);
        debugLogger.setNext(errorLogger);
        
        // Create observers (appenders) using Observer pattern
        ConsoleAppender consoleAppender = new ConsoleAppender();
        FileAppender fileAppender = new FileAppender();
        
        // Register observers with the subject
        logSubject.registerLogObserver(consoleAppender);
        logSubject.registerLogObserver(fileAppender);
        
        // Demonstrate logging at different levels
        System.out.println("=== Logging INFO message ===");
        infoLogger.logMessage(LogLevel.INFO, "This is an info message");
        
        System.out.println("\n=== Logging DEBUG message ===");
        infoLogger.logMessage(LogLevel.DEBUG, "This is a debug message");
        
        System.out.println("\n=== Logging ERROR message ===");
        infoLogger.logMessage(LogLevel.ERROR, "This is an error message");
        
        System.out.println("\n--- Demo Complete ---");
    }
}

// Enum to represent different log levels
enum LogLevel {
    INFO(1),
    DEBUG(2),
    ERROR(3);

    private int level;

    LogLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

// Message class to encapsulate log data
class LogMessage {
    private String message;
    private LogLevel level;

    public LogMessage(String message, LogLevel level) {
        this.message = message;
        this.level = level;
    }

    public String getMessage() {
        return this.message;
    }

    public LogLevel getLogLevel() {
        return this.level;
    }
}

// Observer interface for logging observers
interface LogObserver {
    void update(String message);
}

// Subject class that maintains list of observers and notifies them
class LogSubject {
    private List<LogObserver> observers;

    public LogSubject() {
        this.observers = new ArrayList<>();
    }

    public void registerLogObserver(LogObserver observer) {
        observers.add(observer);
    }

    public void removeLogObserver(LogObserver observer) {
        observers.remove(observer);
    }

    public void notifyLogObservers(String message) {
        for (LogObserver observer : observers) {
            observer.update(message);
        }
    }
}

// Abstract base class for the Chain of Responsibility pattern
abstract class AbstractLogger {
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

// Concrete logger implementations
class InfoLogger extends AbstractLogger {
    public InfoLogger(LogSubject logSubject) {
        super(logSubject);
    }

    @Override
    public void logMessage(LogLevel level, String message) {
        if (level.getLevel() >= LogLevel.INFO.getLevel()) {
            logSubject.notifyLogObservers("INFO: " + message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }
}

class DebugLogger extends AbstractLogger {
    public DebugLogger(LogSubject logSubject) {
        super(logSubject);
    }

    @Override
    public void logMessage(LogLevel level, String message) {
        if (level.getLevel() >= LogLevel.DEBUG.getLevel()) {
            logSubject.notifyLogObservers("DEBUG: " + message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }
}

class ErrorLogger extends AbstractLogger {
    public ErrorLogger(LogSubject logSubject) {
        super(logSubject);
    }

    @Override
    public void logMessage(LogLevel level, String message) {
        if (level.getLevel() >= LogLevel.ERROR.getLevel()) {
            logSubject.notifyLogObservers("ERROR: " + message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }
}

// Observer implementations (Appenders)
class ConsoleAppender implements LogObserver {
    @Override
    public void update(String message) {
        System.out.println("[CONSOLE] " + message);
    }
}

class FileAppender implements LogObserver {
    @Override
    public void update(String message) {
        System.out.println("[FILE] " + message);
    }
}
