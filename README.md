[![Build Status](https://travis-ci.org/toby-cloud/toby-java.svg?branch=master)](https://travis-ci.org/toby-cloud/toby-java)

# Installing

toby-java uses Gradle. The jars *are not* yet available from a public repo, but will be soon.

To compile it yourself:

```bash
git clone https://github.com/toby-cloud/toby-node
cd toby-node
# see usage below
```

## Install gradle

`curl -s https://get.sdkman.io | bash`

Open new terminal

`sdk install gradle 3.1`


## Usage

```bash
gradle tasks
gradle assemble
gradle build
java -jar build/libs/toby-java-0.1.jar
```
Unit tests are run automatically with every build.

## Testing

You can run unit tests with the command `gradle check`.

- Coverage reports published to `./build/reports/jacoco/test/html/index.html`
- Test results published to `./build/reports/tests/test/index.html`


## Dependencies

- [fusesource/mqtt-client](https://github.com/fusesource/mqtt-client)
- [google/gson](https://github.com/google/gson)


# Documentation

## Connecting to Toby

### Defining callbacks

Before connecting to Toby, you must provide three callbacks for your bot:
- OnConnect: called when bot successfully establishes connection with the server.
- OnDisconnect: called when bot disconnects from the server.
- OnMessage: called when the bot receives a message.

```java
private class OnConnect implements OnConnectCallback {
  public void go(Bot bot) {
    System.out.println("Connected!");
  }
}

private class OnDisconnect implements OnDisconnectCallback {
  public void go(Bot bot) {
    System.out.println("Disconnected!");
  }
}

private class OnMessage implements OnMessageCallback {
  public void go(Bot bot, Message m) {
    System.out.println("Message received: " + m.toString());
  }
}
```

### Start bot

Once the callbacks are defined, you can connect to Toby as follows:

```java
  String id = "{{ botId }}"; // your bot ID from toby.cloud
  String sk = "{{ botSk }}"; // your bot secret from toby.cloud

  Bot bot = new Bot(id, sk, new OnConnect(), new OnDisconnect(), new OnMessage());
  bot.start();  
```
