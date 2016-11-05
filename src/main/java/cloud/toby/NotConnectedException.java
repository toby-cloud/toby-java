
package cloud.toby;

import java.lang.Exception;

public class NotConnectedException extends Exception {
  public NotConnectedException(String message) {
    super(message);
  }
}
