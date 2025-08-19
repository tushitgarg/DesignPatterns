package DesignPatterns.SingletonPattern;

/**
 * A thread-safe Singleton class.
 * The constructor is private to prevent direct instantiation.
 * The getInstance() method provides the global access point.
 */
class SettingsManager {
    // The single, volatile instance of the class.
    // 'volatile' ensures that changes to this variable are visible to all threads.
    private static volatile SettingsManager instance;

    // Private constructor to prevent instantiation from outside.
    private SettingsManager() {
        // Initialization logic here (e.g., load settings from a file)
        System.out.println("SettingsManager instance created.");
    }

    /**
     * The global access point. Uses double-checked locking for thread-safe,
     * lazy initialization.
     */
    public static SettingsManager getInstance() {
        // First check (no lock): avoids locking every time if instance is already created.
        if (instance == null) {
            // Second check (with lock): ensures only one thread can create the instance.
            synchronized (SettingsManager.class) {
                if (instance == null) {
                    instance = new SettingsManager();
                }
            }
        }
        return instance;
    }

    public void displaySetting() {
        System.out.println("Displaying a setting from the single instance.");
    }
}

/**
 * Demo class to show that multiple calls to getInstance() return the same object.
 */
public class SingletonDemo {
    public static void main(String[] args) {
        // Get the instance multiple times
        SettingsManager manager1 = SettingsManager.getInstance();
        SettingsManager manager2 = SettingsManager.getInstance();

        manager1.displaySetting();

        // Check if both references point to the same object
        if (manager1 == manager2) {
            System.out.println("Both manager1 and manager2 are the same instance.");
        } else {
            System.out.println("Singleton failed: different instances were created.");
        }
    }
}

