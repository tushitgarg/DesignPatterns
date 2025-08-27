package DesignPatterns.CommandPattern;

/**
 * Main demo for the Command Pattern.
 */
public class RemoteControlDemo {
    public static void main(String[] args) {
        // The Invoker: It holds a command but doesn't know what it does.
        SimpleRemoteControl remote = new SimpleRemoteControl();

        // The Receiver: The object that will perform the actual work.
        Light light = new Light();

        // The Command: Binds the receiver (light) to an action (turn on).
        Command lightOn = new LightOnCommand(light);

        // Configure the invoker with the command.
        remote.setCommand(lightOn);

        // Press the button on the remote. The remote calls execute(), which
        // in turn calls the action on the light.
        System.out.println("--- Pressing the remote button ---");
        remote.buttonWasPressed(); // Output: Light is ON

        // Now, let's control a Garage Door with the same remote
        GarageDoor garageDoor = new GarageDoor();
        Command garageOpen = new GarageDoorOpenCommand(garageDoor);

        remote.setCommand(garageOpen);
        System.out.println("\n--- Reconfigured remote. Pressing the button again ---");
        remote.buttonWasPressed(); // Output: Garage Door is Open
    }
}

// 1. The Command Interface
interface Command {
    void execute();
}

// 2. The Receiver: Knows how to perform the actual operations.
class Light {
    public void on() { System.out.println("Light is ON"); }
    public void off() { System.out.println("Light is OFF"); }
}

class GarageDoor {
    public void open() { System.out.println("Garage Door is Open"); }
    public void close() { System.out.println("Garage Door is Closed"); }
}

// 3. Concrete Commands: Implements the Command interface.
class LightOnCommand implements Command {
    private Light light; // A reference to the receiver

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();
    }
}

class GarageDoorOpenCommand implements Command {
    private GarageDoor garageDoor;

    public GarageDoorOpenCommand(GarageDoor garageDoor) {
        this.garageDoor = garageDoor;
    }

    @Override
    public void execute() {
        garageDoor.open();
    }
}

// 4. The Invoker: Holds a command and calls its execute() method.
class SimpleRemoteControl {
    private Command slot;

    public void setCommand(Command command) {
        this.slot = command;
    }

    public void buttonWasPressed() {
        if (slot != null) {
            slot.execute();
        }
    }
}

