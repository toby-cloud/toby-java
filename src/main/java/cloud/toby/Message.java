
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
   * Constructor
   *
   * @param  String message
   * @param  String messageType
   * @param  String ackTag
   * @param  List<String> tags
   */
  public Message(String message, String messageType, String ackTag, List<String> tags) {
    this.message = message;
    this.messageType = messageType;
    this.ackTag = ackTag;
    this.tags = tags;
  }
  /**
   * Constructor
   *
   * @param  String message
   * @param  String messageType
   * @param  List<String> tags
   */
  public Message(String message, String messageType, List<String> tags) {
    this.message = message;
    this.messageType = messageType;
    this.ackTag = ackTag;
    this.tags = tags;
  }

  public Message(String messageString) {
    Gson gson = new Gson();
    Message m = gson.fromJson(messageString, Message.class);
    this.message = m.getMessage();
    this.messageType = m.getMessageType();
    this.ackTag = m.getAckTag();
    this.tags = m.getTags();
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
