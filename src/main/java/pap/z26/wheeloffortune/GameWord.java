package pap.z26.wheeloffortune;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class GameWord {

    private final String word;
    private String displayedWord, notGuessedWord;

    public static ArrayList<Character> consonants, vowels, letters;

    static {
        // initializes the character arrays
        consonants = new ArrayList<>();
        vowels = new ArrayList<>();
        String consonantsString = "qwrtpsdfghjklzxcvbnmćżźńłś", vowelsString = "aeiouyąęó";
        for (int i = 0; i < consonantsString.length(); i++) {
            consonants.add(consonantsString.charAt(i));
        }
        for (int i = 0; i < vowelsString.length(); i++) {
            vowels.add(vowelsString.charAt(i));
        }
        letters = new ArrayList<>(consonants);
        letters.addAll(vowels);
    }

    /**
     * Initializes the GameWord with a new phrase
     *
     * @param word phrase to guess in the game
     */
    public GameWord(String word) {
        this.word = word.toLowerCase(Locale.ROOT);
        this.notGuessedWord = word.toLowerCase(Locale.ROOT);
        StringBuilder displayedWordBuilder = new StringBuilder("_".repeat(word.length()));
        for (int i = 0; i < this.word.length(); i++) {
            if (word.charAt(i) == ' ') {
                displayedWordBuilder.setCharAt(i, ' ');
            }
        }
        displayedWord = displayedWordBuilder.toString();
    }

    /**
     * Checks how many times the given letter occurs in the uncovered part of the phrase and returns that amount, used
     * when a {@link Player player} guesses a letter in game
     *
     * @param letter letter to check the count of in the uncovered part of the phrase
     * @return count of this letter in the uncovered part of the phrase
     */
    public int guessLetter(char letter) {
        int count = 0;
        if (notGuessedWord.contains(String.valueOf(letter))) {
            notGuessedWord = notGuessedWord.replaceAll(String.valueOf(letter), "#");
            StringBuilder updatedWord = new StringBuilder(displayedWord);
            int findIndex = word.indexOf(letter);
            while (findIndex != -1) {
                count++;
                updatedWord.setCharAt(findIndex, letter);
                findIndex = word.indexOf(letter, findIndex + 1);
            }
            displayedWord = updatedWord.toString();
        }
        return count;
    }

    /**
     * Checks whether the provided phrase matches the one saved in this GameWord
     *
     * @param phrase phrase that a {@link Player player} guessed
     * @return true if the given phrase matches the solution, false otherwise
     */
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

    public boolean hasNotGuessedLetters() {
        return !displayedWord.equals(word);
    }

    /**
     * Uncovers a single random letter. This method is only called in games being run on the {@link GameServer game server}
     *
     * @return index of the letter to uncover, or -1 if all are uncovered
     */
    public int uncoverRandomLetter() {
        if (!hasNotGuessedLetters()) return -1;
        ArrayList<Integer> hiddenLettersIndexes = new ArrayList<>();
        for (int i = 0; i < displayedWord.length(); i++) {
            if (displayedWord.charAt(i) == '_') hiddenLettersIndexes.add(i);
        }
        Random random = new Random();
        int toUncover = hiddenLettersIndexes.get(random.nextInt(hiddenLettersIndexes.size()));
        uncoverRandomLetter(toUncover);
        return toUncover;
    }

    /**
     * Uncovers a letter at the given index. This method is only called by games run in the {@link WheelOfFortune game client}
     *
     * @param toUncover index of the letter to uncover
     */
    public void uncoverRandomLetter(int toUncover) {
        if (toUncover == -1) return;
        StringBuilder uncovered = new StringBuilder(displayedWord);
        uncovered.setCharAt(toUncover, word.charAt(toUncover));
        displayedWord = uncovered.toString();
        StringBuilder notGuessed = new StringBuilder(notGuessedWord);
        notGuessed.setCharAt(toUncover, '#');
        notGuessedWord = notGuessed.toString();
    }

    public String getPhrase() {
        return word;
    }
}
