package pap.z26.wheeloffortune;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class BotPlayer implements Player {

    public BotPlayer(String name) {
        this.name = name;
    }
    Game game;
    String name;

    private boolean hasSpunTheWheel = false;


    public void setName(){
        this.name = "Jeff";
    }


    private char getVowel(){
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

    private char getConsonant(){
        List<Character> possibleConsonants = new ArrayList<>();
        for (char i : GameWord.consonants){
            possibleConsonants.add(i);
            for (char j : game.getPhrase().toCharArray()){
                if (i == j){
                    possibleConsonants.remove(possibleConsonants.size()-1);
                    break;
                }
            }
        }
        Random rand = new Random();
        return possibleConsonants.get(rand.nextInt(possibleConsonants.size()));
    }

    private String getPhrase(){
        String returnPhraseStr = this.game.getPhrase();
        var returnPhrase = returnPhraseStr.toCharArray();
        for (int i = 0; i < returnPhrase.length; i++){
            if (returnPhrase[i] == '#'){
                Random rand = new Random();
                returnPhrase[i] = GameWord.letters.get(rand.nextInt(GameWord.letters.size()));
            }
        }
        return String.valueOf(returnPhrase);
    }

    @Override
    public void setGame(Game game){
        this.game = game;
    }
    @Override
    public String getName(){
        return this.name;
    }
    @Override
    public Game getGame(){
        return this.game;
    }


    @Override
    public void makeAMove() {
        if (hasSpunTheWheel){
            this.game.guessLetter(this, getConsonant());
            hasSpunTheWheel = false;
            return;
        }
        int choice;
        Random rand = new Random();
        if (this.game.getRoundScores().get(this) >= 200){
            if (this.game.hasNotGuessedConsonants()){
                choice = rand.nextInt(3);
            }
            else{
                choice = rand.nextInt(2);
            }
        } else if (this.game.hasNotGuessedConsonants()) {
            char[] options = {1, 3};
            choice = options[rand.nextInt(2)];
        }else {
            choice = 1;
        }
        if(!this.hasSpunTheWheel)  choice = 2;
        switch (choice) {
            case 3 -> {
                this.game.guessLetter(this, getVowel());
                hasSpunTheWheel = false;
            }
            case 2 -> {

                if (this.game.spinTheWheel(this)){
                    this.hasSpunTheWheel = true;
                }
            }
            default -> {
                this.game.guessPhrase(this, getPhrase());
                hasSpunTheWheel = false;
            }
        }
    }
}
