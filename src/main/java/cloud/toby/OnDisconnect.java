
/**
 * Example implementation of OnDisconnectCallback.
 */


package cloud.toby;


public class OnDisconnect implements OnDisconnectCallback {
    public void go() {
        System.out.println("Disconnected from MQTT broker");
        System.exit(1);
    }
}
