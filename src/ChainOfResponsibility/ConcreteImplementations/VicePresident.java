
package ChainOfResponsibility.ConcreteImplementations;

import ChainOfResponsibility.Approver;
import ChainOfResponsibility.Expense;

public class VicePresident extends Approver {
    @Override
    public void processRequest(Expense expense) {
        System.out.println("Vice President approved expense of $" + expense.getAmount());
    }
}
