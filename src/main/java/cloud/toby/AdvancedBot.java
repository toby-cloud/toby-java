package cloud.toby;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;


class AdvancedBot extends Bot {

    AdvancedBot thisBot = this;

    // maps awaiting ack tags to their on message callbacks
    private Map<String, OnMessageCallback> ackMap = new HashMap<>();

    // used for send and wait
    private Map<String, Message> requestMap = new HashMap<>();

    private class OnConnect implements  OnConnectCallback {
        public void go(Bot b) {
            System.out.println("Connected");
        }
    }
    private class OnDisconnect implements  OnDisconnectCallback {
        public void go() {
            System.out.println("Disconnected");
        }
    }

    private class OnMessage implements OnMessageCallback {
        public void go(Bot b, Message m) {
            String firstTag = m.getTags().get(0);
            if (ackMap.containsKey(firstTag)) {
                //System.out.println("Response time: " + getDateDiff(new Date(Long.parseLong(firstTag)), new Date()));
                ackMap.get(firstTag).go(b, m);
                try {
                    thisBot.unfollow(Arrays.asList(firstTag), "unfollowed");
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                }
            }
            if (requestMap.containsKey(firstTag)) {
                //System.out.println("Response time: " + getDateDiff(new Date(Long.parseLong(firstTag)), new Date()));
                requestMap.put(firstTag, m);
            }
        }
    }

    /**
     * Constructor
     * @param id
     * @param sk
     */
    public AdvancedBot(String id, String sk) {
        super(id, sk, null, null, null);
        super.setOnMessageCallback(new OnMessage());
        super.setOnConnectCallback(new OnConnect());
        super.setOnDisconnectCallback(new OnDisconnect());
    }

    /**
     * Send a message with a response callback. The callback will be executed when an acknowledgement response is received.
     * @param payload
     * @param tags
     * @param onMessage
     */
    public void send(JSONObject payload, List<String> tags, OnMessageCallback onMessage) {
        String ack = "" + new Date().getTime();
        this.ackMap.put(ack, onMessage);
        try {
            super.follow(Arrays.asList(ack), "followed");
            super.send(payload, tags, ack);
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message, and return it's first acknowledgment response.
     * @param payload
     * @param tags
     * @param timeout
     * @return
     */
    public Message send(JSONObject payload, List<String> tags, long timeout){
        Date timeoutDate = new Date(new Date().getTime() + timeout);
        String ack = "" + new Date().getTime();
        this.requestMap.put(ack, null);
        try {
            super.follow(Arrays.asList(ack), "followed");
            super.send(payload, tags, ack);
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
        while (getDateDiff(new Date(), timeoutDate) > 0) {
            if (requestMap.get(ack) != null) {
                return requestMap.get(ack);
            }
        }
        return null;
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    private static long getDateDiff(Date date1, Date date2) {
        return date2.getTime() - date1.getTime();
    }

    /**
     * Main
     */
    public static void main( String[] args ) {
        AdvancedBot bot = new AdvancedBot("gbot", "gbot");
        bot.start();

        JSONObject payload = new JSONObject();

        try {
            payload.put("message", "ping");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Message response = bot.send(payload, Arrays.asList("android", "one"), 1500);
        System.out.println("Response: " + response);

        while (bot.isConnected()) {}
    }
}