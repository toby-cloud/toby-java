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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Example command line application to show complete Toby API functionality.
 *
 */
public class App {

    /**
     * Called when we establish a Toby connection
     */
    private static class OnConnect implements OnConnectCallback {
      public void go(Bot bot) {
        try {
          System.out.println("Connected!");
          bot.info("info"); // get info so we know what type of bot
        } catch (NotConnectedException e) {
          System.out.println("not connected");
        }
      }
    }

    /**
     * Called when we disconnect from Toby.
     */
    private static class OnDisconnect implements OnDisconnectCallback {
        public void go() {
            System.out.println("Disconnected from MQTT broker");
            System.exit(1);
        }
    }

    /**
     * Called when we receive a message from Toby.
     */
    private static class OnMessage implements OnMessageCallback {
      public void go(Bot bot, String from, Message message) {
        System.out.print("Message received: ");
        System.out.println(from);
        System.out.println(message);

      }

      // TODO
      // this is an ugly, temporary fix to deal with the current server bug
      public void malformed(Bot bot, String malformed) {
        JSONObject message = new JSONObject(malformed);
        JSONArray arr = new JSONArray(message.get("tags").toString());
        message = new JSONObject(message.get("message").toString());
        List<String> list = new ArrayList<String>();
        for(int i = 0; i < arr.length(); i++){
          list.add(arr.get(i).toString());
        }
        switch (list.get(0)) {
          case "info": handleInfoResponse(bot, message);break;
        }
      }
    }


    /**
     * handleInfoResponse - called when we receive information response from server
     *
     * @param  Bot bot            the connected bot
     * @param  JSONObject payload the info response payload
     */
    private static void handleInfoResponse(Bot bot, JSONObject payload) {
      // start main menu for the given bot type
      mainMenu(bot, payload.get("type").toString());
    }


    /**
     * getBotCommands - return the commands for a bot of given type
     *
     * @param  String type the type of bot (user, standard, or socket)
     * @return List<String> the commands for the given type of bot
     */
    private static List<String> getBotCommands(String type) {
      switch (type) {
        case "user": return Arrays.asList("(s)end", "(c)reate bot", "(r)emove bot", "(q)uit");
        case "standard": return Arrays.asList("(s)end", "(f)ollow", "(u)nfollow", "(c)reate socket", "(r)emove socket", "(h)ooks", "(n)o hooks", "(q)uit");
        case "socket": return Arrays.asList("(s)end", "(q)uit");
      }
      return null;
    }



    /**
     * mainMenu - the main menu loop
     *
     * @param  Bot bot     the connected bot
     * @param  String type the bot's type
     */
    private static void mainMenu(Bot bot, String type) {

      List<String> commands = getBotCommands(type);

      System.out.println("MENU:");
      System.out.println(commands.toString());

      while(true) {
        String command = prompt(">>> ");

        List<String> commandSplit = new ArrayList<String>();
        Collections.addAll(commandSplit, command.split(" "));

        // NOTE: there are no checks. if a bot tries to invoke a command that
        // they don't have permission for, they will be kicked off by the server
        switch (commandSplit.get(0)) {
          case "s":
          case "send":
            System.out.println("bot wants to send."); break;
          case "c":
          case "create":
            System.out.println("bot wants to create."); break;
          case "r":
          case "remove":
            System.out.println("bot wants to remove."); break;
          case "f":
          case "follow":
            System.out.println("bot wants to follow."); break;
          case "u":
          case "unfollow":
            System.out.println("bot wants to unfollow."); break;
          case "n":
          case "no":
            System.out.println("bot wants to turn hooks off"); break;
          case "h":
          case "hooks":
            System.out.println("bot wants to turn hooks on"); break;
          case "cl":
          case "clear":
            clear(); break;
          case "q":
          case "quit":
            bot.end();
            System.exit(1);
          case "":
            break;
          default:
            System.out.println("-toby: " + commandSplit.get(0) + ": command not found");
        }

      }
    }


    /**
     * prompt - prompt console input
     *
     * @param  String message the prompt message
     * @return String the user's input
     */
    private static String prompt(String message) {
      System.out.print(message);
      return System.console().readLine();
    }

    private static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main( String[] args ) {


      // prompt for credentials
      String username = prompt("username: ");
      String password = prompt("password: ");

      clear();
      System.out.println(" ▄▀▀▀█▀▀▄  ▄▀▀▀▀▄   ▄▀▀█▄▄   ▄▀▀▄ ▀▀▄\n█    █  ▐ █      █ ▐ ▄▀   █ █   ▀▄ ▄▀\n▐   █     █      █   █▄▄▄▀  ▐     █\n   █      ▀▄    ▄▀   █   █        █\n ▄▀         ▀▀▀▀    ▄▀▄▄▄▀      ▄▀\n█                  █    ▐       █\n▐                  ▐            ▐\n");

      Bot bot = new Bot(username, password, new OnConnect(), new OnDisconnect(), new OnMessage());
      bot.start();

      while(true) {}
    }
}
