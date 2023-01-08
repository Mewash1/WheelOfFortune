package pap.z26.wheeloffortune;

import java.util.*;

public class BotPlayer implements Player {

    public BotPlayer() {
        Random random = new Random();
        String[] botNames = {"Kot", "Duke", "Tux", "Blinky", "Ferris", "Geeko", "Gopher", "Konqi", "Kiki", "Larry", "Octocat", "Wilber"};
        this.name = botNames[random.nextInt(botNames.length)];

        currConsonants.addAll(GameWord.consonants);
        currVowels.addAll(GameWord.vowels);
        currConsWeights.addAll(consonantWeights);
        currVowelWeights.addAll(vowelWeights);
//        Collections.copy(currConsonants, GameWord.consonants);
//        Collections.copy(currVowels, GameWord.vowels);
//        Collections.copy(currConsWeights, consonantWeights);
//        Collections.copy(currVowelWeights, vowelWeights);
        currSumConsWeights = consonantSumWeights;
        currSumVowelWeights = vowelSumWeights;
    }

    public BotPlayer(String name) {
        if (name.length() == 0) name = "";
        setName(name);
    }

    Game game;
    String name;

    private boolean hasSpunTheWheel = false;
    private boolean hasGuessedCorrectly = false;

    private static final ArrayList<Integer> consonantWeights;
    private static final ArrayList<Integer> vowelWeights;
    private static final Integer consonantSumWeights= 71;
    private static final Integer vowelSumWeights = 24;

    static {
        consonantWeights = new ArrayList<>();
        vowelWeights = new ArrayList<>();
        String weightsConsonants = "01122125333221020312659735";
        String weightsVowels = "111132555";
        for (int i=0; i < weightsConsonants.length(); i++){
            consonantWeights.add(Integer.parseInt(String.valueOf(weightsConsonants.charAt(i))));
        }
        for (int i=0; i< weightsVowels.length(); i++){
            vowelWeights.add(Integer.parseInt(String.valueOf(weightsVowels.charAt(i))));
        }
    }

    private Integer randomLetterIndex(ArrayList<Integer> listOfWeights, Integer sumOfWeights){
        Random rand = new Random();
        Integer randRes = rand.nextInt(sumOfWeights);
        for (int i =0; i< listOfWeights.size(); i++){
            randRes -= listOfWeights.get(i);
            if (randRes < 0){
                return i;
            }
        }
        return 0;
    }

    private ArrayList<Character> currConsonants = new ArrayList<>();
    private ArrayList<Character> currVowels = new ArrayList<>();
    private ArrayList<Integer> currConsWeights = new ArrayList<>();
    private ArrayList<Integer> currVowelWeights = new ArrayList<>();

    private Integer currSumConsWeights, currSumVowelWeights;

    public void notifyNewRound(){
        Collections.copy(currConsonants, GameWord.consonants);
        Collections.copy(currVowels, GameWord.vowels);
        Collections.copy(currConsWeights, consonantWeights);
        Collections.copy(currVowelWeights, vowelWeights);
        currSumConsWeights = consonantSumWeights;
        currSumVowelWeights = vowelSumWeights;
        hasGuessedCorrectly = false;
        hasSpunTheWheel = false;
    }

    public void notifyLetter(Character letter){
        for(int i=0; i< currConsonants.size(); i++){
            if (letter == currConsonants.get(i)){
                currConsonants.remove(i);
                currSumConsWeights -= currConsWeights.get(i);
                currConsWeights.remove(i);
                return;
            }
        }

        for(int i=0; i<currVowels.size(); i++){
            if (letter == currVowels.get(i)){
                currVowels.remove(i);
                currSumVowelWeights -= currVowelWeights.get(i);
                currVowelWeights.remove(i);
                return;
            }
        }
    }

    public void setName(String name) {
        this.name = name;
        if (name.equals("")) this.name = "Jeff";
    }

    private ArrayList<String> getAllMatchingPhrases(String currGameState){
        return Database.getInstance().getMatchingPhrases(currGameState);
    }


    private char getVowel() {
        return currVowels.get(randomLetterIndex(currVowelWeights, currSumVowelWeights));
    }

    private char getConsonant() {
        return currConsonants.get(randomLetterIndex(currConsWeights, currSumVowelWeights));
    }

    private String getPhrase(){

        var listOfPhrases = getAllMatchingPhrases(this.game.getPhrase());
        String returnPhrase;
        if (listOfPhrases.size() != 0){
            Random rand = new Random();
            int randRes = rand.nextInt(listOfPhrases.size() + 1);
            if (randRes != listOfPhrases.size()){
                returnPhrase = listOfPhrases.get(randRes);
            }
            else{
                returnPhrase = stupidGetPhrase();
            }
        }
        else {
            returnPhrase = stupidGetPhrase();
        }
        return returnPhrase;
    }

    private String stupidGetPhrase() {
        ArrayList<Character> letterList = new ArrayList<>();
        letterList.addAll(currVowels);
        letterList.addAll(currConsonants);

        ArrayList<Integer> letterWeights = new ArrayList<>();
        letterWeights.addAll(currVowelWeights);
        letterWeights.addAll(currConsWeights);

        Integer sumLetterWeights = currSumConsWeights + currSumVowelWeights;

        String returnPhraseStr = this.game.getPhrase();
        var returnPhrase = returnPhraseStr.toCharArray();
        for (int i = 0; i < returnPhrase.length; i++) {
            if (returnPhrase[i] == '_') {
                returnPhrase[i] = letterList.get(randomLetterIndex(letterWeights, sumLetterWeights));
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


        switch (this.game.getState()) {

            case ROUND1, ROUND5, ROUND3 -> {

                if (hasSpunTheWheel) {
                    //guess a consonant after spinning the wheel
                    hasGuessedCorrectly = (this.game.guessLetter(this, getConsonant()) != 0);
                    hasSpunTheWheel = false;
                    return;
                }

                ArrayList<String> listOfPossiblePhrases = this.getAllMatchingPhrases(this.game.getPhrase());
                int amountOfPossiblePhrases = listOfPossiblePhrases.size();

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
                            if (choice == 3) {
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
            case ROUND2 -> {
                if (!hasGuessedCorrectly){
                    if (this.game.hasNotGuessedConsonants()){
                        ArrayList<Character> letterList = new ArrayList<>();
                        letterList.addAll(currVowels);
                        letterList.addAll(currConsonants);

                        ArrayList<Integer> letterWeights = new ArrayList<>();
                        letterWeights.addAll(currVowelWeights);
                        letterWeights.addAll(currConsWeights);

                        Integer sumLetterWeights = currSumConsWeights + currSumVowelWeights;
                        hasGuessedCorrectly = 0 != this.game.guessLetter(this, letterList.get(randomLetterIndex(letterWeights,sumLetterWeights)));
                    }else {
                        hasGuessedCorrectly = 0 != this.game.guessLetter(this, currVowels.get(randomLetterIndex(vowelWeights, currSumVowelWeights)));
                    }
                }else{
                    hasGuessedCorrectly = false;
                    Random rand = new Random();
                    int res = rand.nextInt(2);
                    if (res == 0) this.game.getPhrase();
                }
            }
            case ROUND4 -> {
                return;
            }
            default -> {
                System.out.println("Wrong game state");
            }
        }
    }

    @Override
    public boolean isBot() {
        return true;
    }
}
