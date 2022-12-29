package pap.z26.wheeloffortune;

import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;
import java.util.Random;
//import java.util.stream.Stream;

public class BotPlayer implements Player {

    public BotPlayer(String name) {
        if (name.length() == 0) name = "";
        setName(name);
    }

    Game game;
    String name;

    private boolean hasSpunTheWheel = false;
    private boolean hasGuessedCorrectly = false;


    public void setName(String name) {
        this.name = name;
        if (name.equals("")) this.name = "Jeff";
    }


    private char getVowel() {
        ArrayList<Character> possibleVowels = new ArrayList<>();
        for (int k = 0; k < GameWord.vowels.size(); k++) {
            char i = GameWord.vowels.get(k);
            possibleVowels.add(i);
            for (char j : game.getPhrase().toCharArray()) {
                if (i == j) {
                    possibleVowels.remove(possibleVowels.size() - 1);
                    break;
                }
            }
        }
        Random rand = new Random();
        return possibleVowels.get(rand.nextInt(possibleVowels.size()));
    }

    private char getConsonant() {
        List<Character> possibleConsonants = new ArrayList<>();
        for (char i : GameWord.consonants) {
            possibleConsonants.add(i);
            for (char j : game.getPhrase().toCharArray()) {
                if (i == j) {
                    possibleConsonants.remove(possibleConsonants.size() - 1);
                    break;
                }
            }
        }
        Random rand = new Random();
        return possibleConsonants.get(rand.nextInt(possibleConsonants.size()));
    }

    private String getPhrase() {
        String returnPhraseStr = this.game.getPhrase();
        var returnPhrase = returnPhraseStr.toCharArray();
        for (int i = 0; i < returnPhrase.length; i++) {
            if (returnPhrase[i] == '_') {
                Random rand = new Random();
                returnPhrase[i] = GameWord.letters.get(rand.nextInt(GameWord.letters.size()));
            }
        }
        return String.valueOf(returnPhrase);
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Game getGame() {
        return this.game;
    }


    @Override
    public void makeAMove() {

        if (hasSpunTheWheel) {
            //guess a consonant after spinning the wheel
            hasGuessedCorrectly = (this.game.guessLetter(this, getConsonant()) != 0);
            hasSpunTheWheel = false;
            return;
        }

        int choice;
        Random rand = new Random();

        if (!this.game.hasNotGuessedConsonants()) {
            //no vowels - cannot spin the wheel
            //guess a phrase or buy vowel if you can afford it
            if (this.game.getRoundScores().get(this) >= 200) {
                char[] options = {1, 3};
                choice = options[rand.nextInt(2)];
            } else {
                choice = 1;
            }
        } else {
            if (hasGuessedCorrectly) {
                //can spin, buy vowel, guess the phrase
                if (this.game.getRoundScores().get(this) >= 200) {
                    choice = rand.nextInt(3);

                    if (choice == 1) {
                        //to reduce guessing the phrase (1/21 chance)
                        choice = rand.nextInt(7);
                    }
                } else {
                    //to reduce guessing the phrase (1/20 chance)
                    choice = rand.nextInt(20);
                    if (choice == 3){
                        choice = 2;
                    }
                }
            } else {
                //first move - opponent failed
                //can only spin the wheel
                choice = 2;
            }
        }

        switch (choice) {
            case 3 -> {
                hasGuessedCorrectly = (this.game.guessLetter(this, getVowel()) != 0);
                hasSpunTheWheel = false;
            }
            case 1 -> {
                this.game.guessPhrase(this, getPhrase());
                hasSpunTheWheel = false;
            }
            default -> {

                if (this.game.spinTheWheel(this)) {
                    this.hasSpunTheWheel = true;
                }
            }
        }
    }

    @Override
    public boolean isBot() {
        return true;
    }
}
