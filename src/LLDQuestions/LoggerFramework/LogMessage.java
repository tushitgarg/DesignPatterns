package LLDQuestions.LoggerFramework;

import LLDQuestions.LoggerFramework.enums.LogLevel;

public class LogMessage {
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
