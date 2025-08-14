package DesignPatterns.ChainOfResponsibility;

/**
 * Chain of Responsibility Pattern Demo
 * All classes are included here for a single, runnable example.
 * 
 * This pattern allows requests to be passed along a chain of handlers.
 * Each handler decides either to process the request or pass it to the next handler.
 */
public class ChainOfResponsibilityPattern {
    public static void main(String[] args) {
        System.out.println("--- Chain of Responsibility Pattern Demo ---\n");
        
        // Create the chain of approvers
        Approver manager = new Manager();
        Approver director = new Director();
        Approver vp = new VicePresident();
        
        // Set up the chain: Manager -> Director -> Vice President
        manager.setNext(director);
        director.setNext(vp);
        
        // Test different expense amounts
        System.out.println("=== Expense Approval Chain ===");
        
        System.out.println("\n--- Sending expense for $500 ---");
        manager.processRequest(new Expense(500));
        
        System.out.println("\n--- Sending expense for $2500 ---");
        manager.processRequest(new Expense(2500));
        
        System.out.println("\n--- Sending expense for $10000 ---");
        manager.processRequest(new Expense(10000));
        
        System.out.println("\n--- Demo Complete ---");
    }
}

// Request object that flows through the chain
class Expense {
    private double amount;

    public Expense(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}

// Abstract handler that defines the interface and chain structure
abstract class Approver {
    protected Approver nextApprover;

    public void setNext(Approver approver) {
        this.nextApprover = approver;
    }

    public abstract void processRequest(Expense expense);
}

// Concrete handler - Manager (handles up to $1000)
class Manager extends Approver {
    @Override
    public void processRequest(Expense expense) {
        if (expense.getAmount() <= 1000) {
            System.out.println("Manager approved expense of $" + expense.getAmount());
        } else if (nextApprover != null) {
            System.out.println("Manager cannot approve $" + expense.getAmount() + ". Passing to Director...");
            nextApprover.processRequest(expense);
        }
    }
}

// Concrete handler - Director (handles up to $5000)
class Director extends Approver {
    @Override
    public void processRequest(Expense expense) {
        if (expense.getAmount() <= 5000) {
            System.out.println("Director approved expense of $" + expense.getAmount());
        } else if (nextApprover != null) {
            System.out.println("Director cannot approve $" + expense.getAmount() + ". Passing to Vice President...");
            nextApprover.processRequest(expense);
        }
    }
}

// Concrete handler - Vice President (handles any amount)
class VicePresident extends Approver {
    @Override
    public void processRequest(Expense expense) {
        System.out.println("Vice President approved expense of $" + expense.getAmount());
    }
}
