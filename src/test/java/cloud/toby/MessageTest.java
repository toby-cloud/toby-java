
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
        try {
          Message m = new Message("example message", "TEXT", "exampleAck", tags);
          assertEquals(m.getMessage(), "example message");
          assertEquals(m.getMessageType(), "TEXT");
          assertEquals(m.getAckTag(), "exampleAck");
          assertEquals(m.getTags(), tags);
        } catch (MalformedMessageException e) {
          assertNull(e);
        }
    }

    /**
     * Test Message constructor with no ackTag
     */
     @Test
    public void testConstructorNoAckTag() {
      List<String> tags = Arrays.asList("tag4", "tag5", "tag6");
      try {
        Message m = new Message("example message 2", "TEXT", tags);
        assertEquals(m.getMessage(), "example message 2");
        assertEquals(m.getMessageType(), "TEXT");
        assertNull(m.getAckTag());
        assertEquals(m.getTags(), tags);
      } catch (MalformedMessageException e) {
        assertNull(e);
      }
    }

    /**
     * Test Message constructor using message string
     */
     @Test
    public void testConstructorMessageStringAllFields() {
      String messageString = "{\"message\":\"hey\",\"messageType\":\"TEXT\",\"tags\":[\"java\"],\"ackTag\": \"javaAck\"}";
      try {
        Message m = new Message(messageString);
        assertEquals(m.getMessage(), "hey");
        assertEquals(m.getMessageType(), "TEXT");
        assertEquals(m.getAckTag(), "javaAck");
        assertEquals(m.getTags(), Arrays.asList("java"));
      } catch (MalformedMessageException e) {
        assertNull(e);
      }

    }
    @Test
    public void testConstructorMessageStringNoAckTag() {
      String messageString = "{\"message\":\"hello\",\"messageType\":\"TEXT\",\"tags\":[\"tag\",\"tag2\"]}";
      try {
        Message m = new Message(messageString);
        assertEquals("hello", m.getMessage());
        assertEquals("TEXT", m.getMessageType());
        assertNull(m.getAckTag());
        assertEquals(m.getTags(), Arrays.asList("tag", "tag2"));
      } catch (MalformedMessageException e) {
        assertNull(e);
      }

    }
    @Test
    public void testConstructorMessageStringEmptyTags() {
      String messageString = "{\"message\":\"hello\",\"messageType\":\"TEXT\",\"tags\":[]}";
      try {
        Message m = new Message(messageString);
        assertEquals("hello", m.getMessage());
        assertEquals("TEXT", m.getMessageType());
        assertNull(m.getAckTag());
        assertEquals(m.getTags(), new ArrayList<String>());
      } catch (MalformedMessageException e) {
        assertNull(e);
      }
    }

    @Test
    public void testToStringAllFields() {
      String expectedString = "{\"message\":\"hey\",\"messageType\":\"TEXT\",\"ackTag\":\"javaAck\",\"tags\":[\"java\"]}";
      try {
        Message m = new Message("hey", "TEXT", "javaAck", Arrays.asList("java"));
        Message m2 = new Message(expectedString);
        assertEquals(expectedString, m.toString());
        assertEquals(expectedString, m2.toString());
      } catch (MalformedMessageException e) {
        assertNull(e);
      }
    }

    @Test
    public void testToStringNoAckTag() {
      String expectedString = "{\"message\":\"hey\",\"messageType\":\"TEXT\",\"tags\":[\"java\"]}";
      try {
        Message m = new Message("hey", "TEXT", Arrays.asList("java"));
        Message m2 = new Message(expectedString);
        assertEquals(expectedString, m.toString());
        assertEquals(expectedString, m2.toString());
      } catch (MalformedMessageException e) {
        assertNull(e);
      }
    }

    @Test
    public void testToStringEmptyTags() {
      String expectedString = "{\"message\":\"hey\",\"messageType\":\"TEXT\",\"tags\":[]}";
      try {
        Message m = new Message("hey", "TEXT", new ArrayList<String>());
        Message m2 = new Message(expectedString);
        assertEquals(expectedString, m.toString());
        assertEquals(expectedString, m2.toString());
      } catch (MalformedMessageException e) {
        assertNull(e);
      }
    }

    @Test
    public void testToStringNullTags() {
      String expectedString = "{\"message\":\"hey\",\"messageType\":\"TEXT\",\"tags\":[]}";
      try {
        Message m = new Message("hey", "TEXT", null);
        Message m2 = new Message(expectedString);
        assertEquals(expectedString, m.toString());
        assertEquals(expectedString, m2.toString());
      } catch (MalformedMessageException e) {
        assertNull(e);
      }
    }

    // TODO test MalformedMessageException

}
