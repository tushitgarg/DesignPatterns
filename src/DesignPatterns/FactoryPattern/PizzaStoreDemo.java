package DesignPatterns.FactoryPattern;

import java.util.ArrayList;

/**
 * Main class to demonstrate the Factory Method Pattern.
 */
public class PizzaStoreDemo {
    public static void main(String[] args) {
        System.out.println("--- Ordering from the New York Store ---");
        // We create a specific type of store (the Concrete Creator)
        PizzaStore nyStore = new NYPizzaStore();
        // The orderPizza logic is in the abstract PizzaStore, but it will use
        // the NYPizzaStore's implementation of createPizza.
        Pizza nyPizza = nyStore.orderPizza("cheese");
        System.out.println("Ethan ordered a " + nyPizza.getName() + "\n");

        System.out.println("--- Ordering from the Chicago Store ---");
        PizzaStore chicagoStore = new ChicagoPizzaStore();
        Pizza chicagoPizza = chicagoStore.orderPizza("veggie");
        System.out.println("Joel ordered a " + chicagoPizza.getName() + "\n");
    }
}

// =================================================================
// The Product: Defines the interface for objects the factory creates.
// =================================================================
abstract class Pizza {
    String name;
    String dough;
    String sauce;
    ArrayList<String> toppings = new ArrayList<String>();

    // This logic is generic to all pizzas
    void prepare() {
        System.out.println("Preparing " + name);
        System.out.println("Tossing " + dough);
        System.out.println("Adding " + sauce);
        System.out.println("Adding toppings: ");
        for (String topping : toppings) {
            System.out.println("   " + topping);
        }
    }

    void bake() { System.out.println("Bake for 25 minutes at 350"); }
    void cut() { System.out.println("Cutting the pizza into diagonal slices"); }
    void box() { System.out.println("Place pizza in official PizzaStore box"); }
    public String getName() { return name; }
}

// =================================================================
// Concrete Products: Specific implementations of the Product.
// =================================================================
class NYStyleCheesePizza extends Pizza {
    public NYStyleCheesePizza() {
        name = "NY Style Sauce and Cheese Pizza";
        dough = "Thin Crust Dough";
        sauce = "Marinara Sauce";
        toppings.add("Grated Reggiano Cheese");
    }
}

class NYStyleVeggiePizza extends Pizza {
    public NYStyleVeggiePizza() {
        name = "NY Style Veggie Pizza";
        dough = "Thin Crust Dough";
        sauce = "Marinara Sauce";
        toppings.add("Mushrooms, Onions, Peppers");
    }
}

class ChicagoStyleCheesePizza extends Pizza {
    public ChicagoStyleCheesePizza() {
        name = "Chicago Style Deep Dish Cheese Pizza";
        dough = "Extra Thick Crust Dough";
        sauce = "Plum Tomato Sauce";
        toppings.add("Shredded Mozzarella Cheese");
    }
    @Override
    void cut() { System.out.println("Cutting the pizza into square slices"); }
}

class ChicagoStyleVeggiePizza extends Pizza {
    public ChicagoStyleVeggiePizza() {
        name = "Chicago Style Deep Dish Veggie Pizza";
        dough = "Extra Thick Crust Dough";
        sauce = "Plum Tomato Sauce";
        toppings.add("Spinach, Eggplant, Mozzarella");
    }
    @Override
    void cut() { System.out.println("Cutting the pizza into square slices"); }
}


// =================================================================
// The Creator: The class that needs a product, but lets subclasses create it.
// =================================================================
abstract class PizzaStore {

    // This is the primary method. It defines the high-level steps for making a pizza.
    // Notice it calls `createPizza()`, but it has no idea what kind of pizza
    // will actually be created. That's the job of the subclass.
    public Pizza orderPizza(String type) {
        Pizza pizza;

        // The call to the Factory Method! This is where the magic happens.
        // The subclass's implementation of createPizza will be called.
        pizza = createPizza(type);

        // The rest of the process is generic and defined in the Pizza superclass.
        System.out.println("--- Making a " + pizza.getName() + " ---");
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();

        return pizza;
    }

    // THE FACTORY METHOD:
    // It's abstract, forcing subclasses to provide an implementation.
    // This is how a class defers instantiation to its subclasses.
    protected abstract Pizza createPizza(String type);
}


// =================================================================
// Concrete Creators: These subclasses decide which concrete product to make.
// =================================================================
class NYPizzaStore extends PizzaStore {
    // This subclass implements the factory method to create NY-style pizzas.
    @Override
    protected Pizza createPizza(String item) {
        if (item.equals("cheese")) {
            return new NYStyleCheesePizza();
        } else if (item.equals("veggie")) {
            return new NYStyleVeggiePizza();
        } else return null;
    }
}

class ChicagoPizzaStore extends PizzaStore {
    // This subclass implements the factory method to create Chicago-style pizzas.
    @Override
    protected Pizza createPizza(String item) {
        if (item.equals("cheese")) {
            return new ChicagoStyleCheesePizza();
        } else if (item.equals("veggie")) {
            return new ChicagoStyleVeggiePizza();
        } else return null;
    }
}
