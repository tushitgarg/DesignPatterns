package Concurrency.chapter10;

public class LeftRightDeadlock {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftRight() {
        synchronized (left) {
            synchronized (right) {
                System.out.println("leftRight: Acquired both locks.");
            }
        }
    }

    public void rightLeft() {
        synchronized (right) {
            synchronized (left) {
                System.out.println("rightLeft: Acquired both locks.");
            }
        }
    }

    public static void main(String[] args) {
        LeftRightDeadlock d = new LeftRightDeadlock();
        new Thread(d::leftRight).start();
        new Thread(d::rightLeft).start();
        System.out.println("Started threads. Deadlock is likely.");
    }
}

