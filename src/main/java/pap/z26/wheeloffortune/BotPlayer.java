package pap.z26.wheeloffortune;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class BotPlayer implements Player {

    public BotPlayer(Game game) {
        this.game = game;
        setName();
    }
    Game game;
    String name;

    private boolean hasSpunTheWheel = false;


    public void setName(){
        System.out.println("Name a bot player :");
        Scanner input = new Scanner(System.in);
        this.name = input.nextLine();
    }


    private char getVowel(){
        char [] vowelList ={'a', 'e', 'i', 'o', 'u', 'y'};
        ArrayList<Character> possibleVowels = new ArrayList<>();
        for (int k = 0; k < vowelList.length; k++) {
            char i = vowelList[k];
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
        char [] consonantList ={'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z'};
        List<Character> possibleConsonants = new ArrayList<>();
        for (char i : consonantList){
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
        char [] letterList = {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z', 'a', 'e', 'i', 'o', 'u', 'y'};
        String returnPhraseStr = this.game.getPhrase();
        var returnPhrase = returnPhraseStr.toCharArray();
        for (int i = 0; i < returnPhrase.length; i++){
            if (returnPhrase[i] == '#'){
                Random rand = new Random();
                returnPhrase[i] = letterList[rand.nextInt(letterList.length)];
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
        switch (choice) {
            case 3 -> this.game.guessLetter(this, getVowel());
            case 2 -> {

                if (this.game.spinTheWheel(this)){
                    this.hasSpunTheWheel = true;
                }
            }
            default -> this.game.guessPhrase(this, getPhrase());
        }
    }
}
