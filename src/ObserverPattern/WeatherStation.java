package ObserverPattern;

import ObserverPattern.concreteImplementations.WeatherData;
import ObserverPattern.concreteImplementations.CurrentConditionsDisplay;

public class WeatherStation {
    public static void main(String[] args) {
        // 1. Create the Subject
        WeatherData weatherData = new WeatherData();

        // 2. Create the Observer and register it
        CurrentConditionsDisplay currentDisplay = new CurrentConditionsDisplay(weatherData);

        // 3. Simulate new weather measurements. The display will be notified automatically.
        System.out.println("--- Weather Update ---");
        weatherData.setMeasurements(80, 65, 30.4f);

        System.out.println("\n--- Another Weather Update ---");
        weatherData.setMeasurements(82, 70, 29.2f);
    }
}