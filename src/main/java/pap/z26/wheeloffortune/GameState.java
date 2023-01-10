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

    public String toString() {
        return switch (this) {
            case NOT_STARTED -> "Waiting for players...";
            case ROUND1 -> "Round 1";
            case ROUND2 -> "Round 2";
            case ROUND3 -> "Round 3";
            case ROUND4 -> "Round 4";
            case ROUND5 -> "Round 5";
            case FINAL -> "Final round!";
            case ENDED -> "The game has ended";
        };
    }
}
