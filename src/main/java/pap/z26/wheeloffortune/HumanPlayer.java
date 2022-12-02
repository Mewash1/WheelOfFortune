package pap.z26.wheeloffortune;
import java.util.HashMap;
import java.util.Scanner;

public class HumanPlayer implements Player {

    Game game;
    String name;
    public HumanPlayer(Game game) {
        this.game = game;
        setName();
    }

     public void setName(){
        Scanner input = new Scanner(System.in);
        this.name = input.nextLine();
    }

    private char getVowel(){
        System.out.println("What Vowel you want to uncover? :");
        Scanner vovInput = new Scanner(System.in);
        String vov = vovInput.nextLine();
        return ' ';
    }

    private char getConsonant(){
        return ' ';
    }

    private String getPhrase(){
        return "";
    }


    private int getDecision() throws IllegalArgumentException{
        System.out.println("What is your move:");
        System.out.println("1: Spin the wheel and guess a letter");
        System.out.println("2: Buy a vowel (200 points)");
        System.out.println("3: Guess the Phrase");
        Scanner moveGetter = new Scanner(System.in);
        String theMove = moveGetter.nextLine();
        int moveInt = Integer.parseInt(theMove);
        if ( 3>= moveInt &&  moveInt>= 0){
            return moveInt;
        }else {
                throw new IllegalArgumentException("Wrong input - insert a number from 1 to 3!");
        }
    }

    private void showGameState(){
        //very temporary function

        System.out.println(game.getPhrase());
        HashMap<Player, Integer> scores = game.getScores();
        for (Player pl : scores.keySet()){
            if (pl == this){
                System.out.println("YOU -> ");
            }
            System.out.println(pl.getName() + ": " + scores.get(pl));
        }
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
        showGameState();
        int decision = 0;
        while (decision == 0) {
            try{
            decision = getDecision();}
            catch (IllegalArgumentException ignored){}
        }
        switch (decision){
            case 1:{
                this.game.spinTheWheel(this);
            }
            case 2:{
                this.game.guessLetter(this, getVowel());
            }
            default:{
                this.game.guessPhrase(this, getPhrase());
            }
        }
    }
}
