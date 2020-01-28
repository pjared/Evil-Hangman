package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ReadFile {
    public static Set<String> readFile(File dictionary, int wordLength) throws EmptyDictionaryException {
        Set<String> dictWords =  new HashSet<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(dictionary);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(scanner.hasNext()) {
            String newWord = scanner.next();
            if(newWord.length() == wordLength) {
                dictWords.add(newWord);
            }
        }

        return dictWords;
    }
}
