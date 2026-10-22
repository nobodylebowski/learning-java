package org.example;

import java.util.Random;
import java.util.Scanner;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final int MAX_NUMBER = 100;
    private static final int MAX_ATTEMPTS = 7;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String play;
        do {
            playGame(scanner);
            System.out.println("Do you want to play again? (yes/no)");
            play = scanner.nextLine().trim();
        }while(play.equalsIgnoreCase("yes"));
    }

    public static void playGame(Scanner scanner) {
        System.out.println("I'm thinking of a number between 1 and 100");
        int toGuess = new Random().nextInt(MAX_NUMBER) + 1;
        int userGuess;
        int attempt = 0;

        do{
            attempt++;
            userGuess = readGuess(scanner);
            if (userGuess > toGuess) {
                System.out.println("Too high");
            } else if (userGuess < toGuess){
                System.out.println("Too low");
            }

        }while (userGuess != toGuess && attempt < MAX_ATTEMPTS);

        if(userGuess == toGuess) {
            System.out.println("Correct! You needed " + attempt + " attempts");
        } else {
            System.out.println("Game over! The number to guess was: " + toGuess);
        }
    }

    public static int readGuess(Scanner scanner) {
        int guess;
        while (true) {
            System.out.print("Your guess: ");
            try {
                guess = Integer.parseInt(scanner.nextLine());
                if (guess >= 1 && guess <= MAX_NUMBER) {
                    return guess;
                }
                System.out.println("Please insert a number between 1 and 100");
            } catch (NumberFormatException e) {
                System.out.println("Please insert a whole number");
            }
        }
    }
}