package pap.z26.wheeloffortune;

public interface Player {

    /*
     * getter of name of player
     * @return  name of player
     */
    String getName();

    /*
     * getter of game where player is playing
     * @return  instance of game player is playing in
     */
    Game getGame();

    /*
     * setter of game where player is playing
     * @param   game instance of game where player is playing
     */
    void setGame(Game game);

    /*
     * method to make player make a move
     * used only for bot player (for human player is just nothing)
     */
    void makeAMove();

    /*
     * info whether player is a bot
     * @return  True is invoked on bot False if on player
     */
    boolean isBot();

    /*
     * method to make bot player aware that someone tried to guess a letter
     * need to be invoked on every bot after every letter that were guessee
     * @param   letter to erase from list of consonants/vowels
     */
    void notifyLetter(Character letter);

    /*
     * method that prepares bot to play in next round
     * needs to be invoked on every bot in start of every round
     * invoked on player does nothing (do not do than)
     */
    void notifyNewRound();
}
