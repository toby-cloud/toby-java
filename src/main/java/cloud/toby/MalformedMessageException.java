
package cloud.toby;

import java.lang.Exception;

public class MalformedMessageException extends Exception {
  public MalformedMessageException(String message) {
    super(message);
  }
}
