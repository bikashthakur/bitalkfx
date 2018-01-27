package bitalkclientfx.socket;

import java.io.*;
import java.net.*;

/**
 * Created by BIKASH THAKUR on 10-Dec-18.
 */
public abstract class GenericSocket implements SocketListener {

    public int port;

    protected Socket socket = null;

    protected boolean clientDisconnected = false;

    private boolean ready = false;

    private BufferedReader input = null;
    private BufferedWriter output = null;

    private Thread setUPConnectionThread;

    public void connect() {

        try {
            initializeSocket();
        } catch (IOException e) {
            System.out.println("Error in initializing socket:: " + e.getMessage());
        }

    }

    protected void setUpConnection() {

        setUPConnectionThread = new Thread() {

            @Override
            public void run() {

                //set up streams
                try {

                    if(socket != null && !socket.isClosed()) {
                        input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
                        output = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
                        output.flush();
                        //notify that streams are ready to read the incoming messages
                        notifyReady();
                    }

                } catch (IOException e) {
                    System.out.println("Error in setting up streams..");
                    onError("Error in setting up streams: " + e.getMessage());
                    notifyReady();
                }

                //set up socket reader
                waitForReady();

                if (socket != null && !socket.isClosed()) {
                    isConnected(true);
                    onError("Connected to " + socket.getInetAddress().getHostName());
                }

                try {

                    if(input != null) {
                        String msg;
                        while ((msg = input.readLine()) != null) {
                            System.out.println("recv> " + msg);
                            onMessage(msg);
                        }
                    }

                }  catch (SocketException e) {
                    onError("Error in reading message: Disconnected");
                } catch (IOException e) {
                    System.out.println("Error in reading message from the socket:: " + e.getMessage());
                    onError("Error in reading message: " + e.getMessage());
                } finally {
                    closeSocket();
                }

            }

        };

        setUPConnectionThread.start();

    }

    public void shutdown() {
        closeSocket();
        closeAdditionalSockets();
    }

    public void sendMessage(String msg) {

        try {
            if(output != null) {
                output.write(msg, 0, msg.length());
                output.newLine();
                output.flush();
            }
        } catch (IOException e) {
            System.out.println("Error in sending message: " + e.getMessage());
            onError("Error in sending message: " + e.getMessage());
        }

    }

    protected void stallExec() {
        try {
            wait();
        } catch (InterruptedException e) {
            onError("Thread Interrupted");
        }
    }

    // these two methods are to be implemented by the specific socket - client or server
    protected abstract void initializeSocket() throws SocketException;
    protected abstract void closeAdditionalSockets();

    protected void closeSocket() {

        try {

            if(socket != null && !socket.isClosed()){
                socket.close();
                isConnected(false);
                onError("Connection Closed.");
            }

        } catch (IOException e) {
            System.out.println("Error in closing the connection: " + e.getMessage());
            onError("Error in closing the connection: " + e.getMessage());
        }

    }

    // synchronized methods
    private synchronized void notifyReady() {
        ready = true;
        notifyAll();
    }

    private synchronized void waitForReady() {
        while (!ready) {
            stallExec();
        }
    }

    public GenericSocket() {
        this(Constants.instance().DEFAULT_PORT);
    }

    public GenericSocket(int port) {
        this.port = port;
    }

}
