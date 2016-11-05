package cloud.toby;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Arrays;


/**
 * Hello world!
 *
 */
public class App
{

    public static void main( String[] args )
    {

        OnConnectCallback onConnect = new OnConnect();
        OnDisconnectCallback onDisconnect = new OnDisconnect();
        OnMessageCallback onMessage = new OnMessage();

        Bot bot = new Bot("java", "java", onConnect, onDisconnect, onMessage);
        bot.start();
    }
}
