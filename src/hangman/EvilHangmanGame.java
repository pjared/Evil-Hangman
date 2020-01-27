package hangman;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/* TO ASK
* Throwing exceptions
* how to set numGuesses number
* Best way to make word families*/

public class EvilHangmanGame implements IEvilHangmanGame{
    Set<String> dictWords;
    SortedSet<Character> guessedLetters;
    private int lengthWord;
    private int numGuesses;
    private ArrayList<Character> correctLetter;
    private int userGuesses;

    public EvilHangmanGame () {
        dictWords = new HashSet<String>();
        guessedLetters = new TreeSet<Character>();
    }

    /*You have 10 guesses left
    Used letters:
    Word: -----
    Enter guess: a
    Sorry, there are no a's
    * */

    public void resetGame() {
        dictWords = new HashSet<String>();
        guessedLetters = new TreeSet<Character>();
        numGuesses = 0;
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        //need to have a clear game method
        resetGame();
        Scanner scanner = new Scanner(dictionary);
        readFile(scanner, wordLength);
        lengthWord = wordLength;
        userGuesses = 0;
        //Set<String> correctletters = new TreeSet<>();
        correctLetter = new ArrayList<>();
        for(int i = 0; i < wordLength; ++i) {
            correctLetter.add('-');
        }

        while(userGuesses < numGuesses) {
            printStats();
            char userChar = 'a';

            System.out.print("Enter guess: ");
            Scanner in = new Scanner(System.in);
            userChar = in.next().charAt(0);
            System.out.print("\n");
            System.out.print("Sorry, there are no " + userChar +"'s");
            ++userGuesses;
        }
    }

    public void printStats() {
        System.out.print("You have " + (numGuesses - userGuesses) + "left\n");
        System.out.print("Used letters:" );
        for(char chars: guessedLetters) {
            System.out.print(chars + ", ");
        }
        System.out.print("\n");
        System.out.print("Word: ");
        for(char chars: correctLetter) {
            System.out.print(chars + " ");
        }
        //probably need to do another loop storing a string with correctLetters
        System.out.print("\n");
    }

    public void readFile(Scanner dictScanner, int wordLength) throws EmptyDictionaryException {
        while(dictScanner.hasNext()) {
            String newWord = dictScanner.next();
            if(newWord.length() == wordLength) {
                dictWords.add(newWord);
            }
        }
        if(dictWords.size() == 0) {
            //nothing was added, need to call exception
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        ArrayList<Set<String>> wordFamilies = new ArrayList<Set<String>>();
        ArrayList<ArrayList<Integer>> letterGroups = new ArrayList<>();
        if(!guessedLetters.add(guess))  {
            //nothing was added, need to call exception
        }
        //going to return the largest set of strings
        //need to make word groups
        ArrayList<Integer> wordLocs;
        for (String next : dictWords) {
            wordLocs = new ArrayList<>();
            for(int i = 0; i < next.length(); ++i) {
                if (next.charAt(i) == guess) {
                    // add positions where characters match dictionary
                    wordLocs.add(i);
                }
            }
            // what happens if wordLocs is empty? Might need to add another if
            if(letterGroups.add(wordLocs)) {
                Set<String> newSet = new HashSet<>();
                newSet.add(next);
                //Found a new word Location
                wordFamilies.add(newSet);
            }
            else {
                //need to find the correct word family and add it to that set.
                for(int j = 0; j < letterGroups.size(); ++j) {
                    if(letterGroups.get(j) == wordLocs) {
                        //Found, add it to that group
                        wordFamilies.get(j).add(next);
                    }
                }
            }

        }

        return findSet(wordFamilies, guess);
    }

    public Set<String> findSet(ArrayList<Set<String>> allSets, char guess) {
        if(allSets.size() == 1) {
            return allSets.get(0);
        }
        // Now we have to compare sizes
        int largestSet = -1;
        ArrayList<Set<String>> matchingCountSets =  null;
        for(Set<String> set : allSets) {
            if (set.size() > largestSet) {
                matchingCountSets = new ArrayList<>();
                matchingCountSets.add(set);
            }
            if (set.size() == largestSet) {
                matchingCountSets.add(set);
            }
        }
        if(allSets.size() == 1) {
            return matchingCountSets.get(0);
        } else {
            // There are more than one sets with the same count
            boolean foundChar = false;
            for(Set<String> set: matchingCountSets) {
                String findGuess = (String) set.toArray()[0];
                for(int i = 0; i < findGuess.length(); ++i) {
                    if (findGuess.charAt(i) == guess) {
                        foundChar = true;
                    }
                }
                if(foundChar == false) {
                    return set;
                }
            }
            //Found none that didn't contain character, moving to rule 2
            //Where i'm going to log off for today

            return  matchingCountSets.get(0);
        }
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }
}
