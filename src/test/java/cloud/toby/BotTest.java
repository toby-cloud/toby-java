
package cloud.toby;


import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for Bot class.
 */
public class BotTest {

    private Bot testBot;
    private Message testMessage;

    /**
     * Test Case constructor - instantiates an example bot and message
     *
     */
    public BotTest() {
      OnConnectCallback onConnect = new OnConnect();
      OnDisconnectCallback onDisconnect = new OnDisconnect();
      OnMessageCallback onMessage = new OnMessage();

      this.testBot = new Bot("exampleId", "exampleSk", onConnect, onDisconnect, onMessage);
      try {
        this.testMessage = new Message("message", "TEXT", "ack", Arrays.asList("tag1", "tag2"));;
      } catch (InvalidMessageException e) {
        e.printStackTrace();
      }
    }


    @Test
    public void testConstructor() {
        assertNotNull(this.testMessage);
        assertNotNull(this.testBot);
        assertFalse(this.testBot.isConnected());
    }

    // Test Not Connected Exceptions
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedSend() throws NotConnectedException {
      this.testBot.send(this.testMessage);
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedFollow() throws NotConnectedException {
      this.testBot.follow(Arrays.asList(), "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedUnfollow() throws NotConnectedException {
      this.testBot.unfollow(Arrays.asList(), "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedInfo() throws NotConnectedException {
      this.testBot.info("ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedCreateBot() throws NotConnectedException {
      this.testBot.createBot("username", "password", "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedCreateSocket() throws NotConnectedException {
      this.testBot.createSocket(false, "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedRemoveBot() throws NotConnectedException {
      this.testBot.removeBot("id", "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedRemoveSocket() throws NotConnectedException {
      this.testBot.removeSocket("id", "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedTurnHooksOn() throws NotConnectedException {
      this.testBot.turnHooksOn("password", "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedTurnHooksOff() throws NotConnectedException {
      this.testBot.turnHooksOff("ack");
    }


}
