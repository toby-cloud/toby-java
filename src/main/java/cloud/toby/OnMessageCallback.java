
package cloud.toby;

interface OnMessageCallback {
    void go(Bot bot, String from, Message message);
    void malformed(Bot bot, String malformedMessage);
}
