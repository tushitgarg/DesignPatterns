package LLDQuestions.VendingMachine;

import java.util.HashMap;
import java.util.Map;

/**
 * Main class to demonstrate the Vending Machine.
 * All classes are included here for a single, runnable example.
 */
public class VendingMachineDemo {

    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine();

        // --- SCENARIO 1: Successful Purchase ---
        System.out.println("----- SCENARIO 1: Successful Purchase -----");
        System.out.println("Current State: " + machine.getCurrentState().getClass().getSimpleName());
        System.out.println("Inventory (Coke): " + machine.getInventory().get("Coke"));
        machine.selectItem("Coke");
        machine.insertMoney(25);
        System.out.println("-----------------------------------------\n");


        // --- SCENARIO 2: Attempt to select item without enough money ---
        System.out.println("----- SCENARIO 2: Not enough money -----");
        VendingMachine machine2 = new VendingMachine();
        System.out.println("Current State: " + machine2.getCurrentState().getClass().getSimpleName());
        machine2.selectItem("Pepsi");
        machine2.insertMoney(10); // Price of Pepsi is 35
        System.out.println("--------------------------------------\n");


        // --- SCENARIO 3: Item is out of stock ---
        System.out.println("----- SCENARIO 3: Item out of stock -----");
        VendingMachine machine3 = new VendingMachine();
        // Manually set stock to 0 for demonstration
        machine3.getInventory().put("Garden Salsa", 0);
        System.out.println("Current State: " + machine3.getCurrentState().getClass().getSimpleName());
        machine3.selectItem("Garden Salsa");
        System.out.println("-----------------------------------------\n");


        // --- SCENARIO 4: Cancel transaction ---
        System.out.println("----- SCENARIO 4: Cancel Transaction -----");
        VendingMachine machine4 = new VendingMachine();
        System.out.println("Current State: " + machine4.getCurrentState().getClass().getSimpleName());
        machine4.selectItem("Coke");
        machine4.cancelSelection();
        System.out.println("Current State: " + machine4.getCurrentState().getClass().getSimpleName());
        System.out.println("------------------------------------------\n");
    }
}

// Represents an item available in the vending machine
class Item {
    private String name;
    private int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}


// The State Interface: Defines all possible actions a user can take.
interface State {
    void selectItem(String itemName);
    void insertMoney(int amount);
    void dispenseItem();
    void cancelSelection();
}


// The Context: VendingMachine
// This is the main class that the client interacts with. It delegates all
// actions to its current state object.
class VendingMachine {
    private State currentState;
    private Map<String, Integer> inventory;
    private Map<String, Item> items;
    private Item selectedItem;
    private int insertedMoney;

    // All possible states of the machine
    private State idleState;
    private State hasMoneyState;
    private State dispensingState;
    private State outOfStockState;

    public VendingMachine() {
        // Initialize inventory and items
        inventory = new HashMap<>();
        inventory.put("Coke", 5);
        inventory.put("Pepsi", 3);
        inventory.put("Garden Salsa", 2);

        items = new HashMap<>();
        items.put("Coke", new Item("Coke", 25));
        items.put("Pepsi", new Item("Pepsi", 35));
        items.put("Garden Salsa", new Item("Garden Salsa", 45));

        // Create state objects
        idleState = new IdleState(this);
        hasMoneyState = new HasMoneyState(this);
        dispensingState = new DispensingState(this);
        outOfStockState = new OutOfStockState(this);

        // Initial state is Idle
        this.currentState = idleState;
        this.insertedMoney = 0;
    }

    // --- ACTIONS DELEGATED TO THE CURRENT STATE ---
    public void selectItem(String itemName) {
        this.selectedItem = items.get(itemName);
        currentState.selectItem(itemName);
    }

    public void insertMoney(int amount) {
        currentState.insertMoney(amount);
    }

    public void dispenseItem() {
        currentState.dispenseItem();
    }

    public void cancelSelection() {
        currentState.cancelSelection();
    }

    // --- HELPER METHODS USED BY STATE OBJECTS ---
    public void setState(State state) {
        this.currentState = state;
    }

    public void releaseItem() {
        if (selectedItem != null) {
            System.out.println("Dispensing " + selectedItem.getName());
            inventory.put(selectedItem.getName(), inventory.get(selectedItem.getName()) - 1);
            reset();
        }
    }

    public void returnMoney() {
        System.out.println("Returning money: " + insertedMoney);
        reset();
    }

    public void reset() {
        this.insertedMoney = 0;
        this.selectedItem = null;
        this.currentState = idleState;
    }

    // --- GETTERS ---
    public State getIdleState() { return idleState; }
    public State getHasMoneyState() { return hasMoneyState; }
    public State getDispensingState() { return dispensingState; }
    public State getOutOfStockState() { return outOfStockState; }
    public Map<String, Integer> getInventory() { return inventory; }
    public Item getSelectedItem() { return selectedItem; }
    public int getInsertedMoney() { return insertedMoney; }
    public void setInsertedMoney(int amount) { this.insertedMoney = amount; }
    public State getCurrentState() { return currentState; }
}


// --- CONCRETE STATE IMPLEMENTATIONS ---

// 1. IdleState: The machine is waiting for an item selection.
class IdleState implements State {
    private VendingMachine machine;

    public IdleState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void selectItem(String itemName) {
        if (machine.getInventory().getOrDefault(itemName, 0) > 0) {
            System.out.println("Item '" + itemName + "' selected. Please insert money.");
            machine.setState(machine.getHasMoneyState());
        } else {
            System.out.println("Sorry, '" + itemName + "' is out of stock.");
            machine.setState(machine.getOutOfStockState());
        }
    }

    @Override
    public void insertMoney(int amount) {
        System.out.println("Please select an item first.");
    }

    @Override
    public void dispenseItem() {
        System.out.println("Please select an item and insert money first.");
    }

    @Override
    public void cancelSelection() {
        System.out.println("Nothing to cancel.");
    }
}


// 2. HasMoneyState: An item has been selected, waiting for money.
class HasMoneyState implements State {
    private VendingMachine machine;

    public HasMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void selectItem(String itemName) {
        System.out.println("Already processing a selection. Please insert money or cancel.");
    }

    @Override
    public void insertMoney(int amount) {
        machine.setInsertedMoney(machine.getInsertedMoney() + amount);
        System.out.println("Inserted " + amount + ". Total: " + machine.getInsertedMoney());

        if (machine.getInsertedMoney() >= machine.getSelectedItem().getPrice()) {
            machine.setState(machine.getDispensingState());
            machine.dispenseItem(); // Auto-dispense when enough money is inserted
        } else {
            System.out.println("Not enough money. Price: " + machine.getSelectedItem().getPrice() + ", Inserted: " + machine.getInsertedMoney());
        }
    }

    @Override
    public void dispenseItem() {
        System.out.println("Please insert more money.");
    }

    @Override
    public void cancelSelection() {
        System.out.println("Transaction cancelled.");
        machine.returnMoney();
    }
}


// 3. DispensingState: The machine is dispensing the item.
class DispensingState implements State {
    private VendingMachine machine;

    public DispensingState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void selectItem(String itemName) {
        System.out.println("Cannot select another item while dispensing.");
    }

    @Override
    public void insertMoney(int amount) {
        System.out.println("Cannot insert money while dispensing.");
    }

    @Override
    public void dispenseItem() {
        machine.releaseItem();
    }

    @Override
    public void cancelSelection() {
        System.out.println("Cannot cancel while dispensing.");
    }
}


// 4. OutOfStockState: The selected item is not available.
class OutOfStockState implements State {
    private VendingMachine machine;

    public OutOfStockState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void selectItem(String itemName) {
        System.out.println("This item is out of stock, please select another.");
    }

    @Override
    public void insertMoney(int amount) {
        System.out.println("Cannot insert money, item is out of stock.");
    }

    @Override
    public void dispenseItem() {
        System.out.println("Cannot dispense, item is out of stock.");
    }

    @Override
    public void cancelSelection() {
        System.out.println("Returning to idle state.");
        machine.reset();
    }
}
