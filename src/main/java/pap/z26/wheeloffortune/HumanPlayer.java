package pap.z26.wheeloffortune;


public class HumanPlayer implements Player {

    Game game;
    String name;

    public HumanPlayer(Game game) {
        setGame(game);
        setName("Player");
    }


    public void setName(String name) {
        this.name = name;
    }

//    private char getVowel() {
//        char[] vowelList = {'a', 'e', 'i', 'o', 'u', 'y'};
//        while (true) {
//            try {
//                System.out.println("What Vowel you want to uncover? :");
//                Scanner vowInput = new Scanner(System.in);
//                String vow = vowInput.nextLine();
//                for (char i : vowelList) {
//                    if (i == vow.charAt(0)) {
//                        return vow.charAt(0);
//                    }
//                }
//                throw new IllegalArgumentException("Provided character is not a vowel");
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//
//            }
//        }
//    }
//
//    private char getConsonant() {
//        char[] consonantList = {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z'};
//        while (true) {
//            try {
//                System.out.println("What consonant you want to uncover? :");
//                Scanner consonantInput = new Scanner(System.in);
//                String consonant = consonantInput.nextLine();
//                for (char i : consonantList) {
//                    if (i == consonant.charAt(0)) {
//                        return consonant.charAt(0);
//                    }
//                }
//                throw new IllegalArgumentException("Provided character is not a consonant");
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//
//            }
//        }
//    }
//
//    private String getPhrase() {
//        System.out.println("The phrase is :");
//        Scanner consonantInput = new Scanner(System.in);
//        return consonantInput.nextLine();
//    }
//
//
//    private int getDecision() throws IllegalArgumentException {
//        System.out.println("What is your move? :");
//        System.out.println("1: Spin the wheel to guess a consonant");
//        System.out.println("2: Guess a consonant");
//        System.out.println("3: Buy a vowel (200 points)");
//        System.out.println("4: Guess the Phrase");
//        Scanner moveGetter = new Scanner(System.in);
//        String theMove = moveGetter.nextLine();
//        int moveInt = Integer.parseInt(theMove);
//        if (4 >= moveInt && moveInt >= 0) {
//            return moveInt;
//        } else {
//            throw new IllegalArgumentException("Wrong input - insert a number from 1 to 4!");
//        }
//    }
//
//    private void showGameState() {
//        //very temporary function
//
//        System.out.println(game.getPhrase());
//        HashMap<Player, Integer> scores = game.getRoundScores();
//        for (Player pl : scores.keySet()) {
//            if (pl == this) {
//                System.out.println("YOU -> ");
//            }
//            System.out.println(pl.getName() + ": " + scores.get(pl));
//        }
//    }

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
//        showGameState();
//        int decision = 0;
//        while (decision == 0) {
//            try {
//                decision = getDecision();
//                switch (decision) {
//                    case 1: {
//                        if (game.hasNotGuessedConsonants()) {
//                            this.game.spinTheWheel(this);
//                            System.out.println(this.game.getLastRolled());
//                        }
//                        throw new IllegalArgumentException("You cannot spin the wheel as there are no uncovered consonants in the phrase");
//                    }
//                    case 2: {
//                        this.game.guessLetter(this, getConsonant());
//                    }
//
//                    case 3: {
//                        this.game.guessLetter(this, getVowel());
//                    }
//                    default: {
//                        this.game.guessPhrase(this, getPhrase());
//                    }
//                }
//            } catch (IllegalArgumentException e) {
//                decision = 0;
//                System.out.println(e.getMessage());
//            }
//        }
    }
}
