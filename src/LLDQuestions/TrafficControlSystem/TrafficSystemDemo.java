package LLDQuestions.TrafficControlSystem;

/**
 * Main class to demonstrate the Traffic Light Control System.
 * All classes are included here for a single, runnable example.
 */
public class TrafficSystemDemo {
    public static void main(String[] args) {
        // Create the context object (the traffic light)
        TrafficLight trafficLight = new TrafficLight();

        // Run the simulation for a few cycles
        System.out.println("Starting Traffic Light Simulation...");
        for (int i = 0; i < 6; i++) { // Run for 6 state changes
            trafficLight.change();
        }
        System.out.println("\nSimulation complete.");
    }
}


// The State Interface: Defines a single action for the light.
interface TrafficLightState {
    /**
     * Handles the current state's behavior and transitions to the next state.
     * @param light The context (the traffic light itself).
     */
    void handle(TrafficLight light);
}


// The Context: TrafficLight
// This class holds the current state and delegates the behavior to it.
class TrafficLight {
    private TrafficLightState currentState;

    // All possible states are instantiated once
    private final TrafficLightState redState;
    private final TrafficLightState greenState;
    private final TrafficLightState yellowState;

    public TrafficLight() {
        // Create all possible state objects
        this.redState = new RedState();
        this.greenState = new GreenState();
        this.yellowState = new YellowState();

        // Set the initial state to RED
        this.currentState = redState;
    }

    /**
     * The main action method, which delegates the work to the current state.
     */
    public void change() {
        currentState.handle(this);
    }

    // --- GETTERS AND SETTERS ---
    public void setState(TrafficLightState state) {
        this.currentState = state;
    }

    public TrafficLightState getRedState() {
        return redState;
    }

    public TrafficLightState getGreenState() {
        return greenState;
    }

    public TrafficLightState getYellowState() {
        return yellowState;
    }
}


// --- CONCRETE STATE IMPLEMENTATIONS ---

// 1. RedState: The light is RED.
class RedState implements TrafficLightState {
    @Override
    public void handle(TrafficLight light) {
        System.out.println("Light is RED. Stop! (Waiting for 5 seconds)");
        try {
            // Simulate the duration of the red light
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Transition to the next state: GREEN
        System.out.println("Transitioning from RED to GREEN...\n");
        light.setState(light.getGreenState());
    }
}


// 2. GreenState: The light is GREEN.
class GreenState implements TrafficLightState {
    @Override
    public void handle(TrafficLight light) {
        System.out.println("Light is GREEN. Go! (Waiting for 5 seconds)");
        try {
            // Simulate the duration of the green light
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Transition to the next state: YELLOW
        System.out.println("Transitioning from GREEN to YELLOW...\n");
        light.setState(light.getYellowState());
    }
}


// 3. YellowState: The light is YELLOW.
class YellowState implements TrafficLightState {
    @Override
    public void handle(TrafficLight light) {
        System.out.println("Light is YELLOW. Prepare to stop! (Waiting for 2 seconds)");
        try {
            // Simulate the duration of the yellow light
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Transition to the next state: RED
        System.out.println("Transitioning from YELLOW to RED...\n");
        light.setState(light.getRedState());
    }
}

