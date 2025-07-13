package ChainOfResponsibility;

public abstract class Approver {
    protected Approver nextApprover;

    public void setNext(Approver approver) {
        this.nextApprover = approver;
    }

    public abstract void processRequest(Expense expense);
}
