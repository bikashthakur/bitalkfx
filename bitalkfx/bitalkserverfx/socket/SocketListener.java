package bitalkserverfx.socket;

/**
 * Created by BIKASH THAKUR on 10-Dec-18.
 */
public interface SocketListener {

    void onMessage(String msg);
    void onError(String error);
    void isConnectionAlive(boolean alive);
    void isConnected(boolean connected);

}
