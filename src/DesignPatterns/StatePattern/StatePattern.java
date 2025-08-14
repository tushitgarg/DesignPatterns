package DesignPatterns.StatePattern;

/**
 * State Pattern Demo
 * All classes are included here for a single, runnable example.
 * 
 * This pattern allows an object to alter its behavior when its internal state changes.
 * The object will appear to change its class.
 */
public class StatePattern {
    public static void main(String[] args) {
        System.out.println("--- State Pattern Demo ---\n");
        
        // Create a gumball machine with 3 gumballs
        GumballMachine gumballMachine = new GumballMachine(3);
        
        System.out.println("=== Gumball Machine Interactions ===");
        System.out.println(gumballMachine);
        
        // Test sequence 1: Normal operation
        System.out.println("\n--- Sequence 1: Insert quarter and turn crank ---");
        gumballMachine.insertQuarter();
        gumballMachine.turnCrank();
        
        System.out.println("\n" + gumballMachine);
        
        // Test sequence 2: Insert quarter, then eject it
        System.out.println("\n--- Sequence 2: Insert quarter, then eject ---");
        gumballMachine.insertQuarter();
        gumballMachine.ejectQuarter();
        
        // Test sequence 3: Try to turn crank without quarter
        System.out.println("\n--- Sequence 3: Turn crank without quarter ---");
        gumballMachine.turnCrank();
        
        // Test sequence 4: Insert quarter and buy another gumball
        System.out.println("\n--- Sequence 4: Buy another gumball ---");
        gumballMachine.insertQuarter();
        gumballMachine.turnCrank();
        
        System.out.println("\n" + gumballMachine);
        
        System.out.println("\n--- Demo Complete ---");
    }
}

// =================================================================
// State Interface
// =================================================================
interface State {
    void insertQuarter();
    void ejectQuarter();
    void turnCrank();
    void dispense();
}

// =================================================================
// Context: Gumball Machine
// =================================================================
class GumballMachine {
    State soldOutState;
    State noQuarterState;
    State hasQuarterState;
    State soldState;
    
    State currentState;
    int count = 0;

    public GumballMachine(int numberGumballs) {
        this.count = numberGumballs;
        soldOutState = new SoldOutState(this);
        noQuarterState = new NoQuarterState(this);
        hasQuarterState = new HasQuarterState(this);
        soldState = new SoldState(this);

        if (numberGumballs > 0) {
            currentState = noQuarterState;
        } else {
            currentState = soldOutState;
        }
    }

    public void insertQuarter() {
        currentState.insertQuarter();
    }

    public void ejectQuarter() {
        currentState.ejectQuarter();
    }

    public void turnCrank() {
        currentState.turnCrank();
        currentState.dispense();
    }

    public void setState(State state) {
        this.currentState = state;
    }

    public void releaseBall() {
        System.out.println("A gumball comes rolling out the slot...");
        if (count != 0) {
            count = count - 1;
        }
    }

    public int getCount() {
        return count;
    }

    public State getNoQuarterState() { return noQuarterState; }
    public State getHasQuarterState() { return hasQuarterState; }
    public State getSoldState() { return soldState; }
    public State getSoldOutState() { return soldOutState; }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("\nMighty Gumball, Inc.");
        result.append("\nJava-enabled Standing Gumball Model #2004");
        result.append("\nInventory: " + count + " gumball");
        if (count != 1) {
            result.append("s");
        }
        result.append("\n");
        if (currentState == soldOutState) {
            result.append("Machine is sold out");
        } else {
            result.append("Machine is waiting for quarter");
        }
        result.append("\n");
        return result.toString();
    }
}

// =================================================================
// Concrete States
// =================================================================
class NoQuarterState implements State {
    GumballMachine gumballMachine;
    
    public NoQuarterState(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    public void insertQuarter() {
        System.out.println("You inserted a quarter");
        gumballMachine.setState(gumballMachine.getHasQuarterState());
    }

    public void ejectQuarter() {
        System.out.println("You haven't inserted a quarter");
    }

    public void turnCrank() {
        System.out.println("You turned, but there's no quarter");
    }

    public void dispense() {
        System.out.println("You need to pay first");
    }
}

class HasQuarterState implements State {
    GumballMachine gumballMachine;
    
    public HasQuarterState(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    public void insertQuarter() {
        System.out.println("You can't insert another quarter");
    }

    public void ejectQuarter() {
        System.out.println("Quarter returned");
        gumballMachine.setState(gumballMachine.getNoQuarterState());
    }

    public void turnCrank() {
        System.out.println("You turned...");
        gumballMachine.setState(gumballMachine.getSoldState());
    }

    public void dispense() {
        System.out.println("No gumball dispensed");
    }
}

class SoldState implements State {
    GumballMachine gumballMachine;
    
    public SoldState(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    public void insertQuarter() {
        System.out.println("Please wait, we're already giving you a gumball");
    }

    public void ejectQuarter() {
        System.out.println("Sorry, you already turned the crank");
    }

    public void turnCrank() {
        System.out.println("Turning twice doesn't get you another gumball!");
    }

    public void dispense() {
        gumballMachine.releaseBall();
        if (gumballMachine.getCount() > 0) {
            gumballMachine.setState(gumballMachine.getNoQuarterState());
        } else {
            System.out.println("Oops, out of gumballs!");
            gumballMachine.setState(gumballMachine.getSoldOutState());
        }
    }
}

class SoldOutState implements State {
    GumballMachine gumballMachine;
    
    public SoldOutState(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    public void insertQuarter() {
        System.out.println("You can't insert a quarter, the machine is sold out");
    }

    public void ejectQuarter() {
        System.out.println("You can't eject, you haven't inserted a quarter yet");
    }

    public void turnCrank() {
        System.out.println("You turned, but there are no gumballs");
    }

    public void dispense() {
        System.out.println("No gumball dispensed");
    }
}
