package bitalkserverfx.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

/**
 * Created by BIKASH THAKUR on 10-Dec-18.
 */

public class FxServerSocket extends GenericSocket implements SocketListener {

    private SocketListener fxListener;
    private ServerSocket serverSocket;

    @Override
    public void onMessage(final String msg) {

        javafx.application.Platform.runLater( () -> fxListener.onMessage(msg) );

    }

    @Override
    public void onError(final String error) {

        javafx.application.Platform.runLater( () -> fxListener.onError(error) );

    }

    @Override
    public void isConnectionAlive(final boolean alive) {

        javafx.application.Platform.runLater( () -> fxListener.isConnectionAlive(alive) );

    }

    @Override
    public void isConnected(final boolean connected) {

        javafx.application.Platform.runLater( () -> fxListener.isConnected(connected) );

    }

    @Override
    protected void initializeSocket() throws SocketException {

        try {

            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            isConnectionAlive(true);

            new ListenForClient().start();

        } catch (IOException e) {

            System.out.println("Error in opening connection:: " + e.getMessage());
            onError("Error in opening connection:: " + e.getMessage());
            throw new SocketException();

        }

    }

    private class ListenForClient extends Thread {

        @Override
        public void run() {

            while (serverSocket != null && !serverSocket.isClosed()) {

                try {

                    onError("Waiting for new client..");
                    socket = serverSocket.accept();

                    notifyNewClientFound();

                    setUpConnection();

                    System.out.println("Connection received from " + socket.getInetAddress().getHostName());
                    onError("Connection received from " + socket.getInetAddress().getHostName());

                    waitForClientToDisconnect();

                    closeSocket();

                } catch (IOException e) {

                    System.out.println("Error in listening for clients:: " + e.getMessage());
                    onError("Error in listening for clients:: " + e.getMessage());

                }

            }

        }

    }

    @Override
    protected void closeAdditionalSockets() {

        try {

            if(serverSocket != null && !serverSocket.isClosed()) {

                serverSocket.close();

                isConnectionAlive(false);

            }

        } catch (IOException e) {

            System.out.println("Error in closing the server socket:: " + e.getMessage());
            onError("Error in closing the server socket:: " + e.getMessage());
        }

    }

    @Override
    protected synchronized void notifyNewClientFound() {

        clientDisconnected = false;
        newClientFound = true;

        isConnected(true);
        notifyAll();

    }

    private synchronized void waitForClientToDisconnect () {

        while (!clientDisconnected) {
            stallExec();
        }

    }

    public FxServerSocket(SocketListener fxListener) {
        this(fxListener, Constants.instance().DEFAULT_PORT);
    }

    public FxServerSocket(SocketListener fxListener, int port) {
        super(port);
        this.fxListener = fxListener;
    }

}
