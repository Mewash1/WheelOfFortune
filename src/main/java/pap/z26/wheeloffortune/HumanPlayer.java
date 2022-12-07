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

    }
}
