
package cloud.toby;

interface OnMessageCallback {
    void go(String from, Message message);
}
