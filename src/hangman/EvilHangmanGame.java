package hangman;

import java.io.File;
import java.io.IOException;
import java.rmi.activation.ActivationGroup_Stub;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{
    Set<String> dictWords;
    SortedSet<Character> guessedLetters;
    private int lengthWord;
    private int numGuesses;
    StringBuilder correctLetter;
    private int userGuesses;
    private TreeMap<String,Set<String>> wordFams;

    public EvilHangmanGame () {
        dictWords = new HashSet<String>();
        guessedLetters = new TreeSet<Character>();
        correctLetter = new StringBuilder();
    }

    public void resetGame() {
        dictWords = new HashSet<String>();
        guessedLetters = new TreeSet<Character>();
        correctLetter = new StringBuilder();
        userGuesses = 0;
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        resetGame();
        dictWords = ReadFile.readFile(dictionary, wordLength);
        if(dictWords.size() == 0) {
            throw new EmptyDictionaryException();
        }

        lengthWord = wordLength;
        userGuesses = 0;
        for(int i = 0; i < wordLength; ++i) {
            correctLetter.append('-');
        }

        while(userGuesses < numGuesses) {
            printStats();
            char userChar = 'a';
            Set<String> getSet = new HashSet<>();

            System.out.print("Enter guess: ");
            Scanner in = new Scanner(System.in);
            userChar = in.next().charAt(0);
            try {
                getSet = makeGuess(userChar);
            } catch (GuessAlreadyMadeException e) {
                e.printStackTrace();
            }
            countGuess(getSet, userChar);

            System.out.print("\n\n");
            ++userGuesses;
        }
        if(correctLetter.indexOf("-") >= 0) {
            System.out.print("You lose!\n");
            System.out.print("The word was: " + dictWords.iterator().next());
        } else {
            System.out.print("You win!\n");
        }

    }

    public void countGuess(Set<String> wordSet, char guess) {
        String wordString = "";
        int countChar = 0;
        wordString = wordSet.iterator().next();
        for(int i = 0; i < wordString.length(); ++i) {
            if (wordString.charAt(i) == guess) {
                ++countChar;
            }
        }
        if (countChar == 0) {
            System.out.print("Sorry, there are no " + guess +"'s");
        } else {
            System.out.print("Yes, there is " + countChar + " " + guess);
        }
    }

    public void printStats() {
        System.out.print("You have " + (numGuesses - userGuesses) + " guesses left\n");
        System.out.print("Used letters:" );
        StringBuilder sb = new StringBuilder();
        for(char chars: guessedLetters) {
            sb.append(chars);
            sb.append(" ");
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        System.out.print(sb.toString());
        System.out.print("\n");
        System.out.print("Word: " + correctLetter.toString());
        System.out.print("\n");
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        wordFams = new TreeMap<>();
        Set<String> newList;
        String baseWord = "";

        for(int i = 0; i < lengthWord; ++i) {
            baseWord += '-';
        }

        if (!Character.isLetter(guess)) {
            //not a valid character
            return null;
        }
        guess = Character.toLowerCase(guess);
        StringBuilder charLocs;
        for (String next : dictWords) {
            charLocs = new StringBuilder(baseWord);
            for (int i = 0; i < next.length(); ++i) {
                if (next.charAt(i) == guess) {
                    charLocs.setCharAt(i, guess);
                }
            }

            if(!wordFams.containsKey(charLocs.toString())) {
                newList = new HashSet<>();
                newList.add(next);
                wordFams.put(charLocs.toString(), newList);
            } else {
                //does have word group, need to add it to that set
                newList = wordFams.get(charLocs.toString());
                newList.add(next);
                wordFams.put(charLocs.toString(), newList);
            }
        }

        if(!guessedLetters.add(guess))  {
            throw new GuessAlreadyMadeException();
        }
        //++userGuesses;
        dictWords = findSet(wordFams, guess);
        return dictWords;
    }

    public void replaceDashes(String string, char guess) {
        for(int i = 0; i < string.length(); ++i) {
            if(string.charAt(i) == guess) {
                correctLetter.setCharAt(i, guess);
            }
        }
    }

    public Set<String> findSet(TreeMap<String,Set<String>> wordFams, char guess) {
        if(wordFams.size() == 1) {
            String first = wordFams.firstKey();
            replaceDashes(first, guess);
            return wordFams.get(first);
        }

        int largestSet = -1;
        Set<String> matchingCountSets =  null;
        for(Map.Entry<String,Set<String>> entry : wordFams.entrySet()) {
            if (entry.getValue().size() > largestSet) {
                matchingCountSets =  new HashSet<>();
                matchingCountSets.add(entry.getKey());
                largestSet = entry.getValue().size();
            }
            if (entry.getValue().size() == largestSet) {
                matchingCountSets.add(entry.getKey());
            }
        }

        assert matchingCountSets != null;
        if(matchingCountSets.size() == 1) {
            String onlyVal = matchingCountSets.iterator().next();
            replaceDashes(onlyVal, guess);
            return wordFams.get(onlyVal);
        }
        boolean foundChar = false;
        for(String string : matchingCountSets) {
            if(string.indexOf(guess) != -1) {
                foundChar = true;
            }
            if(!foundChar) {
                replaceDashes(string, guess);
                return wordFams.get(string);
            }
        }
        //END OF RULE 1

        Set<String> matchingLetterCount =  null;
        int letterCount;
        int lowestCount = 45; // The length of the longest word in the english language

        for(String string: matchingCountSets) {
            letterCount = 0;
            for(int i = 0; i < string.length(); ++i) {
                if(string.charAt(i) == guess) {
                    ++letterCount;
                }
            }
            if (letterCount < lowestCount) {
                //matchingLetterCount = new ArrayList<>();
                matchingLetterCount =  new HashSet<>();
                matchingLetterCount.add(string);
                lowestCount = letterCount;
            }
            if (letterCount == lowestCount) {
                matchingLetterCount.add(string);
            }
        }
        if(matchingLetterCount.size() == 1) {
            String onlyVal = matchingLetterCount.iterator().next();
            replaceDashes(onlyVal, guess);
            return wordFams.get(onlyVal);
        }
        //END OF RULE 2

        //If this still has not resolved the issue, choose the one with the rightmost guessed letter
        String onlyVal = rightMost(matchingLetterCount, guess);
        replaceDashes(onlyVal, guess);
        return wordFams.get(onlyVal);
    }

    public String rightMost(Set<String> stringSet, char guess) {
        Set<String> matchingRights = null;
        Set<Integer> markedInts = new HashSet<>();
        int rightMost = -1;
        for(String string: stringSet) {
            for(int i = string.length() - 1; i >= 0; --i) {
                if (string.charAt(i) == guess) {
                    if (i > rightMost) {
                        matchingRights = new HashSet<>();
                        rightMost = i;
                        matchingRights.add(string);
                    }
                    if (i == rightMost) {
                        markedInts = new HashSet<>();
                        markedInts.add(i);
                        matchingRights.add(string);
                    }
                }
            }
        }
        if(matchingRights.size() > 1) {
            return rightMost(matchingRights, guess, markedInts);
            //need to make a recursive call
        }
        //replaceDashes(onlyVal, guess);
        return matchingRights.iterator().next();
    }

    public String rightMost(Set<String> stringSet, char guess, Set<Integer> markedInts) {
        Set<String> matchingRights = null;
        //Set<Integer> markedInts;
        int rightMost = -1;
        for(String string: stringSet) {
             for(int i = string.length() - 1; i >= 0; --i) {
                if (string.charAt(i) == guess) {
                    boolean beenMarked = false;
                    for(int checkInt: markedInts) {
                        if(i == checkInt) {
                            beenMarked = true;
                        }
                    }
                    if(beenMarked) {
                        continue;
                    }
                    if (i > rightMost) {
                        matchingRights = new HashSet<>();
                        rightMost = i;
                        matchingRights.add(string);
                    }
                    if (i == rightMost) {
                        markedInts = new HashSet<>();
                        markedInts.add(i);
                        matchingRights.add(string);
                    }
                }
            }
        }
        if(matchingRights.size() > 1) {
            return rightMost(matchingRights, guess, markedInts);
            //need to make a recursive call
        }
        //replaceDashes(onlyVal, guess);
        return matchingRights.iterator().next();
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public void setNumGuesses(int numGuesses) {
        this.numGuesses = numGuesses;
    }
}
