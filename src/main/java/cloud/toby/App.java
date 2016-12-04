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
import java.util.concurrent.TimeUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Example command line application to show complete client functionality.
 *
 */
class App {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static List<String> hidden = new ArrayList<>();
    private static boolean isConnected;
    private static Date connectTime;


    /**
     * Called when we establish a Toby connection
     */
    private static class OnConnect implements OnConnectCallback {
        public void go(Bot bot) {
            isConnected = true;
            connectTime = new Date();
            System.out.println("Connected! " + connectTime.toString());
            try {
                bot.info("initial_info"); // get info so we know what type of bot
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
            System.out.println("Disconnected from Toby");
            System.exit(1);
        }
    }

    /**
     * Called when we receive a message from Toby.
     */
    private static class OnMessage implements OnMessageCallback {
        public void go(Bot bot, Message message) {

            if (hidden.contains(message.getFrom())) return;

            if (message.getTags().size() > 0 && message.getTags().get(0).equals("initial_info")) {
                handleInfoResponse(bot, message.getPayload());
                String type = message.getPayload().getString("type");
                mainMenu(bot, type);
            } else if (message.getTags().size() > 0 && message.getTags().get(0).equals("info")) {
                handleInfoResponse(bot, message.getPayload());
            } else if (message.getPayload().has("status")) {
                System.out.print("\b\b\b\b");
                if (message.getPayload().getInt("status") == 200) {
                    System.out.println("success");
                } else {
                    System.out.println("failed");
                }
                System.out.print(">>> ");
            } else {
                JSONObject payload = message.getPayload();
                String m = payload.toString();
                if (payload.has("message")) {
                    m = (String) payload.get("message"); // cast instead of toString to handle non string case
                }
                printToConsole(String.format(ANSI_CYAN + "@%s: " + ANSI_RESET + "%s", message.getFrom(), m));

            }
        }
    }

    /**
     * Print to console
     * @param text the text to print
     */
    private static void printToConsole(String text) {
        printToConsole(text, null);
    }

    /**
     * Print to console with color
     * @param text the text to print
     * @param color the color to print the text in
     */
    private static void printToConsole(String text, String color) {
        String s = String.format("\b\b\b\b%s\n", text);
        if (color != null)
            s = color + s + ANSI_RESET;
        System.out.print(s);
        System.out.print(">>> ");
    }

    /**
     * handleInfoResponse - called when we receive information response from server
     *
     * @param  Bot bot            the connected bot
     * @param  JSONObject payload the info response payload
     */
    private static void handleInfoResponse(Bot bot, JSONObject payload) {
      // start main menu for the given bot type
        //JSONArray bots = payload.getJSONArray("bots");
        String type = payload.getString("type");
        if (type.equals("user")) printUserInfo(payload);
        if (type.equals("standard")) printStandardInfo(payload);
    }

    private static void printUserInfo(JSONObject infoResponse) {

        String info = "";

        info += divide("", "\n");
        info += String.format("ID: %s\n", infoResponse.getString("id"));
        info += "Type: user\n";

        JSONArray bots = infoResponse.getJSONArray("bots");

        info += "Online bots:  ";
        for(int i=0; i<bots.length(); i++) {
            JSONObject bot = bots.getJSONObject(i);
            if ((bot != null) && bot.getBoolean("online")) {
                info += bot.getString("id") + " ";
            }
        }
        info += "\nOffline bots: ";
        for(int i=0; i<bots.length(); i++) {
            JSONObject bot = bots.getJSONObject(i);
            if ((bot != null) && !bot.getBoolean("online")) {
                info += bot.getString("id") + " ";
            }
        }
        info += divide("\n", "");
        printToConsole(info, ANSI_BLUE);

    }

    private static String divide(String before, String after) {
        String r = before;
        for (int i=0; i<80; i++) r+="-";
        return r + after;
    }


    private static void printStandardInfo(JSONObject infoResponse) {
        String info = "";
        info += divide("", "\n");
        info += String.format("ID: %s\n", infoResponse.getString("id"));
        info += "Type:\tstandard\n";

        JSONArray subs = infoResponse.getJSONArray("subscriptions");
        info += "Subs:\t";
        for (int i=0; i<subs.length(); i++) info += "#" + subs.getString(i) + " ";

        info += "\nHook:\t" + infoResponse.getBoolean("hook");
        info += divide("\n", "");

        printToConsole(info);
    }


    /**
     * getBotCommands - return the commands for a bot of given type
     *
     * @param  String type the type of bot (user, standard, or socket)
     * @return List<String> the commands for the given type of bot
     */
    private static List<String> getBotCommands(String type) {
      switch (type) {
        case "user": return Arrays.asList("(i)nfo", "(s)end", "(c)reate bot", "(r)emove bot", "(q)uit");
        case "standard": return Arrays.asList("(i)nfo", "(s)end", "(f)ollow", "(u)nfollow", "(c)reate socket", "(r)emove socket", "hooks (on)", "hooks (off)", "(q)uit");
        case "socket": return Arrays.asList("(i)nfo", "(s)end", "(q)uit");
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
        printToConsole(commands.toString(), ANSI_YELLOW);

        while(true) {
            String command = prompt("\b\b\b\b\b>>> ");

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
                      JSONObject payload = new JSONObject();
                      payload.put("message", messageNoTags);

                      if (messageNoTags.length() > 0) {
                        if (ackTags.size() > 0)
                          bot.send(payload, tags, ackTags.get(0));
                        else
                          bot.send(payload, tags, "");
                      }
                      break;
                    case "i":
                    case "info":
                      bot.info("info");
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
                          break;
                      }
                      hashtagString = command.substring(commandSplit.get(0).length());
                      bot.follow(extractTags(hashtagString, "#"), "follow");
                      break;
                    case "u":
                    case "unfollow":
                      if (commandSplit.size() < 2) {
                        System.out.println("usage: (u)nfollow <hashtagString>");
                          break;
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
                          break;
                      }
                      bot.turnHooksOn(commandSplit.get(1), "hook");
                      break;
                    case "hide":
                        if (commandSplit.size() < 2) {
                            System.out.println("usage: hide <botId>");
                            break;
                        }
                        hidden.add(commandSplit.get(1));
                        break;
                    case "unhide":
                        hidden = new ArrayList<>();
                        break;
                    case "cl":
                    case "clear":
                      clear(); break;
                    case "h":
                    case "help":
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
    private static void clear(boolean banner) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        if (banner) System.out.println(ANSI_RED + " ▄▀▀▀█▀▀▄  ▄▀▀▀▀▄   ▄▀▀█▄▄   ▄▀▀▄ ▀▀▄\n█    █  ▐ █      █ ▐ ▄▀   █ █   ▀▄ ▄▀\n▐   █     █      █   █▄▄▄▀  ▐     █\n   █      ▀▄    ▄▀   █   █        █\n ▄▀         ▀▀▀▀    ▄▀▄▄▄▀      ▄▀\n█                  █    ▐       █\n▐                  ▐            ▐\n" + ANSI_RESET);
        if (banner && isConnected) System.out.println("Connection duration: " + getDateDiff(connectTime, new Date(), TimeUnit.SECONDS) + " seconds");
    }

    private static void clear() {
        clear(true);
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
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
        while (m.find()) { tags.add(m.group().trim().substring(1)); }
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

        Date startTime = new Date();
        Bot bot = new Bot(username, password, new OnConnect(), new OnDisconnect(), new OnMessage());
        bot.start();

        while (!bot.isConnected()) {}

        while(bot.isConnected()) {}
    }
}
