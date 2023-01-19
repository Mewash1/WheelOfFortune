import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import pap.z26.wheeloffortune.Database;
import pap.z26.wheeloffortune.LeaderboardRecord;
import pap.z26.wheeloffortune.Phrase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DatabaseTest {
    @Test
    public void testMatchingPhrases(){
        Database db = Database.getInstance();
        ArrayList<String> phrases = db.getMatchingPhrases("P_________z");
        assertEquals("Pan Tadeusz", phrases.get(0));
        phrases = db.getMatchingPhrases("___________");
        assertEquals("Pan Tadeusz", phrases.get(0));
        assertEquals("Dom z liści", phrases.get(1));
        phrases = db.getMatchingPhrases("_");
        assertEquals(0, phrases.size());
    }

    @Test
    public void testGetAllCategories(){
        Database db = Database.getInstance();
        ArrayList<String> categories = db.getAllCategories();
        ArrayList<String> allCategories = new ArrayList<>(Arrays.asList("Przysłowia", "Książki", "Filmy", "Gry", "Stolice"));
        assertEquals(categories, allCategories);
    }

    @Test
    public void testGetCategoriesID(){
        Database db = Database.getInstance();
        HashMap<String, Integer> categoriesIDs = db.getCategoriesID();
        assertEquals(1, categoriesIDs.get("Przysłowia"));
        assertEquals(2, categoriesIDs.get("Książki"));
        assertEquals(3, categoriesIDs.get("Filmy"));
        assertEquals(4, categoriesIDs.get("Gry"));
        assertEquals(5, categoriesIDs.get("Stolice"));
    }
    @Test
    public void testGetAllPhrasesFromCategory(){
        Database db = Database.getInstance();
        ArrayList<Phrase> phrases = db.getAllPhrasesFromCategory(null);
        assertEquals("Geometry Dash", phrases.get(33).phrase());
        assertEquals("Praga", phrases.get(44).phrase());
        assertEquals("John Wick", phrases.get(19).phrase());
    }
}
