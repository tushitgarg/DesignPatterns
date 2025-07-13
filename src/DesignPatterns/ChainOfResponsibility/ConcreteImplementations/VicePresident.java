
package DesignPatterns.ChainOfResponsibility.ConcreteImplementations;

import DesignPatterns.ChainOfResponsibility.Approver;
import DesignPatterns.ChainOfResponsibility.Expense;

public class VicePresident extends Approver {
    @Override
    public void processRequest(Expense expense) {
        System.out.println("Vice President approved expense of $" + expense.getAmount());
    }
}
