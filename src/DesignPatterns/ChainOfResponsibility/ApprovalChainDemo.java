package DesignPatterns.ChainOfResponsibility;

import DesignPatterns.ChainOfResponsibility.ConcreteImplementations.Director;
import DesignPatterns.ChainOfResponsibility.ConcreteImplementations.Manager;
import DesignPatterns.ChainOfResponsibility.ConcreteImplementations.VicePresident;

public class ApprovalChainDemo {
    public static void main(String[] args) {
        Approver manager = new Manager();
        Approver director = new Director();
        Approver vp = new VicePresident();
        manager.setNext(director);
        director.setNext(vp);
        System.out.println("--- Sending expense for $500 ---");
        manager.processRequest(new Expense(500));
        System.out.println("\n--- Sending expense for $2500 ---");
        manager.processRequest(new Expense(2500));
        System.out.println("\n--- Sending expense for $10000 ---");
        manager.processRequest(new Expense(10000)); // Handled by Vice President
    }
}
