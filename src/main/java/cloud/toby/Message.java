
/**
 * Toby Message Class
 *
 */

 package cloud.toby;

import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;

public class Message {

  private String message;
  private String messageType;
  private String ackTag;
  private List<String> tags;

  /**
   * Constructor - create Message object with all fields
   *
   * @param  String message
   * @param  String messageType
   * @param  String ackTag
   * @param  List<String> tags
   */
  public Message(String message, String messageType, String ackTag, List<String> tags) throws MalformedMessageException {
    if (message == null || messageType == null)
      throw new MalformedMessageException("message and messageType required");

    if (tags == null)
      tags = Arrays.asList();

    this.message = message;
    this.messageType = messageType;
    this.ackTag = ackTag;
    this.tags = tags;
  }
  /**
   * Constructor - create Message object with no ackTag
   *
   * @param  String message
   * @param  String messageType
   * @param  List<String> tags
   */
  public Message(String message, String messageType, List<String> tags) throws MalformedMessageException {
    this(message, messageType, null, tags);
  }


  /**
   * Constructor - create Message object from message JSON string
   *
   * @param String messageString the JSON string representing the message
   */
  public Message(String messageString) throws MalformedMessageException {
    Gson gson = new Gson();
    Message m;
    try {
      m = gson.fromJson(messageString, Message.class);
    } catch (Exception e) {
      throw new MalformedMessageException("could not parse message");
    }
    if (m.getMessage() == null || m.getMessageType() == null)
      throw new MalformedMessageException("message and messageType required");

    this.message = m.getMessage();
    this.messageType = m.getMessageType();
    this.ackTag = m.getAckTag();
    this.tags = m.getTags();

    if (this.tags == null)
      this.tags = Arrays.asList();
  }

  public String getMessage() {
    return this.message;
  }
  public String getMessageType() {
    return this.messageType;
  }

  public String getAckTag() {
    return this.ackTag;
  }

  public List<String> getTags() {
    return this.tags;
  }

  /**
   * Get JSON String
   *
   * @return String  the JSON representation of the message
   */
  public String toString() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

}
