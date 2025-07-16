package LLDQuestions.SnakeAndLadderGame;

import java.util.*;

/**
 * Main class to demonstrate the Snake and Ladder game.
 * All classes are included here for a single, runnable example.
 */
public class SnakeAndLadderGame {
    public static void main(String[] args) {
        // --- Game Setup ---
        int boardSize = 100;
        List<Player> players = Arrays.asList(new Player("Alice"), new Player("Bob"));

        // Define snakes (start > end)
        Map<Integer, Integer> snakes = new HashMap<>();
        snakes.put(17, 7);
        
        snakes.put(54, 34);
        snakes.put(62, 19);
        snakes.put(64, 60);
        snakes.put(87, 24);
        snakes.put(93, 73);
        snakes.put(95, 75);
        snakes.put(99, 78);

        // Define ladders (start < end)
        Map<Integer, Integer> ladders = new HashMap<>();
        ladders.put(4, 14);
        ladders.put(9, 31);
        ladders.put(20, 38);
        ladders.put(28, 84);
        ladders.put(40, 59);
        ladders.put(51, 67);
        ladders.put(63, 81);
        ladders.put(71, 91);

        Game game = new Game(boardSize, players, snakes, ladders);
        game.play();
    }
}

// Represents a single player in the game
class Player {
    private String name;
    private int position;

    public Player(String name) {
        this.name = name;
        this.position = 0; // All players start off the board
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

// Represents the game board, including snakes and ladders
class Board {
    private int size;
    private Map<Integer, Integer> snakes;
    private Map<Integer, Integer> ladders;

    public Board(int size, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
        this.size = size;
        this.snakes = snakes;
        this.ladders = ladders;
    }

    public int getSize() {
        return size;
    }

    /**
     * Calculates the final position after considering snakes and ladders.
     * @param position The current position after a dice roll.
     * @return The new final position.
     */
    public int getNextPosition(int position) {
        if (snakes.containsKey(position)) {
            System.out.println("    Oh no! Bitten by a snake!");
            return snakes.get(position);
        }
        if (ladders.containsKey(position)) {
            System.out.println("    Yay! Climbed a ladder!");
            return ladders.get(position);
        }
        return position;
    }
}

// A simple dice that returns a random number from 1 to 6
class Dice {
    private Random random;

    public Dice() {
        this.random = new Random();
    }

    public int roll() {
        return random.nextInt(6) + 1;
    }
}

// The main class that orchestrates the game
class Game {
    private Board board;
    private Queue<Player> players;
    private Dice dice;
    private boolean isGameWon;

    public Game(int boardSize, List<Player> playerList, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
        this.board = new Board(boardSize, snakes, ladders);
        this.players = new LinkedList<>(playerList);
        this.dice = new Dice();
        this.isGameWon = false;
    }

    public void play() {
        System.out.println("--- Welcome to Snake and Ladder! ---");
        while (!isGameWon) {
            // Get the current player from the front of the queue
            Player currentPlayer = players.poll();
            takeTurn(currentPlayer);

            // Add the player back to the end of the queue for the next round
            players.add(currentPlayer);
        }
    }

    private void takeTurn(Player player) {
        System.out.println("\n" + player.getName() + "'s turn. Current position: " + player.getPosition());
        System.out.print("Press Enter to roll the dice...");
        try {
            System.in.read(); // Wait for user to press Enter
        } catch (Exception e) {}

        int diceRoll = dice.roll();
        System.out.println("    " + player.getName() + " rolled a " + diceRoll);

        int currentPosition = player.getPosition();
        int nextPosition = currentPosition + diceRoll;

        if (nextPosition > board.getSize()) {
            System.out.println("    Roll is too high! You need to land exactly on " + board.getSize() + ".");
            // Player doesn't move
        } else {
            // Check for snakes or ladders at the new position
            nextPosition = board.getNextPosition(nextPosition);
            player.setPosition(nextPosition);
            System.out.println("    " + player.getName() + " moved to position " + player.getPosition());

            // Check for a winner
            if (player.getPosition() == board.getSize()) {
                System.out.println("\n=====================================");
                System.out.println("    " + player.getName() + " wins the game!");
                System.out.println("=====================================");
                isGameWon = true;
            }
        }
    }
}

