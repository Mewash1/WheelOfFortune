import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import pap.z26.wheeloffortune.Game;
import pap.z26.wheeloffortune.HumanPlayer;
import pap.z26.wheeloffortune.NetworkClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class GameTest {

    @Test
    public void createTest() {
        Game game = new Game();
        assertEquals(" ", game.getLastRolled());
        assertNull(game.getCategory());
        assertNull(game.getMoveState());
        assertNull(game.getPhrase());
        assertEquals(0, game.getPlayers().size());
        assertEquals(0, game.getScores().size());
        assertEquals(0, game.getRoundScores().size());
        assertNull(game.getState());
        assertNull(game.getWinner());
    }

    @Test
    public void joinTest() {
        Game game = new Game();
        HumanPlayer player = new HumanPlayer("TestPlayer");
        assertTrue(game.joinGame(player));
        assertEquals(game, player.getGame());
        assertEquals(player, game.getPlayers().get(0));

        game.executeFromOutside(new JSONObject("{player:\"SYSTEM\",action:\"start\"}"), false);
        assertEquals(3, game.getPlayers().size());
        HumanPlayer player2 = new HumanPlayer("test2");
        assertFalse(game.joinGame(player2));
        assertNull(player2.getGame());

        game.reset();
        assertNull(player.getGame());
        assertTrue(game.joinGame(player2));
        assertEquals(game, player2.getGame());
        assertEquals(player2, game.getPlayers().get(0));

        HumanPlayer p3 = new HumanPlayer("p3"), p4 = new HumanPlayer("p4");
        assertTrue(game.joinGame(p3));
        assertEquals(game, p3.getGame());
        assertEquals(p3, game.getPlayers().get(1));
        assertTrue(game.joinGame(p4));
        assertEquals(game, p4.getGame());
        assertEquals(p4, game.getPlayers().get(2));
        assertFalse(game.joinGame(player));
        assertNull(player.getGame());
        assertEquals(3, game.getPlayers().size());
    }

    @Test
    public void leaveTest() {
        Game game = new Game();
        HumanPlayer player = new HumanPlayer("TestPlayer");
        game.joinGame(player);
        game.leaveGame(player);
        assertNull(player.getGame());
        assertEquals(0, game.getPlayers().size());

        game.joinGame(player);
        game.executeFromOutside(new JSONObject("{player:\"SYSTEM\",action:\"start\"}"), false);
        game.leaveGame(player);
        assertEquals(game, player.getGame());
        assertEquals(3, game.getPlayers().size());

        game.executeFromOutside(new JSONObject("{player:\"TestPlayer\",action:\"left\",repl:\"TP2\"}"), false);
        assertNull(player.getGame());
        assertEquals(3, game.getPlayers().size());
        assertEquals("TP2", game.getPlayers().get(0).getName());
    }

    @Test
    public void gameTest() {
        NetworkClient mockNetworkClient = Mockito.mock(NetworkClient.class);
        doNothing().when(mockNetworkClient).sendData(anyString());

        Game game = new Game();
        game.setNetworkClient(mockNetworkClient);

        HumanPlayer p1 = new HumanPlayer("p1"), p2 = new HumanPlayer("p2"), p3 = new HumanPlayer("p3");
        game.joinGame(p1);
        game.joinGame(p2);
        game.joinGame(p3);
        assertEquals(3, game.getPlayers().size());

        game.executeFromOutside(new JSONObject("{player:\"SYSTEM\",action:\"start\"}"), false);
        game.executeFromOutside(new JSONObject("{player:\"SYSTEM\",action:\"newword\",word:\"test phrase\",cat:\"testcat\"}"), false);
        assertTrue(game.isInProgress());
        assertEquals("____ ______", game.getPhrase());
        assertEquals("testcat", game.getCategory());

        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(game.getMoveState(), Game.MoveState.HAS_TO_SPIN);
        game.executeFromOutside(new JSONObject("{player:\"p1\",action:\"spin\",value:-1}"), false);
        assertEquals("Pass", game.getLastRolled());

        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(game.getMoveState(), Game.MoveState.HAS_TO_SPIN);
        game.executeFromOutside(new JSONObject("{player:\"p2\",action:\"spin\",value:200}"), false);
        assertEquals("$200", game.getLastRolled());
        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(game.getMoveState(), Game.MoveState.HAS_TO_GUESS_CONSONANT);
        assertEquals(2, game.guessLetter(p2, 't'));
        assertEquals(400, game.getRoundScores().get(p2));
        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(game.getMoveState(), Game.MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS);
        game.guessPhrase(p2, "test string");

        assertEquals(p3, game.getCurrentPlayer());
        assertEquals(game.getMoveState(), Game.MoveState.HAS_TO_SPIN);
        game.executeFromOutside(new JSONObject("{player:\"p2\",action:\"spin\",value:200}"), false);
        game.executeFromOutside(new JSONObject("{player:\"p3\",action:\"spin\",value:500}"), false);
        assertEquals(p3, game.getCurrentPlayer());
        assertEquals(game.getMoveState(), Game.MoveState.HAS_TO_GUESS_CONSONANT);
        assertEquals(2, game.guessLetter(p3, 's'));
        assertEquals(1000, game.getRoundScores().get(p3));
        assertEquals(p3, game.getCurrentPlayer());
        assertEquals(game.getMoveState(), Game.MoveState.CAN_BUY_VOWEL_SPIN_OR_GUESS);
        game.guessPhrase(p2, "test phrase");
        game.executeFromOutside(new JSONObject("{player:\"SYSTEM\",action:\"newword\",word:\"test phrase two\",cat:\"testcat\"}"), false);
        assertEquals(0, game.getRoundScores().get(p1));
        assertEquals(0, game.getRoundScores().get(p2));
        assertEquals(0, game.getRoundScores().get(p3));
        assertEquals(0, game.getScores().get(p1));
        assertEquals(0, game.getScores().get(p2));
        assertEquals(1000, game.getScores().get(p3));
    }
}
