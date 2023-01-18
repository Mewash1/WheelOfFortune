
import org.junit.jupiter.api.Test;
import pap.z26.wheeloffortune.*;

import static org.junit.jupiter.api.Assertions.*;

public class BotPlayerTest {

    @Test
    public void testInitialization() {
        Player botPlayer = new BotPlayer();
        assertNotNull(botPlayer.getName());
        assertTrue(botPlayer.isBot());
    }
}
