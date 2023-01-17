package pap.z26.wheeloffortune;

import java.util.*;

/**
 * class for Bots
 * makes a move if makeAMove is called
 * to work correctly:
 * bot should be notified about new round by notifyNewRound
 * bot should be notified about any player guessing a letter by notifyLetter
 */
public class BotPlayer implements Player {

        /**
         * constructor for BotPlayer
         * assigns random name for bot
         *
         */
        public BotPlayer() {
            Random random = new Random();
            String[] botNames = {"Kot", "Duke", "Tux", "Blinky", "Ferris", "Geeko", "Gopher", "Konqi", "Kiki", "Larry", "Octocat", "Wilber"};
            this.name = botNames[random.nextInt(botNames.length)];
            notifyNewRound();
        }

        /**
         * A game that bot is playing in
         */
        Game game;

        /**
         * Name of bot
         */
        String name;

        /**
         * variable to keep track if last move was spinning the wheel with positive outcome (a money amount)
         */
        private boolean hasSpunTheWheel = false;

        /**
         * variable to keep track if bot's last move was successful guess of either consonant or vowel
         */
        private boolean hasGuessedCorrectly = false;

        /**
         * list of initial weights (for randomization) of all possible consonants
         */
        private static final ArrayList<Integer> consonantWeights;

        /**
         * list of initial weights (for randomization) of all possible vowels
         */
        private static final ArrayList<Integer> vowelWeights;

        /**
         * sum of initial consonants weights (for randomization) (kept to not sum entire array many times)
         */
        private static final Integer consonantSumWeights;

        /**
         * sum of initial vowel weights (for randomization) (kept to not sum entire array many times)
         */
        private static final Integer vowelSumWeights;

        /**
         * gives list of all letter that weren't guessed already
         *
         * @return  ArrayList of Characters containing all letters that weren't guessed already
         */
        private ArrayList<Character> getLetterList(){
            ArrayList<Character> letterList = new ArrayList<>();
            letterList.addAll(currVowels);
            letterList.addAll(currConsonants);
            return letterList;
        }

        /**
         * gives list of weights (for randomization)  of all letter that weren't guessed already
         *
         * @return  ArrayList of Characters containing all letters that weren't guessed already
         */
        private ArrayList<Integer> getLetterWeights(){
            ArrayList<Integer> letterWeights = new ArrayList<>();
            letterWeights.addAll(currVowelWeights);
            letterWeights.addAll(currConsWeights);
            return letterWeights;
        }


        /**
         * code that initializes consonantWeights, vowelWeights, vowelSumWeights, consonantSumWeights
         */
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

        /**
         * method to randomize an index of arrayList of weights given as a parameter
         * need to provide sum of given weights
         * if list is empty or sum is less than 1, 0 is returned
         *
         * @param   listOfWeights list of weights for randomization (list {1, 9} has 10% to return 0 and 90% to return 1)
         * @param   sumOfWeights sum of all listOfWeights elements (for the {1, 9} example has to be 10)
         * @return  Integer of index that was randomized
         */
        private Integer randomLetterIndex(ArrayList<Integer> listOfWeights, Integer sumOfWeights){
            try {
                Random rand = new Random();
                Integer randRes = rand.nextInt(sumOfWeights);
                for (int i =0; i< listOfWeights.size(); i++){
                    randRes -= listOfWeights.get(i);
                    if (randRes < 0){
                        return i;
                    }
                }
                return 0;
            } catch (Exception ignored) {
            }
            return 0;
        }

        /**
         * method to count empty letters in phrase
         * @param   phrase the String to count letters in
         * @return  amount of empty letters in given phrase
         */
        private Integer countEmptyLetters(String phrase){
            int result = 0;
            var phraseChars = phrase.toCharArray();
            for (char letter : phraseChars){
                if (letter == '_') result++;
            }
            return result;
        }

        /**
         * list of consonants that weren't guessed already in current round
         */
        private ArrayList<Character> currConsonants = new ArrayList<>();

        /**
         * list of vowels that weren't guessed already in current round
         */
        private ArrayList<Character> currVowels = new ArrayList<>();

        /**
         * list of weights (for randomization) of consonants that weren't guessed already in current round
         */
        private ArrayList<Integer> currConsWeights = new ArrayList<>();

        /**
         * list of weights (for randomization) of vowels that weren't guessed already in current round
         */
        private ArrayList<Integer> currVowelWeights = new ArrayList<>();

        /**
         * sums of weights (for randomization) of consonants and vowels that weren't guessed already in current round
         */
        private Integer currSumConsWeights, currSumVowelWeights;

        /**
         * method that resets currConsonants, currVowels, currConsWeights,
         * currVowelWeights, currSumConsWeights, currSumVowelWeights,
         * hasGuessedCorrectly and hasSpunTheWheel to initial state for next round
         */
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

        /**
         * method that needs to be invoked after every guess of letter (by game)
         * removes given letter from list of possible consonants or vowels and from list of weights
         * subtracts letter's weight from sum of weights
         * @param   letter to erase from list of consonants/vowels
         */
        public void notifyLetter(Character letter){

            /**
             * loop for erasing consonants
             */
            for(int i=0; i< currConsonants.size(); i++){
                if (letter.charValue() == currConsonants.get(i)){
                    currConsonants.remove(i);
                    currSumConsWeights -= currConsWeights.get(i);
                    currConsWeights.remove(i);
                    return;
                }
            }

            /**
             * loop for erasing vowels
             */
            for(int i=0; i<currVowels.size(); i++){
                if (letter.charValue() == currVowels.get(i)){
                    currVowels.remove(i);
                    currSumVowelWeights -= currVowelWeights.get(i);
                    currVowelWeights.remove(i);
                    return;
                }
            }
        }

        /**
         * method that gives lis of all phrases from database that match (only '_' signs are not matching) given game state
         * @param   currGameState of a game state to match ('_' in place of not known letter)
         * @return  list of all matching phrases
         */
        private ArrayList<String> getAllMatchingPhrases(String currGameState){
            return Database.getInstance().getMatchingPhrases(currGameState);
        }

        /**
         * method that returns a random vowel from list of not guessed vowels (with probability of weights)
         * @return  randomized character
         */
        private char getVowel() {
            return currVowels.get(randomLetterIndex(currVowelWeights, currSumVowelWeights));
        }

        /**
         * method that returns a random consonant from list of not guessed consonant (with probability of weights)
         * @return  randomized character
         */
        private char getConsonant() {
            return currConsonants.get(randomLetterIndex(currConsWeights, currSumConsWeights));
        }

        /**
         * method that returns a phrase based on game's current game state
         * returns one of matching to game state phrases (from database)
         * or phrase made by replacing '_' with random available letters (stupidGetPhrase)
         * chance for stupid is 1/(amountOfMatchingPhrasesFromDatabase + 1)
         * @return  phrase to guess either from databse or from stupid get phrase
         */
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

        /**
         * method that returns a phrase based on game's current game state
         * phrase made by replacing '_' with random available letters
         * @return  phrase to guess
         */
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

        /**
         * setter of game where player is playing
         * @param   game instance of game where player is playing
         */
        @Override
        public void setGame(Game game) {
            this.game = game;
        }

        /**
         * getter of name of player
         * @return  name of player
         */
        @Override
        public String getName() {
            return this.name;
        }

        /**
         * getter of game where player is playing
         * @return  instance of game player is playing in
         */
        @Override
        public Game getGame() {
            return this.game;
        }

        /**
         * method to make player make a move
         * divided to rounds 1,3,5, round 2, round 4 and Final round options
         * randomizes move type by using randomLetterIndex - provides listOfWages of choices
         * invokes this.game.guessLetter(this, getConsonant()) to guess consonant
         * invokes this.game.guessLetter(this, getVowel()) to guess vowel
         * invokes this.game.spinTheWheel(this) to spin the wheel
         * invokes this.game.guessPhrase(this, getPhrase()) to guess phrase
         * invokes only one of said methods per makeAMove invocation
         */
        @Override
        public void makeAMove() {


            switch (this.game.getState()) {

                case ROUND1, ROUND5, ROUND3 -> {
                    //guess a consonant after spinning the wheel
                    if (hasSpunTheWheel) {
                        if (consonantSumWeights == 0) {
                            hasGuessedCorrectly = (this.game.guessLetter(this, 't') != 0);
                        }else hasGuessedCorrectly = (this.game.guessLetter(this, getConsonant()) != 0);
                        hasSpunTheWheel = false;
                        return;
                    }

                    ArrayList<String> listOfPossiblePhrases = this.getAllMatchingPhrases(this.game.getPhrase());
                    int amountOfPossiblePhrases = listOfPossiblePhrases.size();

                    int choice;

                    //no consonants - cannot spin the wheel
                    //guess a phrase or buy vowel if you can afford it
                    if (!this.game.hasNotGuessedConsonants()) {
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

                        //can spin, buy vowel, guess the phrase
                        if (hasGuessedCorrectly) {
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
                            } else
                            //can spin or guess the phrase
                                {
                                ArrayList <Integer> options = new ArrayList<>(Arrays.asList(1, 2));
                                ArrayList <Integer> optionsWages = new ArrayList<>(Arrays.asList(2, countEmptyLetters(game.getPhrase())));
                                int chanceSum = 0;
                                for (int i=0; i<2; i++){
                                    chanceSum += optionsWages.get(i);
                                }
                                choice = options.get(randomLetterIndex(optionsWages, chanceSum));
                            }
                        } else
                        //first move - opponent failed
                        //can only spin the wheel
                            {
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
                        //case 2
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

        /**
         * info whether player is a bot
         * @return  True as bot is always a bot
         */
        @Override
        public boolean isBot() {
            return true;
        }
}
