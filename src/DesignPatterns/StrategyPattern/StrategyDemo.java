package DesignPatterns.StrategyPattern;
import DesignPatterns.StrategyPattern.strategies.CreditCardPayment;
import DesignPatterns.StrategyPattern.strategies.PayPalPayment;
import DesignPatterns.StrategyPattern.strategies.PaymentStrategy;
import DesignPatterns.StrategyPattern.strategies.ShoppingCart;

public class StrategyDemo {
    public static void main(String[] args) throws Exception {
        ShoppingCart cart = new ShoppingCart(100);
        PaymentStrategy creditCard = new CreditCardPayment("John Doe", "1234-5678-9876-5432");
        cart.pay(creditCard);
        PaymentStrategy payPal = new PayPalPayment("john.doe@example.com");
        cart.pay(payPal);
    }
}