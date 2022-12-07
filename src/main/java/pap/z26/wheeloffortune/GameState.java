package pap.z26.wheeloffortune;

enum GameState {
    NOT_STARTED,
    ROUND1,
    ROUND2,
    ROUND3,
    ROUND4,
    ROUND5,
    FINAL,
    ENDED;

    public GameState next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
