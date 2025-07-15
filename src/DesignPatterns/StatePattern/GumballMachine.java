package DesignPatterns.StatePattern;

import DesignPatterns.StatePattern.states.HasQuarterState;
import DesignPatterns.StatePattern.states.NoQuarterState;

public class GumballMachine {
    State soldOutState;
    State noQuarterState;
    State hasQuarterState;
    State soldState;
    State currentState;
    int count = 0;

    public GumballMachine(int numberGumballs) {
        this.count = numberGumballs;
        noQuarterState = new NoQuarterState(this);
        hasQuarterState = new HasQuarterState(this);

        if (numberGumballs > 0) {
            currentState = noQuarterState;
        } else {
            // currentState = soldOutState;
        }
    }

    public void setState(State state) {
        this.currentState = state;
    }

    public void turnCrank() {
        currentState.turnCrank();
        // The SoldState would handle dispensing after the crank is turned
        // currentState.dispense();
    }

    public State getNoQuarterState() { return noQuarterState; }
    public State getHasQuarterState() { return hasQuarterState; }
    public State getSoldState() { return soldState; }
    
}
