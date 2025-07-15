package DesignPatterns.StrategyPattern.strategies;

public class ShoppingCart {
    private int amount;
    public ShoppingCart(int amount) {
        this.amount = amount;
    }

    public void pay(PaymentStrategy paymentMethod){
        paymentMethod.pay(amount);
    }
}
