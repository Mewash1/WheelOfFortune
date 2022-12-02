package pap.z26.wheeloffortune;

import java.util.Scanner;

public class BotPlayer implements Player {

    public BotPlayer(Game game) {
        this.game = game;
        setName();
    }
    Game game;
    String name;

    public void setName(){
        Scanner input = new Scanner(System.in);
        this.name = input.nextLine();
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

    }
}
