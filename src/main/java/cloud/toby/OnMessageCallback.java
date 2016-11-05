
package cloud.toby;

interface OnMessageCallback {
    void go(Bot bot, String from, Message message);
}
