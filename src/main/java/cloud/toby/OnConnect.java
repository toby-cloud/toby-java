
/**
 * Example implementation of OnConnectCallback.
 */


package cloud.toby;

import java.util.Arrays;


public class OnConnect implements OnConnectCallback {
    public void go(Bot bot) {
        System.out.println("Connected!");
        Message m;
        try {
          m = new Message("hey", "TEXT", Arrays.asList("java"));

          try {
            bot.send(m);
            //bot.follow(Arrays.asList("java", "java2", "java3"), "java");
            //bot.unfollow(Arrays.asList("java2", "java3"), "java");
            bot.info("java");

          } catch (NotConnectedException e) {
            System.out.println("not connected");
            e.printStackTrace();
          }
        } catch (InvalidMessageException e) {
          e.printStackTrace();
          System.exit(1);
        }
    }
}
