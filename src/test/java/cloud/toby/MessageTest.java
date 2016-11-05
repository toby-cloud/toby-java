
package cloud.toby;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.List;
import java.util.Arrays;

/**
 * Unit test for Message class.
 */
public class MessageTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MessageTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( MessageTest.class );
    }

    /**
     * Test Message constructor with all fields
     */
    public void testConstructorAllFields() {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        Message m = new Message("example message", "TEXT", "exampleAck", tags);
        assertEquals(m.getMessage(), "example message");
        assertEquals(m.getMessageType(), "TEXT");
        assertEquals(m.getAckTag(), "exampleAck");
        assertEquals(m.getTags(), tags);
    }

    /**
     * Test Message constructor with no ackTag
     */
    public void testConstructorNoAckTag() {
      List<String> tags = Arrays.asList("tag4", "tag5", "tag6");
      Message m = new Message("example message 2", "TEXT", tags);
      assertEquals(m.getMessage(), "example message 2");
      assertEquals(m.getMessageType(), "TEXT");
      assertEquals(m.getAckTag(), "");
      assertEquals(m.getTags(), tags);
    }

    /**
     * Test Message constructor using message string
     */
    public void testConstructorMessageString() {
      String messageString = "{\"message\":\"hey\",\"messageType\":\"TEXT\",\"tags\":[\"java\"],\"ackTag\": \"javaAck\"}";
      Message m = new Message(messageString);
      assertEquals(m.getMessage(), "hey");
      assertEquals(m.getMessageType(), "TEXT");
      assertEquals(m.getAckTag(), "javaAck");
      assertEquals(m.getTags(), Arrays.asList("java"));

      // TODO test more cases, i.e. without tags, without ackTag, etc.
    }

    public void testToString() {
      
    }


}
