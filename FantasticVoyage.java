/*
 *        Class: IT-516 - Data Structures & Algorithms
 *   Instructor: Sean Harrington
 *         Date: 20 Jul 2023
 *        Group: Fantastic Voyage (Inside the Human Body)
 *   Assignment: GD02
 *
 *   Compile: javac-algs4 FantasticVoyage.java
 *
 *       Run: java-algs4 FantasticVoyage 4
 */


import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class FantasticVoyage {

    // Variables to hold game-related data
    public static int numPlayers;
    public static Color[] playerColors;
    public static double[] playerCoords;
    public static double[] initialCoords;
    public static Integer[] playerSpace;

    // Constants for graphical display
    public static double x1 = 0.13;
    public static double y1 = 0.1795;
    public static double radius = 0.01;

    public static void main(String[] args) {
        numPlayers = Integer.parseInt(args[0]);
        // Check if the number of players is within the allowed range (1 to 4)
        if (numPlayers < 1 || numPlayers > 4) {
            // Print an error message and return if the number of players is invalid
            StdOut.println("The minimum required number of players is 1, and the maximum is 4. Please enter a value between 1 and 4.");
            return;
        }


        // Initialize arrays to hold player data and set up graphical display
        initialCoords = new double[2 * numPlayers];
        playerCoords = new double[2 * numPlayers];
        StdDraw.setCanvasSize(700, 600);
        StdDraw.setXscale(0, 1.0);
        StdDraw.setYscale(0, 1.0);

        // Print game introduction
        StdOut.println("Welcome to Inside the Body Game! ");
        StdOut.println("This will be a " + numPlayers + " player game.");
        StdOut.println("The game begins now:");

        // Variables to keep track of game statistics
        int dieRolls = 0;
        int[] playerRolls = new int[numPlayers];
        int[] diceRolls = new int[6];
        int[][] playerRollCount = new int[numPlayers][6];
        int[] warps = new int[numPlayers];
        int[] arteries = new int[numPlayers];

        // Read the game board data from "game.txt" file and create a directed graph
        In in = new In("game.txt");
        Digraph spacesDigraph = new Digraph(in);
        int winningSpace = spacesDigraph.V() - 1;

        // Define colors for players
        playerColors = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

        // Set up graphical display for Player 1
        StdDraw.setPenColor(playerColors[0]);
        StdDraw.filledCircle(0.1, 0.025, 0.01);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.circle(0.1, 0.025, 0.01);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 12));
        StdDraw.text(0.1, 0.0125, Integer.toString(1));

        // Initialize player coordinates and current spaces
        initialCoords[0] = 0.075;
        initialCoords[1] = 0.0125;
        playerCoords[0] = initialCoords[0];
        playerCoords[1] = initialCoords[1];
        playerSpace = new Integer[numPlayers];
        Arrays.fill(playerSpace, 1);

        // Set the initial x-coordinate for each player based on their position (right to left)
        double xIncrement = 0.0200; // The increment in x-coordinate for each player.
        double yIncrement = 0.0250; // The increment in y-coordinate for each row.
        for (int playerID = 0; playerID < numPlayers; playerID++) {
            int row = playerID / 9; // Divide by the number of players per row (9 players per row).
            initialCoords[2 * playerID] = 0.012 + xIncrement * (playerID % 9); // Players in the same row move left to right.
            playerCoords[2 * playerID] = initialCoords[2 * playerID];
            playerCoords[2 * playerID + 1] = initialCoords[2 * playerID + 1];
        }

        // Main Game Loop
        for (int i = 0; i < 1000000; i++) {
            // Mathematically determines which player's turn it is by remainder
            int player = i % numPlayers;
            // Create a random number holder
            Random r = new Random();
            // Create an integer to hold the random number & randomize
            int roll = r.nextInt((6)) + 1;
            diceRolls[roll - 1]++;
            playerRollCount[player][roll - 1]++;
            // Increment times die has been rolled
            dieRolls++;
            // Increment times player has rolled the die
            playerRolls[player]++;
            StdOut.println("player " + (player + 1) + " rolls a " + roll);
            StdOut.println("       Moves from position " + playerSpace[player]);

            // "Move" the player forward by rolling the die
            int initialSpace = playerSpace[player];
            playerSpace[player] = playerSpace[player] + roll;
            wait(500);

            movePlayer(player, initialSpace);
            StdOut.println("         Moves to position " + playerSpace[player]);

            if (playerSpace[player] < winningSpace) {
                // Check the digraph to see if there are any veins or warps
                int numJumps = spacesDigraph.outdegree(playerSpace[player]);
                // If there are any, move the player accordingly
                if (numJumps != 0) {
                    for (Integer bigMove : spacesDigraph.adj(playerSpace[player])) {
                        // If it's a warp (player space is less than digraph space), move them forward.
                        if (playerSpace[player] < bigMove) {
                            playerSpace[player] = bigMove;
                            wait(500);
                            movePlayer(player, initialSpace);
                            StdOut.println("         carry ahead to space " + bigMove);
                            warps[player]++;
                        }
                        // Otherwise, player moves backward.
                        else {
                            playerSpace[player] = bigMove;
                            wait(500);
                            movePlayer(player, initialSpace);
                            StdOut.println("  arteries move, backward to space " + bigMove);
                            arteries[player]++;
                        }
                    }
                }
            }
            // If player's new space is greater than or equal to the win space, they win the game.
            if (playerSpace[player] >= winningSpace) {
                StdOut.println("Hurray...! player " + (player + 1) + ", you won the game!");
                break;
            }
        }

        // Display end of game results
        StdOut.println();
        StdOut.println("End of game results:");
        StdOut.println();
        StdOut.println("Total die rolls for the game " + dieRolls);
        for (int z = 0; z < numPlayers; z++) {
            StdOut.println("Total die rolls for player " + (z + 1) + " is " + playerRolls[z]);
        }

        // Calculate the most common die roll for the entire game
        Integer[] commonDieRollsResults = new Integer[6];
        for (int w = 0; w < 6; w++) {
            commonDieRollsResults[w] = diceRolls[w];
        }
        int max = commonDieRollsResults[0];
        int index = 0;
        for (int p = 1; p < 6; p++) {
            if (max < commonDieRollsResults[p]) {
                max = commonDieRollsResults[p];
                index = p;
            }
        }
        StdOut.println();
        StdOut.println("Most common die roll for the game is " + (index + 1));

        // Calculate the most common die roll for each player
        for (int i = 0; i < numPlayers; i++) {
            for (int j = 0; j < 6; j++) {
                commonDieRollsResults[j] = playerRollCount[i][j];
            }
            int high = commonDieRollsResults[0];
            int index1 = 0;
            for (int p = 1; p < 6; p++) {
                if (high < commonDieRollsResults[p]) {
                    high = commonDieRollsResults[p];
                    index1 = p;
                }
            }
            StdOut.println("Most common die roll for player " + (i + 1) + " is " + (index1 + 1));
        }

        // Display total and individual warps
        StdOut.println();
        int veins = 0;
        for (int l = 0; l < numPlayers; l++) {
            veins = veins + warps[l];
        }
        StdOut.println("Total veins pushes for the game was " + veins);
        for (int l = 0; l < numPlayers; l++) {
            StdOut.println("Total veins pushes for player " + (l + 1) + " was " + warps[l]);
        }
        StdOut.println();

        // Display total and individual arteries
        int arteries1 = 0;
        for (int l = 0; l < numPlayers; l++) {
            arteries1 = arteries1 + arteries[l];
        }
        StdOut.println("Total artery carries encountered in the game was " + arteries1);
        for (int l = 0; l < numPlayers; l++) {
            StdOut.println("Total artery carries encountered for player " + (l + 1) + " was " + arteries[l]);
        }
        StdOut.println();
    }

    // Method to update the graphical display for player's movement
    public static void movePlayer(int player, int initialSpace) {
        StdDraw.enableDoubleBuffering();
        StdDraw.picture(0.5, 0.5, "Heartpng.jpg", 1.0, 1.0);
        double x = initialCoords[2 * player] + (x1 * ((playerSpace[player] - 1) % 9));
        double y = initialCoords[2 * player + 1] + (y1 * ((playerSpace[player] - 1) / 9));
        playerCoords[2 * player] = x;
        playerCoords[2 * player + 1] = y;
        for (int playerID = 0; playerID < numPlayers; playerID++) {
            StdDraw.setPenColor(playerColors[playerID]);
            StdDraw.filledCircle(playerCoords[2 * playerID], playerCoords[2 * playerID + 1], radius);
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.circle(playerCoords[2 * playerID], playerCoords[2 * playerID + 1], radius);
            StdDraw.text(playerCoords[2 * playerID], playerCoords[2 * playerID + 1], Integer.toString(playerID + 1));
        }
        StdDraw.show();
    }

    // Method to add a wait/delay between player movements
    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            // Do nothing if interrupted
        }
    }
}
