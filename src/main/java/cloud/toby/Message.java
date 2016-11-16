
/**
 * Toby Message Class
 *
 */

package cloud.toby;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

public class Message {

  private String from;
  private JSONObject payload;
  private List<String> tags;
  private String ack;

  /**
   * Constructor - create Message object with all fields
   *
   * @param  {String}  message
   * @param  {String}  messageType
   * @param  {String}  ack
   * @param  {List<String>} tags
   */
  public Message(String from, JSONObject payload, List<String> tags, String ack) throws MalformedMessageException {
    if (from == null || payload == null || tags == null || ack == null)
      throw new MalformedMessageException("missing parameters");

    if (tags == null)
      tags = Arrays.asList();

    this.from = from;
    this.payload = payload;
    this.tags = tags;
    this.ack = ack;
  }

  /**
   * Constructor - create Message object from message JSON string
   *
   * @param {String} messageString the JSON string representing the message
   */
  public Message(String messageString) throws MalformedMessageException {

    try {

      JSONObject m = new JSONObject(messageString);
      JSONArray t = m.getJSONArray("tags");

      List<String> tags = new ArrayList<String>();
      for (int i=0; i<t.length(); i++) {
        tags.add(t.getString(i));
      }

      this.from = m.getString("from");
      this.payload = m.getJSONObject("payload");
      this.tags = tags;
      this.ack = m.getString("ack");

    } catch (JSONException e) {
      throw new MalformedMessageException("could not parse string into message");
    }

  }

  public String getFrom() {
    return this.from;
  }
  public JSONObject getPayload() {
    return this.payload;
  }
  public List<String> getTags() {
    return this.tags;
  }
  public String getAck() {
    return this.ack;
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
