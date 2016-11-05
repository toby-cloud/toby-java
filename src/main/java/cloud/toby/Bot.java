
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
import java.lang.String;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 * Toby Bot
 *
 */
public class Bot {

    private String botId = "";
    private String botSk = "";
    private OnConnectCallback onConnect;
    private OnDisconnectCallback onDisconnect;
    private OnMessageCallback onMessage;
    private boolean connected = false;
    private CallbackConnection connection;

    /**
     * Constructor
     * @param botId
     * @param botSk
     * @param onConnect
     */
    public Bot(String botId, String botSk, OnConnectCallback onConnect, OnDisconnectCallback onDisconnect, OnMessageCallback onMessage) {
        this.botId = botId;
        this.botSk = botSk;
        this.onConnect = onConnect;
        this.onDisconnect = onDisconnect;
        this.onMessage = onMessage;
    }

    /**
     *
     * @param onConnect
     */
    public void setOnConnect(OnConnectCallback onConnect) {
        this.onConnect = onConnect;
    }

    /**
     *
     * @param onDisconnect
     */
    public void setOnDisconnect(OnDisconnectCallback onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    /**
     *
     * @param onMessage
     */
    public void setOnMessage(OnMessageCallback onMessage) {
        this.onMessage = onMessage;
    }

    public boolean isConnected() {
      return this.connected;
    }

    /**
     * Start the bot.
     */
    public void start() {

      MQTT mqtt = new MQTT();

      try {
          mqtt.setHost("toby.cloud", 444);
      } catch (java.net.URISyntaxException e) {
          e.printStackTrace();
      }
      mqtt.setClientId(this.botId);
      mqtt.setUserName(this.botId);
      mqtt.setPassword(this.botSk);

      final CallbackConnection connection = mqtt.callbackConnection();
      connection.listener(new Listener() {
          @Override
          public void onConnected() {}

          @Override
          public void onDisconnected() {}

          @Override
          public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
              // Called when we receive an MQTT message

              // Start in new thread to avoid connection issues
              Thread t = new Thread() {
                  public void run() {
                      String[] topicSplit = new String(topic.toByteArray()).split("/");
                      String from = topicSplit[2];
                      Message message = new Message(new String(body.toByteArray()));
                      onMessage.go(from, message);
                  }
              };
              t.start();
              ack.run();
          }

          @Override
          public void onFailure(Throwable value) {
              System.out.println("MQTT failure");
              connected = false;
          }
      });

      // Attempt connection to MQTT broker
      // If successful, subscribe to bot data (Toby)
      connection.connect(new Callback<Void>() {
          @Override
          public void onSuccess(Void value) {
              // Toby, subscribe to bot messages
              Topic[] topics = {new Topic("client/" + botId + "/#", QoS.AT_LEAST_ONCE)};
              connection.subscribe(topics, new Callback<byte[]>() {
                  public void onSuccess(byte[] qoses) {
                      System.out.println("Subscribed to bot messages");
                      connected = true;
                  }
                  public void onFailure(Throwable value) {
                      connection.disconnect(null); //subscribe failed
                      connected = false;
                  }
              });
          }

          @Override
          public void onFailure(Throwable value) {
              System.out.println("MQTT: Could not connect to broker.");
              onDisconnect.go();
          }
      });

      this.connection = connection;

      // TODO find better way of keeping process alive
      while (true) {}
    }

    /**
     * send - send a toby message
     *
     * @param  Message message the message to be sent to the server
     */
    public void send(Message message) {
    }

    /**
     * follow - subscribe to tags
     *
     * @param  List<String> tags the list of tags to follow
     * @param  {type} String AckTag the tag to respond to
     */
    public void follow(List<String> tags, String ackTag) {
    }

    /**
     * unfollow - unsubscribe from tags
     *
     * @param  List<String> tags the list of tags to unfollow
     * @param  {type} String AckTag the tag to respond to
     */
    public void unfollow(List<String> tags, String ackTag) {
    }

    /**
     * info - get bot information
     *
     * @param  {type} String AckTag the tag to respond to
     */
    public void info(String AckTag) {
    }

    /**
     * createBot - create a new bot (users only)
     *
     * @param  String username description
     * @param  String password description
     * @param  String ackTag   description
     */
    public void createBot(String username, String password, String ackTag) {
    }

    /**
     * createSocket - create a new socket (bots only)
     *
     * @param  boolean persist if false, socket will be automatically removed on first disconnect
     * @param  String ackTag  the tag to respond to
     */
    public void createSocket(boolean persist, String ackTag) {
    }

    /**
     * removeBot - remove a bot (users only)
     *
     * @param  String botId  the ID of the bot to delete
     * @param  String ackTag the tag to respond to
     */
    public void removeBot(String botId, String ackTag) {
    }

    /**
     * removeSocket - remove a socket (bots only)
     *
     * @param  String socketId  the ID of the socket to delete
     * @param  String ackTag the tag to respond to
     */
    public void removeSocket(String socketId, String ackTag) {
    }

    /**
     * turnHooksOn - turn bot hooks on (bots only)
     *
     * @param  String password the hook password
     * @param  String ackTag   the tag to respond to
     */
    public void turnHooksOn(String password, String ackTag) {
    }

    /**
     * turnHooksOff - turn bot hooks off (bots only)
     *
     * @param  String ackTag   the tag to respond to
     */
    public void turnHooksOff(String ackTag) {
    }
}
