package pap.z26.wheeloffortune;
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


    /*private void getDecision(){
        System.out.println("What is your move:");
        System.out.println("1: Spin the wheel and guess a letter");
        System.out.println("2: Buy a vowel (200 points)");
        System.out.println("3: Guess the Phrase");
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
    */
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

    }
}
