
import org.junit.jupiter.api.Test;
import pap.z26.wheeloffortune.*;

import static org.junit.jupiter.api.Assertions.*;

public class HumanPlayerTest {

    @Test
    public void testInitialization() {
        Player humanPlayer = new HumanPlayer("Marcin");
        assertEquals(humanPlayer.getName(), "Marcin");
        assertFalse(humanPlayer.isBot());
    }

}
