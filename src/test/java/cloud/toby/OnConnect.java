
/**
 * Example implementation of OnConnectCallback.
 */


package cloud.toby;

import java.util.Arrays;
import org.json.JSONObject;


public class OnConnect implements OnConnectCallback {

    public void go(Bot bot) {
        System.out.println("Connected!");
        try {
          JSONObject payload = new JSONObject();
          payload.put("hello", "world");
          bot.send(payload, Arrays.asList("java"), "ack");
          //bot.follow(Arrays.asList("java", "java2", "java3"), "java");
          //bot.unfollow(Arrays.asList("java2", "java3"), "java");
          //bot.info("java");
          //bot.turnHooksOn("javaSecret", "java");
          //bot.turnHooksOff("java");

        } catch (NotConnectedException e) {
          System.out.println("not connected");
          e.printStackTrace();
        }
    }
}
