package bitalkclientfx.socket;

import java.io.IOException;
import java.net.*;

/**
 * Created by BIKASH THAKUR on 10-Dec-18.
 */

public class FxClientSocket extends GenericSocket {

    private String host;
    private SocketListener fxListener;

    @Override
    public void onMessage(final String msg) {

        javafx.application.Platform.runLater( () -> fxListener.onMessage(msg) );

    }

    @Override
    public void onError(final String error) {

        javafx.application.Platform.runLater( () -> fxListener.onError(error) );

    }

    ////this method is specific to server to track its connection status
    @Override
    public void isConnectionAlive(final boolean alive) {}

    @Override
    public void isConnected(final boolean connected) {

        javafx.application.Platform.runLater( () -> fxListener.isConnected(connected) );

    }

    @Override
    protected void initializeSocket() throws SocketException {

        try {

            socket = new Socket();
            socket.setReuseAddress(true);

            socket.connect(new InetSocketAddress(host, port));

            setUpConnection();

        } catch (UnknownHostException e) {

            System.out.println("Error in opening connection:: " + e.getMessage());
            onError("Please enter a valid IP address and try again: " + e.getMessage());
            throw new SocketException();

        } catch (SocketException e) {

            System.out.println("Error in opening connection:: " + e.getMessage());
            onError("Error in opening connection: Host Not Found.");
            throw new SocketException();

        }  catch (IOException e) {

            System.out.println("Error in opening connection:: " + e.getMessage());
            onError("Error in opening connection: " + e.getMessage());
            throw new SocketException();

        }

    }

    //this method is specific to server
    @Override
    protected void closeAdditionalSockets() {}

    /*
    public FxClientSocket(SocketListener fxListener) {
        this(fxListener, Constants.instance().DEFAULT_PORT);
    }
    */

    public FxClientSocket(SocketListener fxListener, String ip, int port) {
        super(port);
        this.host = ip;
        this.fxListener = fxListener;
    }

}
