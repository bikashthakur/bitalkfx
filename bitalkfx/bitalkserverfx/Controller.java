package bitalkserverfx;

/**
 * Created by BIKASH THAKUR on 10-Dec-18.
 */

import java.lang.invoke.MethodHandle;
import java.net.URL;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.Insets;
import javafx.scene.control.*;

import bitalkserverfx.socket.SocketListener;
import bitalkserverfx.socket.FxServerSocket;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class Controller implements Initializable {

    @FXML
    private Button btnMsgSend;
    @FXML
    private Button btnConnect;
    @FXML
    private CheckBox checkBoxAutoConnect;
    @FXML
    private Label lblConnStatus;
    @FXML
    private Label lblPort;
    @FXML
    private Label lblErrorMsg;
    @FXML
    private TextField textFieldPort;
    @FXML
    private TextField textFieldMsg;
    @FXML
    private ListView<HBox> msgDisplayArea;

    private ObservableList<HBox> messages;

    private boolean isConnectionOpen;
    private boolean isClientConnected;

    private boolean portError;

    private FxServerSocket socket;

    private enum ConnectionState {
        DISCONNECTED, CONNECTED, WAITING
    }

    private void connect() {

        try {

            socket = new FxServerSocket(new FxSocketListener(), Integer.valueOf(textFieldPort.getText().trim()));
            socket.connect();

            if (portError) {
                portError = false;
                lblErrorMsg.setText("");
            }

        } catch (NumberFormatException e) {
            portError = true;
            lblErrorMsg.setText("ERROR: Please Enter a valid port number.");
            System.out.println("Please Enter a valid port number...");
        }

    }

    private void showConnectionState(ConnectionState state) {

        switch (state) {

            case DISCONNECTED:
                btnConnect.setText("Connect");
                btnMsgSend.setDisable(true);
                textFieldMsg.setDisable(true);
                lblConnStatus.setText("Not Connected");
                break;

            case CONNECTED:
                btnConnect.setText("Disconnect");
                btnMsgSend.setDisable(false);
                textFieldMsg.setDisable(false);
                lblConnStatus.setText("Connected");
                break;

            case WAITING:
                btnConnect.setText("Close");
                btnMsgSend.setDisable(true);
                textFieldMsg.setDisable(true);
                lblConnStatus.setText("Waiting..");
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        isConnectionOpen = false;
        isClientConnected = false;

        textFieldMsg.setDisable(true);
        btnMsgSend.setDisable(true);

        messages = FXCollections.observableArrayList();
        msgDisplayArea.setItems(messages);

        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    @Override
                    public void run() {
                        socket.shutdown();
                    }
                }
        );

    }

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String msg) {
            if (msg != null && !msg.equals("")) {
                messages.add(0, handleMessageDisplay(msg.trim(), true));
            }
        }

        @Override
        public void onError(String error) {
            if (error != null && !error.equals("")) {
                lblErrorMsg.setText(error);
            }
        }

        @Override
        public void isConnectionAlive(boolean alive) {

            isConnectionOpen = alive;

            if(isConnectionOpen) {
                showConnectionState(ConnectionState.WAITING);
            } else {
                showConnectionState(ConnectionState.DISCONNECTED);
            }

        }

        @Override
        public void isConnected(boolean connected) {

            isClientConnected = connected;

            if (isClientConnected) {
                showConnectionState(ConnectionState.CONNECTED);
            } else {
                showConnectionState(ConnectionState.WAITING);
            }

        }

    }

    private HBox handleMessageDisplay(String msg, boolean client) {

        HBox hbox = new HBox(12);
        hbox.setPadding(new Insets(9));
        hbox.setPrefHeight(HBox.USE_COMPUTED_SIZE);
        hbox.setPrefWidth(240);

        Circle circle =  new Circle(15);
        circle.setSmooth(true);
        circle.setEffect(new DropShadow(+5d, 0d, 2d, Color.valueOf("#333333")));

        hbox.getChildren().addAll(circle);

        Label txtMsg;
        StringBuilder msgInLines  = new StringBuilder();

        int letterPerLine = 32,
            msgLength = msg.length(),
            beginIndex = 0,
            endIndex = letterPerLine,
            spaceIndex = -1;

        while ((msgLength - beginIndex) >= letterPerLine ) {
            spaceIndex = msg.trim().lastIndexOf(" ", endIndex) > spaceIndex ? msg.lastIndexOf(" ", endIndex) : endIndex;
            msgInLines.append(msg.substring(beginIndex, spaceIndex).trim() + "\n");
            beginIndex = spaceIndex;
            endIndex = beginIndex + letterPerLine;
        }
        msgInLines.append(msg.substring(beginIndex).trim());
        txtMsg = new Label(msgInLines.toString());
        txtMsg.setFont(new Font("Arial", 16));

        txtMsg.setPadding(new Insets(7));

        hbox.getChildren().add(txtMsg);

        if(client) {
            txtMsg.setStyle("-fx-background-color:rgba(51, 102, 153, 0.5);");
            circle.setFill(Color.valueOf("#f3a333"));
            circle.setStroke(Color.valueOf("#f77f33"));
        } else {
            txtMsg.setStyle("-fx-background-color:#e5e5e5;");
            circle.setFill(Color.valueOf("#336699"));
            circle.setStroke(Color.valueOf("#2e4466"));
        }
        return hbox;
    }

    @FXML
    private void handleMessageSend(ActionEvent ae) {

        if(!textFieldMsg.getText().trim().equals("")) {

            socket.sendMessage(textFieldMsg.getText().trim());
            messages.add(0, handleMessageDisplay(textFieldMsg.getText().trim(), false));
            textFieldMsg.clear();

        }

    }

    @FXML
    private void handleConnectButton(ActionEvent ae) {

        if (!isConnectionOpen) {
            System.out.println("connecting..");
            connect();
        } else {
            socket.shutdown();
        }

    }

    @FXML
    private void handleAutoConnectCheckBox(ActionEvent ae) {

        if (checkBoxAutoConnect.isSelected()) {

            if (isClientConnected) {
                showConnectionState(ConnectionState.CONNECTED);
            } else {
                showConnectionState(ConnectionState.WAITING);
                connect();
            }

        } else {

            if (isClientConnected) {
                showConnectionState(ConnectionState.CONNECTED);
            } else {
                showConnectionState(ConnectionState.DISCONNECTED);
            }

        }

    }

}
