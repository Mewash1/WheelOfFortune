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
    private static final Integer consonantSumWeights;
    private static final Integer vowelSumWeights;

    private ArrayList<Character> getLetterList(){
        ArrayList<Character> letterList = new ArrayList<>();
        letterList.addAll(currVowels);
        letterList.addAll(currConsonants);
        return letterList;
    }

    private ArrayList<Integer> getLetterWeights(){
        ArrayList<Integer> letterWeights = new ArrayList<>();
        letterWeights.addAll(currVowelWeights);
        letterWeights.addAll(currConsWeights);
        return letterWeights;
    }



    static {
        int tempVowelSumWeights = 0;
        int tempConsonantSumWeights = 0;
        consonantWeights = new ArrayList<>();
        vowelWeights = new ArrayList<>();
        String weightsConsonants = "91122125333221929312659735";
        String weightsVowels = "111132555";
        for (int i=0; i < weightsConsonants.length(); i++){
            consonantWeights.add(10 - Integer.parseInt(String.valueOf(weightsConsonants.charAt(i))));
            tempConsonantSumWeights += consonantWeights.get(i);
        }
        for (int i=0; i< weightsVowels.length(); i++){
            vowelWeights.add(10 - Integer.parseInt(String.valueOf(weightsVowels.charAt(i))));
            tempVowelSumWeights += vowelWeights.get(i);
        }
        vowelSumWeights = tempVowelSumWeights;
        consonantSumWeights = tempConsonantSumWeights;
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

    private Integer countEmptyLetters(String phrase){
        int result = 0;
        var phraseChars = phrase.toCharArray();
        for (char letter : phraseChars){
            if (letter == '_') result++;
        }
        return result;
    }

    private ArrayList<Character> currConsonants = new ArrayList<>();
    private ArrayList<Character> currVowels = new ArrayList<>();
    private ArrayList<Integer> currConsWeights = new ArrayList<>();
    private ArrayList<Integer> currVowelWeights = new ArrayList<>();

    private Integer currSumConsWeights, currSumVowelWeights;

    public void notifyNewRound(){
        currConsonants = new ArrayList<>();
        currVowels = new ArrayList<>();
        currConsWeights = new ArrayList<>();
        currVowelWeights = new ArrayList<>();

        currConsonants.addAll(GameWord.consonants);
        currVowels.addAll(GameWord.vowels);
        currConsWeights.addAll(consonantWeights);
        currVowelWeights.addAll(vowelWeights);
        currSumConsWeights = consonantSumWeights;
        currSumVowelWeights = vowelSumWeights;
        hasGuessedCorrectly = false;
        hasSpunTheWheel = false;
    }

    public void notifyLetter(Character letter){

        for(int i=0; i< currConsonants.size(); i++){
            if (letter.charValue() == currConsonants.get(i)){
                currConsonants.remove(i);
                currSumConsWeights -= currConsWeights.get(i);
                currConsWeights.remove(i);
                return;
            }
        }

        for(int i=0; i<currVowels.size(); i++){
            if (letter.charValue() == currVowels.get(i)){
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
        return currConsonants.get(randomLetterIndex(currConsWeights, currSumConsWeights));
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
        ArrayList<Character> letterList = getLetterList();
        ArrayList<Integer> letterWeights = getLetterWeights();

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
                    if (consonantSumWeights == 0) {
                        hasGuessedCorrectly = (this.game.guessLetter(this, 't') != 0);
                    }else hasGuessedCorrectly = (this.game.guessLetter(this, getConsonant()) != 0);
                    hasSpunTheWheel = false;
                    return;
                }

                ArrayList<String> listOfPossiblePhrases = this.getAllMatchingPhrases(this.game.getPhrase());
                int amountOfPossiblePhrases = listOfPossiblePhrases.size();

                int choice;

                if (!this.game.hasNotGuessedConsonants()) {

                    //no consonants - cannot spin the wheel
                    //guess a phrase or buy vowel if you can afford it
                    currConsonants = new ArrayList<>();
                    currConsWeights = new ArrayList<>();
                    currSumConsWeights = 0;
                    if (this.game.getRoundScores().get(this) >= 200) {
                        ArrayList <Integer> options = new ArrayList<>(Arrays.asList(1, 3));
                        ArrayList <Integer> optionsWages = new ArrayList<>(Arrays.asList(1, Math.min(1, currSumVowelWeights)));
                        choice = options.get(randomLetterIndex(optionsWages, 2));
                    } else {
                        choice = 1;
                    }
                } else if (amountOfPossiblePhrases == 0) {
                    choice = 2;
                } else {
                    if (hasGuessedCorrectly) {
                        //can spin, buy vowel, guess the phrase
                        if (this.game.getRoundScores().get(this) >= 200) {
                            ArrayList <Integer> options = new ArrayList<>(Arrays.asList(1, 2, 3));
                            int chanceVowel = Math.max(0, this.game.getRoundScores().get(this)/1000 + countEmptyLetters(game.getPhrase())/2 - Math.max(0, (5000 - this.game.getRoundScores().get(this))/1000));
                            chanceVowel *= Math.min(1, currSumVowelWeights);
                            ArrayList <Integer> optionsWages = new ArrayList<>(Arrays.asList(3/amountOfPossiblePhrases, countEmptyLetters(game.getPhrase()), chanceVowel));
                            int chanceSum = 0;
                            for (int i=0; i<3; i++){
                                chanceSum += optionsWages.get(i);
                            }
                            choice = options.get(randomLetterIndex(optionsWages, chanceSum));
                        } else {
                            //can spin or guess the phrase
                            ArrayList <Integer> options = new ArrayList<>(Arrays.asList(1, 2));
                            ArrayList <Integer> optionsWages = new ArrayList<>(Arrays.asList(2, countEmptyLetters(game.getPhrase())));
                            int chanceSum = 0;
                            for (int i=0; i<2; i++){
                                chanceSum += optionsWages.get(i);
                            }
                            choice = options.get(randomLetterIndex(optionsWages, chanceSum));
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
                        hasGuessedCorrectly = false;
                    }
                    default -> {

                        if (this.game.spinTheWheel(this)) {
                            hasSpunTheWheel = this.game.getLastRolled().contains("$");
                        }
                    }
                }
            }
            case ROUND2 -> {
                if (!hasGuessedCorrectly){
                    if (this.game.hasNotGuessedConsonants()){

                        ArrayList<Character> letterList = getLetterList();
                        ArrayList<Integer> letterWeights = getLetterWeights();
                        Integer sumLetterWeights = currSumConsWeights + currSumVowelWeights;

                        hasGuessedCorrectly = this.game.guessLetter(this, letterList.get(randomLetterIndex(letterWeights,sumLetterWeights))) != 0;
                    }else {
                        hasGuessedCorrectly = this.game.guessLetter(this, currVowels.get(randomLetterIndex(vowelWeights, currSumVowelWeights))) != 0;
                    }
                }else{
                    hasGuessedCorrectly = false;
                    Random rand = new Random();
                    int res = rand.nextInt(2);
                    if (res == 0) this.game.guessPhrase(this, getPhrase());
                }
            }
            case ROUND4 -> {
                Random rand = new Random();
                int res = rand.nextInt(1 + countEmptyLetters(this.game.getPhrase()));
                if (res == 0) this.game.guessPhrase(this, getPhrase());
            }
            case FINAL ->{
                if(!hasGuessedCorrectly){
                    hasGuessedCorrectly = true;
                    char[] outputChar = "    ".toCharArray();
                    for (int i =0; i<3; i++){
                        outputChar[i] = currConsonants.get(randomLetterIndex(currConsWeights, currSumConsWeights));
                        notifyLetter(outputChar[i]);
                    }
                    outputChar[3] = currVowels.get((randomLetterIndex(currVowelWeights, currSumVowelWeights)));
                    this.game.guessPhrase(this, String.valueOf(outputChar));
                }else{
                    var matches = getAllMatchingPhrases(this.game.getPhrase());
                    if(matches.size() != 0){
                        Random rand = new Random();
                        int ind = rand.nextInt(matches.size());
                        this.game.guessPhrase(this, matches.get(ind));
                    }
                    else{
                        this.game.guessPhrase(this, this.stupidGetPhrase());
                    }
                }
            }
            default -> System.out.println("Wrong game state");
        }
    }

    @Override
    public boolean isBot() {
        return true;
    }
}
