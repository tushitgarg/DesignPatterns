package StrategyPattern;
import StrategyPattern.strategies.CreditCardPayment;
import StrategyPattern.strategies.PayPalPayment;
import StrategyPattern.strategies.PaymentStrategy;
import StrategyPattern.strategies.ShoppingCart;

public class StrategyDemo {
    public static void main(String[] args) throws Exception {
        ShoppingCart cart = new ShoppingCart(100);
        PaymentStrategy creditCard = new CreditCardPayment("John Doe", "1234-5678-9876-5432");
        cart.pay(creditCard);
        PaymentStrategy payPal = new PayPalPayment("john.doe@example.com");
        cart.pay(payPal);
    }
}