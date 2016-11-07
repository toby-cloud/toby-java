
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

    private Bot testBot1;
    private Bot testBot2;
    private Message testMessage;

    /**
     * Test Case constructor - instantiates an example bot and message
     *
     */
    public BotTest() {
      OnConnectCallback onConnect = new OnConnect();
      OnDisconnectCallback onDisconnect = new OnDisconnect();
      OnMessageCallback onMessage = new OnMessage();

      this.testBot1 = new Bot("exampleId", "exampleSk", onConnect, onDisconnect, onMessage);
      try {
        this.testMessage = new Message("message", "TEXT", "ack", Arrays.asList("tag1", "tag2"));;
      } catch (MalformedMessageException e) {
        e.printStackTrace();
      }
    }

    @Test
    public void testConstructor() {
        assertNotNull(this.testMessage);
        assertNotNull(this.testBot1);
        assertFalse(this.testBot1.isConnected());
    }

    // Test Not Connected Exceptions
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedSend() throws NotConnectedException {
      this.testBot1.send(this.testMessage);
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedFollow() throws NotConnectedException {
      this.testBot1.follow(Arrays.asList("asdf"), "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedUnfollow() throws NotConnectedException {
      this.testBot1.unfollow(Arrays.asList("asdf"), "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedInfo() throws NotConnectedException {
      this.testBot1.info("ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedCreateBot() throws NotConnectedException {
      this.testBot1.createBot("username", "password", "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedCreateSocket() throws NotConnectedException {
      this.testBot1.createSocket(false, "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedRemoveBot() throws NotConnectedException {
      this.testBot1.removeBot("id", "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedRemoveSocket() throws NotConnectedException {
      this.testBot1.removeSocket("id", "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedTurnHooksOn() throws NotConnectedException {
      this.testBot1.turnHooksOn("password", "ack");
    }
    @Test(expected = NotConnectedException.class)
    public void testNotConnectedTurnHooksOff() throws NotConnectedException {
      this.testBot1.turnHooksOff("ack");
    }

    // TODO simulate interactions between bots

}
