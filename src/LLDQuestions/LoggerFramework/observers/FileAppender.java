package LLDQuestions.LoggerFramework.observers;

import LLDQuestions.LoggerFramework.LogObserver;

public class FileAppender implements LogObserver {

    @Override
    public void update(String message) {
        System.out.println("File log added message:" + message);
    }
}
