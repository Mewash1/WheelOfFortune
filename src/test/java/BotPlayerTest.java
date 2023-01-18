import org.junit.jupiter.api.Test;
import pap.z26.wheeloffortune.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BotPlayerTest {

    @Test
    public void testInitialization() {
        Player botPlayer = new BotPlayer();
        assertNotNull(botPlayer.getName());
        assertTrue(botPlayer.isBot());
    }

    @Test
    public void testNotifyLetter(){
        BotPlayer bot = new BotPlayer();
        ArrayList<Character> vowBef = new ArrayList<>(bot.getLetterList());
        bot.notifyLetter('b');
        assertFalse(bot.getLetterList().contains('b'));
        assertEquals(bot.getLetterList().size() + 1, vowBef.size());
    }

    @Test
    public void testGetLetterWeights(){
        BotPlayer bot = new BotPlayer();
        int sumBef = 0;
        for (Integer a: bot.getLetterWeights()){
            sumBef += a;
        }
        bot.notifyLetter('a');
        int sumAft = 0;
        for (Integer a: bot.getLetterWeights()){
            sumAft += a;
        }
        assertEquals(sumAft, sumBef - 9);
    }

    @Test
    public void testCountEmptyLetters(){
        BotPlayer bot = new BotPlayer();
        assertEquals(bot.countEmptyLetters("a_ nano_ haha"), 2);
        assertEquals(bot.countEmptyLetters("as nano haha"), 0);
        assertEquals(bot.countEmptyLetters("__ _____ _____"), 12);
    }


    @Test
    public void testGetLetterList(){
        BotPlayer bot = new BotPlayer();

        for (int i = 0; i< GameWord.consonants.size(); i++){
            if (GameWord.consonants.get(i) != 'b') {
                bot.notifyLetter(GameWord.consonants.get(i));
            }
        }

        for (int i=0; i< GameWord.vowels.size(); i++){
            bot.notifyLetter(GameWord.vowels.get(i));
        }

        assertTrue(bot.getLetterList().contains('b'));
        assertEquals(1, bot.getLetterList().size());
    }

}