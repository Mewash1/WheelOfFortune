package pap.z26.wheeloffortune;


public class HumanPlayer implements Player {

    Game game;
    String name;

    /*
     * constructor of HumanPlayer class
     * @param   name
     */
    public HumanPlayer(String name) {
        this.name = name;
    }

    /*
     * setter of name of player
     * @param   name
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * setter of game where player is playing
     * @param   game instance of game where player is playing
     */
    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    /*
     * getter of name of player
     * @return  name of player
     */
    @Override
    public String getName() {
        return this.name;
    }

    /*
     * getter of game where player is playing
     * @return  instance of game player is playing in
     */
    @Override
    public Game getGame() {
        return this.game;
    }

    /*
     * method to make player make a move
     * used only for bot player (for human player is just nothing)
     */
    @Override
    public void makeAMove() {

    }

    /*
     * info whether player is a bot
     * @return  False
     */
    @Override
    public boolean isBot() {
        return false;
    }

    /*
     * shouldn't be invoked
     */
    @Override
    public void notifyLetter(Character letter) {

    }
    /*
     * shouldn't be invoked
     */
    @Override
    public void notifyNewRound() {

    }
}
