
/**
 * Example implementation of OnMessageCallback.
 */

package cloud.toby;

public class OnMessage implements OnMessageCallback {
    public void go(String from, Message message) {
        System.out.print("Message received: ");
        System.out.println(from);
        System.out.println(message);
    }
}
