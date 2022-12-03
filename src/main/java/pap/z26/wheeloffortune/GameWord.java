package pap.z26.wheeloffortune;

import java.util.Locale;

public class GameWord {

    private final String word;
    private String displayedWord, notGuessedWord;

    public GameWord(String word) {
        this.word = word.toLowerCase(Locale.ROOT);
        this.notGuessedWord = word;
        displayedWord = "_".repeat(word.length());
    }

    public boolean guessLetter(char letter) {
        if (notGuessedWord.contains(String.valueOf(letter))) {
            notGuessedWord = notGuessedWord.replaceAll(String.valueOf(letter), "#");
            StringBuilder updatedWord = new StringBuilder(displayedWord);
            int findIndex = notGuessedWord.indexOf(letter);
            while (findIndex != -1) {
                updatedWord.setCharAt(findIndex, letter);
                findIndex = notGuessedWord.indexOf(letter, findIndex);
            }
            displayedWord = updatedWord.toString();
        }
        return false;
    }

    public boolean guessPhrase(String phrase) {
        return phrase.toLowerCase(Locale.ROOT).equals(word.toLowerCase(Locale.ROOT));
    }

    public String getCurrentState() {
        return displayedWord;
    }

    public boolean hasNotGuessedConsonants() {
        char[] consonants = {'w', 'r', 't', 'p', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'c', 'b', 'n', 'm'};
        for (char consonant : consonants) {
            if (notGuessedWord.contains(String.valueOf(consonant))) {
                return true;
            }
        }
        return false;
    }
}
