
/**
 * Example implementation of OnMessageCallback.
 */

package cloud.toby;

import java.util.Arrays;

public class OnMessage implements OnMessageCallback {
    public void go(Bot bot, Message message) {
        System.out.print("Message received: ");
        System.out.println(message);
    }
}
