package DesignPatterns.StrategyPattern;

/**
 * Strategy Pattern Demo
 * All classes are included here for a single, runnable example.
 * 
 * This pattern defines a family of algorithms, encapsulates each one,
 * and makes them interchangeable. Strategy lets the algorithm vary
 * independently from clients that use it.
 */
public class StrategyPattern {
    public static void main(String[] args) {
        System.out.println("--- Strategy Pattern Demo ---\n");
        
        // Create a shopping cart with items totaling $150
        ShoppingCart cart = new ShoppingCart(150);
        
        System.out.println("=== Payment Processing Demo ===");
        System.out.println("Shopping cart total: $" + cart.getAmount());
        
        // Use Credit Card payment strategy
        System.out.println("\n--- Paying with Credit Card ---");
        PaymentStrategy creditCard = new CreditCardPayment("John Doe", "1234-5678-9876-5432", "123", "12/25");
        cart.pay(creditCard);
        
        // Create another cart
        ShoppingCart cart2 = new ShoppingCart(85);
        System.out.println("\n=== Another Purchase ===");
        System.out.println("Shopping cart total: $" + cart2.getAmount());
        
        // Use PayPal payment strategy
        System.out.println("\n--- Paying with PayPal ---");
        PaymentStrategy payPal = new PayPalPayment("john.doe@example.com", "mypassword");
        cart2.pay(payPal);
        
        // Create third cart
        ShoppingCart cart3 = new ShoppingCart(200);
        System.out.println("\n=== Third Purchase ===");
        System.out.println("Shopping cart total: $" + cart3.getAmount());
        
        // Use Bank Transfer payment strategy
        System.out.println("\n--- Paying with Bank Transfer ---");
        PaymentStrategy bankTransfer = new BankTransferPayment("123456789", "BANK001");
        cart3.pay(bankTransfer);
        
        System.out.println("\n--- Demo Complete ---");
    }
}

// =================================================================
// Strategy Interface
// =================================================================
interface PaymentStrategy {
    void pay(int amount);
}

// =================================================================
// Context: Shopping Cart
// =================================================================
class ShoppingCart {
    private int amount;
    
    public ShoppingCart(int amount) {
        this.amount = amount;
    }
    
    public int getAmount() {
        return amount;
    }

    public void pay(PaymentStrategy paymentMethod) {
        paymentMethod.pay(amount);
    }
}

// =================================================================
// Concrete Strategies: Different Payment Methods
// =================================================================
class CreditCardPayment implements PaymentStrategy {
    private String name;
    private String cardNumber;
    private String cvv;
    private String dateOfExpiry;

    public CreditCardPayment(String name, String cardNumber, String cvv, String dateOfExpiry) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.dateOfExpiry = dateOfExpiry;
    }

    @Override
    public void pay(int amount) {
        System.out.println("Processing credit card payment...");
        System.out.println("Card holder: " + name);
        System.out.println("Card number: ****-****-****-" + cardNumber.substring(cardNumber.length() - 4));
        System.out.println("$" + amount + " paid successfully with Credit Card!");
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;
    private String password;

    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public void pay(int amount) {
        System.out.println("Processing PayPal payment...");
        System.out.println("Logging into PayPal account: " + email);
        System.out.println("Authentication successful!");
        System.out.println("$" + amount + " paid successfully using PayPal!");
    }
}

class BankTransferPayment implements PaymentStrategy {
    private String accountNumber;
    private String bankCode;

    public BankTransferPayment(String accountNumber, String bankCode) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
    }

    @Override
    public void pay(int amount) {
        System.out.println("Processing bank transfer...");
        System.out.println("Bank code: " + bankCode);
        System.out.println("Account: ****" + accountNumber.substring(accountNumber.length() - 4));
        System.out.println("$" + amount + " transferred successfully!");
    }
}
