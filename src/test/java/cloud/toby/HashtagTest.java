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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test for Message class.
 */
public class HashtagTest  {

    /**
     * Create the test case
     */
    public HashtagTest() {}

    /**
     *
     */
     @Test
     public void testExtract() {
       String text = "hello, #this here #is #a really cool #test.";
       String delimiter = "#";
       Pattern p = Pattern.compile(String.format("(?:^|\\s|[\\p{Punct}&&[^/]])(%s[\\p{L}0-9-_]+)", delimiter));
       Matcher m = p.matcher(text);   // get a matcher object
       int count = 0;

       List<String> tags = new ArrayList<String>();

       while (m.find()) {
         tags.add(m.group().trim().substring(1));
       }
        assertEquals(Arrays.asList("this", "is", "a", "test"), tags);
     }

     @Test
     public void testRemove() {
       String text = "hello, #this here #is #a really cool #test.";
       String delimiter = "#";
       String replaced = text.replaceAll(String.format("(?:^|\\s|[\\p{Punct}&&[^/]])(%s[\\p{L}0-9-_]+)", delimiter), "");

        assertEquals("hello, here really cool.", replaced);

     }

}
