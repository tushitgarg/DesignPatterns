package DesignPatterns.ObserverPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern Demo
 * All classes are included here for a single, runnable example.
 * 
 * This pattern defines a one-to-many dependency between objects so that when one
 * object changes state, all its dependents are notified and updated automatically.
 */
public class ObserverPattern {
    public static void main(String[] args) {
        System.out.println("--- Observer Pattern Demo ---\n");
        
        // 1. Create the Subject (Observable)
        WeatherData weatherData = new WeatherData();

        // 2. Create the Observers and register them
        CurrentConditionsDisplay currentDisplay = new CurrentConditionsDisplay(weatherData);
        ForecastDisplay forecastDisplay = new ForecastDisplay(weatherData);

        // 3. Simulate new weather measurements. The displays will be notified automatically.
        System.out.println("=== First Weather Update ===");
        weatherData.setMeasurements(80, 65, 30.4f);

        System.out.println("\n=== Second Weather Update ===");
        weatherData.setMeasurements(82, 70, 29.2f);

        System.out.println("\n=== Third Weather Update ===");
        weatherData.setMeasurements(78, 90, 29.8f);
        
        System.out.println("\n--- Demo Complete ---");
    }
}

// =================================================================
// Observer Interface
// =================================================================
interface Observer {
    void update();
}

// =================================================================
// Subject Interface
// =================================================================
interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}

// =================================================================
// Display Interface
// =================================================================
interface DisplayElement {
    void display();
}

// =================================================================
// Concrete Subject: Weather Data
// =================================================================
class WeatherData implements Subject {
    private List<Observer> observers;
    private float temperature;
    private float humidity;
    private float pressure;

    public WeatherData() {
        this.observers = new ArrayList<Observer>();
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public void measurementsChanged() {
        notifyObservers();
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        measurementsChanged();
    }

    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
    public float getPressure() { return pressure; }
}

// =================================================================
// Concrete Observer: Current Conditions Display
// =================================================================
class CurrentConditionsDisplay implements Observer, DisplayElement {
    private float temperature;
    private float humidity;
    private WeatherData weatherData;

    public CurrentConditionsDisplay(WeatherData weatherData) {
        this.weatherData = weatherData;
        weatherData.registerObserver(this);
    }

    @Override
    public void update() {
        this.temperature = weatherData.getTemperature();
        this.humidity = weatherData.getHumidity();
        display();
    }

    @Override
    public void display() {
        System.out.println("Current conditions: " + temperature + "°F and " + humidity + "% humidity");
    }
}

// =================================================================
// Concrete Observer: Forecast Display
// =================================================================
class ForecastDisplay implements Observer, DisplayElement {
    private float currentPressure = 29.92f;
    private float lastPressure;
    private WeatherData weatherData;

    public ForecastDisplay(WeatherData weatherData) {
        this.weatherData = weatherData;
        weatherData.registerObserver(this);
    }

    @Override
    public void update() {
        lastPressure = currentPressure;
        currentPressure = weatherData.getPressure();
        display();
    }

    @Override
    public void display() {
        System.out.print("Forecast: ");
        if (currentPressure > lastPressure) {
            System.out.println("Improving weather on the way!");
        } else if (currentPressure == lastPressure) {
            System.out.println("More of the same");
        } else if (currentPressure < lastPressure) {
            System.out.println("Watch out for cooler, rainy weather");
        }
    }
}
