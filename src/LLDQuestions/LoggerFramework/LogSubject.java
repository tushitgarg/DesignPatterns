package LLDQuestions.LoggerFramework;

import java.util.ArrayList;
import java.util.List;

public class LogSubject {
    List<LogObserver> observers;

    public LogSubject() {
        this.observers = new ArrayList<>();
    }

    public void registerLogObserver(LogObserver o) {
        observers.add(o);
    }
    public void removeLogObserver(LogObserver o) {
        observers.remove(o);
    }
    public void notifyLogObservers(String message) {
        for(LogObserver observer: observers) {
            observer.update(message);
        }
    }
}
