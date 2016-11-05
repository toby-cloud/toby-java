
package cloud.toby;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.Arrays;

/**
 * Unit test for Bot class.
 */
public class BotTest extends TestCase {

    private Bot testBot;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BotTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( BotTest.class );
    }


    public void testConstructor() {
        OnConnectCallback onConnect = new OnConnect();
        OnDisconnectCallback onDisconnect = new OnDisconnect();
        OnMessageCallback onMessage = new OnMessage();

        this.testBot = new Bot("exampleId", "exampleSk", onConnect, onDisconnect, onMessage);
        assertFalse(this.testBot.isConnected());
    }


}
