package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {

    public static void main(String[] args) throws IOException, EmptyDictionaryException {
        File fileName = null;
        int wordLength = 0;
        int numGuesses = 0;
        int userGuesses = 0;
        if (0 < args.length) {
            fileName = new File(args[0]);
            wordLength = Integer.parseInt(args[1]);
            numGuesses = Integer.parseInt(args[2]);
        }
        EvilHangmanGame newGame = new EvilHangmanGame();
        //game loop should be in main function
        newGame.startGame(fileName, wordLength);

        while(userGuesses < numGuesses) {
            System.out.print("You have " + (numGuesses - userGuesses) + " guesses left\n");
            newGame.printStats();
            char userChar = 'a';
            Set<String> getSet = new HashSet<>();

            System.out.print("Enter guess: ");
            Scanner in = new Scanner(System.in);
            userChar = in.next().charAt(0);
            boolean validInput = false;
            while(!validInput) {
                try {
                    getSet = newGame.makeGuess(userChar);
                    validInput = true;
                } catch (GuessAlreadyMadeException e) {
                    validInput = false;
                    System.out.print("You've already made that guess!\n");
                    System.out.print("Enter guess: ");
                    in = new Scanner(System.in);
                    userChar = in.next().charAt(0);
                }
            }

            newGame.countGuess(getSet, userChar);

            System.out.print("\n\n");
            ++userGuesses;
        }
        if(newGame.correctLetter.indexOf("-") >= 0) {
            System.out.print("You lose!\n");
            System.out.print("The word was: " + newGame.dictWords.iterator().next());
        } else {
            System.out.print("You win!\n");
        }
    }
}
