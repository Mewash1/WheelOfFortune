package pap.z26.wheeloffortune;

public interface Player {

    String getName();

    Game getGame();

    void setGame(Game game);

    void makeAMove();

    boolean isBot();

    void notifyLetter(Character letter);

    void notifyNewRound();
}
