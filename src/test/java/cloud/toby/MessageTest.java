
package cloud.toby;


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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;

/**
 * Unit test for Message class.
 */
public class MessageTest  {

    /**
     * Create the test case
     */
    public MessageTest() {}

    /**
     * Test Message constructor with all fields
     */
     @Test
    public void testConstructorAllFields() {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        JSONObject payload = new JSONObject();
        payload.put("hello", "world");
        try {
          Message m = new Message("sender", payload, tags, "exampleAck");
          assertEquals(m.getFrom(), "sender");
          assertEquals(m.getPayload(), payload);
          assertEquals(m.getAck(), "exampleAck");
          assertEquals(m.getTags(), tags);
        } catch (MalformedMessageException e) {
          assertNull(e);
        }
    }

    @Test(expected = MalformedMessageException.class)
    public void testConstructorAllFieldsMalformed() throws MalformedMessageException {
        Message m = new Message(null, null, null, null);
    }

    /**
     * Test Message constructor using message string
     */
     @Test
    public void testConstructorMessageStringAllFields() {
      String messageString = "{\"from\":\"server\",\"payload\":{\"hello\":\"world\", \"hi\":{\"there\":\"guy\"}},\"tags\":[\"java\"],\"ack\": \"javaAck\"}";
      JSONObject payload = new JSONObject();
      payload.put("hello", "world");
      try {
        Message m = new Message(messageString);
        assertEquals(m.getFrom(), "server");
        assertEquals(m.getPayload().get("hello"), "world");
        assertEquals(m.getPayload().getJSONObject("hi").get("there"), "guy");
        assertEquals(m.getTags(), Arrays.asList("java"));
        assertEquals(m.getAck(), "javaAck");
      } catch (MalformedMessageException e) {
        assertNull(e);
      }

    }

    @Test(expected = MalformedMessageException.class)
    public void testConstructorMessageStringMalformedMessage() throws MalformedMessageException {
        Message m = new Message("{ }");
    }
    @Test(expected = MalformedMessageException.class)
    public void testConstructorMessageStringInvalidJSON() throws MalformedMessageException {
        Message m = new Message("}{");
    }

    @Test
    public void testToStringAllFields() {

    }

    // TODO
}
