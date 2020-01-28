package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EvilHangman {

    public static void main(String[] args) throws IOException, EmptyDictionaryException {
        File fileName = null;
        int wordLength = 0;
        int numGuesses = 0;
        if (0 < args.length) {
            fileName = new File(args[0]);
            wordLength = Integer.parseInt(args[1]);
            numGuesses = Integer.parseInt(args[2]);
        }
        EvilHangmanGame newGame = new EvilHangmanGame();
        newGame.setNumGuesses(numGuesses);
        newGame.startGame(fileName, wordLength);
    }
}
