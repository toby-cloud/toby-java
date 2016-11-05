
package cloud.toby;

import java.lang.Exception;

public class InvalidMessageException extends Exception {
  public InvalidMessageException(String message) {
    super(message);
  }
}
