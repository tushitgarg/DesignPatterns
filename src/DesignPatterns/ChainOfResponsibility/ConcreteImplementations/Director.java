
package DesignPatterns.ChainOfResponsibility.ConcreteImplementations;

import DesignPatterns.ChainOfResponsibility.Approver;
import DesignPatterns.ChainOfResponsibility.Expense;

public class Director extends Approver {
    @Override
    public void processRequest(Expense expense) {
        if (expense.getAmount() <= 5000) {
            System.out.println("Director approved expense of " + expense.getAmount());
        } else if (nextApprover != null) {
            System.out.println("Director cannot approve. Passing to Vice President...");
            nextApprover.processRequest(expense);
        }
    }
}
