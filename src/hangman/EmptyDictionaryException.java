package hangman;

public class EmptyDictionaryException extends Exception {
    static {
        try {
            System.out.print("not workibg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	//Thrown when dictionary file is empty or no words in dictionary match the length asked for
}
