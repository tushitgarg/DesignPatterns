package DesignPatterns.ChainOfResponsibility.ConcreteImplementations;

import DesignPatterns.ChainOfResponsibility.Approver;
import DesignPatterns.ChainOfResponsibility.Expense;

public class Manager extends Approver {
    @Override
    public void processRequest(Expense expense) {
        if (expense.getAmount() <= 1000) {
            System.out.println("Manager approved expense of " + expense.getAmount());
        } else if (nextApprover != null) {
            System.out.println("Manager cannot approve. Passing to Director...");
            nextApprover.processRequest(expense);
        }
    }
}
