package LLDQuestions.LoggerFramework.observers;

import LLDQuestions.LoggerFramework.LogObserver;

public class ConsoleAppender implements LogObserver {

    @Override
    public void update(String message) {
        System.out.println("Console log added message:" + message);
    }
}
