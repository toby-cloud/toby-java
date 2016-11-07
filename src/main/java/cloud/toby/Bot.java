
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

    public boolean isConnected() {
      return this.connected;
    }

    /**
     * Start the bot.
     */
    public void start() {

      MQTT mqtt = new MQTT();
      final Bot bot = this;

      try {
          mqtt.setHost("toby.cloud", 444);
      } catch (java.net.URISyntaxException e) {
          e.printStackTrace();
      }
      mqtt.setClientId(this.botId);
      mqtt.setUserName(this.botId);
      mqtt.setPassword(this.botSk);

      // TODO implement connection failed timeout (currently, incorrect credentials will just hang)

      final CallbackConnection connection = mqtt.callbackConnection();
      connection.listener(new Listener() {
          @Override
          public void onConnected() {} // this does nothing

          @Override
          public void onDisconnected() {
            System.out.println("disconnected");
            bot.connected = false;
            connection.disconnect(null);
            System.exit(1); // if we don't exit, it will continue to reconnect
          } // this does nothing

          @Override
          public void onPublish(UTF8Buffer t, Buffer b, Runnable ack) {
              // Called when we receive an MQTT message
              final UTF8Buffer topic = t;
              final Buffer body = b;

              // Start in new thread to avoid connection issues
              Thread thread = new Thread() {
                  public void run() {
                      String[] topicSplit = new String(topic.toByteArray()).split("/");
                      String from = topicSplit[2];
                      try {
                        Message message = new Message(new String(body.toByteArray()));
                        onMessage.go(bot, from, message);
                      } catch (MalformedMessageException e) {
                        onMessage.malformed(bot, new String(body.toByteArray()));
                      }
                  }
              };
              thread.start();
              ack.run();
          }

          @Override
          public void onFailure(Throwable value) {
              System.out.println("MQTT failure");
              connected = false;
              connection.disconnect(null);
              System.exit(1);
          }
      });


      // Attempt connection to MQTT broker
      // If successful, subscribe to bot data
      connection.connect(new Callback<Void>() {
          @Override
          public void onSuccess(Void value) {
              // Subscribe to bot messages
              Topic[] topics = {new Topic("client/" + botId + "/#", QoS.AT_MOST_ONCE)};
              connection.subscribe(topics, new Callback<byte[]>() {
                  public void onSuccess(byte[] qoses) {
                      connected = true;
                      onConnect.go(bot);
                  }
                  public void onFailure(Throwable value) {
                    bot.end(); // subscribe failed
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

    }

    /**
     * Disconnect from MQTT broker
     */
    public void end() {
      connection.disconnect(null);
      connected = false;
    }

    /**
     * send - send a toby message
     *
     * @param  {Message} message the message to be sent to the server
     */
    public void send(Message message) throws NotConnectedException {
      final Bot bot = this;

      if (!this.isConnected()) {
        throw new NotConnectedException("Bot#send requires MQTT connection");
      }

      this.connection.publish("server/" + botId + "/send", message.toString().getBytes(), QoS.AT_MOST_ONCE, false, new Callback<Void>() {
          public void onSuccess(Void v) {
            // the pubish operation completed successfully
          }
          public void onFailure(Throwable value) {
            bot.end();
          }
      });
    }

    /**
     * follow - subscribe to tags
     *
     * @param  {List<String>} tags the list of tags to follow
     * @param  {type} String AckTag the tag to respond to
     */
    public void follow(List<String> tags, String ackTag) throws NotConnectedException {
      final Bot bot = this;

      if (!this.isConnected()) {
        throw new NotConnectedException("Bot#follow requires MQTT connection");
      }

      // Build request object
      JSONObject req = new JSONObject();
      JSONArray t = new JSONArray(tags);
        try {
            req.put("ackTag", ackTag);
            req.put("tags", t);
        } catch (JSONException e) {
            e.printStackTrace();
        }


      this.connection.publish("server/" + botId + "/follow", req.toString().getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
        public void onSuccess(Void v) {
          // the pubish operation completed successfully
        }
        public void onFailure(Throwable value) {
          bot.end();
        }
      });
    }

    /**
     * unfollow - unsubscribe from tags
     *
     * @param  {List<String>} tags the list of tags to unfollow
     * @param  {String} AckTag the tag to respond to
     */
    public void unfollow(List<String> tags, String ackTag) throws NotConnectedException {
      final Bot bot = this;

      if (!this.isConnected()) {
        throw new NotConnectedException("Bot#unfollow requires MQTT connection");
      }

      // Build request object
      JSONObject req = new JSONObject();
      JSONArray t = new JSONArray(tags);
        try {
            req.put("ackTag", ackTag);
            req.put("tags", t);
        } catch (JSONException e) {
            e.printStackTrace();
        }


      this.connection.publish("server/" + botId + "/unfollow", req.toString().getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
        public void onSuccess(Void v) {
          // the pubish operation completed successfully
        }
        public void onFailure(Throwable value) {
          bot.end();
        }
      });
    }

    /**
     * info - get bot information
     *
     * @param  {type} String AckTag the tag to respond to
     */
    public void info(String ackTag) throws NotConnectedException {
      final Bot bot = this;

      if (!this.isConnected()) {
        throw new NotConnectedException("Bot#info requires MQTT connection");
      }

      // Build request object
      JSONObject req = new JSONObject();
        try {
            req.put("ackTag", ackTag);
        } catch (JSONException e) {
            e.printStackTrace();
        }

      this.connection.publish("server/" + botId + "/info", req.toString().getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
        public void onSuccess(Void v) {
          // the pubish operation completed successfully
        }
        public void onFailure(Throwable value) {
          bot.end();
        }
      });
    }

    /**
     * createBot - create a new bot (users only)
     *
     * @param  {String} username description
     * @param  {String} password description
     * @param  {String} ackTag   description
     */
    public void createBot(String username, String password, String ackTag) throws NotConnectedException {
      final Bot bot = this;

      if (!this.isConnected()) {
        throw new NotConnectedException("Bot#createBot requires MQTT connection");
      }
      // Build request object
      JSONObject req = new JSONObject();
        try {
            req.put("id", username);
            req.put("secret", password);
            req.put("ackTag", ackTag);
        } catch (JSONException e) {
            e.printStackTrace();
        }


      this.connection.publish("server/" + botId + "/create-bot", req.toString().getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
        public void onSuccess(Void v) {
          // the pubish operation completed successfully
        }
        public void onFailure(Throwable value) {
          bot.end();
        }
      });

    }

    /**
     * createSocket - create a new socket (bots only)
     *
     * @param  {boolean} persist if false, socket will be automatically removed on first disconnect
     * @param  {String}ackTag  the tag to respond to
     */
    public void createSocket(boolean persist, String ackTag) throws NotConnectedException {
      final Bot bot = this;

      if (!this.isConnected()) {
        throw new NotConnectedException("Bot#createSocket requires MQTT connection");
      }

      // Build request object
      JSONObject req = new JSONObject();
        try {
            req.put("persist", persist);
            req.put("ackTag", ackTag);
        } catch (JSONException e) {
            e.printStackTrace();
        }


      this.connection.publish("server/" + botId + "/create-socket", req.toString().getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
        public void onSuccess(Void v) {
          // the pubish operation completed successfully
        }
        public void onFailure(Throwable value) {
          bot.end();
        }
      });
    }

    /**
     * removeBot - remove a bot (users only)
     *
     * @param  {String}targetId  the ID of the bot to delete
     * @param  {String}ackTag the tag to respond to
     */
    public void removeBot(String targetId, String ackTag) throws NotConnectedException {
      final Bot bot = this;

      if (!this.isConnected()) {
        throw new NotConnectedException("Bot#removeBot requires MQTT connection");
      }
      // Build request object
      JSONObject req = new JSONObject();
        try {
            req.put("targetId", targetId);
            req.put("ackTag", ackTag);
        } catch (JSONException e) {
            e.printStackTrace();
        }


      this.connection.publish("server/" + botId + "/remove-bot", req.toString().getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
        public void onSuccess(Void v) {
          // the pubish operation completed successfully
        }
        public void onFailure(Throwable value) {
          bot.end();
        }
      });
    }

    /**
     * removeSocket - remove a socket (bots only)
     *
     * @param  {String} socketId  the ID of the socket to delete
     * @param  {String} ackTag the tag to respond to
     */
    public void removeSocket(String targetId, String ackTag) throws NotConnectedException {
      final Bot bot = this;

      if (!this.isConnected()) {
        throw new NotConnectedException("Bot#removeSocket requires MQTT connection");
      }
      // Build request object
      JSONObject req = new JSONObject();
        try {
            req.put("targetId", targetId);
            req.put("ackTag", ackTag);
        } catch (JSONException e) {
            e.printStackTrace();
        }


      this.connection.publish("server/" + botId + "/remove-socket", req.toString().getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
        public void onSuccess(Void v) {
          // the pubish operation completed successfully
        }
        public void onFailure(Throwable value) {
          bot.end();
        }
      });
    }

    /**
     * turnHooksOn - turn bot hooks on (bots only)
     *
     * @param  {String} password the hook password
     * @param  {String} ackTag   the tag to respond to
     */
    public void turnHooksOn(String password, String ackTag) throws NotConnectedException {
      final Bot bot = this;

      if (!this.isConnected()) {
        throw new NotConnectedException("Bot#turnHooksOn requires MQTT connection");
      }

      // Build request object
      JSONObject req = new JSONObject();
        try {
            req.put("hookSecret", password);
            req.put("ackTag", ackTag);
        } catch (JSONException e) {
            e.printStackTrace();
        }


      this.connection.publish("server/" + botId + "/hooks-on", req.toString().getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
        public void onSuccess(Void v) {
          // the pubish operation completed successfully
        }
        public void onFailure(Throwable value) {
          bot.end();
        }
      });
    }

    /**
     * turnHooksOff - turn bot hooks off (bots only)
     *
     * @param  {String} ackTag   the tag to respond to
     */
    public void turnHooksOff(String ackTag) throws NotConnectedException {
      final Bot bot = this;

      if (!this.isConnected()) {
        throw new NotConnectedException("Bot#turnHooksOff requires MQTT connection");
      }

      // Build request object
      JSONObject req = new JSONObject();
        try {
            req.put("ackTag", ackTag);
        } catch (JSONException e) {
            e.printStackTrace();
        }

      this.connection.publish("server/" + botId + "/hooks-off", req.toString().getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
        public void onSuccess(Void v) {
          // the pubish operation completed successfully
        }
        public void onFailure(Throwable value) {
          bot.end();
        }
      });
    }

}
