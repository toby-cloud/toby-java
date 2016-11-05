
/**
 * Example implementation of OnMessageCallback.
 */

package cloud.toby;

import java.util.Arrays;

public class OnMessage implements OnMessageCallback {
    public void go(Bot bot, String from, Message message) {
        System.out.print("Message received: ");
        System.out.println(from);
        System.out.println(message);

        try {

          if (message.getAckTag() != null)
            bot.send(new Message("received", "TEXT", "java", Arrays.asList(message.getAckTag())));

        } catch (Exception e) {
          e.printStackTrace();
        }
    }
}
