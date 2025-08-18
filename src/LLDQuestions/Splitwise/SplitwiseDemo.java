import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class to demonstrate the Splitwise application.
 * All classes are included here for a single, runnable example.
 */
public class SplitwiseDemo {
    public static void main(String[] args) {
        // 1. Setup Users and ExpenseManager
        ExpenseManager expenseManager = new ExpenseManager();
        expenseManager.addUser(new User("u1", "Tushit"));
        expenseManager.addUser(new User("u2", "Ankit"));
        expenseManager.addUser(new User("u3", "Deepak"));
        expenseManager.addUser(new User("u4", "Rohit"));

        System.out.println("----- Initial Balances -----");
        expenseManager.showBalances();
        System.out.println();

        // 2. Add an EQUAL expense
        System.out.println("----- Adding an EQUAL Expense: 1000 paid by Tushit, shared by all 4 -----");
        List<Split> equalSplits = new ArrayList<>();
        equalSplits.add(new EqualSplit(expenseManager.getUser("u1")));
        equalSplits.add(new EqualSplit(expenseManager.getUser("u2")));
        equalSplits.add(new EqualSplit(expenseManager.getUser("u3")));
        equalSplits.add(new EqualSplit(expenseManager.getUser("u4")));
        expenseManager.addExpense(ExpenseType.EQUAL, 1000, expenseManager.getUser("u1"), equalSplits);
        expenseManager.showBalances();
        System.out.println();

        // 3. Add an EXACT expense
        System.out.println("----- Adding an EXACT Expense: 1250 paid by Ankit, shared between Tushit(370) and Deepak(880) -----");
        List<Split> exactSplits = new ArrayList<>();
        exactSplits.add(new ExactSplit(expenseManager.getUser("u1"), 370));
        exactSplits.add(new ExactSplit(expenseManager.getUser("u3"), 880));
        expenseManager.addExpense(ExpenseType.EXACT, 1250, expenseManager.getUser("u2"), exactSplits);
        expenseManager.showBalances();
        System.out.println();

        // 4. Add a PERCENT expense
        System.out.println("----- Adding a PERCENT Expense: 1200 paid by Rohit, shared Tushit(40%), Ankit(20%), Deepak(20%), Rohit(20%) -----");
        List<Split> percentSplits = new ArrayList<>();
        percentSplits.add(new PercentSplit(expenseManager.getUser("u1"), 40));
        percentSplits.add(new PercentSplit(expenseManager.getUser("u2"), 20));
        percentSplits.add(new PercentSplit(expenseManager.getUser("u3"), 20));
        percentSplits.add(new PercentSplit(expenseManager.getUser("u4"), 20));
        expenseManager.addExpense(ExpenseType.PERCENT, 1200, expenseManager.getUser("u4"), percentSplits);
        expenseManager.showBalances();
    }
}


// --- MODELS ---
class User {
    private String id;
    private String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public String getId() { return id; }
    public String getName() { return name; }
}

// Using an abstract class for Split allows common fields and behavior
abstract class Split {
    private User user;
    protected double amount; // The calculated share for this user

    public Split(User user) {
        this.user = user;
    }
    public User getUser() { return user; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}

class EqualSplit extends Split {
    public EqualSplit(User user) {
        super(user);
    }
}

class ExactSplit extends Split {
    public ExactSplit(User user, double amount) {
        super(user);
        this.amount = amount; // For exact, the amount is provided upfront
    }
}

class PercentSplit extends Split {
    private double percent;

    public PercentSplit(User user, double percent) {
        super(user);
        this.percent = percent;
    }
    public double getPercent() { return percent; }
}

class Expense {
    private String description;
    private double amount;
    private User paidBy;
    private List<Split> splits;
    private ExpenseType type;

    public Expense(double amount, User paidBy, List<Split> splits, ExpenseType type) {
        this.amount = amount;
        this.paidBy = paidBy;
        this.splits = splits;
        this.type = type;
    }
    public double getAmount() { return amount; }
    public User getPaidBy() { return paidBy; }
    public List<Split> getSplits() { return splits; }
}

enum ExpenseType {
    EQUAL, EXACT, PERCENT;
}


// --- SERVICE / MANAGER CLASS ---

// This is the main engine of the application
class ExpenseManager {
    private Map<String, User> userMap;
    private Map<String, Map<String, Double>> balanceSheet;

    public ExpenseManager() {
        this.userMap = new HashMap<>();
        this.balanceSheet = new HashMap<>();
    }

    public void addUser(User user) {
        userMap.put(user.getId(), user);
        balanceSheet.put(user.getId(), new HashMap<>());
    }

    public User getUser(String id) {
        return userMap.get(id);
    }

    // This method uses the STRATEGY pattern logic via the ExpenseService factory
    public void addExpense(ExpenseType type, double amount, User paidBy, List<Split> splits) {
        // The factory validates the expense and calculates the shares
        Expense expense = ExpenseService.createExpense(type, amount, paidBy, splits);
        if (expense == null) {
            System.out.println("Expense could not be created. Invalid split details.");
            return;
        }

        // Update the balance sheet
        for (Split split : expense.getSplits()) {
            String paidToId = split.getUser().getId();
            
            // Don't create a balance entry for the person who paid for themselves
            if (paidBy.getId().equals(paidToId)) continue;

            // Update balance for the person who paid
            Map<String, Double> paidByBalances = balanceSheet.get(paidBy.getId());
            paidByBalances.put(paidToId, paidByBalances.getOrDefault(paidToId, 0.0) + split.getAmount());

            // Update balance for the person who owes
            Map<String, Double> paidToBalances = balanceSheet.get(paidToId);
            paidToBalances.put(paidBy.getId(), paidToBalances.getOrDefault(paidBy.getId(), 0.0) - split.getAmount());
        }
    }

    public void showBalances() {
        boolean hasBalances = false;
        for (Map.Entry<String, Map<String, Double>> allBalances : balanceSheet.entrySet()) {
            for (Map.Entry<String, Double> userBalance : allBalances.getValue().entrySet()) {
                if (userBalance.getValue() > 0) {
                    hasBalances = true;
                    printBalance(allBalances.getKey(), userBalance.getKey(), userBalance.getValue());
                }
            }
        }
        if (!hasBalances) {
            System.out.println("No balances to show.");
        }
    }

    private void printBalance(String user1Id, String user2Id, double amount) {
        String user1Name = userMap.get(user1Id).getName();
        String user2Name = userMap.get(user2Id).getName();
        System.out.println("  " + user2Name + " owes " + user1Name + ": " + String.format("%.2f", amount));
    }
}


// --- FACTORY CLASS ---

// This class acts as a Factory for creating and validating expenses.
// It encapsulates the logic for different split types.
class ExpenseService {
    public static Expense createExpense(ExpenseType type, double amount, User paidBy, List<Split> splits) {
        switch (type) {
            case EQUAL:
                int totalSplits = splits.size();
                double splitAmount = amount / totalSplits;
                for (Split split : splits) {
                    split.setAmount(splitAmount);
                }
                return new Expense(amount, paidBy, splits, type);
            
            case EXACT:
                double totalExactAmount = 0;
                for (Split split : splits) {
                    totalExactAmount += split.getAmount();
                }
                if (totalExactAmount != amount) {
                    return null; // Validation failed
                }
                return new Expense(amount, paidBy, splits, type);

            case PERCENT:
                double totalPercent = 0;
                for (Split split : splits) {
                    PercentSplit percentSplit = (PercentSplit) split;
                    totalPercent += percentSplit.getPercent();
                }
                if (totalPercent != 100) {
                    return null; // Validation failed
                }
                for (Split split : splits) {
                    PercentSplit percentSplit = (PercentSplit) split;
                    split.setAmount((amount * percentSplit.getPercent()) / 100.0);
                }
                return new Expense(amount, paidBy, splits, type);
            
            default:
                return null;
        }
    }
}
