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
import java.util.Date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Example command line application to show complete client functionality.
 *
 */
class App {

    /**
     * Called when we establish a Toby connection
     */
    private static class OnConnect implements OnConnectCallback {
      public void go(Bot bot) {
        try {
          System.out.println("Connected! " + new Date().toString());
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
        System.out.println(String.format("\b\b\b\bMessage received: %s %s", from, message));
        System.out.print(">>> ");
      }

      // TODO
      // this is an ugly, temporary fix to deal with the current server bug
      public void malformed(Bot bot, String malformed) {
        System.out.println(String.format("\b\b\b\bMalformed message received: %s", malformed));
        System.out.print(">>> ");

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
        case "standard": return Arrays.asList("(s)end", "(f)ollow", "(u)nfollow", "(c)reate socket", "(r)emove socket", "hooks (on)", "hooks (off)", "(q)uit");
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
        String message, messageNoTags, hashtagString;
        List<String> tags, ackTags;
        boolean persist;

        try {

            // NOTE: there are no checks. if a bot tries to invoke a command that
            // they don't have permission for, they will be kicked off by the server
            switch (commandSplit.get(0)) {
                case "s":
                case "send":
                  message = command.substring(commandSplit.get(0).length());
                  tags = extractTags(message, "#");
                  ackTags = extractTags(message, "&");
                  messageNoTags = removeTags(removeTags(message, "#"), "&");
                  if (messageNoTags.length() > 0)
                    if (ackTags.size() > 0)
                      bot.send(new Message(messageNoTags, "TEXT", ackTags.get(0), tags));
                    else
                      bot.send(new Message(messageNoTags, "TEXT", tags));
                  break;
                case "i":
                case "info":
                  bot.info("i");
                  break;
                case "c":
                case "create":
                  if (type.equals("user")) {
                    if (commandSplit.size() < 3) {
                      System.out.println("usage: (c)reate <newBotId> <newBotSk>");
                      break;
                    }
                    bot.createBot(commandSplit.get(1), commandSplit.get(2), "create");
                  }
                  else if (type.equals("standard")) {
                    if (commandSplit.size() < 2) {
                      System.out.println("usage: (c)reate <persist>");
                      break;
                    }
                    persist = false;
                    if (commandSplit.get(1) == "true")
                      persist = true;
                    bot.createSocket(persist, "create");
                  }
                  else
                    System.out.println("you will get kicked off");

                  break;
                case "r":
                case "remove":
                  if (type.equals("user")) {
                    if (commandSplit.size() < 2) {
                      System.out.println("usage: (r)emove <botId>");
                      break;
                    }
                    bot.removeBot(commandSplit.get(1), "remove");
                  }
                  else if (type.equals("standard")) {
                    if (commandSplit.size() < 2) {
                      System.out.println("usage: (r)emove <socketId>");
                      break;
                    }
                    bot.removeSocket(commandSplit.get(1), "remove");
                  }
                  else
                    System.out.println("you will get kicked off");
                  break;
                case "f":
                case "follow":
                  if (commandSplit.size() < 2) {
                    System.out.println("usage: (f)ollow <hashtagString>");
                  }
                  hashtagString = command.substring(commandSplit.get(0).length());
                  bot.follow(extractTags(hashtagString, "#"), "follow");
                  break;
                case "u":
                case "unfollow":
                  if (commandSplit.size() < 2) {
                    System.out.println("usage: (u)nfollow <hashtagString>");
                  }
                  hashtagString = command.substring(commandSplit.get(0).length());
                  bot.unfollow(extractTags(hashtagString, "#"), "unfollow");
                  break;
                case "off":
                  bot.turnHooksOff("hook");
                  break;
                case "on":
                  if (commandSplit.size() < 2) {
                    System.out.println("usage: on <hookPassword>");
                  }
                  bot.turnHooksOn(commandSplit.get(1), "hook");
                  break;
                case "cl":
                case "clear":
                  clear(); break;
                case "h":
                case "help":
                  System.out.println("MENU:");
                  System.out.println(commands.toString());
                  break;
                case "q":
                case "quit":
                  bot.end();
                  System.exit(1);
                case "":
                  break;
                default:
                  System.out.println("-toby: " + commandSplit.get(0) + ": command not found");
            }

        } catch (Exception e) {
          System.out.println("error");
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

    /**
     * clear - clear console and print Toby ASCII
     */
    private static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(" ▄▀▀▀█▀▀▄  ▄▀▀▀▀▄   ▄▀▀█▄▄   ▄▀▀▄ ▀▀▄\n█    █  ▐ █      █ ▐ ▄▀   █ █   ▀▄ ▄▀\n▐   █     █      █   █▄▄▄▀  ▐     █\n   █      ▀▄    ▄▀   █   █        █\n ▄▀         ▀▀▀▀    ▄▀▄▄▄▀      ▄▀\n█                  █    ▐       █\n▐                  ▐            ▐\n");
    }

    /**
     * extractTags - extract the tags from a string
     *
     * @param  String text      the text to extract tags from
     * @param  String delimiter the tag delimiter
     * @return {List<String}    a list of extracted tags
     */
    private static List<String> extractTags(String text, String delimiter) {
       Pattern p = Pattern.compile(String.format("(?:^|\\s|[\\p{Punct}&&[^/]])(%s[\\p{L}0-9-_]+)", delimiter));
       Matcher m = p.matcher(text);   // get a matcher object
       List<String> tags = new ArrayList<String>();
       while (m.find()) {
         tags.add(m.group().trim().substring(1));
       }
       return tags;
    }

    /**
     * private - description
     *
     * @param  {String} text      the text to remove tags from
     * @param  {String} delimiter the tag delimiter
     * @return {String}           the text without tags
     */
    private static String removeTags(String text, String delimiter) {
      return text.replaceAll(String.format("(?:^|\\s|[\\p{Punct}&&[^/]])(%s[\\p{L}0-9-_]+)", delimiter), "").trim();
    }

    /**
     * Main
     */
    public static void main( String[] args ) {

      // prompt for credentials
      String username = prompt("username: ");
      String password = prompt("password: ");

      clear(); // clear console

      Bot bot = new Bot(username, password, new OnConnect(), new OnDisconnect(), new OnMessage());
      bot.start();

      while(true) {}
    }
}
