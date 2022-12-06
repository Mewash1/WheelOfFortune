package pap.z26.wheeloffortune;

import java.util.ArrayList;
import java.util.Locale;

public class GameWord {

    private final String word;
    private String displayedWord, notGuessedWord;

    public static ArrayList<Character> consonants, vowels, letters;

    static {
        consonants = new ArrayList<>();
        vowels = new ArrayList<>();
        String consonantsString = "qwrtpsdfghjklzxcvbnmćżźńłś", vowelsString = "aeiouyąęó";
        for (int i=0; i<consonantsString.length(); i++) {
            consonants.add(consonantsString.charAt(i));
        }
        for(int i=0; i<vowelsString.length(); i++) {
            vowels.add(vowelsString.charAt(i));
        }
        letters = new ArrayList<>(consonants);
        letters.addAll(vowels);
    }

    public GameWord(String word) {
        this.word = word.toLowerCase(Locale.ROOT);
        this.notGuessedWord = word.toLowerCase(Locale.ROOT);
        displayedWord = "_".repeat(word.length());
    }

    public int guessLetter(char letter) {
        int count = 0;
        if (notGuessedWord.contains(String.valueOf(letter))) {
            count = 1;
            notGuessedWord = notGuessedWord.replaceAll(String.valueOf(letter), "#");
            StringBuilder updatedWord = new StringBuilder(displayedWord);
            int findIndex = notGuessedWord.indexOf(letter);
            while (findIndex != -1) {
                count++;
                updatedWord.setCharAt(findIndex, letter);
                findIndex = notGuessedWord.indexOf(letter, findIndex);
            }
            displayedWord = updatedWord.toString();
        }
        return count;
    }

    public boolean guessPhrase(String phrase) {
        return phrase.toLowerCase(Locale.ROOT).equals(word.toLowerCase(Locale.ROOT));
    }

    public String getCurrentState() {
        return displayedWord;
    }

    public boolean hasNotGuessedConsonants() {
        for (char consonant : consonants) {
            if (notGuessedWord.contains(String.valueOf(consonant))) {
                return true;
            }
        }
        return false;
    }
}
