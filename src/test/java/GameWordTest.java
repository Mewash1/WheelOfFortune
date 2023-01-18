import org.junit.jupiter.api.Test;
import pap.z26.wheeloffortune.GameWord;

import static org.junit.jupiter.api.Assertions.*;


public class GameWordTest {

    @Test
    public void testInitialization() {
        GameWord gameWord = new GameWord("test");
        assertEquals("____", gameWord.getCurrentState());
        assertEquals("test", gameWord.getPhrase());
        assertTrue(gameWord.hasNotGuessedConsonants());
        assertTrue(gameWord.hasNotGuessedLetters());

        gameWord = new GameWord("aeiouy");
        assertEquals("______", gameWord.getCurrentState());
        assertEquals("aeiouy", gameWord.getPhrase());
        assertFalse(gameWord.hasNotGuessedConsonants());
        assertTrue(gameWord.hasNotGuessedLetters());
    }

    @Test
    public void testUncover() {
        GameWord gameWord = new GameWord("qwertyuiop");
        gameWord.uncoverRandomLetter();
        int uncovered = 0;
        for(int i=0; i<gameWord.getCurrentState().length(); i++) {
            if(gameWord.getCurrentState().charAt(i) == '_') {
                uncovered++;
            } else{
                assertEquals(gameWord.getCurrentState().charAt(i), gameWord.getPhrase().charAt(i));
            }
        }
        assertEquals(9, uncovered);

        gameWord = new GameWord("qwertyuiop");
        gameWord.uncoverRandomLetter(3);
        for(int i=0; i<gameWord.getCurrentState().length(); i++) {
            if(i == 3) {
                assertEquals(gameWord.getPhrase().charAt(i), gameWord.getCurrentState().charAt(i));
            } else {
                assertEquals('_', gameWord.getCurrentState().charAt(i));
            }
        }
    }
}
