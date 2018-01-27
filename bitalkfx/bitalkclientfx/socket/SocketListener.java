package bitalkclientfx.socket;

/**
 * Created by BIKASH THAKUR on 10-Dec-18.
 */
public interface SocketListener {

    void onMessage(String msg);
    void onError(String error);
    //this method is specific to server to track its connection status
    void isConnectionAlive(boolean alive);
    void isConnected(boolean connected);

}
